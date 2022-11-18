package org.example;

import java.util.ArrayList;

public class ResultTable {

    private double[][] energyResultTable;
    private double[][] valenceResultTable;
    private QLearning qLearning = new QLearning(0.4, 0.1);
    private int genreLength = Genres.values().length;
    private int rows = 4;


    public double[][] getEnergyResultTable() {
        return energyResultTable;
    }

    public double[][] getValenceResultTable() {
        return valenceResultTable;
    }

    public ResultTable() {
        energyResultTable = new double[genreLength][rows];
        valenceResultTable = new double[genreLength][rows];
    }


    public void updateData(DataTable dataTable) {
        ArrayList<Double>[][] energyData = dataTable.getEnergyTable();
        ArrayList<Double>[][] valenceData = dataTable.getValenceTable();

        //EnergyData
        for (int i = 0; i < genreLength; i++) {
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < energyData[i][j].size(); k++) {
                    Double rewVal = energyData[i][j].get(k);
                    double oldVal = energyResultTable[i][j];
                    double result = qLearning.calculate(oldVal, rewVal);
                    energyResultTable[i][j] = result;
                }
            }
        }

        //ValenceData
        for (int i = 0; i < genreLength; i++) {
            for (int j = 0; j < rows; j++) {
                for (int k = 0; k < valenceData[i][j].size(); k++) {
                    Double rewVal = valenceData[i][j].get(k);
                    double oldVal = valenceResultTable[i][j];
                    double result = qLearning.calculate(oldVal, rewVal);
                    valenceResultTable[i][j] = result;
                }
            }
        }
    }


    public void PrettyPrint() {
        System.out.println();
        System.out.println("ENERGY RESULT TABLE");
        for (int i = 0; i < genreLength; i++) {
            System.out.print(Genres.values()[i] + " ");
            for (int j = 0; j < rows; j++) {
                if (j == 1) {
                    System.out.print(" HIGH: ");
                } else {
                    System.out.print("LOW: ");
                }
                System.out.print(energyResultTable[i][j]);
            }
            System.out.println();
        }
        System.out.println();

        System.out.println("VALENCE RESULT TABLE");
        for (int i = 0; i < genreLength; i++) {
            System.out.print(Genres.values()[i] + ": ");
            for (int j = 0; j < rows; j++) {
                if (j == 1) {
                    System.out.print(" HIGH: ");
                } else {
                    System.out.print("LOW: ");
                }
                System.out.print(valenceResultTable[i][j]);
            }
            System.out.println();
        }
    }

}
