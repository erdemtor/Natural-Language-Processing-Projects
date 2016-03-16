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
            String tokenizedAuthorAllText = tokenizer(authorAllText);
            String[] words = tokenizedAuthorAllText.split(" ");
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
            String tokenizedAuthorAllText = tokenizer(authorAllText);
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
        double alpha = 1;
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
                double insideValue = occurrence*Math.log((data + alpha)/ (vocabSize +totalNumberOfWords));
                rest += insideValue;
                counter++;
            }
               rest += Math.abs(avgWordLength.get(author) - testDoc.getAvgWordLength())*1000;
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
