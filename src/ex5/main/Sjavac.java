package ex5.main;

import ex5.validator.ValidationException;
import ex5.validator.VarException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Gets input file from the user and checks correctness.
 */
public class Sjavac {

    private static final int LEGAL_CODE = 0;
    private static final int ILLEGAL_CODE_CODE = 1;
    private static final int ILLEGAL_IO_CODE = 2;
    private static final String ILLEGAL_NUM_ARGS = "Usage: java Sjavac <filename>";
    private static final String ILLEGAL_FILE_NAME = "File name should be <something>.sjava";


    /**
     * Main mathod.
     * @param args The command line arguments.
     */
    public static void main(String[] args) {


        // Check input length:
        if (args.length != 1) {
            printError(ILLEGAL_IO_CODE, ILLEGAL_NUM_ARGS);
            return;
        }

        // Check file name:
        Pattern fileNamePattern = Pattern.compile(".+\\.sjava");
        Matcher fileNameMatcher = fileNamePattern.matcher(args[0]);
        if (!fileNameMatcher.matches()) {
            printError(ILLEGAL_IO_CODE, ILLEGAL_FILE_NAME);
            return;
        }

        // Open the file for parsing:
        try (FileReader fileReader = new FileReader(args[0]);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            Verifier verifier = new Verifier(bufferedReader);
            verifier.verify();
            // Verification succeeded:
            System.out.println(LEGAL_CODE);
        } catch (IOException e) {
            printError(ILLEGAL_IO_CODE, e.getMessage());
        } catch (CodeException e) {
            printError(ILLEGAL_CODE_CODE, e.getMessage());
            e.printStackTrace();
        }


    }


    /* Prints an error. */
    private static void printError(int errorCode, String message) {
        System.out.println(errorCode);
        System.err.println(message);
    }
}
