package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.infrastructure.pojo.Base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

public class API {

    private Logger logger = Logger.getLogger(API.class.getName());

    private Cache cache;

    private Repository repo;

    public API(String redisUrl, String postgresUrl) {
        this.cache = new Cache(redisUrl);
        this.repo = new Repository(postgresUrl);
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
            if (this.repo.update(o) != 1) {
                try {
                    this.repo.insert(o);
                } catch (SQLException ne) {
                    logger.severe(ne.toString());
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.severe(e.toString());
            return false;
        }
        return this.cache.set(o);
    }

    public <T extends Base> boolean del(Class<T> cls, int id) {
        try {
            T o = this.repo.select(cls, id);
            if (this.repo.delete(o) != 1) {
                return false;
            }
        } catch (SQLException e) {
            logger.severe(e.toString());
            return false;
        }
        return this.cache.del(cls, id);
    }

    public <T extends Base> boolean del(T o) {
        try {
            if (this.repo.delete(o) != 1) {
                return false;
            }
        } catch (SQLException e) {
            logger.severe(e.toString());
            return false;
        }
        return this.cache.del(o.getClass(), o.getId());
    }

    public void close() {
        cache.flush();
        repo.reset();
        try {
            cache.close();
            repo.close();
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }
}
