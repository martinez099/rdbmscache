package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.pojo.Base;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {

    private Logger logger = Logger.getLogger(API.class.getName());

    private Cache cache;

    private Repository repo;

    public API() {
        this.cache = new Cache("redis://localhost:6379");
        this.repo = new Repository("jdbc:postgresql://127.0.0.1:5432/postgres",
                "postgres",
                "postgres");
    }

    public <T extends Base> T get(Class<T> cls, int id) {
        T cached = cache.get(cls, id);
        if (cached == null) {
            T[] stored = repo.get(cls);
            if (stored.length == 0) {
                return null;
            }
            for (T s : stored) {
                cache.set(s);
            }
            cached = cache.get(cls, id);
        }
        return cached;
    }

    public <T extends Base> boolean set(T o) {
        return this.repo.set(o) && this.cache.set(o);
    }

    public <T extends Base> boolean del(Class<T> cls, int id) {
        return this.repo.del(cls, id) && this.cache.del(cls, id);
    }

    public void close() {
        try {
            cache.close();
            repo.close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
