import java.io.BufferedReader; // import only what's necessary, to make it explicit, performance, for IDE usage.
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
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
 * #change formatting for better comprehension and to reduce line length (style-guide)
 * 4. Write a <b>single</b> method that <b>returns</b>
 *      - the number of items in tokenMap #change: now called frequencyTable
 *      - the average length (as double value) of the elements in frequencyTable after calling applyFilters()
 *      - the number of tokens starting with "a" (case sensitive).
 *    Output this information.
 *
 * @author zesch
 * @version 2 #change filename to match class, put version in doc and use version control for keeping track.
 */

/*

    @TODO put all overall changes in separate block
    Definitions:
        style-guide = Google Java Style Guide

    #change line breaks and indents for consistency, better readability, and to adhere to style-guide.
    #change: make everything private beside constructor, run(), and getStats().
 */


public class Tokenizer { // #change classname to express what it does.
    // #change modifier order. #change charset/encoding to UTF-8, since it's defacto standard and ISO-8859-1 is dead.
    private static final String CHARSET = "UTF-8";

    private File inputDir;
    private int minTokenLength; // #change: give descriptive names
    private int maxTokenLength;
    // frequencyTable<token, frequency/count> #change: add explanation for central variable.
    // #change: use Map instead of Hashmap since it's more versatile and call var for what it is.
    private Map<String, Integer> frequencyTable;

    // @TODO Constructor needs to be public?
    // #change parameter names to match instance vars. Don't use prefixes (Hungarian Notation).
    public Tokenizer(File inputDir, int minTokenLength, int maxTokenLength) {
        this.inputDir = inputDir; // #change: use this.name for clarification.
        this.minTokenLength = minTokenLength;
        this.maxTokenLength = maxTokenLength;
        // #change: check that maxTokenLength is >= min
        if (minTokenLength == 0 || maxTokenLength == 0 || minTokenLength > maxTokenLength) {
            throw new RuntimeException("Both min/max token length must me greater zero and max length >= min."); // @TODO error handling ....
        }
    }
    
    public void run() {
        readFiles();
        applyFilters();
        outputTokens();
    }
    
    private void readFiles() {
        // @TODO consider subfolders // If Java 8 I would use Files.walk(). With Java 7 Files.newDirectoryStream()
        File[] files = inputDir.listFiles();
        if (files == null || files.length == 0) { // #change: check length to detect if folder empty.
            System.err.println("No files found in directory: " + inputDir.getAbsolutePath()); // #change: Better wording.
            System.exit(1);
        }
        for (int i = 0; i < files.length; i++) {
            File file = new File(files[i].getAbsoluteFile().toString());
            if (file.length() == 0) {
                System.out.println("Skipping emtpy file " + file.getAbsolutePath());
                continue;
            }
            System.out.println(file.getAbsolutePath());
            frequencyTable = getFileTokens(file); // @TODO merge existing tokens with new tokens. ! same tokens
        }
    }

    // @TODO put tokens directly in frequencyTable? Would save much effort.
    private Map<String, Integer> getFileTokens(File infile) {
        Map<String, Integer> fileTokens = new HashMap<String, Integer>();
        BufferedReader in;
        String line;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(infile), CHARSET));
            while ((line = in.readLine()) != null) {
                String lineParts[] = line.split(" "); // split text in tokens @TODO rename parts into tokens
                for (String part : lineParts) {
                    if (fileTokens.containsKey(part)) {
                        fileTokens.put(part, fileTokens.get(part) + 1); // increment occurrence count @TODO use replace to make it specific
                    } else {
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
        // #change: alter frequencyTable only on success of all filters for all entries. Becomes relevant with more complex filters.
        Map<String, Integer> filteredFT = new HashMap<>();
        try {
            for (Map.Entry<String, Integer> token : frequencyTable.entrySet()) {
                int tokenLength = token.getKey().length();
                if (tokenLength >= minTokenLength && tokenLength <= maxTokenLength) {
                    filteredFT.put(token.getKey(), token.getValue());
                }
            }
        } catch(Exception e) {
            System.err.println("Couldn't apply filters because: " + e);
        } finally { // @TODO appropriately handle exception
            frequencyTable = filteredFT;
        }
    }
    
    private void outputTokens() {
        // #change: use stringBuiler instead of += to improve performance by not creating new string objects in every loop.
        StringBuilder stringBuilder = new StringBuilder();
        for (String token : frequencyTable.keySet()) {
            stringBuilder.append(token);
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder);
    }

    public Stats getStats() {
        applyFilters();
        int numDistinctTokens = frequencyTable.size();
        long tokenCount = 0;
        long textLengthSum = 0;
        double avgTokenLength;
        int numATokens = 0;
        for (Map.Entry<String, Integer> token : frequencyTable.entrySet()) {
            long tokenLengthSum = token.getKey().length() * token.getValue();
            textLengthSum += tokenLengthSum;
            tokenCount += token.getValue();
            if(token.getKey().startsWith("a")) {
                numATokens++; // Make type token distinction
            }
        }
        avgTokenLength = textLengthSum / tokenCount;
        return new Stats(numDistinctTokens, avgTokenLength, numATokens);
    }

    private class Stats {
        int numDistinctTokens;
        double avgTokenLength;
        int numATokens;

        public Stats(int numDistinctTokens, double avgTokenLength, int numATokens) {
            this.numDistinctTokens = numDistinctTokens;
            this.avgTokenLength = avgTokenLength;
            this.numATokens = numATokens;
        }

        @Override
        public String toString() {
            return "Tokenizer Stats: " +
                    "numDistinctTokens=" + numDistinctTokens +
                    ", avgTokenLength=" + avgTokenLength +
                    ", numATokens=" + numATokens +
                    '}';
        }
    }
    
    public static void main(String[] args) {
        // #change error handling to check path validity and that args1&2 are integers. Basic error handling is a must.
        if (args.length != 3) {
            System.err.println("Expected three arguments: inputFolder minTokenLength maxTokenLength");
            System.exit(1);
        }
        File inputDir = new File(args[0]); // @TODO perhaps move checks to constructor
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            System.err.println("Args[0] is not a valid path to a directory/folder.");
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
        Tokenizer tokenizer = new Tokenizer(inputDir, minTokenLength, maxTokenLength);
        tokenizer.run();
        Stats stats = tokenizer.getStats();
        System.out.println(stats);
    }
}