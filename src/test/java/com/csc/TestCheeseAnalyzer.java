package com.csc;

import java.io.*;
import java.util.ArrayList;

public class CheeseAnalyzer {

    // Cheese class to represent each cheese entry
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

    // CheeseList class to handle collection of Cheese objects and perform operations
    static class CheeseList {
        private ArrayList<Cheese> cheeses = new ArrayList<>();

        public void addCheese(Cheese cheese) {
            cheeses.add(cheese);
        }

        // Count cheeses by milk treatment type
        public int countCheesesByMilkTreatment(String treatment) {
            int count = 0;
            for (Cheese cheese : cheeses) {
                if (cheese.getMilkTreatmentType().equals(treatment)) {
                    count++;
                }
            }
            return count;
        }

        // Count organic cheeses with moisture greater than 41%
        public int countOrganicCheesesWithHighMoisture() {
            int count = 0;
            for (Cheese cheese : cheeses) {
                if (cheese.isOrganic() && cheese.getMoisturePercent() > 41) {
                    count++;
                }
            }
            return count;
        }

        // Find the most common milk type
        public String mostCommonMilkType() {
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

            if (cowCount >= goatCount && cowCount >= eweCount && cowCount >= buffaloCount) {
                return "Cow";
            } else if (goatCount >= cowCount && goatCount >= eweCount && goatCount >= buffaloCount) {
                return "Goat";
            } else if (eweCount >= cowCount && eweCount >= goatCount && eweCount >= buffaloCount) {
                return "Ewe";
            } else {
                return "Buffalo";
            }
        }
    }

    // CheeseReader class to read from CSV file and populate CheeseList
// CheeseReader class to read from CSV file and populate CheeseList
    static class CheeseReader {
        private String filename;

        public CheeseReader(String inputFilename) {
            filename = inputFilename;
        }

        public CheeseList readCheeses() {
            CheeseList list = new CheeseList();

            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line = reader.readLine(); // Read header line
                if (line == null) {
                    throw new IllegalStateException("CSV file is empty or improperly formatted");
                }

                // Dynamically determine the column indices based on the header
                String[] headers = line.split(",");
                int moisturePercentIndex = -1;
                int milkTypeIndex = -1;
                int milkTreatmentIndex = -1;
                int organicIndex = -1;

                // Find the actual index of each relevant column
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].equals("MoisturePercent")) {
                        moisturePercentIndex = i;
                    } else if (headers[i].equals("MilkTypeEn")) {
                        milkTypeIndex = i;
                    } else if (headers[i].equals("MilkTreatmentTypeEn")) {
                        milkTreatmentIndex = i;
                    } else if (headers[i].equals("Organic")) {
                        organicIndex = i;
                    }
                }

                if (moisturePercentIndex == -1 || milkTypeIndex == -1 || milkTreatmentIndex == -1 || organicIndex == -1) {
                    throw new IllegalStateException("CSV is missing required columns");
                }

                // Process the rows
                while ((line = reader.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length < headers.length) {
                        continue; // Skip incomplete rows
                    }

                    String id = values[0];
                    String milkType = values[milkTypeIndex];
                    String milkTreatmentType = values[milkTreatmentIndex];
                    boolean isOrganic = values[organicIndex].equals("1");

                    // Parse moisture percentage
                    double moisturePercent = 0.0;
                    try {
                        if (!values[moisturePercentIndex].isEmpty()) {
                            moisturePercent = Double.parseDouble(values[moisturePercentIndex]);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid moisture percentage value: " + values[moisturePercentIndex]);
                        continue; // Skip this row if moisture percent is invalid
                    }

                    // Create and add cheese to the list
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
        // Read in the cheese data
        CheeseReader reader = new CheeseReader("cheese_data.csv");
        CheeseList list = reader.readCheeses();

        // Perform the calculations
        int pasteurizedCount = list.countCheesesByMilkTreatment("Pasteurized");
        int rawMilkCount = list.countCheesesByMilkTreatment("Raw Milk");
        int organicHighMoistureCount = list.countOrganicCheesesWithHighMoisture();
        String mostCommonMilkType = list.mostCommonMilkType();

        // Write results to output.txt
        try (PrintWriter writer = new PrintWriter("output.txt")) {
            writer.println("Number of cheeses with pasteurized milk: " + pasteurizedCount);
            writer.println("Number of cheeses with raw milk: " + rawMilkCount);
            writer.println("Number of organic cheeses with moisture > 41%: " + organicHighMoistureCount);
            writer.println("Most common milk type in Canadian cheeses: " + mostCommonMilkType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
