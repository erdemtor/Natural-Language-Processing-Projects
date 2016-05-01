package srcPos;

import java.io.Serializable;

/**
 * Created by Erdem on 4/25/2016.
 */
public class Word implements Serializable {

    int order;
    String content;
    POS pos;

    public Word(int order, String content, POS pos) {
        this.order = order;
        this.content = content;
        this.pos = pos;
    }

    public Word(String word) {
        this.content = word;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        int result = getOrder();
        result = 31 * result + getContent().hashCode();
        result = 31 * result + getPos().hashCode();
        return result;
    }

    public int getOrder() {

        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public POS getPos() {
        return pos;
    }

    public void setPos(POS pos) {
        this.pos = pos;
    }

    public Word(String s, POS guessedPOS) {
        this.content = s;
        this.pos = guessedPOS;
    }

}
