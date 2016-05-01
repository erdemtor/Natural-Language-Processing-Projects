package srcPos;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Erdem on 4/27/2016.
 */
public class Test {
    public static String POSType;
    public static HashMap<String, HashMap<String, Double>> posNamesToWordPossibilities;
    public static HashMap<String, HashMap<String, Double>> posNamesToPosNamesProbablities;
    public static ArrayList<Sentence> allSentences;
    public static HashSet<POS> allPOS = new HashSet<POS>();
    public static HashMap<POS, HashMap<String, Double>> posToWords;
    public static HashMap<POS, HashMap<String, Double>> posToWordPossibilities = new HashMap<POS, HashMap<String, Double>>();
    public static HashSet<String> allWords = new HashSet<String>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String path = args[0];
        String outputFile = args[1];
        deserialize();
        readTestData(path);
        printSuccessRate();
        printOut(outputFile);
        System.out.println("Test is over");

    }

    private static void printSuccessRate() {
        double knownAll = 0;
        double knownSuc = 0;
        double unKnownAll = 0;
        double unKnownSuc = 0;
        for (Sentence s : allSentences) {
            ArrayList<POS> testOutput = applyViterbi(s.getSentenceWordsInOrder());
            for (int i = 0; i < testOutput.size(); i++) {
                if (allWords.contains(s.getSentenceWordsInOrder().get(i).getContent())) {
                    knownAll++;
                    if(testOutput.get(i).getType().hashCode() == s.getSentenceWordsInOrder().get(i).getPos().hashCode() ){
                        knownSuc++;

                    }

                }
                else {
                    unKnownAll++;
                    if(testOutput.get(i).getType().hashCode() == s.getSentenceWordsInOrder().get(i).getPos().hashCode() ){
                        unKnownSuc++;

                    }
                }

            }
        }

        System.out.println("UnknownSuccess: "+  unKnownSuc  + "/"+ unKnownAll+ "=>  "+(unKnownSuc * 100/ unKnownAll) +"%");
        System.out.println( "KnownSuccess: "+ knownSuc  + "/"+ knownAll+ ": "+(knownSuc * 100/ knownAll)+"%");
        System.out.println("OVERALL SUCCESS "+(unKnownSuc+knownSuc) + "/"+ (knownAll+ unKnownAll) + "=>  "+ ((unKnownSuc+knownSuc)*100 / (knownAll+ unKnownAll)) + "%" );

    }

    public static void printOut(String outputFile) throws FileNotFoundException, UnsupportedEncodingException {
        String toBeWrittenOnOutputFile = "";
        for (Sentence s : allSentences) {
            ArrayList<POS> testOutput = applyViterbi(s.getSentenceWordsInOrder());
            for (int i = 0; i < testOutput.size(); i++) {
                String line = s.getSentenceWordsInOrder().get(i).getContent() + "|" + testOutput.get(i).getType() + "\n";
                toBeWrittenOnOutputFile += line;
            }
            if (s.getSentenceWordsInOrder().size() >0)
            toBeWrittenOnOutputFile += "\n";
        }
        int x = 5;
        PrintWriter writer = new PrintWriter(outputFile, "UTF-8");
        writer.print(toBeWrittenOnOutputFile);
        writer.close();


    }

    public static void readTestData(String path) throws FileNotFoundException {
        allSentences = Main.readTrainingData(path, POSType);
    }

    public static void deserialize() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream("posNamesToWordPossibilities.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        posNamesToWordPossibilities = (HashMap<String, HashMap<String, Double>>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("posNamesToPosNamesProbablities.ser");
        in = new ObjectInputStream(fileIn);
        posNamesToPosNamesProbablities = (HashMap<String, HashMap<String, Double>>) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("posType.ser");
        in = new ObjectInputStream(fileIn);
        POSType = (String) in.readObject();
        in.close();
        fileIn.close();
        fillData();
        fileIn = new FileInputStream("allWords.ser");
        in = new ObjectInputStream(fileIn);
        allWords = (HashSet<String>) in.readObject();
        in.close();
        fileIn.close();
        fillData();

    }

    public static void fillData() {

        for (String POSNAME : posNamesToPosNamesProbablities.keySet()) {
            POS p = new POS(POSNAME);
            allPOS.add(p);
        }
        for (POS p : allPOS) {
            for (POS pIN : allPOS) {
                if (posNamesToPosNamesProbablities.containsKey(p.getType())) {
                    if (posNamesToPosNamesProbablities.get(p.getType()).containsKey(pIN.getType()))
                        p.getAfterPossibilities().put(pIN, posNamesToPosNamesProbablities.get(p.getType()).get(pIN.getType()));
                }

            }
        }
        for (POS p : allPOS) {
            HashMap<String, Double> wordsToPossibilities = new HashMap<String, Double>();

            for (String word : posNamesToWordPossibilities.get(p.getType()).keySet()) {
                if (posNamesToWordPossibilities.containsKey(p.getType())) {
                    wordsToPossibilities.put(word, posNamesToWordPossibilities.get(p.getType()).get(word));

                }
                posToWordPossibilities.put(p, wordsToPossibilities);
            }


        }
    }

    public static ArrayList<POS> applyViterbi(ArrayList<Word> wordsObjects) {
        ArrayList<String> words = new ArrayList<String>();
        for (int i = 0; i < wordsObjects.size(); i++) {
            words.add(wordsObjects.get(i).getContent());
        }
        ArrayList<POS> allPosList = new ArrayList<POS>(allPOS);
        double[][] viterbi = new double[allPosList.size()][words.size()];
        int[] backtrace = new int[words.size()];
        POS previousPOS = findRealPOS("start"); // initial src.POS
        POS endPos = findRealPOS("end");
        for (int column = 0; column < words.size(); column++) {
            double maxValueForThisColumn = -100000;
            for (int row = 0; row < allPosList.size(); row++) {
                POS currentPOS = allPosList.get(row);
                String currentWord = words.get(column);
                if (column == 0) {
                    double possibilityOfThisPosBeingTheStartPOS = -30; // a very small number just in case
                    if (previousPOS.getAfterPossibilities().containsKey(currentPOS)) {
                        possibilityOfThisPosBeingTheStartPOS = previousPOS.getAfterPossibilities().get(currentPOS);
                    }
                    double probabilityOfThisWordBeingThisPOS = -30;
                    try {
                        if (posToWordPossibilities.containsKey(currentPOS) && posToWordPossibilities.get(currentPOS).containsKey(currentWord)) {
                            probabilityOfThisWordBeingThisPOS = posToWordPossibilities.get(currentPOS).get(currentWord);
                        }
                    } catch (Exception e) {

                    }
                    double suspectedViterbiValue = possibilityOfThisPosBeingTheStartPOS + probabilityOfThisWordBeingThisPOS; // in fact in this case no need to suspect, it is the one
                    viterbi[row][column] = suspectedViterbiValue;
                    if (suspectedViterbiValue > maxValueForThisColumn) {
                        maxValueForThisColumn = suspectedViterbiValue;
                        backtrace[column] = row;
                    }
                }
                if (column > 0 && column != words.size() - 1) {
                    double maxValueForVitervbi = -100000;
                    for (int viterbiRow = 0; viterbiRow < allPosList.size(); viterbiRow++) { // to find the max of the old
                        double previousViterbiValue = viterbi[viterbiRow][column - 1];
                        double probabilityOfThisNodePOSToCurrentPOS = -30;
                        if (allPosList.get(viterbiRow).getAfterPossibilities().containsKey(currentPOS)) {
                            probabilityOfThisNodePOSToCurrentPOS = allPosList.get(viterbiRow).getAfterPossibilities().get(currentPOS);
                        }
                        double suspectedVal = previousViterbiValue + probabilityOfThisNodePOSToCurrentPOS; // this time it is really suspected
                        if (suspectedVal > maxValueForVitervbi) {
                            maxValueForVitervbi = suspectedVal;
                            backtrace[column] = viterbiRow;
                        }
                    }
                    double probabilityOfThisWordBeingThisPOS = -30;
                    if (posToWordPossibilities.containsKey(currentPOS) && posToWordPossibilities.get(currentPOS).containsKey(currentWord)) {
                        probabilityOfThisWordBeingThisPOS = posToWordPossibilities.get(currentPOS).get(currentWord);
                    }
                    viterbi[row][column] = maxValueForVitervbi + probabilityOfThisWordBeingThisPOS;
                }
                if (column > 0 && column == words.size() - 1) {
                    double maxValueForVitervbi = -100000;
                    for (int viterbiRow = 0; viterbiRow < allPosList.size(); viterbiRow++) { // to find the max of the old
                        double previousViterbiValue = viterbi[viterbiRow][column - 1];
                        double probabilityOfThisNodePOSToCurrentPOS = -30;
                        if (allPosList.get(viterbiRow).getAfterPossibilities().containsKey(currentPOS)) {
                            probabilityOfThisNodePOSToCurrentPOS = allPosList.get(viterbiRow).getAfterPossibilities().get(currentPOS);
                        }
                        double probabilityOfThisPOSBeingTheLastOne = -30;
                        if (currentPOS.getAfterPossibilities().containsKey(endPos)) {
                            probabilityOfThisPOSBeingTheLastOne = currentPOS.getAfterPossibilities().get(endPos);
                        }
                        double suspectedVal = previousViterbiValue + probabilityOfThisNodePOSToCurrentPOS + probabilityOfThisPOSBeingTheLastOne; // this time it is really suspected
                        if (suspectedVal > maxValueForVitervbi) {
                            maxValueForVitervbi = suspectedVal;
                            backtrace[column] = viterbiRow;
                        }
                    }
                    double probabilityOfThisWordBeingThisPOS = -30;
                    if (posToWordPossibilities.containsKey(currentPOS) && posToWordPossibilities.get(currentPOS).containsKey(currentWord)) {
                        probabilityOfThisWordBeingThisPOS = posToWordPossibilities.get(currentPOS).get(currentWord);
                    }
                    viterbi[row][column] = maxValueForVitervbi + probabilityOfThisWordBeingThisPOS;
                    viterbi[row][column] = maxValueForVitervbi;

                }
                previousPOS = currentPOS;
            }
        }


        //  printViterbi(viterbi);
        ArrayList<POS> res = new ArrayList<POS>();
        for (int j = 0; j < viterbi[0].length; j++) { // column
            double maxValue = -10000;
            int index = -1;
            for (int i = 0; i < viterbi.length; i++) { //row
                double value = viterbi[i][j];
                if (value > maxValue) {
                    maxValue = value;
                    index = i;
                }
            }
            res.add(allPosList.get(index));

        }
        return res;
    }

    public static POS findRealPOS(String pIn) {
        for (POS p : allPOS) {
            if (p.getType().equals(pIn)) return p;

        }
        return null;
    }


}
