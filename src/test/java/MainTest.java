import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
    void mainWithDerivedGraphFile(){
        try {
            Main.reset();
            String walkFile = new File(MainTest.class.getClassLoader().getResource("./walkTestFile.txt").getFile()).getAbsolutePath();
            Main.main(new String[]{"-walks", walkFile});
            File generatedFile = new File(new File(walkFile).getParent() + File.separator +  "graph.nt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(generatedFile), StandardCharsets.UTF_8));
            int numberOfLines = 0;
            String content = "";
            String readLine;
            while((readLine = reader.readLine()) != null){
                content += readLine + "\n";
                numberOfLines++;
            }
            assertTrue(numberOfLines > 1);
            assertTrue(content.contains("*Vertices 8"));
            assertTrue(content.contains("concordia"));
            assertTrue(content.contains("Europe"));
            assertTrue(content.contains("*arcs"));
            assertFalse(content.contains("9"));
            reader.close();
            generatedFile.delete();
        } catch (Exception e){
            e.printStackTrace();
            fail("An exception occurred.");
        } finally {
            String walkFile = new File(MainTest.class.getClassLoader().getResource("./walkTestFile.txt").getFile()).getAbsolutePath();
            File generatedFile = new File(new File(walkFile).getParent() + File.separator +  "graph.nt");
            generatedFile.delete();
        }
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

    @Test
    void getHelp(){
        String result = Main.getHelp();
        assertNotNull(result);
        System.out.println(result);
    }

    @Test
    public void containsIgnoreCase() {
        assertTrue(Main.containsIgnoreCase("hello", new String[]{"hello", "world"}));
        assertTrue(Main.containsIgnoreCase("HELLO", new String[]{"hello", "world"}));
        assertFalse(Main.containsIgnoreCase("Europa", new String[]{"hello", "world"}));
        assertFalse(Main.containsIgnoreCase(null, null));
    }

    @Test
    public void getValue() {
        assertNull(Main.getValue(null, null));
        assertNull(Main.getValue(null, new String[]{"european", "union"}));
        assertNull(Main.getValue("hello", null));
        assertNull(Main.getValue("-hello", new String[]{"european", "union"}));
        assertEquals("union", Main.getValue("-european", new String[]{"-european", "union"}));
    }

    @Test
    void runMainWithInsufficientArguments(){
        // just making sure that there are no exceptions.
        try {
            Main.main(null);
            Main.main(new String[]{"-helloWorld"});
        } catch (Exception e){
            e.printStackTrace();
            fail("Exception thrown.");
        }
    }

}