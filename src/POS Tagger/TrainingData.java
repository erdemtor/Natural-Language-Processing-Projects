import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by Erdem on 4/27/2016.
 */
public class TrainingData implements Serializable{
    public  ArrayList<Sentence> allSentences = new ArrayList<Sentence>();
    public HashSet<POS> allPOS = new HashSet<POS>();
    public HashMap<POS, HashMap<String, Double>> posToWords = new HashMap<POS, HashMap<String, Double>>();

    public TrainingData(ArrayList<Sentence> allSentences, HashSet<POS> allPOS, HashMap<POS, HashMap<String, Double>> posToWords, HashMap<POS, HashMap<String, Double>> posToWordPossibilities) {
        this.allSentences = allSentences;
        this.allPOS = allPOS;
        this.posToWords = posToWords;
        this.posToWordPossibilities = posToWordPossibilities;
    }

    public HashMap<POS, HashMap<String, Double>> posToWordPossibilities = new HashMap<POS, HashMap<String, Double>>();

    public TrainingData() {

    }
}
