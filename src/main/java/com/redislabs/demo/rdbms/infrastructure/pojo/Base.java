package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

abstract public class Base implements Serializable {

    @DatabaseField(generatedId = true)
    private Integer id;

    public Base() {}

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) { this.id = id; }
}
