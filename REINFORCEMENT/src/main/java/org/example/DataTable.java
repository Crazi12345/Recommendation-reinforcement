package org.example;

import java.util.ArrayList;

public class DataTable {

    private ArrayList<Double>[][] energyTable;
    private ArrayList<Double>[][] valenceTable;
    private int genreLength = Genres.values().length;
    private int rows = 4;

    public DataTable() {
        energyTable = new ArrayList[genreLength][rows];
        valenceTable = new ArrayList[genreLength][rows];

        for (int i = 0; i < genreLength; i++) {
            for (int j = 0; j < rows; j++) {
                energyTable[i][j] = new ArrayList<Double>();
                valenceTable[i][j] = new ArrayList<Double>();
            }
        }
    }

    public ArrayList<Double>[][] getEnergyTable() {
        return this.energyTable;
    }

    public ArrayList<Double>[][] getValenceTable() {
        return this.valenceTable;
    }

    public void addData(double value, float energy, float valence, String genre) {

        int eng;
        if (energy > 0.75) {
            eng = 3;
        } else if (energy > 0.5) {
            eng = 2;
        } else if (energy > 0.25) {
            eng = 1;
        } else {
            eng = 0;
        }

        int val;
        if (valence > 0.75) {
            val = 3;
        } else if (valence > 0.5) {
            val = 2;
        } else if (valence > 0.25) {
            val = 1;
        } else {
            val = 0;
        }

        for (int i = 0; i < genreLength; i++) {
            if (this.decryptGenre(Genres.values()[i].name()).equals(genre)) {
                energyTable[i][eng].add(value);
                valenceTable[i][val].add(value);
                continue;

            }
        }
    }

    public String decryptGenre(String genre) {
        genre = genre.replace('_', ' ');
        genre = genre.replace('Å', '-');
        genre = genre.replace('Ø', '&');
        return genre;
    }

    public void PrettyPrint() {
        System.out.println();
        System.out.println("ENERGY-TABLE");

        for (int i = 0; i < genreLength; i++) {
            System.out.print(Genres.values()[i] + ": ");
            for (int j = 0; j < energyTable[i].length; j++) {
                if (j == 1) {
                    System.out.print(" HIGH ");
                } else {
                    System.out.print("LOW ");
                }
                System.out.print("{");
                for (int k = 0; k < energyTable[i][j].size(); k++) {
                    System.out.print(energyTable[i][j].get(k));
                    System.out.print(" ");
                }
                System.out.print("}");

            }
            System.out.println();
        }
        System.out.println();
        System.out.println("VALENCE-TABLE");
        for (int i = 0; i < genreLength; i++) {
            System.out.print(Genres.values()[i] + ": ");
            for (int j = 0; j < valenceTable[i].length; j++) {
                if (j == 1) {
                    System.out.print(" HIGH ");
                } else {
                    System.out.print("LOW ");
                }
                System.out.print("{");
                for (int k = 0; k < valenceTable[i][j].size(); k++) {
                    System.out.print(valenceTable[i][j].get(k));
                    System.out.print(" ");
                }
                System.out.print("}");

            }
            System.out.println();
        }


    }
}

