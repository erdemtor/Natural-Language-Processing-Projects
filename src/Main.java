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
    static HashMap<String,ArrayList<String>> fullTestData = new HashMap<>(); // this is from author to their list of test texts
    static HashMap<String,ArrayList<String>> testDataPaths = new HashMap<>();
    static HashMap<String,Double> commaNumbers = new HashMap<>();
    static HashMap<String,Integer> totalNumOfWords = new HashMap<>();
    static HashMap<String , Integer> corpus = new HashMap<>();
    static HashMap<String,Integer> numberOfDocuments = new HashMap<>();
    static double totalNumOfDocuments = 0 ;

    public static void main(String[] args) throws FileNotFoundException {
        fillTheData("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\69yazar\\raw_texts",true);
        test()

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
        if(!first)
        {
            numberOfDocuments.put(dir.getName(),faFiles.length);
            totalNumOfDocuments += faFiles.length;
        }
        for(File file: faFiles){
            if(file.isDirectory()){
                fillTheData(file.getAbsolutePath(), false);
                calcCommaFreq(file.getName());
            }else{
                parseFile(file.getAbsolutePath());

            }
        }
    }

    /*
     *
     * Given path is for the considered for training data and will be filled
     *
     *

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
            ArrayList<String> authorTextsTokenized = new ArrayList<>();
            if (fullTestData.containsKey(authorName)){

                authorTextsTokenized = fullTestData.get(authorName);
            }
            authorTextsTokenized.add(fullText);
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
        if(answer > 70){

            return true;
        }

        return false;
    }


    public static String test(HashMap<String,Integer> document){
        double max = Double.MIN_VALUE;
        String authorRes = "";
        double alpha = 1;
        double vocabSize = corpus.size(); // tum sozlukte unique sayi
        for (String author: numberOfDocuments.keySet()) {
            double totalNumberOfWords = totalNumOfWords.get(author); // o yazarin kac tane kelimesi var, kac tane ayni oldugu onemsiz hepsi sayiliyor
            double pc = Math.log(numberOfDocuments.get(author) / totalNumOfDocuments);
            double rest = pc;
            for (String word: document.keySet()) {
                double occcurence = document.get(word); // will be used as power
                double insideValue = Math.log(( fullTrainingData.get(author).get(word)+ alpha)/ (vocabSize +totalNumberOfWords));
                rest += insideValue;
            }
            if (rest> max){
                authorRes = author;
                max = rest;
            }


        }
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
