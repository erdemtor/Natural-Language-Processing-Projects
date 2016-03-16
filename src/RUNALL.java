import java.io.IOException;

/**
 * Created by Erdem on 3/16/2016.
 */
public class RUNALL {

    public static void main(String[] args) throws IOException {
double counter =0;
        for (double i = 0; i < 10; i++){
            Classifier.main(args);
            counter += Main.main(args);
        }
        System.out.println("EN GENEL ORTALAMA: "+ counter/10);

    }
}
