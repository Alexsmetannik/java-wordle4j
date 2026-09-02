package ru.yandex.practicum;

import java.util.*;
import java.util.stream.Collectors;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final List<String> words;
    private final Set<String> wordSet;

    public WordleDictionary(List<String> words) {
        this.words = List.copyOf(words);
        this.wordSet = new HashSet<>(words);
    }

    public static String normalizeWord(String word) {
        if (word == null) return null;
        String normalized = word.toLowerCase(Locale.ROOT).trim();
        return normalized.replace('ё', 'е');
    }

    public static boolean isValidRussianWord(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        } else {
            for (char c : word.toCharArray()) {
                if (!((c >= 'а' && c <= 'я') || c == 'ё')) {
                    return false;
                }
            }
            return true;
        }
    }

    public static String compareWords(String guess, String answer) {
        if (guess.length() != answer.length()) {
            throw new IllegalArgumentException("Длины слов должны совпадать");
        }

        char[] guessChars = guess.toCharArray();
        char[] answerChars = answer.toCharArray();
        char[] result = new char[guess.length()];
        boolean[] usedInAnswer = new boolean[answer.length()];
        boolean[] usedInGuess = new boolean[guess.length()];

        for (int i = 0; i < guess.length(); i++) {
            if (guessChars[i] == answerChars[i]) {
                result[i] = '+';
                usedInAnswer[i] = true;
                usedInGuess[i] = true;
            }
        }

        for (int i = 0; i < guess.length(); i++) {
            if (usedInGuess[i]) continue;
            boolean found = false;
            for (int j = 0; j < answer.length(); j++) {
                if (!usedInAnswer[j] && guessChars[i] == answerChars[j]) {
                    result[i] = '^';
                    usedInAnswer[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) {
                result[i] = '-';
            }
        }

        return new String(result);
    }

    public int getSizeDictionary() {
        return words.size();
    }

    public boolean isDictionaryContainsWord(String word) {
        return wordSet.contains(word);
    }

    public String getRandomWordFromDictionary() {
        if (!words.isEmpty()) {
            Random random = new Random();
            return words.get(random.nextInt(words.size()));
        } else {
            return null;
        }
    }

    public List<String> getAllWordsFromDictionary() {
        return words;
    }

    public List<String> filterWordsByCriterial(FilterCriteria criteria) {
        return words.stream()
                .filter(criteria::checkWord)
                .collect(Collectors.toList());
    }
}
