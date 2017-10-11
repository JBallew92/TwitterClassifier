package twitterclassifier;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
    static ArrayList<Threshold> thresholds = new ArrayList<>();
    static double prob;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        ConvertDataSet cdt = new ConvertDataSet();
//      cdt.randomize();
//      cdt.startConversion();
        try {
            // read the training data
            InputStreamFactory dataIn = new MarkableFileInputStreamFactory(new File(System.getProperty("user.home") + "\\TwitterClassifier\\Sentiment Analysis CleanDataSubset.txt"));
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
            BufferedOutputStream modelOut = new BufferedOutputStream(new FileOutputStream(System.getProperty("user.home") + "\\TwitterClassifier\\en-twitter-classifier-naive-bayes.bin"));
            model.serialize(modelOut);
            System.out.println("\nTrained Model is saved locally at : " + System.getProperty("user.home") + "\\TwitterClassifier\\en-twitter-classifier-naive-bayes.bin");

            // test the model file by subjecting it to prediction
            DocumentCategorizer doccat = new DocumentCategorizerME(model);
            int totalP = 0;
            int totalN = 0;
            String testSet = System.getProperty("user.home") + "\\TwitterClassifier\\Sentiment Analysis CleanTestset.txt";
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(testSet));
            String line = "";
            //create the thresholds
            for (int i = 0; i < 5; i++) {
                thresholds.add(new Threshold());
            }
            //begin classifying each tweet (each line)
            while ((line = br.readLine()) != null) {
                String[] lineContent = line.split(" ");
                String sentence = "";
                //the first char in the string is its actual sentiment value so we only classify the tweet on the sentence that comes after.
                for (int i = 0; i < lineContent.length; i++) {
                    sentence += lineContent[i];
                    sentence += " ";
                }
                String[] docWords = sentence.replaceAll("[^A-Za-z]", " ").split(" ");
                double[] aProbs = doccat.categorize(docWords);

                prob = aProbs[0] * 100;
                if ("1".equals(lineContent[0])) {
                    totalP++;
                } else {
                    totalN++;
                }
                if (prob > 90) {
                    if ("1".equals(lineContent[0])) {
                        thresholds.get(4).incrementTp();
                    } else {
                        thresholds.get(4).incrementFp();
                    }
                }
                if (prob > 80) {
                    if ("1".equals(lineContent[0])) {
                        thresholds.get(3).incrementTp();
                    } else {
                        thresholds.get(3).incrementFp();
                    }
                }
                if (prob > 70) {
                    if ("1".equals(lineContent[0])) {
                        thresholds.get(2).incrementTp();
                    } else {
                        thresholds.get(2).incrementFp();
                    }
                }
                if (prob > 60) {
                    if ("1".equals(lineContent[0])) {
                        thresholds.get(1).incrementTp();
                    } else {
                        thresholds.get(1).incrementFp();
                    }
                }
                if (prob > 50) {
                    if ("1".equals(lineContent[0])) {
                        thresholds.get(0).incrementTp();
                    } else {
                        thresholds.get(0).incrementFp();
                    }
                }
            }
            //threshold 0 = 50%, threshold 4 = 90%
            for (int i = 0; i < thresholds.size(); i++) {
                System.out.println("Threshold: " + i);
                System.out.println("TPR: " + thresholds.get(i).getTp() / totalP);
                System.out.println("FPR: " + thresholds.get(i).getFp() / totalN);
            }
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("An exception in reading the training file. Please check.");
        }
    }
}
