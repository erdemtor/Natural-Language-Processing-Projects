import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by Erdem on 3/16/2016.
 */
public class RUNALL {

    public static void main(String[] args) throws IOException {
        Scanner console = new Scanner(System.in);
        String sDir = "";
        while (true) {
            System.out.println("Please write the full path of the source directory where authors are located in");
            sDir = console.next();
            File f = new File(sDir);

            if (!f.isDirectory()) {
                System.out.println("This is not a directory please write the full path of the source directory where authors are located in");
            } else {
                break;
            }
        }
        String[] arr = new String[1];
        arr[0] = sDir;
        args = FileClassifier.main(arr);
        MainOld.main(args, true);
        args = FileClassifier.main(arr);
        MainOld.main(args, false);
    }
}
