import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Intended for command line usage.
 */
public class Main {

    public static void main(String[] args) {
        if(args == null || args.length > 2){
            System.out.println("Invalid arguments.\n\n\n" + getHelp());
            return;
        }
        if (containsIgnoreCase("-help", args) || containsIgnoreCase("--help", args) || containsIgnoreCase("-h", args)) {
            System.out.println(getHelp());
            return;
        }
        String walkDirectory = getValue("-walks", args);
        if(walkDirectory == null){
            System.out.println("Missing '-walks' argument.\n\n\n" + getHelp());
            return;
        }
        writeGraphFile(walkDirectory, getValue("-fileToWrite", args));
    }

    //---------------------------------------------------------------------------
    // You do not need to set anything below this point.
    //---------------------------------------------------------------------------

    /**
     * Map from URI to identifier.
     */
    private static HashMap<String, Integer> uriIdMapping = new HashMap<>();

    /**
     * Edges where one edge is defined by two node ids.
     */
    private static HashSet<IntegerTuple> edges = new HashSet<>();

    /**
     * The next ID to be used.
     * Access this variable through {@link Main#getNextId()}.
     */
    private static int nextId = 0;

    /**
     * Returns incremented id.
     * @return Incremented id. Prints a warning when the maximum id is accessed.
     */
    private static int getNextId() {
        nextId += 1;
        if (nextId == Integer.MAX_VALUE) System.out.println("ERROR: The maximum integer value has been reached.");
        return nextId;
    }

    public static void writeGraphFile(String walkDirectoryOrFile, String fileToWrite) {
        File walkFile = new File(walkDirectoryOrFile);
        if (walkFile.exists() == false) {
            System.out.println("ERROR: The specified file does not exist.");
            return;
        }

        // set file to write if null
        if(fileToWrite == null){
            System.out.println("File to write not set. Writing graph.nt into walk directory.");
            if(walkFile.isDirectory()){
                fileToWrite = (Paths.get(walkFile.getAbsolutePath(), File.separator + "graph.nt")).toString();
            } else {
                fileToWrite = (Paths.get(walkFile.getParent(), File.separator + "graph.nt")).toString();
            }
        }

        if (walkFile.isDirectory()) {
            for (File file : walkFile.listFiles()) {
                processFile(file);
            }
        } else processFile(walkFile);

        String contentToWrite = getFileContent();
        writeContentToFile(contentToWrite, fileToWrite);
    }

    /**
     * Process a single file, i.e. add components to internal data structures.
     *
     * @param singleWalkFile The file to be processed. The file can be gzipped.
     */
    static void processFile(File singleWalkFile) {
        if (singleWalkFile.isDirectory()) return;
        BufferedReader reader;

        try {
            if (singleWalkFile.getName().endsWith(".gz")) {
                GZIPInputStream gzip = new GZIPInputStream(new FileInputStream(singleWalkFile));
                reader = new BufferedReader(new InputStreamReader(gzip, StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(singleWalkFile), StandardCharsets.UTF_8));
            }

            String readLine;

            nextLine:
            while ((readLine = reader.readLine()) != null) {
                String[] tokens = readLine.split("\\s");
                String previousToken = null;
                for (String token : tokens) {
                    processToken(token);
                    if (previousToken == null) {
                        previousToken = token;
                    } else {
                        addEdge(previousToken, token);
                        previousToken = token;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Problem processing file: " + singleWalkFile.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private static void processToken(String token) {
        if (uriIdMapping.containsKey(token)) return;
        uriIdMapping.put(token, getNextId());
    }

    private static void addEdge(String node1, String node2) {
        edges.add(new IntegerTuple(uriIdMapping.get(node1), uriIdMapping.get(node2)));
    }

    /**
     * Get the file content given filled meta structures.
     *
     * @return File content.
     */
    static String getFileContent() {
        StringBuffer resultBuffer = new StringBuffer();
        int numberOfVertices = uriIdMapping.size();
        resultBuffer.append("*Vertices " + numberOfVertices + "\n");
        for (Map.Entry<String, Integer> entry : entriesSortedByValues(uriIdMapping)) {
            resultBuffer.append(entry.getValue() + " \"" + entry.getKey() + "\"\n");
        }
        resultBuffer.append("*arcs\n");
        for (IntegerTuple tuple : edges) {
            resultBuffer.append(tuple.integer_1 + " " + tuple.integer_2 + "\n");
        }
        return resultBuffer.toString();
    }

    private static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1; // Special fix to preserve items with equal values
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }

    /**
     * Write the content to the specified file.
     * @param content Content to be written.
     * @param fileToWritePath File to be created.
     */
    private static void writeContentToFile(String content, String fileToWritePath) {
        File fileToWrite = new File(fileToWritePath);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileToWrite), "UTF-8"));
            writer.write(content);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("There was a problem writing the file specified: " + fileToWritePath);
            e.printStackTrace();
        }
    }

    /**
     * Helper method.
     *
     * @param key       Arg key.
     * @param arguments Arguments as received upon program start.
     * @return Value of argument if existing, else null.
     */
    public static String getValue(String key, String[] arguments) {
        if (arguments == null) return null;
        int positionSet = -1;
        for (int i = 0; i < arguments.length; i++) {
            if (arguments[i].equalsIgnoreCase(key)) {
                positionSet = i;
                break;
            }
        }
        if (positionSet != -1 && arguments.length >= positionSet + 1) {
            return arguments[positionSet + 1];
        } else return null;
    }

    /**
     * Check whether {@code element} is contained in {@code array}.
     *
     * @param element The element that shall be looked for.
     * @param array   The array in which shall be looked for the element.
     * @return True if {@code element} is contained in {@code array}, else false.
     */
    public static boolean containsIgnoreCase(String element, String[] array) {
        if(element == null || array == null) return false;
        for (String s : array) {
            if (element.equalsIgnoreCase(s)) return true;
        }
        return false;
    }



    /**
     * Help text as string.
     * @return Help text.
     */
    public static String getHelp(){
        return  "WalksToPajektNetFile Help\n" +
                "-------------------------\n\n" +
                "-walks <walk_file_or_walk_directory>\n" +
                "\t[Required parameter] Path to the walk directory or the walk file.\n\n" +
                "-fileToWrite <file_to_write>\n" +
                "\t[optional parameter] The file that will be written.";
    }

    /**
     * For testing purposes. Resets all variables.
     */
    public static void reset(){
        uriIdMapping = new HashMap<>();
        edges = new HashSet<>();
        nextId = 0;
    }

}
