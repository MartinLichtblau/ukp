import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
 * 4.1 the number of items in tokenMap
 *      #note: What's meant with items? I take items = types
 * 4.2 the average length (as double value) of the elements in tokenMap after calling applyFilters()
 *      #note: What is an element? I assume elements = tokens
 * 4.3 the number of tokens starting with "a" (case sensitive).
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
    // #change: reorder modifier correctly and change charset/encoding to UTF-8, since it's defacto standard and ISO-8859-1 is dead.
    private static final String CHARSET = "UTF-8";

    private Path inputDir;
    private int minTokenLength; // #change: give descriptive names
    private int maxTokenLength;
    // #change: use Map instead of Hashmap since it's more versatile and give variable better name.
    // frequencyTable<type/token, frequency/count> #change: add explanation for central variable.
    private Map<String, Integer> frequencyTable = new HashMap<>();

    // @TODO Constructor needs to be public?
    // #change parameter names to match instance vars. Don't use prefixes (Hungarian Notation).
    // #change use string instead of file, to makes things cleaner and caller doesn't have to import io.file just for that.
    // #change do checks in constructor and not in main.
    public Tokenizer(String dir, int minTokenLength, int maxTokenLength) {
        Path inputDir = Paths.get(dir);
        if (!Files.exists(inputDir) || !Files.isDirectory(inputDir)) {
            System.err.println("Args[0] is not a valid path to a directory/folder.");
            System.exit(1);
        }
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

    // readFiles is the gate that makes sure that FT contains tokens.
    private void readFiles() {
        // #change: use Stream instead of listFiles for better performance>memory.
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputDir)) { // use try/catch to autoclose stream.
            for (Path path : stream) {
                if(Files.isDirectory(path)) {
                    System.err.println("Subdirectories are not allowed.");
                } else {
                    List<String> tokens = getFileTokens(path);
                    if (tokens.isEmpty()) {
                        System.out.println("No tokens found in file: " + path);
                    } else {
                        for (String token : tokens) {   // #change: merge tokens of files in frequencyTable.
                            // add new token or increment frequency of existing
                            frequencyTable.put(token, frequencyTable.getOrDefault(token, 0) + 1);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("An I/O problem has occurred while reading files." + ex);
        }
        // #change: exit directly if no tokens found in inputDir in order not to do null-checks in every downstream method.
        if(frequencyTable == null || frequencyTable.size() == 0) {
            System.err.println("No tokens found in directory: " + inputDir);
            System.exit(1);
        }
    }

    // #change: return String[] instead of Hashmap, path parameter instead of File, BufferedReader, checks, functionality, ...
    // Simply return token array for a given file.
    private List<String> getFileTokens(Path filePath) {
        List<String> tokenList = new ArrayList<>();
        String line;
        try (BufferedReader in = new BufferedReader(new FileReader(filePath.toFile(), Charset.forName(CHARSET)))) {
            while ((line = in.readLine()) != null) { // #note: could use Java8's .foreach() for parallelization
                tokenList.addAll(Arrays.asList(line.split(" "))); // split text-line in tokens and add to list.
            }
        } catch (Exception e) {
            System.err.println("Couldn't get Tokens of file: " + filePath + "\n" + e);
            // @TODO do appropriate error handling
        }
        return tokenList;
    }
    
    private void applyFilters() {
        // #change: alter frequencyTable only on success of all filters for all entries. Becomes relevant with more complex filters.
        Map<String, Integer> filteredFT = new HashMap<>();
        try {
            for (Map.Entry<String, Integer> entry : frequencyTable.entrySet()) {
                int tokenLength = entry.getKey().length();
                if (tokenLength >= minTokenLength && tokenLength <= maxTokenLength) {
                    filteredFT.put(entry.getKey(), entry.getValue());
                }
            }
            frequencyTable = filteredFT;
        } catch(Exception e) {
            System.err.println("Filters were not applied because: " + e);
        }  // @TODO appropriately handle exception
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
        // #change: check FT since getStats() is public and can be called any time.
        if(frequencyTable == null || frequencyTable.size() == 0) {
            System.err.println("No stats because no tokens in table. Run tokenizer.run() first.");
            return null;
        }
        applyFilters();
        int typeCount = frequencyTable.size(); // the number of types in FT (see Type/Token distinction) = rows in table.
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
        return new Stats(typeCount, avgTokenLength, numATokens);
    }

    // use a class because it's explicit, thus aids comprehension, and further metrics can be easily added.
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
                    ", numATokens=" + numATokens;
        }
    }
    
    public static void main(String[] args) {
        // #change error handling to check path validity and that args1&2 are integers. Basic error handling is a must.
        if (args.length != 3) {
            System.err.println("Expected three arguments: inputFolder minTokenLength maxTokenLength");
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
        Tokenizer tokenizer = new Tokenizer(args[0], minTokenLength, maxTokenLength);
        tokenizer.run();
        // #change: get statistics as in task 4. and output them.
        Stats stats = tokenizer.getStats();
        System.out.println(stats);
    }
}