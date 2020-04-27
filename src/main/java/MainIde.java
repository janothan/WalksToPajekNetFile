/**
 * Intended for usage within the IDE.
 */
public class MainIde {

    public static void main(String[] args) {
        String walkDirectoryOrFile = "/Users/janportisch/Documents/PhD/ISWC_2020/WalksLight/RandomMidWalks/DocumentSimilarityLP50/walk_file.gz";
        String fileToWrite = "/Users/janportisch/Documents/PhD/ISWC_2020/WalksLight/RandomMidWalks/DocumentSimilarityLP50/LP50_graph.net";

        Main.writeGraphFile(walkDirectoryOrFile, fileToWrite);
        System.out.println("Done");

    }

}
