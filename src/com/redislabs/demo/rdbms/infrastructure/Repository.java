package com.redislabs.demo.rdbms.infrastructure;

import com.redislabs.demo.rdbms.pojo.Base;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository implements Closeable {

    private Logger logger = Logger.getLogger(Repository.class.getName());

    private Connection con;

    public Repository(String url, String username, String password) {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);

            return;
        }

        try {
            con = DriverManager.getConnection(url, username, password);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public <T extends Base> T[] get(Class<T> cls) {
        List<T> result = new ArrayList<>();
        ResultSet rs = this.query("SELECT * FROM " + getTableName(cls));

        try {
            while (rs.next()) {
                Constructor<?>[] cnsts = cls.getConstructors();
                if (cnsts[0].getParameterTypes().length == 2) {
                    Constructor<T> cnst = cls.getConstructor(int.class, String.class);
                    T t = cnst.newInstance(rs.getInt(1), rs.getString(2));
                    result.add(t);
                }
                if (cnsts[0].getParameterTypes().length == 3) {
                    Constructor<T> cnst = cls.getConstructor(Integer.class, String.class, String.class);
                    T t = cnst.newInstance(rs.getInt(1), rs.getString(2), rs.getString(3));
                    result.add(t);
                }
            }
        } catch (SQLException|
                NoSuchMethodException|
                InstantiationException|
                IllegalAccessException|
                InvocationTargetException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);

            return null;
        }

        return result.toArray((T[]) Array.newInstance(cls, 0));
    }

    public <T extends Base> boolean set(T o) {
        Class cls = o.getClass();
        Field[] fieldlist = cls.getDeclaredFields();
        Method[] methodList = cls.getDeclaredMethods();
        Map<String, String> vals = new HashMap<>();

        for (int i = 0; i < fieldlist.length; i++) {
            String fname = fieldlist[i].getName();
            try {
                Object value = methodList[i].invoke(o);
                vals.put(fname, String.valueOf(value));
            } catch (IllegalAccessException|InvocationTargetException ex) {
                logger.log(Level.SEVERE, ex.getMessage(), ex);

                return false;
            }
        }

        StringBuilder stmt = new StringBuilder("INSERT INTO " + getTableName(cls) + " (id, ");
        for (String key : vals.keySet()) {
            stmt.append(key).append(", ");
        }
        stmt.replace(stmt.length() - 2, stmt.length(), ") ");
        stmt.append("VALUES (").append(o.getId()).append(", '");
        for (String val : vals.values()) {
            stmt.append(val).append(", ");
        }
        stmt.replace(stmt.length() - 2, stmt.length(), "')");
        stmt.append(" ON CONFLICT (id) DO UPDATE SET ");
        for (Map.Entry<String, String> ent : vals.entrySet()) {
            stmt.append(ent.getKey()).append(" = '").append(ent.getValue()).append("', ");
        }
        stmt.replace(stmt.length() - 2, stmt.length(), " WHERE ");
        stmt.append(getTableName(cls)).append(".id = ").append(o.getId());

        return this.command(stmt.toString());
    }

    public <T> boolean del(Class<T> cls, int id) {
        String stmt = "DELETE FROM " + getTableName(cls) + " WHERE id = " + id;

        return this.command(stmt);
    }

    @Override
    public void close() throws IOException {
        try {
            con.close();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);

            throw new IOException(ex);
        }
    }

    private ResultSet query(String stmt) {
        try {
            Statement st = con.createStatement();

            return st.executeQuery(stmt);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);

            return null;
        }
    }

    private boolean command(String stmt) {
        try {
            Statement st = con.createStatement();

            return st.execute(stmt);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);

            return false;
        }
    }

    private String getTableName(Class<?> cls) {
        return cls.getName().substring(cls.getName().lastIndexOf('.') + 1).toLowerCase() + 's';
    }
}
