package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.infrastructure.domain.Base;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.io.IOException;
import java.util.logging.Logger;

import com.google.gson.Gson;

public class Cache implements Closeable {

    private Logger logger = Logger.getLogger(Cache.class.getName());

    private RedisClient redisClient;

    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> sync;

    private Gson gson = new Gson();

    public Cache(String url) {
        redisClient = RedisClient.create(url);
        redisConnection = redisClient.connect();
        sync = redisConnection.sync();
    }

    public void flush() {
        sync.flushdb();
    }

    public <T extends Base> boolean set(T o) {
        String value = gson.toJson(o);
        return this.sync.set(o.getClass().getName() + ':' + o.getId(), value).equals("OK");
    }

    public <T extends Base> T get(Class<T> cls, Integer id) {
        String value = this.sync.get(cls.getName() + ':' + id);
        return value != null ? gson.fromJson(value, cls) : null;
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
