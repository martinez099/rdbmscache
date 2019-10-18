package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.domain.Base;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;


public class API {

    private static Logger logger = Logger.getLogger(API.class.getName());

    private Cache cache;

    private Repository repo;

    public API(String redisUrl, String postgresUrl) {
        this.cache = new Cache(redisUrl);
        this.repo = new Repository(postgresUrl);
    }

    /**
     * Get all domain objects.
     *
     * @param cls The class of the objects.
     * @param <T> The type of the objects.
     * @return The objects.
     */
    public <T extends Base> T[] get(Class<T> cls) {
        T[] stored;
        try {
            stored = repo.select(cls);
        } catch (SQLException e) {
            logger.severe(e.toString());
            return null;
        }
        for (T s : stored) {
            cache.set(s);
        }
        return stored;
    }

    /**
     * Get a domain object.
     *
     * @param cls The class of the object.
     * @param id The ID of the object.
     * @param <T> The type of the object.
     * @return The object.
     */
    public <T extends Base> T get(Class<T> cls, int id) {
        T cached = cache.get(cls, id);
        if (cached == null) {
            T[] stored;
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

    /**
     * Set a domain object.
     *
     * @param o The object.
     * @param <T> The type of the object.
     * @return Success.
     */
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

    /**
     * Delete a domain object.
     *
     * @param cls The class of the object.
     * @param id The ID of the object.
     * @param <T> The type of the object.
     * @return Success.
     */
    public <T extends Base> boolean del(Class<T> cls, int id) {
        try {
            T o = this.repo.select(cls, id);
            return this.del(o);
        } catch (SQLException e) {
            logger.severe(e.toString());
            return false;
        }
    }

    /**
     * Delete a domain object.
     *
     * @param o The object.
     * @param <T> The type of the object.
     * @return Success.
     */
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

    /**
     * Reset the cache and the repository.
     */
    public void reset() {
        cache.flush();
        repo.reset();
    }

    /**
     * Close the API and reset the datastores.
     */
    public boolean close() {
        try {
            cache.close();
            repo.close();
            return true;
        } catch (IOException e) {
            logger.severe(e.toString());
            return false;
        }
    }
}
