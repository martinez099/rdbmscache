package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "authors")
public class Author extends Base {

    public static final String NAME_FIELD_NAME = "name";

    @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
    private String name;

    public Author() {}

    public Author(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return this.name.equals(((Author) other).name);
    }
}
