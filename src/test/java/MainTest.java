import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void axioms() {
        String[] tokens = {"hello", "world", "peace"};
        assertTrue(tokens.length % 2 == 1);
        String[] tokens2 = {"united", "states", "of", "europe"};
        assertTrue(tokens2.length % 2 == 0);
    }

    @Test
    void testGraphContent() {
        Main.reset();
        try {
            Main.processFile(new File(MainTest.class.getClassLoader().getResource("./walkTestFile.txt").getFile()));
            String result = Main.getFileContent();
            assertTrue(result.contains("*Vertices 8"));
            assertTrue(result.contains("concordia"));
            assertTrue(result.contains("Europe"));
            assertTrue(result.contains("*arcs"));
            assertFalse(result.contains("9"));
        } catch (Exception e){
            e.printStackTrace();
            fail("Could not read from file. An exception occurred.");
        }
    }

    @Test
    void testFullRun() {
        String filePathToWrite = "./test_graph.net";
        Main.writeGraphFile(MainTest.class.getClassLoader().getResource("./walkTestFile.txt").getFile(), filePathToWrite);
        File writtenFile = new File(filePathToWrite);
        assertTrue(writtenFile.exists());
        writtenFile.delete();
    }

}