package twitterclassifier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;

/**
 *
 * @author james
 */
public class ConvertDataSet {

    String csvFile = (System.getProperty("user.home"))+"\\Downloads\\Sentiment Analysis Dataset.csv";
    BufferedReader br = null;
    FileWriter fwDataset = null;  
    FileWriter fwTestset = null; 
    BufferedWriter bwDataset = null;
    BufferedWriter bwTestset = null;
    String line = "";
    String cvsSplitBy = ",";
    
    public void randomize() throws IOException {
        try {
            Random rand = new Random();
            br = new BufferedReader(new FileReader(csvFile));
            fwDataset = new FileWriter(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Dataset.csv");
            bwDataset = new BufferedWriter(fwDataset);
            br.readLine();
            System.out.println("Reading in data....");
            ArrayList<String> lines = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                lines.add(line);              
            }
            System.out.println("Randomizing data.....");
            while (!lines.isEmpty()) {
                int i = rand.nextInt(lines.size());
                bwDataset.write(lines.get(i) + "\n");
                lines.remove(i);
            }
            System.out.println("Randomization complete!");
        } catch (FileNotFoundException e) {
        }
    }

    public void startConversion() throws IOException {
        try {
            br = new BufferedReader(new FileReader(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Dataset.csv"));
            fwDataset = new FileWriter(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis DataSubset.txt");
            fwTestset = new FileWriter(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Testset.txt");
            bwDataset = new BufferedWriter(fwDataset);
            bwTestset = new BufferedWriter(fwTestset);
            int count = 0;
            System.out.println("Reading in data....");
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineContent = line.split(cvsSplitBy);
                String newContent = lineContent[1];
                for (int i = 3; i < lineContent.length; i++) {
                    newContent += " ";
                    newContent += lineContent[i];
                }
                //split dataset for training and testing
                if (count < 1000000) {
                bwDataset.write(newContent);
                count++;
                bwDataset.write("\n");
                }
                else {
                    bwTestset.write(newContent);
                    bwTestset.write("\n");
                }
            }
        } catch (FileNotFoundException e) {
        }
    }
}
