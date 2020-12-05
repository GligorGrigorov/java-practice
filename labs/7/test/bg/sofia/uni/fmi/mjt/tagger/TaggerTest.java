package bg.sofia.uni.fmi.mjt.tagger;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Collection;

public class TaggerTest {
    private Tagger tagger;
    private static final String TEXT = "Plovdiv's old town is a major ShaHraK tourist attraction."
            + " It plovdiv is the second largest city " + System.lineSeparator()
            + "in Bulgaria, after the capital ,Sofia.";
    private static final String TAGGED_TEXT = "<city country=\"Bulgaria\">Plovdiv</city>'s old town is a"
            + " major <city country=\"Afghanistan\">ShaHraK</city> tourist attraction. It <city country=\""
            + "Bulgaria\">plovdiv</city> is the second largest city "
            + System.lineSeparator() + "in Bulgaria, after the capital ,<city country=\"Bulgaria\">Sofia</city>.";

    private static final String WORLD_CITIES = "Plovdiv,Bulgaria" + System.lineSeparator() + "Sofia,Bulgaria"
            + System.lineSeparator() + "Shahrak,Afghanistan";
    private static final String MOST_TAGGED_CITY = "Plovdiv";
    private static final String CITY_SOFIA = "Sofia";
    private static final String CITY_SHAHRAK = "Shahrak";

    @Before
    public void setup() throws IOException {
        try (Reader reader = new StringReader(WORLD_CITIES);) {
            tagger = new Tagger(reader);
        }
    }

    @Test
    public void tagCitiesTest() throws IOException {
        try (Reader textReader = new StringReader(TEXT);) {
            try (Writer outputWriter = new StringWriter();) {
                tagger.tagCities(textReader, outputWriter);
                assertEquals(TAGGED_TEXT, outputWriter.toString());
            }
        }
    }

    @Test
    public void getNMostTaggedCitiesTest() throws IOException {
        try (Reader textReader = new StringReader(TEXT);) {
            try (Writer outputWriter = new StringWriter();) {
                tagger.tagCities(textReader, outputWriter);
                Collection<String> cities = tagger.getNMostTaggedCities(1);
                assertEquals(1, cities.size());
                assertTrue(cities.contains(MOST_TAGGED_CITY));
            }
        }
    }

    @Test
    public void getAllTaggedCitiesTest() throws IOException {
        try (Reader textReader = new StringReader(TEXT);) {
            try (Writer outputWriter = new StringWriter();) {
                tagger.tagCities(textReader, outputWriter);
                Collection<String> cities = tagger.getAllTaggedCities();
                assertEquals(3, cities.size());
                assertTrue(cities.contains(MOST_TAGGED_CITY));
                assertTrue(cities.contains(CITY_SHAHRAK));
                assertTrue(cities.contains(CITY_SOFIA));
            }
        }
    }

    @Test
    public void getAllTagsCount() throws IOException {
        try (Reader textReader = new StringReader(TEXT);) {
            try (Writer outputWriter = new StringWriter();) {
                tagger.tagCities(textReader, outputWriter);
                long count = tagger.getAllTagsCount();
                assertEquals(4, count);
            }
        }
    }
}
