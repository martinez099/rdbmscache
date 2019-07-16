package com.redislabs.demo.rdbms.pojo;

public class Picture {

    private int id;

    private int authorId;

    private byte[] data;

    public Picture(int id, int authorId, byte[] data) {
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

    public void setAuthorId(int id) {
        this.authorId = id;
    }

    public byte[] getData() {
        return this.data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
