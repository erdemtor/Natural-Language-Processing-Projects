import java.util.HashMap;

/**
 * Created by Erdem on 3/10/2016.
 */
public class TestDocument {
    double avgSentenceLength = 0;
    HashMap<String,Integer> documentData = new HashMap<>();
    String author;

    public double getAvgSentenceLength() {
        return avgSentenceLength;
    }

    public void setAvgSentenceLength(double avgSentenceLength) {
        this.avgSentenceLength = avgSentenceLength;
    }

    public HashMap<String, Integer> getDocumentData() {
        return documentData;
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
