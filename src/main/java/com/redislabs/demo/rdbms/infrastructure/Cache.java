package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.infrastructure.pojo.Base;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cache implements Closeable {

    private Logger logger = Logger.getLogger(Cache.class.getName());

    private RedisClient redisClient;

    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> sync;

    public Cache(String url) {
        redisClient = RedisClient.create(url);
        redisConnection = redisClient.connect();
        sync = redisConnection.sync();
    }

    public <T extends Base> boolean set(T o) {
        Map<String, String> vals = getVals(o);
        if (vals == null) {
            return false;
        }

        return this.sync.hmset(o.getClass().getName() + ':' + o.getId(), vals).equals("OK");
    }

    public <T extends Base> T get(Class<T> cls, Integer id) {
        Map<String, String> vals = sync.hgetall(cls.getName() + ':' + id);
        if (vals.isEmpty()) {
            return null;
        }

        try {
            Constructor<?>[] cnsts = cls.getConstructors();
            Class<?>[] paramTypes = cnsts[0].getParameterTypes();

            if (paramTypes.length == 1) {
                Constructor<T> cnst = cls.getConstructor(paramTypes[0]);
                return cnst.newInstance(id);
            }
            Object[] params = decodeTypes(vals);
            if (paramTypes.length == 2) {
                Constructor<T> cnst = cls.getConstructor(paramTypes[0], paramTypes[1]);
                return cnst.newInstance(id, params[0]);
            }
            if (paramTypes.length == 3) {
                Constructor<T> cnst = cls.getConstructor(paramTypes[0], paramTypes[1], paramTypes[2]);
                return cnst.newInstance(id, params[0], params[1]);
            }
        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public <T extends Base> boolean del(Class<T> cls, int id) {
        return this.sync.del(cls.getName() + ':' + id) > 0;
    }

    @Override
    public void close() throws IOException {
        redisConnection.close();
        redisClient.shutdown();
    }

    private Object[] decodeTypes(Map<String, String> vals) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : vals.entrySet()) {
            Integer maybeInt;
            try {
                maybeInt = Integer.parseInt(entry.getValue());
            } catch (NumberFormatException e) {
                result.put(entry.getKey(), entry.getValue());
                continue;
            }
            result.put(entry.getKey(), maybeInt);
        }
        return result.values().toArray(new Object[0]);
    }

    private <T extends Base> Map<String, String> getVals(T o) {
        Class cls = o.getClass();
        Field[] fieldlist = cls.getDeclaredFields();
        Map<String, String> result = new LinkedHashMap<>();

        for (Field field : fieldlist) {
            String fname = field.getName();
            try {
                Method getter = cls.getDeclaredMethod(getMethodName(fname));
                Object value = getter.invoke(o);
                result.put(fname, String.valueOf(value));
            } catch (IllegalAccessException| InvocationTargetException |NoSuchMethodException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);

                return null;
            }
        }
        return result;
    }

    private String getMethodName(String fieldname) {
        return "get" + fieldname.substring(0, 1).toUpperCase() + fieldname.substring(1);
    }

}
