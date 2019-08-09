package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "books")
public class Book extends Base {

    public static final String AUTHOR_FIELD_NAME = "author_id";

    public static final String TITLE_FIELD_NAME = "title";

    @DatabaseField(columnName = AUTHOR_FIELD_NAME, canBeNull = false)
    private Integer authorId;

    @DatabaseField(columnName = TITLE_FIELD_NAME, canBeNull = false)
    private String title;

    public Book() {}

    public Book(Integer id, Integer authorId, String title) {
        super(id);
        this.authorId = authorId;
        this.title = title;
    }

    public Integer getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(Integer id) {
        this.authorId = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return this.authorId.hashCode() + this.title.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Book otherBook = (Book) other;
        return this.title.equals(otherBook.title) && this.authorId.equals(otherBook.authorId);
    }
}
