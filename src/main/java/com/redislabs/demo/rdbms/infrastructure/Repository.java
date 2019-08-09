package com.redislabs.demo.rdbms.infrastructure;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.redislabs.demo.rdbms.infrastructure.pojo.Author;
import com.redislabs.demo.rdbms.infrastructure.pojo.Base;
import com.redislabs.demo.rdbms.infrastructure.pojo.Book;
import com.redislabs.demo.rdbms.infrastructure.pojo.Picture;

import java.io.Closeable;
import java.io.IOException;
import java.rmi.activation.UnknownObjectException;
import java.sql.*;
import java.util.List;
import java.util.logging.Logger;

public class Repository implements Closeable {

    private Logger logger = Logger.getLogger(Repository.class.getName());

    private JdbcConnectionSource con;

    private Dao<Author, Integer> authorDao;

    private Dao<Book, Integer> bookDao;

    private Dao<Picture, Integer> pictureDao;

    public Repository(String url) {
        try {
            this.setup(url);
        } catch (SQLException e) {
            logger.severe(e.toString());
        }
    }

    public void setup(String url) throws SQLException {
        con = new JdbcConnectionSource(url);

        authorDao = DaoManager.createDao(con, Author.class);
        bookDao = DaoManager.createDao(con, Book.class);
        pictureDao = DaoManager.createDao(con, Picture.class);
    }


    public <T extends Base> T[] select(Class<T> cls) throws SQLException {
        switch (cls.getName()) {
            case "Author":
                List<Author> authors = authorDao.queryForAll();
                return (T[]) authors.toArray(new Author[0]);
            case "Book":
                List<Book> books = bookDao.queryForAll();
                return (T[]) books.toArray(new Book[0]);
            case "Picture":
                List<Picture> pics = pictureDao.queryForAll();
                return (T[]) pics.toArray(new Picture[0]);
            default:
                throw new RuntimeException("unknown class " + cls.getName());
        }
    }

    public <T extends Base> T select(Class<T> cls, int id) throws SQLException {
        switch (cls.getName()) {
            case "Author":
                return (T) authorDao.queryForId(id);
            case "Book":
                return (T) bookDao.queryForId(id);
            case "Picture":
                return (T) pictureDao.queryForId(id);
            default:
                throw new RuntimeException("unknown class " + cls.getName());
        }
    }

    public <T extends Base> int update(T o) throws SQLException {
        switch (o.getClass().getName()) {
            case "Author":
                return authorDao.update((Author) o);
            case "Book":
                return bookDao.update((Book) o);
            case "Picture":
                return pictureDao.update((Picture) o);
            default:
                throw new RuntimeException("unknown class " + o.getClass().getName());
        }
    }

    public <T extends Base> int insert(T o) throws SQLException {
        switch (o.getClass().getName()) {
            case "Author":
                authorDao.create((Author) o);
                return o.getId();
            case "Book":
                bookDao.create((Book) o);
                return o.getId();
            case "Picture":
                pictureDao.create((Picture) o);
                return o.getId();
            default:
                throw new RuntimeException("unknown class " + o.getClass().getName());
        }
    }

    public <T extends Base> int delete(T o) throws SQLException {
        switch (o.getClass().getName()) {
            case "Author":
                return authorDao.delete((Author) o);
            case "Book":
                return bookDao.delete((Book) o);
            case "Picture":
                return pictureDao.delete((Picture) o);
            default:
                throw new RuntimeException("unknown class " + o.getClass().getName());
        }
    }

    @Override
    public void close() throws IOException {
        this.con.close();
    }

}
