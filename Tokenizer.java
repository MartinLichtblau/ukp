import java.io.BufferedReader; // import only what's necessary, to make it explicit, performance, for IDE usage.
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Hello UKP candidate.
 * The following program should read an arbitrary number of files from a directory and store all their tokens in a member variable.
 * The program may contain some bugs interfering with the desired functionality.
 * Your tasks are:
 * 1. Understand the program and correct all errors that lead to erroneous behaviour. Comment your changes and shortly explain the reasons.
 * 2. The program also contains some questionable programming constructs (e.g. disregarding Java naming conventions, etc.).
 *    Try to find as many as you can, and correct them. Comment your changes and shortly explain the reasons.
 * 3. Add the missing JavaDocs at a level of detail that you consider as appropriate.
 * #change formatting for better comprehension and to reduce line length (style convention)
 * 4. Write a <b>single</b> method that <b>returns</b>
 *      - the number of items in tokenMap
 *      - the average length (as double value) of the elements in tokenMap after calling applyFilters()
 *      - the number of tokens starting with "a" (case sensitive).
 *    Output this information.
 *
 * @author zesch
 * @version 2 #change filename to match class, put version in doc and use version control for keeping track.
 */

/*

    @TODO put all overall changes in separate block
    Definitions:
        Style Guide = Google Java Style Guide

    @TODO remove unnecessary line breaks based on
 */


public class Tokenizer { // #change classname to express what it does.
    // @TODO remove line spacing between variables
    public static final String CHARSET = "ISO-8859-1"; // #style #change order of modifiers

    public File inputDir;
    
    public double nrofFiles; // @TODO remove unused code in general
    
    public int minimumCharacters; // @TODO rename to minTokenLength and max ... Reason naming should be consistent and concise.
    public int maximumCharacters;
    
    HashMap<String, Integer> tokenMap; // @TODO add variable description. What is the string and integer
    // @TODO the only var that could be public
    // @TODO use map instead of HashMap for more freedom without a downside.
    
    public Tokenizer(File pInputDir, int pMinChars, int pMaxChars) {
        // @TODO rename. Don't use prefixes (Hungarian Notation)
        // Constructor needs to be public?
        
        inputDir = pInputDir; // @TODO this.inputDir = inputDir
        minimumCharacters = pMinChars;
        maximumCharacters = pMaxChars;
        
        if ((pMinChars == 0) || (pMaxChars == 0)) { // @TODO max must be equal or bigger than min. Give explanation in error.
            throw new RuntimeException("Configuration parameters have not been correctly initialized.");
        }
    }
    
    public void run() {
        readFiles();
        applyFilters();
        outputTokens();
    }
    
    private void readFiles() {

        // @TODO consider subfolders
        File[] files = inputDir.listFiles(); // If Java 8 I would use Files.walk(). With Java 7 Files.newDirectoryStream()
        if (files == null) {
            System.err.println("Filelist is empty. Directory: " + inputDir.getAbsolutePath());
            System.exit(1);
        }
        
        for (int i = 0; i < files.length; i++) {
            File file = new File(files[i].getAbsoluteFile().toString());
            
            if (file.length() == 0) {
                System.out.println("Skipping emtpy file " + file.getAbsolutePath());
                continue;
            }
            
            System.out.println(file.getAbsolutePath());
            
            tokenMap = getFileTokens(file); // @TODO merge existing tokens with new tokens. ! same tokens
        }
    }

    // @TODO put tokens directly in tokenMap? Would save much effort.
    private HashMap<String, Integer> getFileTokens(File infile) {
        
        HashMap<String, Integer> fileTokens = new HashMap<String, Integer>();
        
        BufferedReader in;
        String line;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(infile), CHARSET));
            while ((line = in.readLine()) != null) {
                String lineParts[] = line.split(" "); // split text in tokens @TODO rename parts into tokens
                for (String part : lineParts) {
                    if (fileTokens.containsKey(part)) {
                        fileTokens.put(part, fileTokens.get(part) + 1); // increment occurrence count @TODO use replace to make it specific
                    } // @TODO check parenthesis formatting
                    else {
                        fileTokens.put(part, 1);
                    }    
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println(e);
            // @TODO do appropriate error handling, file would stay open, refactor the whole function?
        }

        return fileTokens;
    }
    
    private void applyFilters() {
        try {
            for (String token : tokenMap.keySet()) {
                if (token.length() < minimumCharacters || token.length() > maximumCharacters) {
                        tokenMap.remove(token);
                }
            }
        } // @TODO remove line break before catch
        catch(Exception e) {} // @TODO appropriately handle exception
    }
    
    private void outputTokens() {
        // @TODO directly print foreach or use stringbuilder. Since no fx no return don't use var.
        String output = "";
        for (String token : tokenMap.keySet()) {
            output += token + "\n";
        }
        System.out.println(output);
    }
    
    public static void main(String[] args) {
        // #change error handling to check path validity and that args1&2 are integers. Basic error handling is a must.
        if (args.length != 3) {
            System.err.println("Expected three arguments: inputFolder minTokenLength maxTokenLength");
            System.exit(1);
        }
        File inputFolder = new File(args[0]);
        if (!inputFolder.exists() || !inputFolder.isDirectory()) {
            System.err.println("Args[0] is not a valid path to a folder.");
            System.exit(1);
        }
        int minTokenLength = 0, maxTokenLength = 0;
        try {
            minTokenLength = Integer.parseInt(args[1]);
            maxTokenLength = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Args[1] and Args[2] must be integers.");
            System.exit(1);
        }
        Tokenizer tokenizer = new Tokenizer(inputFolder, minTokenLength, maxTokenLength);
        tokenizer.run();
    }
}