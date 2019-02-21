import java.io.BufferedReader; // import only what's necessary, to make it explicit, performance, for IDE usage.
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections; // @TODO remove unused imports
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
public class ProgrammingSkillsTest { // @TODO this is a tokenizer, should call it such. But am I allowed to change classname?
    // @TODO remove line spacing between variables
    public static final String CHARSET = "ISO-8859-1"; // #style #change order of modifiers

    public File inputDir;
    
    public double nrofFiles; // @TODO remove unused code in general
    
    public int minimumCharacters; // @TODO rename to minTokenLength and max ... Reason naming should be consistent and concise.
    public int maximumCharacters;
    
    HashMap<String, Integer> tokenMap; // @TODO add variable description. What is the string and integer
    // @TODO the only var that could be public
    // @TODO use map instead of HashMap for more freedom without a downside.
    
    public ProgrammingSkillsTest(File pInputDir, int pMinChars, int pMaxChars) {
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
        if (args.length != 3) {
            System.err.println("Wrong number of parameters: java <application> <indir> <minChars> <maxChars>"); // @TODO seems strange....
            System.exit(1);
        }

        // @TODO don't use vars, use directly as parameters
        File inputDir = new File(args[0]);
        int minChars = new Integer(args[1]);
        int maxChars = new Integer(args[2]);
        
        ProgrammingSkillsTest pst = new ProgrammingSkillsTest(inputDir, minChars, maxChars);
        pst.run();
    }
}