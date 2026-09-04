package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryTest {
    private WordleDictionary dictionary;

    @BeforeEach
    void beforeStep() {
        List<String> words = Arrays.asList(
                "абзац", "абрис", "аврал", "автор", "адрес",
                "азбука", "аист", "акробат", "алмаз", "ангел"
        );
        dictionary = new WordleDictionary(words);
    }

    @Test
    void testSize() {
        assertEquals(10, dictionary.getSizeDictionary());
    }

    @Test
    void testContains() {
        assertTrue(dictionary.isDictionaryContainsWord("абзац"));
        assertTrue(dictionary.isDictionaryContainsWord("автор"));
        assertFalse(dictionary.isDictionaryContainsWord("несуществующее"));
    }

    @Test
    void testNormalize() {
        assertEquals("абзац", WordleDictionary.normalizeWord("Абзац"));
        assertEquals("абзац", WordleDictionary.normalizeWord("  Абзац  "));
        assertEquals("аежик", WordleDictionary.normalizeWord("АЁжик"));
    }

    @Test
    void testIsValidRussianWord() {
        assertTrue(WordleDictionary.isValidRussianWord("абзац"));
        assertTrue(WordleDictionary.isValidRussianWord("ёжик"));
        assertFalse(WordleDictionary.isValidRussianWord("hello"));
        assertFalse(WordleDictionary.isValidRussianWord("абзац1"));
        assertFalse(WordleDictionary.isValidRussianWord(""));
    }

    @Test
    void testCompareWords() {
        assertEquals("+++++", WordleDictionary.compareWords("абзац", "абзац"));
        assertEquals("+^-^-", WordleDictionary.compareWords("гонец", "герой"));
        assertEquals("++++-", WordleDictionary.compareWords("баран", "барак"));
        assertEquals("++-^^", WordleDictionary.compareWords("балка", "барак"));
    }

    @Test
    void testFilterWords() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.addMustContainLetter('а');
        List<String> filtered = dictionary.filterWordsByCriterial(criteria);
        assertTrue(filtered.stream().allMatch(word -> word.contains("а")));
    }

    @Test
    void testGetRandomWord() {
        String word = dictionary.getRandomWordFromDictionary();
        assertNotNull(word);
        assertTrue(dictionary.isDictionaryContainsWord(word));
    }
}
