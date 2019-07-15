package com.redislabs.demo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {

    private Cache cache;
    private Repository repo;

    public Client() {
        this.cache = new Cache("redis://localhost");
        this.repo = new Repository("jdbc:postgresql://127.0.0.1:5432/postgres",
                "postgres",
                "postgres");
    }

    private Author getAuthor(int id) {

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

    private void setAuthor(Author author) {
        Map<String, String> vals = new HashMap<>();
        vals.put("name", author.getName());
        cache.set("author:" + author.getId(), vals);
    }

    private void close() {
        try {
            cache.close();
            repo.close();
        } catch (IOException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();

        Author a = client.getAuthor(1);
        System.out.println(a);

        client.close();
    }
}
