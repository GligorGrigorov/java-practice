import bg.sofia.uni.fmi.mjt.tagger.Tagger;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException, IOException {
        Path cities = Path.of("world-cities.csv");
        try (Reader reader = Files.newBufferedReader(cities)) {
            Tagger t = new Tagger(reader);
            Path text = Path.of("text.txt");
            Path output = Path.of("o1.txt");
            try (Reader textReader = Files.newBufferedReader(text)){
                try (Writer writer = Files.newBufferedWriter(output)){
                    t.tagCities(textReader,writer);
                    Collection<String> c = t.getAllTaggedCities();
                    Collection<String> c1 = t.getNMostTaggedCities(2);
                    for (String city:
                         c) {
                        System.out.println(city);
                    }
                    System.out.println("--------------------------");
                    for (String city:
                            c1) {
                        System.out.println(city);
                    }
                    System.out.println("--------------------------");
                    long count = t.getAllTagsCount();
                    System.out.println(count);

                }
            }

        }
    }
}
