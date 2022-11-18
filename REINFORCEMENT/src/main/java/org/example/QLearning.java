package org.example;

public class QLearning {

    private double learningRate;

    private double dicountFactor;

    private int maxQ = 1;

    public QLearning(double LR, double DF) {

        this.learningRate = LR;

        this.dicountFactor = DF;

    }

    public double calculate(double currentValue, double reward) {

        double newQ = currentValue + learningRate * (reward + dicountFactor * maxQ - currentValue);

        return newQ;

    }

}