package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private WordleDictionary dictionary;
    private WordleGame game;
    private PrintWriter log;

    @BeforeEach
    void beforeStep() throws DictionaryLoadException {
        List<String> words = Arrays.asList(
                "абзац", "абрис", "аврал", "автор", "адрес",
                "азбука", "аист", "акробат", "алмаз", "ангел",
                "бабка", "базар", "байка", "бакен", "балка",
                "банка", "барак", "барин", "барон", "басня"
        );
        dictionary = new WordleDictionary(words);
        StringWriter logWriter = new StringWriter();
        log = new PrintWriter(logWriter);
        game = new WordleGame(dictionary, log, "автор");
    }

    @Test
    void testInitialState() {
        assertFalse(game.isFinished());
        assertFalse(game.isWon());
        assertEquals(0, game.getSteps());
        assertEquals(6, game.getMaxSteps());
        assertNotNull(game.getAnswer());
        assertEquals("автор", game.getAnswer());
    }

    @Test
    void testMakeStepValidWord() throws GameException {
        String result = game.makeStep("базар");
        assertNotNull(result);
        assertEquals(5, result.length());
        assertEquals(1, game.getSteps());
        assertFalse(game.isFinished());
        assertFalse(game.isWon());
    }

    @Test
    void testMakeStepWinningWord() throws GameException {
        String result = game.makeStep("автор");
        assertEquals("+++++", result);
        assertTrue(game.isWon());
        assertTrue(game.isFinished());
        assertEquals(1, game.getSteps());
    }

    @Test
    void testMakeStepInvalidLength() {
        assertThrows(InvalidWordLengthException.class, () -> game.makeStep("аб"));
        assertThrows(InvalidWordLengthException.class, () -> game.makeStep("абзацы"));
        assertEquals(0, game.getSteps());
    }

    @Test
    void testMakeStepEmptyWord() {
        assertThrows(EmptyWordException.class, () -> game.makeStep(""));
        assertThrows(EmptyWordException.class, () -> game.makeStep("   "));
        assertEquals(0, game.getSteps());
    }

    @Test
    void testMakeStepInvalidCharacters() {
        assertThrows(InvalidCharactersException.class, () -> game.makeStep("hello"));
        assertThrows(InvalidCharactersException.class, () -> game.makeStep("abcde"));
        assertThrows(InvalidCharactersException.class, () -> game.makeStep("абвг$"));
        assertThrows(InvalidCharactersException.class, () -> game.makeStep("12абв"));
        assertEquals(0, game.getSteps());
    }

    @Test
    void testMakeStepWordNotFound() {
        assertThrows(WordNotFoundException.class, () -> game.makeStep("абвгд"));
        assertEquals(0, game.getSteps());
    }

    @Test
    void testMakeStepAfterGameFinished() throws GameException {
        game.makeStep("автор");
        assertTrue(game.isFinished());
        assertThrows(GameAlreadyFinishedException.class, () -> game.makeStep("базар"));
    }

    @Test
    void testGameLose() throws GameException {
        for (int i = 0; i < 6; i++) {
            game.makeStep("абзац");
        }
        assertTrue(game.isFinished());
        assertFalse(game.isWon());
        assertEquals(6, game.getSteps());
    }

    @Test
    void testHint() throws GameException {
        game.makeStep("абзац");
        String hint = game.getHint();
        assertNotNull(hint);
        assertEquals(5, hint.length());
        assertTrue(dictionary.isDictionaryContainsWord(hint));
    }

    @Test
    void testHintAfterGameFinished() throws GameException {
        game.makeStep("автор");
        assertTrue(game.isFinished());
        assertThrows(GameAlreadyFinishedException.class, () -> game.getHint());
    }

    @Test
    void testResultHistory() throws GameException {
        game.makeStep("абзац");
        game.makeStep("базар");
        List<String> results = game.getResultHistory();
        assertEquals(2, results.size());
        assertNotNull(results.get(0));
        assertNotNull(results.get(1));
    }

    @Test
    void testGetAvailableHintCount() throws GameException {
        game.makeStep("абзац");
        int count = game.getAvailableHintCount();
        assertTrue(count >= 0);
    }

    @Test
    void testDuplicateLetters() throws GameException {
        WordleGame duplicateTest = new WordleGame(dictionary, log, "барак");
        String result = duplicateTest.makeStep("балка");
        assertEquals(5, result.length());
        assertEquals('+', result.charAt(0));
        assertEquals('+', result.charAt(1));
        assertFalse(duplicateTest.getMoveHistory().isEmpty());
    }
}
