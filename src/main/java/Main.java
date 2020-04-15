import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.GZIPInputStream;

public class Main {

    public static void main(String[] args) {
        String walkDirectoryOrFile = "/Users/janportisch/Documents/PhD/ISWC_2020/WalksLight/RandomMidWalks/DocumentSimilarityLP50/walk_file.gz";
        String fileToWrite = "/Users/janportisch/Documents/PhD/ISWC_2020/WalksLight/RandomMidWalks/DocumentSimilarityLP50/LP50_graph.net";

        writeGraphFile(walkDirectoryOrFile, fileToWrite);
        System.out.println("Done");
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

    private static int nextId = 0;

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
     * For testing purposes. Resets all variables.
     */
    public static void reset(){
        uriIdMapping = new HashMap<>();
        edges = new HashSet<>();
        nextId = 0;
    }

}
