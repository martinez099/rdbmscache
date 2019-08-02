package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.pojo.Base;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
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
        Class cls = o.getClass();
        Field[] fieldlist = cls.getDeclaredFields();
        Method[] methodList = cls.getDeclaredMethods();
        Map<String, String> vals = new HashMap<>();
        for (int i = 0; i < fieldlist.length; i++) {
            String fname = fieldlist[i].getName();
            try {
                Object value = methodList[i].invoke(o);
                vals.put(fname, String.valueOf(value));
            } catch (IllegalAccessException|InvocationTargetException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);

                return false;
            }
        }
        return this.sync.hmset(cls.getName() + ':' + o.getId(), vals).equals("OK");
    }

    public <T extends Base> T get(Class<T> cls, int id) {
        Map<String, String> vals = sync.hgetall(cls.getName() + ':' + id);

        if (vals.isEmpty()) {
            return null;
        }

        try {
            Constructor<?>[] cnsts = cls.getConstructors();
            if (cnsts[0].getParameterTypes().length == 2) {
                Constructor<T> cnst = cls.getConstructor(int.class, String.class);
                return cnst.newInstance(id, vals.values().toArray(new String[0])[0]);
            }
            if (cnsts[0].getParameterTypes().length == 3) {
                Constructor<T> cnst = cls.getConstructor(Integer.class, String.class, String.class);
                return cnst.newInstance(id, vals.values().toArray(new String[0])[0], vals.values().toArray(new String[0])[1]);
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

}
