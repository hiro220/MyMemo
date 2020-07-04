package com.example.mymemo;

class ListItem {
    private long id = 0;
    private String uuid = null;
    private String title = null;
    private String body = null;
    private String[] date = {null, null, null};

    long getId() { return id; }

    String getUuid() { return uuid; }

    String getTitle() { return title; }

    String getBody() { return body; }

    String[] getDate() { return date; }

    void setId(long id) { this.id = id; }

    void setUuid(String uuid) { this.uuid = uuid; }

    void setTitle(String title) { this.title = title; }

    void setBody(String body) { this.body = body; }

    void setDate(String[] date) {
        // 深いコピー(コピー元配列, <-のコピー開始位置, コピー先配列, <-のコピー開始位置, コピーする要素数)
        System.arraycopy(date, 0, this.date, 0, 3);
    }

}
