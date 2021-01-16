package bg.sofia.uni.fmi.mjt.spellchecker;

import java.io.Writer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NaiveSpellChecker implements SpellChecker {

    private final Set<String> stopWords;
    private final Set<String> dictionary;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopWordsReader) {
        if (dictionaryReader == null || stopWordsReader == null) {
            throw new IllegalArgumentException("Some of readers are null");
        }
        dictionary = processWords(dictionaryReader, "dictionary");
        stopWords = processWords(stopWordsReader, "stopWords");
    }

    private Set<String> processWords(Reader reader, String type) {
        Set<String> processedWords = new HashSet<>();
        try (BufferedReader bufferedReader = new BufferedReader(reader)) {
            Set<String> words = bufferedReader.lines()
                    .map(String::toLowerCase)
                    .map(String::strip)
                    .collect(Collectors.toSet());
            if (type.compareTo("stopWords") == 0) {
                return words;
            }
            for (String word : words) {
                List<Character> firstWord = new LinkedList<>();
                for (char ch : word.toCharArray()) {
                    firstWord.add(ch);
                }
                List<Character> m = firstWord.stream()
                        .dropWhile(x -> !Character.isLetterOrDigit(x))
                        .collect(Collectors.toList());
                Collections.reverse(m);
                List<Character> secondWord = m.stream().dropWhile(x -> !Character.isLetterOrDigit(x))
                        .collect(Collectors.toList());
                Collections.reverse(secondWord);
                StringBuilder sb = new StringBuilder();
                for (Character ch :
                        secondWord) {
                    sb.append(ch);
                }
                String finalWord = sb.toString();
                if (finalWord.length() > 1) {
                    processedWords.add(finalWord);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from reader", e);
        }
        return processedWords;
    }

    private boolean isNonAlphaNumeric(String word) {
        return word.chars().filter(Character::isLetterOrDigit).count() != 0;
    }

    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        if (suggestionsCount < 0) {
            throw new IllegalArgumentException("Suggestions count is negative");
        }
        if (textReader == null || output == null) {
            throw new IllegalArgumentException("Reader of writer are null");
        }
        try (BufferedReader reader = new BufferedReader(textReader);
             PrintWriter writer = new PrintWriter(output);
             PrintWriter sWriter = new PrintWriter(new StringWriter())) {
            StringBuffer sb = new StringBuffer();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
                sb.append(System.lineSeparator());
            }
            String text = sb.toString();
            writer.printf("%s", text);
            try (Reader sReader = new BufferedReader(new StringReader(text))) {
                Metadata metadata = metadata(sReader);
                writer.println("= = = Metadata = = =");
                writer.println(metadata.characters() + " characters, "
                        + metadata.words() + " words, "
                        + metadata.mistakes() + " spelling issue(s) found");
            }
            try (BufferedReader sReader = new BufferedReader(new StringReader(text))) {
                writer.println("= = = Findings = = =");
                String line;
                int lineNumber = 1;
                while ((line = sReader.readLine()) != null) {
                    final int number = lineNumber;
                    Arrays.stream(line.split("[^a-zA-Z0-9]"))
                            .filter(x -> !x.isBlank())
                            .filter(x -> x.length() > 1)
                            .filter(x -> !stopWords.contains(x.toLowerCase()))
                            .filter(x -> !dictionary.contains(x.toLowerCase()))
                            .forEach(x -> writer.println("Line #"
                                    + number + ", {" + x
                                    + "} - Possible suggestions are {"
                                    + String.join(",", findClosestWords(x, suggestionsCount)) + "}"));
                    lineNumber++;
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while analyzing text", e);
        }
    }

    @Override
    public Metadata metadata(Reader textReader) {
        String input = "";
        try (BufferedReader reader = new BufferedReader(textReader)) {
            StringBuilder sb = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                sb.append(str);
                sb.append(System.lineSeparator());
            }
            input = sb.toString();
            long words = Arrays.stream(input.split("[^a-zA-Z0-9]"))
                    .filter(x -> x.length() > 0)
                    .filter(x -> !stopWords.contains(x.toLowerCase()))
                    .filter(this::isNonAlphaNumeric)
                    .count();
            long chars = input.chars()
                    .filter(x -> !Character.isWhitespace(x))
                    .count();
            long issues = Arrays.stream(input.split("[^a-zA-Z0-9]"))
                    .filter(x -> !x.isBlank())
                    .filter(x -> x.length() > 0)
                    .filter(x -> !stopWords.contains(x.toLowerCase()))
                    .filter(this::isNonAlphaNumeric)
                    .filter(x -> !dictionary.contains(x.toLowerCase()))
                    .count();
            return new Metadata((int) chars, (int) words, (int) issues);
        } catch (IOException e) {
            throw new IllegalStateException("A problem occurred while reading from reader", e);
        }
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        return WordsSimilarityUtils.findSimilarWords(dictionary, word, n);
    }
}
