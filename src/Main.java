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
    static HashMap<String,ArrayList<HashMap<String,Integer>>> fullTestData = new HashMap<>(); // this is from author to their list of test texts
    static HashMap<String,ArrayList<String>> testDataPaths = new HashMap<>();
    static HashMap<String,Double> commaNumbers = new HashMap<>();
    static HashMap<String,Integer> totalNumOfWords = new HashMap<>();
    static HashMap<String , Integer> corpus = new HashMap<>();
    static HashMap<String,Integer> numberOfDocuments = new HashMap<>();
    static double totalNumOfDocuments = 0 ;
    static HashMap<String,ArrayList<Integer>> sentencesLengths = new HashMap<>();
    static HashMap<String,ArrayList<Integer>> testSentencesLengths = new HashMap<>();
    static HashMap<String,Double> avgSentenceLength = new HashMap<>();
    static HashMap<String,Double> standartDev = new HashMap<>();
    static HashMap<String,Double> testAvgSentenceLength = new HashMap<>();
    static ArrayList<TestDocument> testDocs = new ArrayList<>();




    private static void emptyTheData() {
        fullTestData.clear();
        fullTrainingData.clear();
        testDataPaths.clear();
        commaNumbers.clear();
        totalNumOfWords.clear();
        corpus.clear();
        numberOfDocuments.clear();
        totalNumOfDocuments = 0;
        sentencesLengths.clear();
        standartDev.clear();
        testAvgSentenceLength.clear();
        testDocs.clear();

    }
    public static void main(String[] args) throws FileNotFoundException {
        //  fillTheData("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\raw_texts",true);
        double countTrue = 0;
        double countTotal =0;
        double totalofAvg = 0;
        for (int i = 0; i <10 ; i++) {
            fillTheData("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\others",true);
            for (TestDocument td : testDocs ) {
                if(td.getAuthor().equals(test(td))){
                    countTrue++;
                }
                countTotal++;
            }
            emptyTheData();
        }
        System.out.println(countTrue/10+"/"+countTotal/10);
        System.out.println(100*countTrue/countTotal+"%");

    }


    public static void fillAvgAndStd(String authorname){


        double avg = getAvg(sentencesLengths.get(authorname));
        avgSentenceLength.put(authorname,avg);
        standartDev.put(authorname,getStandardDeviation(sentencesLengths.get(authorname),avg));

    }

    public static double getAvg(ArrayList<Integer> list){
        double x=0;
        for(int i=0;i<list.size();i++) {
            x += list.get(i);
        }
        double avg = x/list.size();
        return  avg;
    }

    public static double getStandardDeviation(ArrayList<Integer> list, double avg){
        int x=0;
        for(int i=0;i<list.size();i++) {
            x += (list.get(i)-avg)*(list.get(i)-avg);
        }
        double stddev = Math.sqrt(x/list.size());
        return  stddev;
    }

    public static void calcCommaFreq(String authorName){
        int totalWordCount = 0;
        for (String x :fullTrainingData.get(authorName).keySet() ) {
            totalWordCount+= fullTrainingData.get(authorName).get(x);
        }
        commaNumbers.put(authorName, commaNumbers.get(authorName)/totalWordCount);
        totalNumOfWords.put(authorName, totalWordCount);
    }
    public static void fillTheData(String sDir, boolean first) throws FileNotFoundException {
        File dir =  new File(sDir);
        File[] faFiles =dir.listFiles();
        if(!first) {
            numberOfDocuments.put(dir.getName(),faFiles.length);
            totalNumOfDocuments += faFiles.length;
        }
        for(File file: faFiles){
            if(file.isDirectory()){
                fillTheData(file.getAbsolutePath(), false);
                calcCommaFreq(file.getName());
                fillAvgAndStd(file.getName());
            }
            else{
                parseFile(file.getAbsolutePath());
            }
        }
    }
    /*
     * Given path is for the considered for training data and will be filled
     */
    public static void parseFile(String fullPath) throws FileNotFoundException {
        String[] filePathNames = fullPath.split("\\\\");
        String authorName = filePathNames[filePathNames.length-2];
        int commaNum = 0;
        Path path = Paths.get(fullPath);

        Charset charset = Charset.forName("ISO-8859-1");
        try {
            List<String> lines = Files.readAllLines(path, charset);
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(line.toLowerCase());
                builder.append(" ");
            }
            String authorAllText = builder.toString();
            String tokenizedAuthorAllText = tokenizer(authorAllText);
            if(!freqArrange(authorName , tokenizedAuthorAllText)){
                if (testDataPaths.containsKey(authorName) ){
                    testDataPaths.get(authorName).add(fullPath);
                    testDataPaths.put(authorName,testDataPaths.get(authorName));
                }
                else {
                    ArrayList<String> paths = new ArrayList<>();
                    paths.add(fullPath);
                    testDataPaths.put(authorName,paths);
                }
                TestDocument activeTestDoc = testDocs.get(testDocs.size()-1);
                String[] sentences = authorAllText.split("\\.");
                ArrayList<Integer> lenghtSent = new ArrayList<>(); // number of words in a sentence is calculated
                double counter =0 ;
                double total = 0;
                for (String sentence: sentences) {
                    total+=sentence.split(" ").length;
                    counter++;
                }
                double average = total/counter;
                activeTestDoc.setAvgSentenceLength(average);
                testDocs.remove(testDocs.size()-1);
                testDocs.add(activeTestDoc);
                testSentencesLengths.put(authorName,lenghtSent);
            }
            else{
                String[] sentences = authorAllText.split("\\.");
                ArrayList<Integer> lenghtSent = new ArrayList<>(); // number of words in a sentence is calculated
                if(sentencesLengths.containsKey(authorName)){
                    lenghtSent = sentencesLengths.get(authorName);
                }
                for (String sentence: sentences) {
                    lenghtSent.add(sentence.split(" ").length);
                }
                sentencesLengths.put(authorName,lenghtSent);
                for (int i = 0; i < authorAllText.length() ; i++) {
                    char letter = authorAllText.charAt(i);
                    if(letter == ','){
                        commaNum++;
                    }
                }
                if(commaNumbers.containsKey(authorName)){
                    commaNumbers.put(authorName,commaNumbers.get(authorName)+ commaNum ) ;
                }else {
                    commaNumbers.put(authorName, (double) commaNum) ;
                }



            }
        } catch (IOException e) {
            System.out.println(e);
        }


    }
    public static boolean freqArrange(String authorName, String fullText){
        HashMap<String ,Integer> authorFreqs;
        if(!isTest()){
            if (!fullTrainingData.containsKey(authorName)){
                authorFreqs = new HashMap<>();
                fullTrainingData.put(authorName,authorFreqs);
            }
            authorFreqs = fullTrainingData.get(authorName);
        }
        else {

            TestDocument testDoc = new TestDocument();
            testDoc.setAuthor(authorName);
            ArrayList<HashMap<String,Integer>> authorTextsTokenized = new ArrayList<>();
            if (fullTestData.containsKey(authorName)){

                authorTextsTokenized = fullTestData.get(authorName);
            }
            HashMap<String ,Integer> authorFreqsTest = new HashMap<>();
            String[] words = fullText.split(" ");
            for (int i = 0; i <words.length ; i++) {
                String word = words[i];
                if(authorFreqsTest.containsKey(word)){ // specific to the author
                    authorFreqsTest.put(word,authorFreqsTest.get(word)+1);
                }
                else{
                    authorFreqsTest.put(word,1);
                }
            }
            authorTextsTokenized.add(authorFreqsTest);
            testDoc.setDocumentData(authorFreqsTest);
            testDocs.add(testDoc);
            fullTestData.put(authorName,authorTextsTokenized);
            return false;
        }

        String[] words = fullText.split(" ");
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
        return true;
    }
    public static boolean isTest(){
        Random random = new Random();
        int answer = random.nextInt(100) + 1;
        if(answer > 60){
            return true;
        }
        return false;
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
        double alpha = 1;
        HashMap<String ,Integer> document = testDoc.getDocumentData();
        double vocabSize = corpus.size(); // tum sozlukte unique sayi
        for (String author: numberOfDocuments.keySet()) { // considering all the authors if they are the best
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
                double insideValue = occurrence*Math.log((data + alpha)/ (vocabSize +totalNumberOfWords));
                rest += insideValue;
            counter++;
            }
           rest += Math.log(calculatePdf(testDoc.getAvgSentenceLength(),avgSentenceLength.get(author),standartDev.get(author)));
           // System.out.println(Math.log(calculatePdf(testDoc.getAvgSentenceLength(),avgSentenceLength.get(author),standartDev.get(author))));
            if (rest> max){
                authorRes = author;
                max = rest;
            }
            if(rest < min){
                min = rest;
            }
        }
    /*    System.out.println("MAX RES IS: "+ max);
        System.out.println("MIN RES IS: "+ min);*/
        return authorRes;
    }
    /*
     * tokenize a whole group of words including the new lines and tab blanks and such
     */
    public static String tokenizer(String words){
        StringBuilder builder = new StringBuilder();
        Scanner scanWords = new Scanner(words);
        while (scanWords.hasNextLine()){
            String line = scanWords.nextLine();
            Scanner scanLine = new Scanner(line);
            while (scanLine.hasNext()){
                String potentialToken = scanLine.next();
                String token = tokenizeString(potentialToken);
                builder.append(token);
                builder.append(" ");
            }
            scanLine.close();
        }
        scanWords.close();

        return builder.toString();
    }

    /*
     *  check a potential token is really a pretty token, if not avoid unnecessary chars and create a good token  out of it
     */
    private static String tokenizeString(String potentialToken) {
        StringBuilder stringBuilder = new StringBuilder();
        if (potentialToken.length() > 0) { // parseDouble thinks "1980." a double. avoid that.
            if (!Character.isDigit(potentialToken.charAt(potentialToken.length() - 1)) && !Character.isLetter(potentialToken.charAt(potentialToken.length() - 1))) {
                potentialToken = potentialToken.substring(0, potentialToken.length() - 1);
            }
        }
        if(isDouble(potentialToken) && isDouble(potentialToken.replace(",","."))){

            return potentialToken;
        }
        else{
            for (int j = 0; j < potentialToken.length(); j++) {
                if (Character.isDigit(potentialToken.charAt(j)) || Character.isLetter(potentialToken.charAt(j))) {

                    stringBuilder.append(potentialToken.charAt(j));
                }
            }


        }
        return stringBuilder.toString();
    }

    /*
     * checks a string is a double returns accordingly
     */
    public static boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException nfe) {}
        return false;
    }

}
