package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "pictures")
public class Picture extends Base {

    public static final String AUTHOR_FIELD_NAME = "author_id";

    @DatabaseField(columnName = AUTHOR_FIELD_NAME, canBeNull = false)
    private Integer authorId;

    private byte[] data;

    public Picture() {}

    public Picture(Integer id, Integer authorId, byte[] data) {
        super(id);
        this.authorId = authorId;
        this.data = data;
    }

    public Integer getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Integer id) {
        this.authorId = id;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        return this.authorId.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        return this.authorId.equals(((Picture) other).authorId);
    }
}
