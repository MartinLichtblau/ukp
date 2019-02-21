import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
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
 */
public class ProgrammingSkillsTest {
    
    public final static String CHARSET = "ISO-8859-1";

    public File inputDir;
    
    public double nrofFiles;
    
    public int minimumCharacters;
    public int maximumCharacters;
    
    HashMap<String, Integer> tokenMap; 
    
    public ProgrammingSkillsTest(File pInputDir, int pMinChars, int pMaxChars) {
        
        inputDir = pInputDir;
        minimumCharacters = pMinChars;
        maximumCharacters = pMaxChars;
        
        if ((pMinChars == 0) || (pMaxChars == 0)) {
            throw new RuntimeException("Configuration parameters have not been correctly initialized.");
        }
    }
    
    public void run() {
        readFiles();
        applyFilters();
        outputTokens();
    }
    
    private void readFiles() {
        
        File[] files = inputDir.listFiles();
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
            
            tokenMap = getFileTokens(file);
        }
    }
    
    private HashMap<String, Integer> getFileTokens(File infile) {
        
        HashMap<String, Integer> fileTokens = new HashMap<String, Integer>();
        
        BufferedReader in;
        String line;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(infile), CHARSET));
            while ((line = in.readLine()) != null) {
                String lineParts[] = line.split(" ");
                for (String part : lineParts) {
                    if (fileTokens.containsKey(part)) {
                        fileTokens.put(part, fileTokens.get(part) + 1);
                    }
                    else {
                        fileTokens.put(part, 1);
                    }    
                }
            }
            in.close();
        } catch (Exception e) {
            System.err.println(e);
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
        }
        catch(Exception e) {}
    }
    
    private void outputTokens() {
        String output = "";
        for (String token : tokenMap.keySet()) {
            output += token + "\n";
        }
        System.out.println(output);
    }
    
    public static void main(String[] args) {
        if (args.length != 3) {
            System.err.println("Wrong number of parameters: java <application> <indir> <minChars> <maxChars>");
            System.exit(1);
        }

        File inputDir = new File(args[0]);
        int minChars = new Integer(args[1]);
        int maxChars = new Integer(args[2]);
        
        ProgrammingSkillsTest pst = new ProgrammingSkillsTest(inputDir, minChars, maxChars);
        pst.run();
    }
}