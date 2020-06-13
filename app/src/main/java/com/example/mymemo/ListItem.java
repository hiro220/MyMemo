package com.example.mymemo;

class ListItem {
    private long id = 0;
    private String uuid = null;
    private String title = null;
    private String[] date = {null, null, null};

    long getId() { return id; }

    String getUuid() { return uuid; }

    String getTitle() { return title; }

    String[] getDate() { return date; }

    void setId(long id) { this.id = id; }

    void setUuid(String uuid) { this.uuid = uuid; }

    void setTitle(String title) { this.title = title; }

    void setDate(String[] date) {
        System.arraycopy(this.date, 0, date, 0, date.length);
    }

}
