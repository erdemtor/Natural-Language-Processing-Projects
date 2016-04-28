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
        while(true) {
            System.out.println("Please write the full path of the source directory where authors are located in");
            sDir =console.next();
            File f = new File(sDir);
            if (!f.isDirectory()) {
                System.out.println("This is not a directory please write the full path of the source directory where authors are located in");
            } else {
                break;
            }
        }
        System.out.println("How many times do you want all system to run and test? \n(BE CAREFUL, if you did not put cleanData() back to the code in Main.java this will give extremely good results)");
        System.out.println("and if you  put cleanData() back to the code in Main.java then when the program is over you wont be able to see testdata and trainingdata folders)");
       int run = console.nextInt();
        double counter =0;
        for (double i = 0; i <run ; i++){
            String[] arr = new String [1];
            arr[0] = sDir;
            args= FileClassifier.main(arr);
            double overalRes =  (double) Math.round(Main.main(args) * 100) / 100;
            counter += overalRes;
            System.out.println((int)(i+1)+". try overal success rate is: "+ overalRes);


        }



    }
}
