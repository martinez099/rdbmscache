package com.redislabs.demo.rdbms.pojo;

public class Picture extends Base {

    private int authorId;

    private byte[] data;

    public Picture(int id, int authorId, byte[] data) {
        super(id);
        this.authorId = authorId;
        this.data = data;
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
