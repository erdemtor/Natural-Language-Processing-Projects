import java.util.HashMap;

/**
 * Created by Erdem on 3/10/2016.
 */
public class TestDocument {
    double avgWordLength = 0;
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
