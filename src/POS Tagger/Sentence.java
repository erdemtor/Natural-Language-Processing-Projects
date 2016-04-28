import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Erdem on 4/25/2016.
 */
public class Sentence implements Serializable {
    ArrayList<Word> sentenceWordsInOrder = new ArrayList<Word>();

    public Sentence() {
    }

    public Sentence(ArrayList<Word> sentenceWordsInOrder) {

        this.sentenceWordsInOrder = sentenceWordsInOrder;
    }

    public ArrayList<Word> getSentenceWordsInOrder() {

        return sentenceWordsInOrder;
    }

    public void setSentenceWordsInOrder(ArrayList<Word> sentenceWordsInOrder) {
        this.sentenceWordsInOrder = sentenceWordsInOrder;
    }
}
