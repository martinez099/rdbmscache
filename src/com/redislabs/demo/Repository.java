package com.redislabs.demo;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository implements Closeable {

    private Connection sqlConnection;

    public Repository(String url, String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver Registered!");
        try {
            sqlConnection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
        }
    }

    public Author[] getAuthors() {
        List<Author> result = new ArrayList<>();
        ResultSet rs = this.query("SELECT * FROM authors");

        try {
            while (rs.next()) {
                Author a = new Author(rs.getInt(1), rs.getString(2));
                result.add(a);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Author[0]);
    }

    public void setAuthor(Author author) {
        /*
        try {
            this.command("INSERT INTO author VALUES (%1, %2)".)
        }
        */
    }

    public Book[] getBooks() {
        List<Book> result = new ArrayList<>();
        ResultSet rs = this.query("SELECT * FROM books");

        try {
            while (rs.next()) {
                Book b = new Book(rs.getInt(1), rs.getInt(2), rs.getString(3));
                result.add(b);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Book[0]);
    }

    public Image[] getImgaes() {
        List<Image> result = new ArrayList<>();
        ResultSet rs = this.query("SELECT * FROM images");

        try {
            while (rs.next()) {
                Image i = new Image(rs.getInt(1), rs.getInt(2), rs.getBytes(3));
                result.add(i);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Image[0]);
    }

    @Override
    public void close() throws IOException {
        try {
            sqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    private ResultSet query(String stmt) {
        try {
            Statement st = sqlConnection.createStatement();

            return st.executeQuery(stmt);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return null;
        }
    }

    private boolean command(String stmt) {
        try {
            Statement st = sqlConnection.createStatement();

            return st.execute(stmt);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }
    }
}
