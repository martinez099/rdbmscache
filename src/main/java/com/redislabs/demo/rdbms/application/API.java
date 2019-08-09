package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.infrastructure.pojo.Base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {

    private Logger logger = Logger.getLogger(API.class.getName());

    private Cache cache;

    private Repository repo;

    public API() {
        this.cache = new Cache("redis://localhost:6379");
        this.repo = new Repository("jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=postgres");
    }

    public <T extends Base> T get(Class<T> cls, int id) {
        T cached = cache.get(cls, id);
        if (cached == null) {
            T[] stored = null;
            try {
                stored = repo.select(cls);
            } catch (SQLException e) {
                logger.severe(e.toString());
                return null;
            }
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
        try {
            if (this.repo.insert(o) == 0) {
                this.repo.update(o);
            };
        } catch (SQLException e) {
            logger.severe(e.toString());
            return false;
        }
        return this.cache.set(o);
    }

    public <T extends Base> boolean del(Class<T> cls, int id) {
        try {
            T o = this.repo.select(cls, id);
            this.repo.delete(o);
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
        return this.cache.del(cls, id);
    }

    public <T extends Base> boolean del(T o) {
        try {
            this.repo.delete(o);
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
        return this.cache.del(o.getClass(), o.getId());
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
