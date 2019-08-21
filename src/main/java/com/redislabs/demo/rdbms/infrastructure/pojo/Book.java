package com.redislabs.demo.rdbms.infrastructure.pojo;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "books")
public class Book extends Base {

    public static final String AUTHOR_FIELD_NAME = "author_id";

    public static final String TITLE_FIELD_NAME = "title";

    @DatabaseField(foreign = true, columnName = AUTHOR_FIELD_NAME, canBeNull = false)
    private Author author;

    @DatabaseField(columnName = TITLE_FIELD_NAME, canBeNull = false)
    private String title;

    Book() {}

    public Book(Author author, String title) {
        this.author = author;
        this.title = title;
    }

    public Author getAuthor() {
        return this.author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int hashCode() {
        return this.author.getId().hashCode() + this.title.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != getClass()) {
            return false;
        }
        Book otherBook = (Book) other;
        return this.title.equals(otherBook.title) && this.author.getId().equals(otherBook.author.getId());
    }
}
