package org.example;

public class Song {

    private String songName;
    private String artist;
    private String genre;
    private double valence;
    private double energy;
    private String model = "REINFORCEMENT";

    public Song(String songName, String artist, String genre, double valence, double energy) {
        this.artist = artist;
        this.songName = songName;
        this.genre = genre;
        this.valence = valence;
    }


    public double getValence() {
        return valence;
    }

    public double getEnergy() {
        return energy;
    }

    public String getSongName() {
        return songName;
    }

    public String getArtist() {
        return artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getModel() {
        return model;
    }

    public void PrettyPrint() {
        System.out.println(songName + "   " + artist + "     " + genre);
    }
}