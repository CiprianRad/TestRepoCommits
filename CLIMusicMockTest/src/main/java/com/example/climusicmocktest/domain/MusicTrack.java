package com.example.climusicmocktest.domain;

public class MusicTrack {
    private int id;
    private String band;
    private String title;
    private String genre;
    private String length;

    public MusicTrack(int id, String band, String title, String genre, String length) {
        this.id = id;
        this.band = band;
        this.title = title;
        this.genre = genre;
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}
