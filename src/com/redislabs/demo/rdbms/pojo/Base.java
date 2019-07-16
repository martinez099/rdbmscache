package com.redislabs.demo.rdbms.pojo;

abstract public class Base {

    private int id;

    public Base(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
