

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
 * Created by Erdem on 3/3/2016.
 *
 *
 */
public class Main {
    static HashMap<String,HashMap<String,Integer>> fullTrainingData = new HashMap<>(); // this is from author to a hashmap <word,freq>

    static HashMap<String,Integer> totalNumOfWords = new HashMap<>();
    static HashMap<String , Integer> corpus = new HashMap<>();
    static HashMap<String,Integer> numberOfDocuments = new HashMap<>();
    static double totalNumOfDocuments = 0 ;
    static HashMap<String,Double> avgWordLength = new HashMap<>();
    static ArrayList<TestDocument> testDocs = new ArrayList<>();
    static HashMap<String , ArrayList<Integer>> sentenceNumberData = new HashMap<>();
    static HashMap<String , Double> sentenceNumbers = new HashMap<>();
    static HashMap<String , ArrayList<Integer>> commaWordData = new HashMap<>();
    static HashMap<String , Double> commaWord = new HashMap<>();
    static HashMap<String , ArrayList<Integer>> questionMarkData = new HashMap<>();
    static HashMap<String , Double> questionmarks = new HashMap<>();
    public static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles())
                delete(c);
        }
        if (!f.delete())
            throw new FileNotFoundException("Failed to delete file: " + f);
    }

    private static void emptyTheData() throws IOException {

        fullTrainingData.clear();
        totalNumOfWords.clear();
        corpus.clear();
        numberOfDocuments.clear();
        totalNumOfDocuments = 0;
        testDocs.clear();
        delete( new File("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\trainingData"));
        delete(new File("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\testData"));
    }

    public static double makeFullTest(){
        double countSucces= 0 ;
        for (TestDocument doc: testDocs) {
            String real = doc.getAuthor();
            String testOutput = test(doc);
            if(real.equals(testOutput)){
                countSucces++;
            }
        }

        return  countSucces/ (double) testDocs.size()*100;
    }
    public static double main(String[] args) throws IOException {
        train("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\trainingData");
        testTheData("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\testData");
        double succRate =  makeFullTest();
        emptyTheData();
        return succRate;
    }


    public static void train(String sDir) throws FileNotFoundException {
        fillTheData(sDir);
        calculateAvgWordLength();
        calculateSentenceNumber();
        calculateAvgCommaPerWord();
        calculateQuestionMark();
    }

    private static void calculateQuestionMark() {
        for (String author: questionMarkData.keySet()) {
            double total = 0;
            double counter =0;
            for (int numberOfCommas:questionMarkData.get(author)) {
                total += numberOfCommas;
                counter++;
            }
            questionmarks.put(author,((double)total)/(counter*totalNumOfWords.get(author)));
        }

    }

    private static void calculateAvgCommaPerWord() {
        for (String author: commaWordData.keySet()) {
            double total = 0;
            int counter =0;
            for (int numberOfCommas:commaWordData.get(author)) {
                total += numberOfCommas;
                counter++;
            }
            commaWord.put(author,total/(counter*totalNumOfWords.get(author)));
        }

    }

    private static void calculateSentenceNumber() {

        for (String author: sentenceNumberData.keySet()) {
            double total = 0;
            double counter =0;
            for (int length:sentenceNumberData.get(author)) {
                total += length;
                counter++;
            }

            sentenceNumbers.put(author,total/(counter*totalNumOfWords.get(author)));
        }
    }

    private static void calculateAvgWordLength() {

        double total = 0;
        double counter =0;
        for (String author: fullTrainingData.keySet()) {
            for (String word: fullTrainingData.get(author).keySet() ) {
                total += fullTrainingData.get(author).get(word)* word.length();
                counter+=fullTrainingData.get(author).get(word);
            }

            avgWordLength.put(author,total/counter);
        }

    }

    public static void fillTheData(String sDir) throws FileNotFoundException {
        File dir =  new File(sDir);
        File[] faFiles =dir.listFiles();

        for(File file: faFiles){
            if(file.isDirectory()){
                numberOfDocuments.put(file.getName(), file.listFiles().length);
                totalNumOfDocuments+= file.listFiles().length;
                fillTheData(file.getAbsolutePath());
                //fillAvgAndStd(file.getName());
            }
            else{
                parseFile(file.getAbsolutePath());
            }
        }




    }
    public static void testTheData(String sDir) throws FileNotFoundException {
        File dir =  new File(sDir);
        File[] faFiles =dir.listFiles();
        for(File file: faFiles){
            if(file.isDirectory()){
                testTheData(file.getAbsolutePath());
            }
            else{
                testFile(file.getAbsolutePath());
            }
        }
    }
    private static void testFile(String fullPath) {
        TestDocument testDoc = new TestDocument();
        String[] filePathNames = fullPath.split("\\\\");
        String authorName = filePathNames[filePathNames.length-2];
        testDoc.setAuthor(authorName);
        Path path = Paths.get(fullPath);
        Charset charset = Charset.forName("ISO-8859-1"); // todo Windows-1252
        try {
            List<String> lines = Files.readAllLines(path, charset);
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line.toLowerCase());
                builder.append(" ");
            }
            String authorAllText = builder.toString();

            String tokenizedAuthorAllText = Tokenizer.tokenizer(authorAllText);
            String[] words = tokenizedAuthorAllText.split(" ");
            testDoc.setNumbOfSentence((authorAllText.length() - authorAllText.replace(".", "").length())/words.length);
            testDoc.setCommaWord((authorAllText.length() - authorAllText.replace(",", "").length())/words.length);
            testDoc.setCommaWord((authorAllText.length() - authorAllText.replace("?", "").length())/words.length);
            HashMap<String,Integer> documentData = testDoc.getDocumentData();
            double total = 0;
            for (int i = 0; i <words.length ; i++) {
                String word = words[i];
                total+= word.length();
                if(documentData.containsKey(word)){
                    documentData.put(word,documentData.get(word)+1);
                }
                else{
                    documentData.put(word,1);
                }
            }

            testDoc.setAvgWordLength(total/(double) words.length);
            testDoc.setDocumentData(documentData);
            testDocs.add(testDoc);
        } catch (IOException e) {
            System.out.println(e);
        }
    }






    /*
     * Given path is for the considered for training data and will be filled
     */
    public static void parseFile(String fullPath) throws FileNotFoundException {
        String[] filePathNames = fullPath.split("\\\\");
        String authorName = filePathNames[filePathNames.length-2];
        Path path = Paths.get(fullPath);
        Charset charset = Charset.forName("ISO-8859-1"); // todo Windows-1252
        try {
            List<String> lines = Files.readAllLines(path, charset);
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line.toLowerCase());
                builder.append(" ");
            }
            String authorAllText = builder.toString();

            // sentence number avg
            ArrayList<Integer> sentenceNumArray = new ArrayList<>();
            if (sentenceNumberData.containsKey(authorName)) {
                sentenceNumArray = sentenceNumberData.get(authorName);
            }
            sentenceNumArray.add(authorAllText.length() - authorAllText.replace(".", "").length());
            sentenceNumberData.put(authorName,sentenceNumArray);
            // comma number avg
            ArrayList<Integer> commaWordDataArr = new ArrayList<>();
            if (commaWordData.containsKey(authorName)) {
                commaWordDataArr = commaWordData.get(authorName);
            }
            commaWordDataArr.add(authorAllText.length() - authorAllText.replace(",", "").length());
            commaWordData.put(authorName,commaWordDataArr);
            // questionMarkavg
            ArrayList<Integer> questionArr = new ArrayList<>();
            if (questionMarkData.containsKey(authorName)) {
                questionArr = questionMarkData.get(authorName);
            }
            questionArr.add(authorAllText.length() - authorAllText.replace("?", "").length());
            questionMarkData.put(authorName,questionArr);



            String tokenizedAuthorAllText = Tokenizer.tokenizer(authorAllText);
            freqArrange(authorName, tokenizedAuthorAllText);
        } catch (IOException e) {
            System.out.println(e);
        }


    }
    public static void freqArrange(String authorName, String fullText){ // arranges all the authors and their word freqs, builds up the dictionary at the same time
        HashMap<String ,Integer> authorFreqs;
        if (!fullTrainingData.containsKey(authorName)){
            authorFreqs = new HashMap<>();
            fullTrainingData.put(authorName,authorFreqs);
        }
        authorFreqs = fullTrainingData.get(authorName);
        String[] words = fullText.split(" ");
        if(totalNumOfWords.containsKey(authorName)){

            totalNumOfWords.put(authorName,totalNumOfWords.get(authorName)+words.length );
        }else{
            totalNumOfWords.put(authorName,words.length );
        }


        for (int i = 0; i <words.length ; i++) {
            String word = words[i];
            if(authorFreqs.containsKey(word)){ // specific to the author
                authorFreqs.put(word,authorFreqs.get(word)+1);
            }
            else{
                authorFreqs.put(word,1);
            }
            if(corpus.containsKey(word)){ // doesnt matter which author wrote it, just builds up the dictionary
                corpus.put(word, corpus.get(word)+1);
            }
            else {
                corpus.put(word,1);
            }
        }
    }



    public static double calculatePdf(double x, double avg, double stdDev){
        double firstTerm = 1 / (stdDev * Math.sqrt(2*Math.PI));
        double secondTerm = Math.exp((-0.5) * Math.pow(((x-avg)/stdDev),2));
        double result = firstTerm * secondTerm ;
        return result;
    }

    public static String test(TestDocument testDoc){
        double max = -1*Double.MAX_VALUE;
        double min = Double.MAX_VALUE;
        String authorRes = "";
        String authorMin = "";
        double alpha = 0.1;
        HashMap<String ,Integer> document = testDoc.getDocumentData();
        double vocabSize = corpus.size(); // tum sozlukte unique sayi
        for (String author: fullTrainingData.keySet()) { // considering all the authors if they are the best
            double totalNumberOfWords = totalNumOfWords.get(author); // o yazarin kac tane kelimesi var, kac tane ayni oldugu onemsiz hepsi sayiliyor
            double pc = Math.log(numberOfDocuments.get(author) / totalNumOfDocuments);
            double rest = pc;
            int counter = 0;
            for (String word: document.keySet()) { // test document is now processed
                double occurrence = document.get(word); // will be used as power
                double data = 0;
                if (fullTrainingData.get(author).containsKey(word)){
                    data = fullTrainingData.get(author).get(word);
                }
                double insideValue = occurrence*Math.log((data + alpha)/ (alpha*vocabSize +totalNumberOfWords));
                rest += insideValue;
                counter++;
            }
            rest -= Math.abs(avgWordLength.get(author) - testDoc.getAvgWordLength())*10;
            rest -= Math.abs(sentenceNumbers.get(author) - testDoc.getNumbOfSentence())*10;
            rest -= Math.abs(commaWord.get(author) - testDoc.getCommaWord())*500;
            rest -= Math.abs(questionmarks.get(author) - testDoc.getQuestionMark())*100;
            if (rest> max){
                authorRes = author;
                max = rest;
            }


        }

        return authorRes;
    }



}
