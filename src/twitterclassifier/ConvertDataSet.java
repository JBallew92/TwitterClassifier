package twitterclassifier;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.normalizer.EmojiCharSequenceNormalizer;
import opennlp.tools.util.normalizer.NumberCharSequenceNormalizer;
import opennlp.tools.util.normalizer.ShrinkCharSequenceNormalizer;
import opennlp.tools.util.normalizer.TwitterCharSequenceNormalizer;
import opennlp.tools.util.normalizer.UrlCharSequenceNormalizer;

/**
 *
 * @author james
 */
public class ConvertDataSet {

    TwitterCharSequenceNormalizer tweetNormalizer = new TwitterCharSequenceNormalizer();
    EmojiCharSequenceNormalizer emojiNormalizer = new EmojiCharSequenceNormalizer();
    UrlCharSequenceNormalizer urlNormalizer = new UrlCharSequenceNormalizer();
    NumberCharSequenceNormalizer numberNormalizer = new NumberCharSequenceNormalizer();
    ShrinkCharSequenceNormalizer shrinkNormalizer = new ShrinkCharSequenceNormalizer();
    InputStream posModelIn;
    POSModel posModel;
    POSTaggerME posTagger;
    InputStream dictLemmatizer;
    DictionaryLemmatizer lemmatizer;
    String csvFile = (System.getProperty("user.home")) + File.separator + "Downloads" + File.separator + "Sentiment Analysis Dataset.csv";
    String newCsvFile = (System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "Sentiment Analysis Dataset.csv");
    String dataSubset = (System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "Sentiment Analysis DataSubset.txt");
    String testSet = (System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "Sentiment Analysis Testset.txt");
    String cleanDataSubset = (System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "Sentiment Analysis CleanDataSubset.txt");
    String cleanTestSet = (System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "Sentiment Analysis CleanTestset.txt");
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
            fwDataset = new FileWriter(newCsvFile);
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
            //initialize file I/O
            br = new BufferedReader(new FileReader(newCsvFile));
            fwDataset = new FileWriter(dataSubset);
            fwTestset = new FileWriter(testSet);
            bwDataset = new BufferedWriter(fwDataset);
            bwTestset = new BufferedWriter(fwTestset);
            // Parts-Of-Speech Tagging
            // reading parts-of-speech model to a stream        
            posModelIn = new FileInputStream(System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "en-pos-maxent.bin");
            // loading the parts-of-speech model from stream
            posModel = new POSModel(posModelIn);
            // initializing the parts-of-speech tagger with model
            posTagger = new POSTaggerME(posModel);
            // loading the dictionary to input stream
            dictLemmatizer = new FileInputStream(System.getProperty("user.home") + File.separator + "TwitterClassifier" + File.separator + "en-dictionary.txt");
            // loading the lemmatizer with dictionary
            lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
            int count = 0;
            System.out.println("Reading in data....");
            while ((line = br.readLine()) != null) {
                // use comma as separator
                //String[] lineContent = line.split(cvsSplitBy);
                //for all lower case data user the following
                String[] lineContent = line.toLowerCase().split(cvsSplitBy);
                String newContent = lineContent[1];
                for (int i = 3; i < lineContent.length; i++) {
                    newContent += " ";
                    newContent += lineContent[i];
                }
                newContent = normalize(newContent);
                newContent = lemmatizer(newContent);
                //split dataset for training and testing
                if (count < 900000) {
                    bwDataset.write(newContent);
                    count++;
                    bwDataset.write("\n");
                } else {
                    bwTestset.write(newContent);
                    bwTestset.write("\n");
                }
            }
            removeStopWords(dataSubset, cleanDataSubset);
            //removeStopWords(testSet, cleanTestSet);
        } catch (FileNotFoundException e) {
        }
    }

    public void removeStopWords(String inputFile, String outputFile) throws IOException {
        try {
            Map<String, Boolean> map = new HashMap<>();
            ArrayList<String> stopwords = new ArrayList<>();
            String newLine = "";
            String word;
            int count = 0;

            br = new BufferedReader(new FileReader(System.getProperty("user.home") + "\\TwitterClassifier\\nlp_en_stopwords.txt"));
            fwDataset = new FileWriter(outputFile);
            bwDataset = new BufferedWriter(fwDataset);

            System.out.println("Reading in data....");
            while ((line = br.readLine()) != null) {
                stopwords.add(line);
            }
            //System.out.println(stopwords.size());
            br = new BufferedReader(new FileReader(inputFile));
            System.out.println("Removing stopwords....");
            while ((line = br.readLine()) != null) {
                String[] lineContent = line.split(" ");
                for (int i = 1; i < lineContent.length; i++) {
                    map.put(lineContent[i], Boolean.TRUE);
                }
                for (int i = 0; i < stopwords.size(); i++) {
                    word = stopwords.get(i);
                    if (map.containsKey(word)) {
                        map.remove(word);
                        count++;
                    }
                }

                newLine += lineContent[0] + " ";
                for (Map.Entry m : map.entrySet()) {
                    newLine += m.getKey() + " ";
                }
                if (newLine.length() > 3) {
                    bwDataset.write(newLine);
                    bwDataset.newLine();
                }
                newLine = "";
                map.clear();
            }
            System.out.println(count);
        } catch (FileNotFoundException e) {
        }
    }

    public String normalize(String sentence) {
        tweetNormalizer.normalize(sentence);
        emojiNormalizer.normalize(sentence);
        urlNormalizer.normalize(sentence);
        numberNormalizer.normalize(sentence);
        shrinkNormalizer.normalize(sentence);
        return sentence;
    }

    public String lemmatizer(String sentence) throws FileNotFoundException, IOException {
        //System.out.println("Getting lemmas for...." + sentence);
        String newSentence = "";
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        //tokenize the sentence
        String[] tokens = tokenizer.tokenize(sentence);
        // Tagger tagging the tokens
        String tags[] = posTagger.tag(tokens);
        // finding the lemmas
        String[] lemmas = lemmatizer.lemmatize(tokens, tags);
        // printing the results
//        System.out.println("\nPrinting lemmas for the given sentence...");
//        System.out.println("WORD -POSTAG : LEMMA");
        newSentence += tokens[0];
        newSentence += " ";
        for (int i = 1; i < tokens.length; i++) {
            if (!"O".equals(lemmas[i])) {
                newSentence += lemmas[i];
                newSentence += " ";
            } else {
                newSentence += tokens[i];
            }
            newSentence += " ";
//            System.out.println(tokens[i] + " -" + tags[i] + " : " + lemmas[i]);
        }
        return newSentence;
    }
}
