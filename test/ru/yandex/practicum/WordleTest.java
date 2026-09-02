package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exceptions.GameException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private WordleDictionary dictionary;
    private WordleGame game;

    @BeforeEach
    void beforeStep() {
        List<String> words = Arrays.asList(
                "абзац", "абрис", "аврал", "автор", "адрес",
                "азбука", "аист", "акробат", "алмаз", "ангел",
                "бабка", "базар", "байка", "бакен", "балка",
                "банка", "барак", "барин", "барон", "басня"
        );
        dictionary = new WordleDictionary(words);
        StringWriter logWriter = new StringWriter();
        PrintWriter log = new PrintWriter(logWriter);
        game = new TestableWordleGame(dictionary, log, "автор");
    }

    @Test
    void testInitialState() {
        assertFalse(game.isFinished());
        assertFalse(game.isWon());
        assertEquals(0, game.getSteps());
        assertEquals(6, game.getMaxSteps());
        assertNotNull(game.getAnswer());
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
    void testFilterCriteria() {
        FilterCriteria criteria = new FilterCriteria();
        criteria.addMustContainLetter('а');
        criteria.addMustNotContainLetter('б');
        criteria.addExactPosition('в', 2);
        criteria.addForbiddenPosition('а', 0);
        assertFalse(criteria.checkWord("автор"));
        assertFalse(criteria.checkWord("абзац"));
        assertFalse(criteria.checkWord("базар"));
        assertFalse(criteria.checkWord("балка"));
    }

    @Test
    void testStepHistory() throws GameException {
        game.makeStep("абзац");
        game.makeStep("базар");
        List<String> history = game.getMoveHistory();
        assertEquals(2, history.size());
        assertEquals("абзац", history.get(0));
        assertEquals("базар", history.get(1));
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
}
