package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class NaiveSpellCheckerTest {
    private SpellChecker spellChecker;
    private final String text = "Dictionary    an testin " + System.lineSeparator() + " cake in phone";
    private final String dictionaryExample = "dictionary" + System.lineSeparator()
            + "dictat" + System.lineSeparator()
            + "k  " + System.lineSeparator()
            + "book" + System.lineSeparator()
            + "booKsHElf" + System.lineSeparator()
            + "cake" + System.lineSeparator()
            + "phone" + System.lineSeparator()
            + " @#$%" + System.lineSeparator()
            + "teSt" + System.lineSeparator()
            + "testing^--'" + System.lineSeparator()
            + "staR" + System.lineSeparator()
            + "-#@starbucks4#@$" + System.lineSeparator()
            + "superSTAR";
    private final String stopWordsExample = "in" + System.lineSeparator()
            + "AND" + System.lineSeparator()
            + "A" + System.lineSeparator()
            + "an" + System.lineSeparator()
            + "TO" + System.lineSeparator();

    @Before
    public void setup() {
        try (Reader stopWordsReader = new BufferedReader(new StringReader(stopWordsExample));
             Reader dictionaryReader = new BufferedReader(new StringReader(dictionaryExample))) {
            spellChecker = new NaiveSpellChecker(dictionaryReader, stopWordsReader);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from files", e);
        }
    }

    @Test
    public void findClosestWordsOnThreeClosestWordsTest() {
        List<String> closestWords = spellChecker.findClosestWords("starting", 3);
        assertEquals(3, closestWords.size());
        assertEquals(0, closestWords.get(0).compareTo("star"));
        assertEquals(0, closestWords.get(1).compareTo("testing"));
        assertEquals(0, closestWords.get(2).compareTo("superstar"));
    }

    @Test
    public void findClosestWordsOnBigNTest() {
        List<String> closestWords = spellChecker.findClosestWords("starting", Integer.MAX_VALUE);
        final int DICTIONARY_SIZE = 11;
        assertEquals(DICTIONARY_SIZE, closestWords.size());
    }

    @Test
    public void metadataTest() {
        try (Reader reader = new StringReader(text)) {
            Metadata metadata = spellChecker.metadata(reader);
            assertEquals(29, metadata.characters());
            assertEquals(4, metadata.words());
            assertEquals(1, metadata.mistakes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void analyzeTest() {
        try (Reader reader = new StringReader(text);
             Writer writer = new StringWriter()) {
            spellChecker.analyze(reader, writer, 2);
            String output = writer.toString();
            assertEquals(text, output.split("= = = Metadata = = =")[0].substring(0, text.length()));
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading or writing", e);
        }
    }
}
