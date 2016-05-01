
package srcPos;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Created by Erdem on 5/1/2016.
 */
public class Validator {
    static ArrayList<Sentence> realSentences = new ArrayList<>();
    static ArrayList<Sentence> guessedSentences = new ArrayList<>();
    public static String POSType;
    public static HashSet<String> posNames = new HashSet<>();
    public static HashMap<String, HashMap<String, Double>> posNamesToPosNamesProbablities;
    public static HashMap<String, HashMap<String, Double>> confusionMatrix = new HashMap<>();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        deserialize();
        readOutFile(args[0]);
        readGoldFile(args[1]);
        compareAndCreateMatrix();
    }

    private static void deserialize() throws IOException, ClassNotFoundException {
      /*  File file = new File("");
        String currentDirectory = file.getAbsolutePath();
        String org = currentDirectory+"\\src\\POS Tagger\\posType.ser";*/
        //   System.out.println(org);
        //  System.out.println(org.equals("C:\\Users\\Erdem\\Desktop\\NLP\\Natural-Language-Processing-Project-1\\src\\POS Tagger\\posType.ser"));
        FileInputStream fileIn = new FileInputStream("posType.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        POSType = (String) in.readObject();
        in.close();
        fileIn.close();
        fileIn = new FileInputStream("posNamesToPosNamesProbablities.ser");
        in = new ObjectInputStream(fileIn);
        posNamesToPosNamesProbablities = (HashMap<String, HashMap<String, Double>>) in.readObject();
        for (String p : posNamesToPosNamesProbablities.keySet()) {
            posNames.add(p);
        }
    }

    private static void compareAndCreateMatrix() {
        for (String pos : posNames) {
            confusionMatrix.put(pos, new HashMap<>());
        }
        for (int i = 0; i < guessedSentences.size(); i++) {
            Sentence realSent = realSentences.get(i);
            Sentence guessedSent = guessedSentences.get(i);
            for (int j = 0; j < realSent.getSentenceWordsInOrder().size(); j++) {
                Word realW = realSent.getSentenceWordsInOrder().get(j);
                Word guessed = guessedSent.getSentenceWordsInOrder().get(j);
                if (confusionMatrix.get(realW.getPos().getType()).containsKey(guessed.getPos().getType())) {
                    confusionMatrix.get(realW.getPos().getType()).put(realW.getPos().getType(), confusionMatrix.get(realW.getPos().getType()).get(guessed.getPos().getType()) + 1);
                } else {
                    confusionMatrix.get(realW.getPos().getType()).put(guessed.getPos().getType(), 1.0);
                }
            }
        }
        String matrix = "";
        for (String pos : confusionMatrix.keySet()) {
            matrix+=pos+": \n";
            matrix += "\t{\n";
            HashMap<String, Double> map = confusionMatrix.get(pos);
            for (String s: map.keySet()) {
                matrix+="\t"+s+": "+map.get(s)+"\n";
            }
            matrix+="\t},\n";
        }
        System.out.println(matrix);


    }

    private static void readGoldFile(String path) throws FileNotFoundException {
        realSentences = Main.readTrainingData(path, POSType);
    }

    private static void readOutFile(String path) throws FileNotFoundException {
        Scanner scanFile = new Scanner(new File(path));
        Sentence currentSentence = new Sentence();
        boolean isNewSentence = false;
        while (scanFile.hasNextLine()) {
            String line = scanFile.nextLine();

            if (!line.equals("")) {
                if (isNewSentence) {
                    guessedSentences.add(currentSentence);
                    currentSentence = new Sentence();
                }
                String[] tokens = line.split("\\|");
                String word = tokens[0];
                String guessedPOS = tokens[1];
                if (!word.equals("_")) {
                    POS pos = new POS();
                    pos.setType(guessedPOS);
                    Word currentWord = new Word(word.toLowerCase(), pos);
                    currentSentence.getSentenceWordsInOrder().add(currentWord);
                    isNewSentence = false;
                }
            } else {
                isNewSentence = true;
            }
        }
        guessedSentences.add(currentSentence);

    }


}