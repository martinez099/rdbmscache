package com.redislabs.demo.rdbms;

import com.redislabs.demo.rdbms.application.API;
import com.redislabs.demo.rdbms.application.JsonTransformer;
import com.redislabs.demo.rdbms.infrastructure.domain.Author;
import com.redislabs.demo.rdbms.infrastructure.domain.Book;
import com.redislabs.demo.rdbms.infrastructure.domain.Picture;

import com.google.gson.Gson;

import static spark.Spark.*;

public class Main {

    static Gson gson = new Gson();

    static API api = new API("redis://localhost:6379",
            "jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=postgres");

    public static void main(String[] args) {

        get("/authors", "application/json", (req, res) ->
                api.get(Author.class), new JsonTransformer()
        );

        get("/books", "application/json", (req, res) ->
                api.get(Book.class), new JsonTransformer()
        );

        get("/pictures", "application/json", (req, res) ->
                api.get(Picture.class), new JsonTransformer()
        );

        get("/author/:id", "application/json", (req, res) -> {
            String id = req.params(":id");
            return api.get(Author.class, Integer.valueOf(id));
        }, new JsonTransformer());

        get("/book/:id", "application/json", (req, res) -> {
            String id = req.params(":id");
            return api.get(Book.class, Integer.valueOf(id));
        }, new JsonTransformer());

        get("/picture/:id", "application/json", (req, res) -> {
            String id = req.params(":id");
            return api.get(Picture.class, Integer.valueOf(id));
        }, new JsonTransformer());

        put("/author/:id", "application/json", (req, res) -> {
            Author author = gson.fromJson(req.body(), Author.class);
            author.setId(Integer.parseInt(req.params(":id")));
            return api.set(author);
        }, new JsonTransformer());

        put("/book/:id", "application/json", (req, res) -> {
            Book book = gson.fromJson(req.body(), Book.class);
            book.setId(Integer.parseInt(req.params(":id")));
            return api.set(book);
        }, new JsonTransformer());

        put("/picture/:id", "application/json", (req, res) -> {
            Picture picture = gson.fromJson(req.body(), Picture.class);
            picture.setId(Integer.parseInt(req.params(":id")));
            return api.set(picture);
        }, new JsonTransformer());

        post("/author", "application/json", (req, res) -> {
            Author author = gson.fromJson(req.body(), Author.class);
            return api.set(author);
        }, new JsonTransformer());

        post("/book", "application/json", (req, res) -> {
            Book book = gson.fromJson(req.body(), Book.class);
            return api.set(book);
        }, new JsonTransformer());

        post("/picture", "application/json", (req, res) -> {
            Picture picture = gson.fromJson(req.body(), Picture.class);
            return api.set(picture);
        }, new JsonTransformer());

        delete("/author/:id", "application/json", (req, res) ->
                api.del(Author.class, Integer.parseInt(req.params(":id"))), new JsonTransformer()
        );

        delete("/book/:id", "application/json", (req, res) ->
                api.del(Book.class, Integer.parseInt(req.params(":id"))), new JsonTransformer()
        );

        delete("/picture/:id", "application/json", (req, res) ->
                api.del(Picture.class, Integer.parseInt(req.params(":id"))), new JsonTransformer()
        );

    }
}
