package com.redislabs.demo.rdbms.domain;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pictures")
public class Picture extends Base {

    public static final String AUTHOR_FIELD_NAME = "author_id";

    public static final String DATA_FIELD_NAME = "data";

    @DatabaseField(foreign = true, columnName = AUTHOR_FIELD_NAME, canBeNull = false)
    private Author author;

    @DatabaseField(columnName = DATA_FIELD_NAME, dataType=DataType.BYTE_ARRAY, canBeNull = false)
    private byte[] data;

    Picture() {}

    public Picture(Author author, byte[] data) {
        this.author = author;
        this.data = data;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return this.author.getId().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return this.author.getId().equals(((Picture) other).author.getId());
    }
}
