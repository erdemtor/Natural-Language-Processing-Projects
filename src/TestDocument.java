import java.util.HashMap;

/**
 * Created by Erdem on 3/10/2016.
 */
public class TestDocument {
    double avgWordLength = 0;
    double numbOfSentence = 0;
    double commaWord = 0;

    public double getCommaWord() {
        return commaWord;
    }

    public void setCommaWord(double commaWord) {
        this.commaWord = commaWord;
    }

    HashMap<String,Integer> documentData = new HashMap<>();
    String author;

    public double getAvgWordLength() {
        return avgWordLength;
    }

    public void setAvgWordLength(double avgWordLength) {
        this.avgWordLength = avgWordLength;
    }

    public HashMap<String, Integer> getDocumentData() {
        return documentData;
    }

    public double getNumbOfSentence() {
        return numbOfSentence;
    }

    public void setNumbOfSentence(double numbOfSentence) {
        this.numbOfSentence = numbOfSentence;
    }

    public void setDocumentData(HashMap<String, Integer> documentData) {
        this.documentData = documentData;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public TestDocument() {

    }


}
