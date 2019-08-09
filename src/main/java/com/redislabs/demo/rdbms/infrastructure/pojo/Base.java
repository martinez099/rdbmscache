package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;

abstract public class Base {

    @DatabaseField(generatedId = true)
    private Integer id;

    public Base() {}

    public Base(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }
}
