package bg.sofia.uni.fmi.mjt.spellchecker;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class WordsSimilarityUtils {
    private static Map<String, Integer> vectorRepresentation(String word) {
        if (word.length() < 2) {
            throw new IllegalArgumentException("Word is too short.");
        }
        Map<String, Integer> twoGrams = new HashMap<>();
        for (int i = 0; i < word.length() - 1; i++) {
            String twoGram = word.substring(i, i + 2);
            if (twoGrams.containsKey(twoGram)) {
                twoGrams.put(twoGram, twoGrams.get(twoGram) + 1);
            } else {
                twoGrams.put(twoGram, 1);
            }
        }
        return twoGrams;
    }

    private static double vectorLength(String word) {
        return Math.sqrt(vectorRepresentation(word)
                .values()
                .stream().map((Integer::doubleValue))
                .map(x -> x * x)
                .reduce(0.0, Double::sum));
    }

    private static int dotProduct(String word1, String word2) {
        Map<String, Integer> word1Representation = vectorRepresentation(word1);
        Map<String, Integer> word2Representation = vectorRepresentation(word2);
        List<String> commonKeys = word1Representation
                .keySet().stream()
                .filter(word2Representation::containsKey)
                .collect(Collectors.toList());
        return commonKeys.stream()
                .map(x -> word1Representation.get(x) * word2Representation.get(x))
                .reduce(0, Integer::sum);
    }

    private static double commonness(String word1, String word2) {
        return -dotProduct(word1, word2) / (vectorLength(word1) * vectorLength(word2));
    }

    public static List<String> findSimilarWords(Set<String> dictionary, String word, int n) {
        return dictionary.stream()
                .sorted(Comparator.comparingDouble(x -> commonness(word, x)))
                .limit(n)
                .collect(Collectors.toList());
    }
}
