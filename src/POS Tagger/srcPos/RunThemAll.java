package srcPos;

import srcPos.Main;

import java.io.IOException;

/**
 * Created by Erdem on 5/1/2016.
 */
public class RunThemAll {
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        String trainingAddress = args[0];
        String type = args[1];
        String testAddress =args[2];
        String outputAddress = args[3];
        String goldStandard = args[4];
        String[] argsForMain = {trainingAddress,type};
        String[] argsForTest = {testAddress,outputAddress};
        String[] validationArgs = {outputAddress, goldStandard};
        Main.main(argsForMain);
        Test.main(argsForTest);
        Validator.main(validationArgs);
    }
}
