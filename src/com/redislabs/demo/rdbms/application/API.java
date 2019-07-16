package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.Test;
import com.redislabs.demo.rdbms.infrastructure.Cache;
import com.redislabs.demo.rdbms.infrastructure.Repository;
import com.redislabs.demo.rdbms.pojo.Author;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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

    public Author getAuthor(int id) {

        Map<String, String> cached = cache.get("author:" + id);

        if (cached.isEmpty()) {
            Author[] stored = repo.getAuthors();
            if (stored.length == 0) {
                return null;
            }

            for (Author a : stored) {
                Map<String, String> vals = new HashMap<>();
                vals.put("name", a.getName());
                cache.set("author:" + a.getId(), vals);
            }

            cached = cache.get("author:" + id);
        }

        if (cached.isEmpty()) {
            return null;
        }

        return new Author(id, cached.get("name"));
    }

    public void setAuthor(Author author) {
        this.repo.setAuthor(author);
        Map<String, String> vals = new HashMap<>();
        vals.put("name", author.getName());
        this.cache.set("author:" + author.getId(), vals);
    }

    public void delAuthor(int id) {
        this.repo.delAuthor(id);
        this.cache.del("author:" + id);
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
