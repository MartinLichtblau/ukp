import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/*
Task Description:
    Hello UKP candidate.
    The following program should read an arbitrary number of files from a directory and store all their tokens in a member variable.
    The program may contain some bugs interfering with the desired functionality.
    Your tasks are:
    1. Understand the program and correct all errors that lead to erroneous behaviour. Comment your changes and shortly explain the reasons.
    2. The program also contains some questionable programming constructs (e.g. disregarding Java naming conventions, etc.).
        Try to find as many as you can, and correct them. Comment your changes and shortly explain the reasons.
    3. Add the missing JavaDocs at a level of detail that you consider as appropriate.
    4. Write a <b>single</b> method that <b>returns</b>
     4.1 the number of items in tokenMap
        #note: What's meant with items? I take items = types
     4.2 the average length (as double value) of the elements in tokenMap after calling applyFilters()
        #note: What is an element? I assume elements = tokens. #note: call applyFilters() right before that.
     4.3 the number of tokens starting with "a" (case sensitive).
    Output this information. #change: format 4. for better comprehension and to limit line length.
 */

/*
Overall Changes (that didn't belong in a single line):
    #change: line breaks and indents for consistency, better readability, and to adhere to style-guide.
    #change: use streams instead of bulk functions like listFiles to reduce resource usage when processing files and strings.
        Use java.nio.file instead of java.io.Files to.
    #change: make only public what is needed for usage, everything else should be private.
        Tokenizer constructor should be public for sure, as well as run() and getStats().
        FrequencyTable var itself shouldn't be public, but get a getter to retrieve the extracted tokens.
 */

/**
 *
 * @author zesch
 * @version 2.1
 * @since  2
 */

/**
 * Tokenizes files and provides additional statistics concerning frequency.
 */
public class Tokenizer { // #change: give classname and file same self-explanatory name and put version in doc.
    // #change: reorder modifier correctly and change charset/encoding to UTF-8, since it's de-facto standard.
    private static final String CHARSET = "UTF-8";

    private Path inputDir;
    private int minTokenLength; // #change: give self-explanatory names.
    private int maxTokenLength;
    // #change: use Map instead of Hashmap for higher versatility. Give fitting name.
    /** stores types of tokens as key and their occurrence count (frequency) as value. */
    public Map<String, Integer> frequencyTable = new HashMap<>();

    /**
     * Creates a {@link Tokenizer} for a given directory
     * @param inputDir {@link java.nio.file.Path Path} to directory containing files to be processed
     * @param minTokenLength tokens below this length are filtered out
     * @param maxTokenLength tokens above this length are filtered out
     */
    public Tokenizer(Path inputDir, int minTokenLength, int maxTokenLength) {
        // #change: do semantic checks in constructor and not in main, since it could be called directly.
        if (!Files.exists(inputDir) || !Files.isDirectory(inputDir)) { // #change: check if folder exits.
            System.err.println(inputDir + "is not a valid path to a directory/folder.");
            System.exit(1);
        }
        this.inputDir = inputDir; // #change: give params and field vars same name and use this.name for clarity. No prefixes!
        this.minTokenLength = minTokenLength;
        this.maxTokenLength = maxTokenLength;
        // #change: check that maxTokenLength is >= min
        if (minTokenLength == 0 || maxTokenLength == 0 || minTokenLength > maxTokenLength) {
            throw new IllegalArgumentException("Both min/max token length must me greater zero and max length >= min."); // @TODO error handling ....
        }
    }

    /**
     * Runs Tokenization on files, applies filters and outputs token types found.
     */
    public void run() {
        readFiles();
        applyFilters();
        outputTokens();
    }

    /**
     * Tokenizes all files in {@link #inputDir}.
     */
    private void readFiles() {
        // #change: total overwork to fix bugs, error handling, clarity, and performance.
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
                            // increment frequency if type already exists or add token if it's the first of it type.
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

    /**
     * Returns list of tokens extracted from given file.
     * @param filePath {@link java.nio.file.Path Path} of file to be processed
     * @return list of extracted tokens, including duplicates
     */
    private List<String> getFileTokens(Path filePath) {
        // #change: return token list instead of Hashmap, Path parameter instead of File, BufferedReader, checks ...
        List<String> tokenList = new ArrayList<>();
        String line;
        try (BufferedReader in = new BufferedReader(new FileReader(filePath.toFile(), Charset.forName(CHARSET)))) {
            while ((line = in.readLine()) != null) { // #note: could use Java8's .foreach() for parallelization
                tokenList.addAll(Arrays.asList(line.split(" "))); // extract tokens from text-line and add to list.
            }
        } catch (Exception e) {
            System.err.println("Couldn't get Tokens of file: " + filePath + "\n" + e);
            // @TODO do appropriate error handling
    }
        return tokenList;
    }

    /**
     * Applies filters on {@link #frequencyTable}
     */
    private void applyFilters() {
        // #change: alter frequencyTable only on if fully successful, otherwise could result in invalid state.
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

    /**
     * Outputs extracted Tokens from {@link #frequencyTable}
     */
    private void outputTokens() {
        // #change: use stringBuiler instead of += to improve performance by not creating new string objects in every loop.
        StringBuilder stringBuilder = new StringBuilder();
        for (String token : frequencyTable.keySet()) {
            stringBuilder.append(token);
            stringBuilder.append("\n");
        }
        System.out.println(stringBuilder);
    }

    /**
     * Returns {@link Stats} of processed texts based on {@link #frequencyTable}.
     */
    public Stats getStats() {
        // check frequencyTable since getStats() is public and could be called any time.
        if(frequencyTable == null || frequencyTable.size() == 0) {
            System.err.println("No stats since frequencyTable is empty. Run tokenizer.run() first.");
            return null;
        }
        int typeCount = frequencyTable.size(); // the number of types in frequencyTable = number rows in table.
        long tokenLengthProduct; // = length * frequency
        long totalLength = 0; // = combined length over all tokens
        long totalNumTokens = 0; // = number of token/instances found in all texts
        long aTokenCount = 0; // = number of tokens beginning with lowercase a
        double avgTokenLength; // = average length of all token/instances.
        applyFilters();
        for (Map.Entry<String, Integer> token : frequencyTable.entrySet()) {
            tokenLengthProduct = token.getKey().length() * token.getValue();
            totalLength += tokenLengthProduct;
            totalNumTokens += token.getValue();
            if(token.getKey().startsWith("a")) {
                aTokenCount += token.getValue();
            }
        }
        avgTokenLength = (double) totalLength / totalNumTokens;
        return new Stats(typeCount, avgTokenLength, aTokenCount);
    }

    /**
     * Container class holding various statistics about processed texts regarding tokenization
     */
    private class Stats {
        // #note: use a class because it's explicit, thus aids comprehension, and further metrics can be easily added.
        int typeCount;
        double avgTokenLength;
        long aTokenCount;

        private Stats(int typeCount, double avgTokenLength, long aTokenCount) {
            this.typeCount = typeCount;
            this.avgTokenLength = avgTokenLength;
            this.aTokenCount = aTokenCount;
        }

        @Override
        public String toString() {
            return "Tokenizer Stats:" +
                    " different types = " + typeCount +
                    ", average token length = " + avgTokenLength +
                    ", tokens beginning with a = " + aTokenCount;
        }
    }

    /**
     * Possible entry Point for processing inputDir with {@link Tokenizer}
     * @param args ("inputDir", "minTokenLength", "maxTokenLength")
     */
    public static void main(String[] args) {
        // #change: basic validity checks of arguments.
        if (args.length != 3) {
            System.err.println("Expected three arguments: inputFolder minTokenLength maxTokenLength");
            System.exit(1);
        }
        Path inputDir = Paths.get(args[0]);
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