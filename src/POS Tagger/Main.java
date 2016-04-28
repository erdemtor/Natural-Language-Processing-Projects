import java.io.*;
import java.util.*;

/**
 * Created by Erdem on 4/25/2016.
 */
public class Main {

    public static ArrayList<Sentence> allSentences = new ArrayList<Sentence>();
    public static HashSet<POS> allPOS = new HashSet<POS>();
    public static HashMap<POS, HashMap<String, Double>> posToWords = new HashMap<POS, HashMap<String, Double>>();
    public static HashMap<POS, HashMap<String, Double>> posToWordPossibilities = new HashMap<POS, HashMap<String, Double>>(); // MAth.loglari alindi bunlarin
    public static HashMap<String, HashMap<String, Double>> posNamesToWordPossibilities = new HashMap<String, HashMap<String, Double>>(); // MAth.loglari alindi bunlarin
    public static HashMap<String, HashMap<String, Double>> posNamesToPosNamesProbablities = new HashMap<String, HashMap<String, Double>>();
    public  static  HashSet<String> allWords = new HashSet<String>();
    public static void main(String[] args) throws IOException {
        readAndCalculateEverything(args[0], args[1]);

    }

    public static void readAndCalculateEverything(String path, String POSType) throws IOException {
        readTrainingData(path, POSType);
        eliminateDuplicatePOSses();
        calculatePOStoWord();
        calculatePOStoWordPossibilities();
        calculatePOSTransitions();
        for (POS p : posToWordPossibilities.keySet()) {
            posNamesToWordPossibilities.put(p.getType(), posToWordPossibilities.get(p));
        }
        for (POS p : allPOS) {
            HashMap<String, Double> probabilities = new HashMap<String, Double>();
            for (POS pIn : p.getAfterPossibilities().keySet()) {
                probabilities.put(pIn.getType(), p.getAfterPossibilities().get(pIn));
            }
            posNamesToPosNamesProbablities.put(p.getType(), probabilities);
        }
        serialize(posNamesToWordPossibilities, posNamesToPosNamesProbablities, POSType);
    }

    public static void serialize(HashMap<String, HashMap<String, Double>> posNamesToWordPossibilities, HashMap<String, HashMap<String, Double>> posNamesToPosNamesProbablities, String POSType) throws IOException {
        FileOutputStream fileOut =
                new FileOutputStream("posNamesToWordPossibilities.ser");
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(posNamesToWordPossibilities);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("posNamesToPosNamesProbablities.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(posNamesToPosNamesProbablities);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("posType.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(POSType);
        out.close();
        fileOut.close();
        fileOut = new FileOutputStream("allWords.ser");
        out = new ObjectOutputStream(fileOut);
        out.writeObject(allWords);
        out.close();
        fileOut.close();
    }

    public static void calculatePOSTransitions() {

        for (Sentence s : allSentences) {
            POS previousPOS = findRealPOS("start");
            for (int i = 0; i < s.getSentenceWordsInOrder().size(); i++) {
                Word currentWord = s.getSentenceWordsInOrder().get(i);
                POS currentPOS = currentWord.getPos();
                if (previousPOS.getAfterPossibilities().containsKey(currentPOS)) {
                    previousPOS.getAfterPossibilities().put(currentPOS, previousPOS.getAfterPossibilities().get(currentPOS) + 1);
                } else {
                    previousPOS.getAfterPossibilities().put(currentPOS, 1.0);
                }
                previousPOS = currentPOS;
            }
            POS lastPOS = findRealPOS("end");
            if (previousPOS.getAfterPossibilities().containsKey(lastPOS)) {
                previousPOS.getAfterPossibilities().put(lastPOS, previousPOS.getAfterPossibilities().get(lastPOS) + 1);
            } else {
                previousPOS.getAfterPossibilities().put(lastPOS, 1.0);
            }
        }

        for (POS p : allPOS) {
            double sum = 0;
            for (POS nextP : p.getAfterPossibilities().keySet()) {
                sum += p.getAfterPossibilities().get(nextP);
            }
            for (POS nextP : p.getAfterPossibilities().keySet()) {
                double poss = Math.log(p.getAfterPossibilities().get(nextP)) - Math.log(sum);
                p.getAfterPossibilities().put(nextP, poss);
            }

        }


    }

    public static void calculatePOStoWordPossibilities() {

        for (POS p : posToWords.keySet()) {
            HashMap<String, Double> wordPoss = new HashMap<String, Double>();
            HashMap<String, Double> wordOccurenceOfPOS = posToWords.get(p);
            double sum = 0;
            for (String w : wordOccurenceOfPOS.keySet()) {
                double occ = wordOccurenceOfPOS.get(w);
                sum += occ;
            }
            for (String w : wordOccurenceOfPOS.keySet()) {
                double occ = wordOccurenceOfPOS.get(w);
                double possLog = Math.log(occ) - Math.log(sum);
                wordPoss.put(w, possLog);
            }
            posToWordPossibilities.put(p, wordPoss);
        }


    }


    public static void calculatePOStoWord() {
        for (POS p : allPOS) {
            HashMap<String, Double> map = new HashMap<String, Double>();
            posToWords.put(p, map);
        }
        for (POS p : allPOS) {
            for (Sentence s : allSentences) {
                for (Word currentWord : s.getSentenceWordsInOrder()) {
                    if (currentWord.getPos().hashCode() == p.hashCode()) {
                        if (posToWords.get(p).containsKey(currentWord.getContent())) {
                            posToWords.get(p).put(currentWord.getContent(), posToWords.get(p).get(currentWord.getContent()) + 1);
                        } else {
                            posToWords.get(p).put(currentWord.getContent(), 1.0);
                        }
                    }

                }
            }
        }
    }

    public static POS findRealPOS(POS pIn) {
        for (POS p : allPOS) {
            if (p.hashCode() == pIn.hashCode()) return p;

        }
        return null;
    }

    public static POS findRealPOS(String pIn) {
        for (POS p : allPOS) {
            if (p.getType().equals(pIn)) return p;

        }
        return null;
    }


    public static void eliminateDuplicatePOSses() {
        for (Sentence sentence : allSentences) {
            for (Word w : sentence.getSentenceWordsInOrder()) {
                w.pos = findRealPOS(w.getPos());
            }
        }

    }

    public static ArrayList<Sentence> readTrainingData(String path, String POSType) throws FileNotFoundException {

        int posNum = 4;
        if (!POSType.contains("c")) {
            posNum = 3;
        }
        Scanner scanFile = new Scanner(new File(path));
        boolean isNewSentence = true;
        Sentence currentSentence = new Sentence();
        while (scanFile.hasNextLine()) {
            String line = scanFile.nextLine();
            if (!line.equals("")) {
                if (isNewSentence) {
                    allSentences.add(currentSentence);
                    currentSentence = new Sentence();
                }
                String[] tokens = line.split("\\s+");
                String order = tokens[0];
                String word = tokens[1];
                if (!word.equals("_")) {
                    String POStype = tokens[posNum];
                    POS pos = new POS();
                    pos.setType(POStype);
                    Word currentWord = new Word(Integer.parseInt(order), word.toLowerCase(), pos);
                    currentSentence.getSentenceWordsInOrder().add(currentWord);
                    allPOS.add(pos);
                    allWords.add(currentWord.getContent());
                    isNewSentence = false;
                }
            } else {
                isNewSentence = true;
            }
        }
        allPOS.add(new POS("start"));
        allPOS.add(new POS("end"));
        return allSentences;
    }
}
