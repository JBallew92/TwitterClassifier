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

/**
 *
 * @author james
 */
public class ConvertDataSet {

    String csvFile = "C:\\Users\\james\\Downloads\\Sentiment Analysis Dataset.csv";
    BufferedReader br = null;
    FileWriter fwDataset = null;  
    FileWriter fwTestset = null; 
    BufferedWriter bwDataset = null;
    BufferedWriter bwTestset = null;
    String line = "";
    String cvsSplitBy = ",";

    public void startConversion() throws IOException {
        try {

            br = new BufferedReader(new FileReader(csvFile));
            fwDataset = new FileWriter(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Dataset.txt");
            fwTestset = new FileWriter(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Testset.txt");
            bwDataset = new BufferedWriter(fwDataset);
            bwTestset = new BufferedWriter(fwTestset);
            br.readLine();
            int count = 0;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] lineContent = line.split(cvsSplitBy);
                String newContent = lineContent[1];
                for (int i = 3; i < lineContent.length; i++) {
                    newContent += " ";
                    newContent += lineContent[i];
                }
                if (count < 1000000) {
                bwDataset.write(newContent);
                count++;
                bwDataset.write("\n");
                }
                else {
                    bwTestset.write(newContent);
                    bwTestset.write("\n");
                }

                //System.out.println(newContent);

            }
//            br.close();
//            fw.close();
//            bw.close();
        } catch (FileNotFoundException e) {
        }
    }
}
