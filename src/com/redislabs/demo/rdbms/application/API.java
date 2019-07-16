package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.Test;
import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.pojo.Base;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {

    private Cache cache;
    private Repository repo;

    public API() {
        this.cache = new Cache("redis://localhost");
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

    public <T extends Base> void set(T o) {
        this.repo.set(o);
        this.cache.set(o);
    }

    public <T extends Base> void del(Class<T> cls, int id) {
        this.repo.del(cls, id);
        this.cache.del(cls, id);
    }

    public void close() {
        try {
            cache.close();
            repo.close();
        } catch (IOException ex) {
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}
