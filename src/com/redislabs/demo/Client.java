package com.redislabs.demo;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.pubsub.RedisPubSubListener;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Client {

    private RedisClient redisClient;

    private StatefulRedisConnection<String, String> redisConnection;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;

    private RedisCommands<String, String> sync;
    private RedisPubSubCommands<String, String> pubsub;

    private Connection sqlConnection;

    public Client() {
        redisClient = RedisClient.create("redis://localhost");

        redisConnection = redisClient.connect();
        pubSubConnection = redisClient.connectPubSub();

        sync = redisConnection.sync();
        pubsub = pubSubConnection.sync();

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
            sqlConnection = DriverManager.getConnection(
                    "jdbc:postgresql://127.0.0.1:5432/postgres", "postgres",
                    "postgres"
            );
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }

    private String test() {

        try (Statement st = sqlConnection.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM books" )) {

            while (rs.next()) {
                System.out.println(rs.getString(3));
            }

        } catch (SQLException ex) {
            Logger lgr = Logger.getLogger(Client.class.getName());
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }

        sync.set("name", "Martin");
        return sync.get("name");
    }

    private void configure() {
        sync.configSet("notify-keyspace-events", "AKE");
        pubSubConnection.addListener(new RedisPubSubListener<String, String>() {

            @Override
            public void message(String channel, String message) {
                System.out.println("[message] " + channel + ": " + message);
            }

            @Override
            public void message(String pattern, String channel, String message) {
                System.out.println("[message] " + channel + ": " + message);
            }

            @Override
            public void subscribed(String channel, long count) {
                System.out.println("[subscribed] " + channel + ": " + count);
            }

            @Override
            public void psubscribed(String pattern, long count) {
                System.out.println("[psubscribed] " + pattern + ": " + count);
            }

            @Override
            public void unsubscribed(String channel, long count) {
                System.out.println("[unsubscribed] " + channel + ": " + count);
            }

            @Override
            public void punsubscribed(String pattern, long count) {
                System.out.println("[punsubscribed] " + pattern + ": " + count);
            }

        });
        pubsub.subscribe("__keyevent@0__:set");

    }

    private void close() {
        pubSubConnection.close();
        redisConnection.close();
        redisClient.shutdown();

        try {
            sqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.configure();

        String value = client.test();
        System.out.println(value);

        client.close();
    }
}
