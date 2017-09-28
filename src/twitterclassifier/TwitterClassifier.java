package twitterclassifier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizer;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.ml.AbstractTrainer;
import opennlp.tools.ml.naivebayes.NaiveBayesTrainer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *
 * @author james
 */
public class TwitterClassifier {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        ConvertDataSet cdt = new ConvertDataSet();
//        cdt.randomize();
//        cdt.startConversion();
        try {
            // read the training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis DataSubset.txt"));
            ObjectStream lineStream = new PlainTextByLineStream(dataIn, "UTF-8");
            ObjectStream sampleStream = new DocumentSampleStream(lineStream);

            // define the training parameters
            TrainingParameters params = new TrainingParameters();
            params.put(TrainingParameters.ITERATIONS_PARAM, 10 + "");
            params.put(TrainingParameters.CUTOFF_PARAM, 0 + "");
            params.put(AbstractTrainer.ALGORITHM_PARAM, NaiveBayesTrainer.NAIVE_BAYES_VALUE);

            // create a model from traning data
            DoccatModel model = DocumentCategorizerME.train("en", sampleStream, params, new DoccatFactory());
            System.out.println("\nModel is successfully trained.");

            // save the model to local
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home")+"\\TwitterClassifier\\en-twitter-classifier-naive-bayes.bin"));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : " + System.getProperty("user.home")+"\\TwitterClassifier\\en-twitter-classifier-naive-bayes.bin");

            // test the model file by subjecting it to prediction
            DocumentCategorizer doccat = new DocumentCategorizerME(model);
            double correct = 0;
            double total = 0;
            String testSet = System.getProperty("user.home")+"\\TwitterClassifier\\Sentiment Analysis Testset.txt";
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(testSet));
            String line = "";
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                String[] lineContent = line.split(" ");
                String sentence = "";
                for (int i = 0; i < lineContent.length; i++) {
                    sentence += lineContent[i];
                    sentence += " ";
                }
                //System.out.println(sentence);
                String[] docWords = sentence.replaceAll("[^A-Za-z]", " ").split(" ");
                double[] aProbs = doccat.categorize(docWords);

                // print the probabilities of the categories
//                System.out.println("\n---------------------------------\nCategory : Probability\n---------------------------------");
//                for (int i = 0; i < doccat.getNumberOfCategories(); i++) {
//                    System.out.println(doccat.getCategory(i) + " : " + aProbs[i]);
//                }
//                System.out.println("---------------------------------");
//
//                System.out.println("\n" + doccat.getBestCategory(aProbs) + " : is the predicted category for the given sentence.");
//                System.out.println(lineContent[0]);
                if (doccat.getBestCategory(aProbs).equals(lineContent[0])) {
                    correct++;
                }
                total++;
                //System.out.println("---------------------------------");
            }
            System.out.println("Correct: " + correct);
            System.out.println("Total Tests: " + total);
            System.out.println("Ratio: " + (correct/total));
        } catch (IOException e) {
            System.out.println("An exception in reading the training file. Please check.");
        }
    }
}
