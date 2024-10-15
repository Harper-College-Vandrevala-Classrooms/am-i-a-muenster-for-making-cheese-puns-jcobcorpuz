package com.csc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CheeseAnalyzer {
    static class Cheese {
        private String id;
        private String milkType;
        private String milkTreatmentType;
        private boolean isOrganic;
        private double moisturePercent;

        public Cheese(String id, String milkType, String milkTreatmentType, boolean isOrganic, double moisturePercent) {
            this.id = id;
            this.milkType = milkType;
            this.milkTreatmentType = milkTreatmentType;
            this.isOrganic = isOrganic;
            this.moisturePercent = moisturePercent;
        }

        public String getMilkType() {
            return milkType;
        }

        public String getMilkTreatmentType() {
            return milkTreatmentType;
        }

        public boolean isOrganic() {
            return isOrganic;
        }

        public double getMoisturePercent() {
            return moisturePercent;
        }
    }

    static class CheeseList {
        private ArrayList<Cheese> cheeses = new ArrayList<>();

        public void addCheese(Cheese cheese) {
            cheeses.add(cheese);
        }

        public int countCheesesByMilkTreatment(String treatment) {
            int count = 0;
            for (Cheese cheese: cheeses) {
                if (cheese.getMilkTreatmentType().equals(treatment)) {
                    count++;
                }
            }
            return count;
        }

        public int countOrganicCheesesWithHighMoisture() {
            int count = 0;
            for (Cheese cheese: cheeses){
                if (cheese.isOrganic() && cheese.getMoisturePercent() > 41){
                    count++;
                }
            }
            return count;
        }

        public String mostCommonMilkType(){
            int cowCount = 0, goatCount = 0, eweCount = 0, buffaloCount = 0;
            for (Cheese cheese : cheeses) {
                switch (cheese.getMilkType()) {
                    case "Cow":
                        cowCount++;
                        break;
                    case "Goat":
                        goatCount++;
                        break;
                    case "Ewe":
                        eweCount++;
                        break;
                    case "Buffalo":
                        buffaloCount++;
                        break;
                }
            }

            if (cowCount >= goatCount && cowCount >= eweCount && cowCount >= buffaloCount){
                return "Cow";
            }
            else if (goatCount >= cowCount && goatCount >= eweCount && goatCount >= buffaloCount){
                return "Goat";
            }
            else if (eweCount >= cowCount && eweCount >= goatCount && eweCount >= buffaloCount){
                return "Ewe";
            }
            else{
                return "Buffalo";
            }
        }
    }

    static class CheeseReader {
        private String filename;

        public CheeseReader(String inputFilename) {
            filename = inputFilename;
        }

        public CheeseList readCheeses() {
            CheeseList list = new CheeseList();

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line = reader.readLine();
                if (line == null) {
                    throw new IllegalStateException("CSV file is empty");
                }

                String[] headers = line.split(",");
                int moisturePercentIndex = -1;
                int milkTypeIndex = -1;
                int milkTreatmentIndex = -1;
                int organicIndex = -1;

                for(int i = 0; i < headers.length; i++) {
                    if(headers[i].equals("MoisturePercent")) {
                        moisturePercentIndex = i;
                    }
                    else if (headers[i].equals("MilkTypeEn")) {
                        milkTypeIndex = i;
                    }
                    else if (headers[i].equals("MilkTreatmentTypeEn")) {
                        milkTreatmentIndex = i;
                    }
                    else if (headers[i].equals("Organic")) {
                        organicIndex = i;
                    }
                }

                if (moisturePercentIndex == -1 || milkTypeIndex == -1 || milkTreatmentIndex == -1 || organicIndex == -1) {
                    throw new IllegalStateException("CSV is missing required columns");
                }

                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length < headers.length) {
                        continue;
                    }

                    String id = values[0];
                    String milkType = values[milkTypeIndex];
                    String milkTreatmentType = values[milkTreatmentIndex];
                    boolean isOrganic = values[organicIndex].equals("1");

                    double moisturePercent = 0.0;
                    try {
                        if (!values[moisturePercentIndex].isEmpty()) {
                            moisturePercent = Double.parseDouble(values[moisturePercentIndex]);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid moisture percentage: " + values[moisturePercentIndex]);
                        continue;
                    }

                    Cheese cheese = new Cheese(id, milkType, milkTreatmentType, isOrganic, moisturePercent);
                    list.addCheese(cheese);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }
    }

    public static void main(String[] args) {
        CheeseReader reader = new CheeseReader("cheese_data.csv");
        CheeseList list = reader.readCheeses();

        int pasteurizedCount = list.countCheesesByMilkTreatment("Pasteurized");
        int rawMilkCount = list.countCheesesByMilkTreatment("Raw Milk");
        int organicHighMoistureCount = list.countOrganicCheesesWithHighMoisture();
        String mostCommonMilkType = list.mostCommonMilkType();

        try (PrintWriter writer = new PrintWriter("output.txt")) {
            writer.println("Number of cheeses with pasteurized milk: " + pasteurizedCount);
            writer.println("Number of cheeses with raw milk: " + rawMilkCount);
            writer.println("Number of organic cheeses with moisture greater than 41.0%: " + organicHighMoistureCount);
            writer.println("Most common milk type in Canadian cheeses: " + mostCommonMilkType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
