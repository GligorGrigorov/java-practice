package bg.sofia.uni.fmi.mjt.tagger;

import java.io.Reader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.HashSet;

public class Tagger {

    private Map<String, String> cities;
    private Map<String, Integer> numberOfTags;
    private int allTagsCount;

    /**
     * Creates a new instance of Tagger for a given list of city/country pairs
     *
     * @param citiesReader a java.io.Reader input stream containing list of cities and countries
     *                     in the specified CSV format
     */
    public Tagger(Reader citiesReader) {
        cities = new HashMap<>();
        numberOfTags = new HashMap<>();
        allTagsCount = 0;
        readCities(citiesReader);
    }

    private void readCities(Reader citiesReader) {
        try (var reader = new BufferedReader(citiesReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                cities.put(parts[0].toLowerCase(), parts[1]);
                numberOfTags.put(parts[0].toLowerCase(), 0);
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Processes an input stream of a text file, tags any cities and outputs result
     * to a text output stream.
     *
     * @param text   a java.io.Reader input stream containing text to be processed
     * @param output a java.io.Writer output stream containing the result of tagging
     */
    public void tagCities(Reader text, Writer output) {
        try (var reader = new BufferedReader(text);) {
            try (var writer = new BufferedWriter(output)) {
                String line;
                if ((line = reader.readLine()) == null) {
                    return;
                }
                do {
                    String[] parts = line.split("[^A-Za-z]");
                    for (int i = 0; i < parts.length; i++) {
                        if (cities.containsKey(parts[i].toLowerCase())) {
                            line = line.replace(parts[i], createTag(parts[i]));
                            numberOfTags.put(parts[i].toLowerCase(), numberOfTags.get(parts[i].toLowerCase()) + 1);
                            ++allTagsCount;
                        }
                    }
                    writer.write(line);
                    if ((line = reader.readLine()) == null) {
                        break;
                    }
                    writer.newLine();
                    writer.flush();
                } while (true);
            } catch (IOException e1) {
                throw new RuntimeException();
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    String createTag(String city) {
        return "<city country=\""
                + cities.get(city.toLowerCase())
                + "\">"
                + city
                + "</city>";
    }

    /**
     * Returns a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If @n exceeds the total number of cities tagged, return as many as available
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @param n the maximum number of top tagged cities to return
     * @return a collection the top @n most tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getNMostTaggedCities(int n) {
        Map<String, Integer> tags = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int result = numberOfTags.get(o2).compareTo(numberOfTags.get(o1));
                if (result == 0) {
                    return o2.compareTo(o1);
                }
                return result;
            }
        });
        tags.putAll(numberOfTags);
        Collection<String> toReturn = new LinkedList<>();
        int i = 0;
        for (String tag :
                tags.keySet()) {
            if (i == n) {
                break;
            }
            if (numberOfTags.get(tag) > 0) {
                toReturn.add(tag.substring(0, 1).toUpperCase() + tag.substring(1).toLowerCase());
            }
            i++;
        }
        return toReturn;
    }

    /**
     * Returns a collection of all tagged cities' unique names
     * from the last tagCities() invocation. Note that if a particular city has been tagged
     * more than once in the text, just one occurrence of its name should appear in the result.
     * If tagCities() has not been invoked at all, return an empty collection.
     *
     * @return a collection of all tagged cities' unique names
     * from the last tagCities() invocation.
     */
    public Collection<String> getAllTaggedCities() {
        Collection<String> toReturn = new HashSet<>();
        for (String city :
                numberOfTags.keySet()) {
            if (numberOfTags.get(city) > 0) {
                toReturn.add(city.substring(0, 1).toUpperCase() + city.substring(1).toLowerCase());
            }
        }
        return toReturn;
    }

    /**
     * Returns the total number of tagged cities in the input text
     * from the last tagCities() invocation
     * In case a particular city has been taged in several occurences, all must be counted.
     * If tagCities() has not been invoked at all, return 0.
     *
     * @return the total number of tagged cities in the input text
     */
    public long getAllTagsCount() {
        return allTagsCount;
    }

}