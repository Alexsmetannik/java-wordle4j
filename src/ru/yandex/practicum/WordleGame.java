package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    private final String answer;
    private int steps;
    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final int maxSteps = 6;
    private boolean won = false;
    private boolean finished = false;
    private final List<String> moveHistory = new ArrayList<>();
    private final List<String> resultHistory = new ArrayList<>();
    private final Set<Character> foundLetters = new HashSet<>();
    private final Set<Character> notFoundLetters = new HashSet<>();
    private final Map<Character, Set<Integer>> forbiddenPositions = new HashMap<>();
    private final Map<Character, Integer> exactPositions = new HashMap<>();
    private final Set<String> usedHints = new HashSet<>();
    private List<String> availableWordsForHint = null;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWordFromDictionary();
        log.println("Загадано слово: " + answer);
        log.println("Словарь содержит " + dictionary.getSizeDictionary() + " слов");
    }

    public static class FilterCriteria {
        private Set<Character> mustContainLetters = new HashSet<>();
        private Set<Character> mustNotContainLetters = new HashSet<>();
        private Map<Character, Integer> exactPositions = new HashMap<>();
        private Map<Character, Set<Integer>> forbiddenPositions = new HashMap<>();

        public void addMustContainLetter(char c) {
            mustContainLetters.add(c);
        }

        public void addMustNotContainLetter(char c) {
            mustNotContainLetters.add(c);
        }

        public void addExactPosition(char c, int pos) {
            exactPositions.put(c, pos);
        }

        public void addForbiddenPosition(char c, int pos) {
            forbiddenPositions.computeIfAbsent(c, k -> new HashSet<>()).add(pos);
        }

        public boolean checkWord(String word) {
            for (char c : word.toCharArray()) {
                if (mustNotContainLetters.contains(c)) {
                    return false;
                }
            }

            char[] chars = word.toCharArray();
            for (char c : mustContainLetters) {
                boolean found = false;
                for (char wc : chars) {
                    if (wc == c) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }

            for (Map.Entry<Character, Integer> entry : exactPositions.entrySet()) {
                char c = entry.getKey();
                int pos = entry.getValue();
                if (pos >= chars.length || chars[pos] != c) {
                    return false;
                }
            }

            for (Map.Entry<Character, Set<Integer>> entry : forbiddenPositions.entrySet()) {
                char c = entry.getKey();
                Set<Integer> forbidden = entry.getValue();
                for (int i = 0; i < chars.length; i++) {
                    if (chars[i] == c && forbidden.contains(i)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWon() {
        return won;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public List<String> getMoveHistory() {
        return Collections.unmodifiableList(moveHistory);
    }

    public List<String> getResultHistory() {
        return Collections.unmodifiableList(resultHistory);
    }

    public String makeStep(String input) throws GameException {
        if (finished) {
            throw new GameAlreadyFinishedException();
        }

        String normalized = WordleDictionary.normalizeWord(input);

        if (normalized == null || normalized.isEmpty()) {
            throw new EmptyWordException();
        }
        if (normalized.length() != 5) {
            throw new InvalidWordLengthException();
        }
        if (!WordleDictionary.isValidRussianWord(normalized)) {
            throw new InvalidCharactersException();
        }
        if (!dictionary.isDictionaryContainsWord(normalized)) {
            throw new WordNotFoundException(normalized);
        }

        String result = WordleDictionary.compareWords(normalized, answer);

        steps++;
        moveHistory.add(normalized);
        resultHistory.add(result);

        updateLetterInfo(normalized, result);

        if (normalized.equals(answer)) {
            won = true;
            finished = true;
            log.println("Пользователь угадал слово за " + steps + " попыток");
        } else if (steps >= maxSteps) {
            finished = true;
            log.println("Пользователь не угадал слово. Ответ: " + answer);
        }

        resetHintCache();

        return result;
    }

    private void updateLetterInfo(String guess, String result) {
        char[] guessChars = guess.toCharArray();
        char[] resultChars = result.toCharArray();

        for (int i = 0; i < guessChars.length; i++) {
            char c = guessChars[i];
            if (resultChars[i] == '+') {
                foundLetters.add(c);
                exactPositions.put(c, i);
                notFoundLetters.remove(c);
            }
        }

        for (int i = 0; i < guessChars.length; i++) {
            char c = guessChars[i];
            if (resultChars[i] == '^') {
                foundLetters.add(c);
                forbiddenPositions.computeIfAbsent(c, k -> new HashSet<>()).add(i);
                notFoundLetters.remove(c);
            }
        }

        for (int i = 0; i < guessChars.length; i++) {
            char c = guessChars[i];
            if (resultChars[i] == '-') {
                if (!foundLetters.contains(c)) {
                    notFoundLetters.add(c);
                } else {
                    forbiddenPositions.computeIfAbsent(c, k -> new HashSet<>()).add(i);
                }
            }
        }
    }

    public String getHint() throws GameException {
        if (finished) {
            throw new GameAlreadyFinishedException();
        }

        List<String> candidates = getAvailableWords();
        if (candidates.isEmpty()) {
            throw new HintNotFoundException();
        }

        String hint = null;
        for (String word : candidates) {
            if (!usedHints.contains(word)) {
                hint = word;
                break;
            }
        }

        if (hint == null) {
            hint = candidates.get(0);
        }

        usedHints.add(hint);
        log.println("Дана подсказка: " + hint);
        return hint;
    }

    private List<String> getAvailableWords() {
        if (availableWordsForHint != null) {
            return availableWordsForHint;
        }

        FilterCriteria criteria = new FilterCriteria();

        for (char c : foundLetters) {
            criteria.addMustContainLetter(c);
        }
        for (char c : notFoundLetters) {
            criteria.addMustNotContainLetter(c);
        }
        for (Map.Entry<Character, Integer> entry : exactPositions.entrySet()) {
            criteria.addExactPosition(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Character, Set<Integer>> entry : forbiddenPositions.entrySet()) {
            char c = entry.getKey();
            for (int pos : entry.getValue()) {
                criteria.addForbiddenPosition(c, pos);
            }
        }
        List<String> candidates = dictionary.filterWordsByCriterial(criteria);

        Set<String> usedWords = new HashSet<>(moveHistory);
        candidates = candidates.stream()
                .filter(word -> !usedWords.contains(word))
                .collect(Collectors.toList());

        candidates = candidates.stream()
                .filter(word -> !usedHints.contains(word))
                .collect(Collectors.toList());

        availableWordsForHint = candidates;
        return candidates;
    }

    public void resetHintCache() {
        this.availableWordsForHint = null;
    }

    public int getAvailableHintCount() {
        return getAvailableWords().size();
    }
}
