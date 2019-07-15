package com.redislabs.demo;

public class Image {

    private int id;

    private int authorId;

    private byte[] data;

    public Image(int id, int authorId, byte[] data) {
        this.id = id;
        this.authorId = authorId;
        this.data = data;
    }

    public int getId() {
        return this.id;
    }

    public int getAuthorId() {
        return this.authorId;
    }

    public byte[] getData() {
        return this.data;
    }
}
