package org.example;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("Starting Data Generator");
        DataGenerator dataGenerator = new DataGenerator();
        dataGenerator.start();
        System.out.println("Data Generator Started");
    }
}