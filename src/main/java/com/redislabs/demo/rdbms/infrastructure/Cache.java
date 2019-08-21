package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.infrastructure.domain.Base;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.*;
import java.util.logging.Logger;

import io.lettuce.core.codec.ByteArrayCodec;

public class Cache implements Closeable {

    private static Logger logger = Logger.getLogger(Cache.class.getName());

    private RedisClient redisClient;

    private StatefulRedisConnection<byte[], byte[]> redisConnection;

    private RedisCommands<byte[], byte[]> sync;

    public Cache(String url) {
        redisClient = RedisClient.create(url);
        redisConnection = redisClient.connect(new ByteArrayCodec());
        sync = redisConnection.sync();
    }

    public void flush() {
        sync.flushdb();
    }

    public <T extends Base> boolean set(T o) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            logger.severe(e.toString());
            return false;
        }
        String key = o.getClass().getName() + ':' + o.getId();
        return this.sync.set(key.getBytes(), baos.toByteArray()).equals("OK");
    }

    public <T extends Base> T get(Class<T> cls, Integer id) {
        String key = cls.getName() + ':' + id;
        byte[] value = sync.get(key.getBytes());
        if (value == null) {
            return null;
        }
        ByteArrayInputStream bais = new ByteArrayInputStream(value);
        try {
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe(e.toString());
            return null;
        }
    }

    public <T extends Base> boolean del(Class<T> cls, int id) {
        String key = cls.getName() + ':' + id;
        return this.sync.del(key.getBytes()) > 0;
    }

    @Override
    public void close() throws IOException {
        redisConnection.close();
        redisClient.shutdown();
    }

}
