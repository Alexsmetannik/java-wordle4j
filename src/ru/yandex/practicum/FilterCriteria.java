package ru.yandex.practicum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilterCriteria {
    private final Set<Character> mustContainLetters = new HashSet<>();
    private final Set<Character> mustNotContainLetters = new HashSet<>();
    private final Map<Character, Integer> exactPositions = new HashMap<>();
    private final Map<Character, Set<Integer>> forbiddenPositions = new HashMap<>();

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
