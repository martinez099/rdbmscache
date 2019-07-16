package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.Test;
import com.redislabs.demo.rdbms.pojo.Author;
import com.redislabs.demo.rdbms.pojo.Book;
import com.redislabs.demo.rdbms.pojo.Picture;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
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
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Author[0]);
    }

    public boolean setAuthor(Author author) {

        String stmt = "INSERT INTO authors (id, name) VALUES (?, ?)";
        PreparedStatement pStmt = null;
        try {
            pStmt = sqlConnection.prepareStatement(stmt);
            pStmt.setInt(1, author.getId());
            pStmt.setString(2, author.getName());
            try {
                pStmt.executeUpdate();

                return true;
            } catch (org.postgresql.util.PSQLException ex) {
                stmt = "UPDATE authors SET name = ? WHERE id = ?";
                pStmt = sqlConnection.prepareStatement(stmt);
                pStmt.setString(1, author.getName());
                pStmt.setInt(2, author.getId());

                return pStmt.executeUpdate() == 1;
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }
    }

    public boolean delAuthor(int id) {
        String stmt = "DELETE FROM authors WHERE id = ?";
        try {
            PreparedStatement pStmt = sqlConnection.prepareStatement(stmt);
            pStmt.setInt(1, id);

            return pStmt.executeUpdate() == 1;
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }
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
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Book[0]);
    }

    public Picture[] getImgaes() {
        List<Picture> result = new ArrayList<>();
        ResultSet rs = this.query("SELECT * FROM images");

        try {
            while (rs.next()) {
                Picture i = new Picture(rs.getInt(1), rs.getInt(2), rs.getBytes(3));
                result.add(i);
            }
        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return result.toArray(new Picture[0]);
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
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return null;
        }
    }

    private boolean command(String stmt) {
        try {
            Statement st = sqlConnection.createStatement();

            return st.execute(stmt);

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Test.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }
    }
}
