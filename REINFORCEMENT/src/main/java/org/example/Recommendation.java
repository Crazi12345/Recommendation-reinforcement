package org.example;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Strings;

public class Recommendation {
    ResultTable resultTable;
    ArrayList<Genres> topGenres;

    public Recommendation(ResultTable rt) {
        resultTable = rt;
        generateTopGenres();
    }

    public void generateTopGenres() {
        HashMap<Genres, Double> genresDoubleHashMap = new HashMap<>();
        double genreScore = 0;
        double[][] energy = resultTable.getEnergyResultTable();
        double[][] valence = resultTable.getValenceResultTable();
        for (int i = 0; i < Genres.values().length; i++) {
            for (int j = 0; j < 4; j++) {
                genreScore += energy[i][j];
                genreScore += valence[i][j];
            }
            genresDoubleHashMap.put(Genres.values()[i], genreScore);
            genreScore = 0;
        }
        double high = 0;
        double mid = 0;
        double low = 0;
        for (int i = 0; i < genresDoubleHashMap.size(); i++) {
            double genreScores = genresDoubleHashMap.get(Genres.values()[i]);
            if (genreScores > high) {
                low = mid;
                mid = high;
                high = genreScores;
            } else if (genreScores > mid) {
                low = mid;
                mid = genreScores;
            } else if (genreScores > low) {
                low = genreScores;
            }

        }
        ArrayList<Genres> topThree = new ArrayList<>();
        for (int i = 0; i < genresDoubleHashMap.size(); i++) {
            double score = genresDoubleHashMap.get(Genres.values()[i]);
            if (score == high) {

                topThree.add(Genres.values()[i]);
                break;
            }
        }
        for (int i = 0; i < genresDoubleHashMap.size(); i++) {
            double score = genresDoubleHashMap.get(Genres.values()[i]);
            if (score == mid && mid != 0) {
                topThree.add(Genres.values()[i]);
                break;
            }
        }
        for (int i = 0; i < genresDoubleHashMap.size(); i++) {
            double score = genresDoubleHashMap.get(Genres.values()[i]);
            if (score == low && score > 0.5) {
                topThree.add(Genres.values()[i]);
                break;
            }
        }

        topGenres = topThree;
        //List<Double> topGenreList= new ArrayList<>(topGenres.values());

    }

    public ArrayList<Genres> getTopGenres() {
        return topGenres;
    }

    public String findSong(DBConnection db) {


        ResultSet rs = null;

        rs = db.getAllResults();
        try {
            while (rs.next()) {
                Array genres = rs.getArray("artist_genre");
                String[] genresList = (String[]) genres.getArray();
                if (/*Arrays.asList(genresList).contains(topGenres.get(0))*/ true) {
                    BigDecimal energyColumn = rs.getBigDecimal("energy");
                    double energy = energyColumn.doubleValue();
                    if (energy > 0.5) {
                        BigDecimal valenceComlumn = rs.getBigDecimal("valence");
                        double valence = valenceComlumn.doubleValue();
                        if (valence > 0.5) {

                            String name = rs.getString("track_name");
                            System.out.println(name);
                            return name;
                        }
                    }


                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public ArrayList<Song> findSongs() {

        DBConnection connection = new DBConnection();
        ArrayList<Song> recommended = new ArrayList<>();
        ArrayList<String> URIs = new ArrayList<>();
        String profile = generateProfile(resultTable);
        String[] profileSplit = profile.split(";");
        HashMap<Character, Double> profileValues = new HashMap<>();
        profileValues.put('L', 0.0); //LOW
        profileValues.put('l', 0.25); // Somewhat Low
        profileValues.put('h', 0.5); // somewhat High
        profileValues.put('H', 0.75); // HIGH
        profileValues.put('N', -1.0); // Null


        try {
            for (int i = 0; i < topGenres.size(); i++) {
                int songCounter = 0;
                int recommendedSize = recommended.size();
                ResultSet rs = connection.getAllResults();
                String[] profileFromGenre = profileSplit[i].split(",");
                while (rs.next()) {
                    String genre = rs.getString("artist_genre");
                    genre = this.decryptGenre(genre);
                    String topGenre = this.decryptGenre(topGenres.get(i).toString());
                    if (Objects.equals(genre, topGenre)) {
                        BigDecimal energyColumn = rs.getBigDecimal("energy");
                        double energy = 0.5;
                        try {
                            energy = energyColumn.doubleValue();
                        } catch (NullPointerException e) {
                            System.out.println("it was null");
                        }

                        boolean isEnergyFound = false;

                        if (energy > profileValues.get(profileFromGenre[0].charAt(1))) {
                            isEnergyFound = true;
                        } else if (energy > profileValues.get(profileFromGenre[0].charAt(2))) {
                            isEnergyFound = true;
                        } else if (energy > profileValues.get(profileFromGenre[0].charAt(3))) {
                            isEnergyFound = true;
                        }
                        double valence = 0.5;
                        BigDecimal valenceComlumn = rs.getBigDecimal("valence");
                        try {
                            valence = valenceComlumn.doubleValue();
                        } catch (NullPointerException e) {
                            System.out.println("IT WAS NULL");
                        }
                        boolean isValenceFound = false;
                        if (valence > profileValues.get(profileFromGenre[1].charAt(1))) {
                            isValenceFound = true;
                        } else if (valence > profileValues.get(profileFromGenre[1].charAt(2))) {
                            isValenceFound = true;
                        } else if (valence > profileValues.get(profileFromGenre[1].charAt(3))) {
                            isValenceFound = true;
                        }
                        if (isValenceFound) {
                            String name = rs.getString("track_name");
                            String artist = rs.getString("artist_individual");

                            String uri = rs.getString("uri");
                            if (!URIs.contains(uri)) {
                                if (recommendedSize < 5) {
                                    URIs.add(uri);
                                    recommended.add(new Song(name, artist, genre, valence, energy));
                                    songCounter++;
                                } else if (songCounter < recommendedSize / (2 * i + 1)) {
                                    URIs.add(uri);
                                    recommended.add(new Song(name, artist, genre, valence, energy));
                                    songCounter++;
                                }
                            }
                        }
                    }


                }
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return recommended;
    }


    public void printRecommended(ArrayList<Song> rec) {
        for (Song val : rec) {
            val.PrettyPrint();
        }
    }

    public String decryptGenre(String genre) {
        genre = genre.replace('_', ' ');
        genre = genre.replace('Å', '-');
        genre = genre.replace('Ø', '&');
        return genre;
    }

    public String generateProfile(ResultTable rt) {
        String profileString = "";

        double[][] energy = resultTable.getEnergyResultTable();
        double[][] valence = resultTable.getValenceResultTable();
        for (int i = 0; i < topGenres.size(); i++) {
            for (int j = 0; j < Genres.values().length; j++) {
                if (topGenres.get(i).equals(Genres.values()[j])) {
                    for (int k = 0; k < 4; k++) {
                        if (energy[j][k] > 0.3) {
                            switch (k) {
                                case 0:
                                    profileString += 'L';
                                    break;
                                case 1:
                                    profileString += 'l';
                                    break;
                                case 2:
                                    profileString += 'h';
                                    break;
                                case 3:
                                    profileString += 'H';
                                    break;
                            }
                        } else {
                            profileString += 'N';
                        }
                    }
                    profileString += ',';
                    for (int k = 0; k < 4; k++) {
                        if (valence[j][k] > 0.3) {
                            switch (k) {
                                case 0:
                                    profileString += 'L';
                                    break;
                                case 1:
                                    profileString += 'l';
                                    break;
                                case 2:
                                    profileString += 'h';
                                    break;
                                case 3:
                                    profileString += 'H';
                                    break;
                            }
                        } else {
                            profileString += 'N';
                        }
                    }
                    profileString += ';';

                }
            }
        }

        return profileString;
    }
}




