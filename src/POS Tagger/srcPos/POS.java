package srcPos;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Erdem on 4/25/2016.
 */
public class POS implements Serializable {
    String type;

    HashMap<POS, Double> afterPossibilities = new HashMap<POS, Double>();

    @Override
    public String toString() {
        return "type='" + type + "\n";
    }

    public POS() {
        afterPossibilities = new HashMap<POS, Double>();
    }

    public POS(String type, HashMap<POS, Double> afterPossibilities) {

        this.type = type;
        this.afterPossibilities = afterPossibilities;
    }

    public POS(String type) {
        this.type = type;
        afterPossibilities = new HashMap<POS, Double>();
    }

    public String getType() {

        return type;

    }

    public POS getHighestNext() {
        double max = -100000;
        POS res = new POS("AAAAAAAAAAAAAAAAAAA NULLLL");
        for (POS p : this.getAfterPossibilities().keySet()) {
            if (this.getAfterPossibilities().get(p) > max) {
                max = this.getAfterPossibilities().get(p);
                res = p;
            }
        }
        return res;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<POS, Double> getAfterPossibilities() {
        return afterPossibilities;
    }

    public void setAfterPossibilities(HashMap<POS, Double> afterPossibilities) {
        this.afterPossibilities = afterPossibilities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof POS)) return false;
        POS pos = (POS) o;
        return getType().equals(pos.getType());
    }

    @Override
    public int hashCode() {
        if(type == null){
            int a = 9;
        }
        return getType().hashCode();
    }
}
