package com.redislabs.demo.rdbms.infrastructure;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;


public class Cache implements Closeable {

    private RedisClient redisClient;
    private StatefulRedisConnection<String, String> redisConnection;
    private RedisCommands<String, String> sync;

    public Cache(String url) {
        redisClient = RedisClient.create(url);
        redisConnection = redisClient.connect();
        sync = redisConnection.sync();
    }

    public String set(String key, Map<String, String> value) {
        return sync.hmset(key, value);
    }

    public Map<String, String> get(String key) {
        return sync.hgetall(key);
    }

    public Long del(String key) {
        return sync.del(key);
    }

    @Override
    public void close() throws IOException {
        redisConnection.close();
        redisClient.shutdown();
    }
}
