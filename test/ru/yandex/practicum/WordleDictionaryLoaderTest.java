package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exceptions.DictionaryLoadException;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class WordleDictionaryLoaderTest {
    @TempDir
    Path tempDir;
    private PrintWriter log;
    private StringWriter logWriter;
    private WordleDictionaryLoader loader;

    @BeforeEach
    void beforeStep() {
        logWriter = new StringWriter();
        log = new PrintWriter(logWriter);
        loader = new WordleDictionaryLoader(log);
    }

    @Test
    void testLoadFromFileWithDuplicates() throws IOException, DictionaryLoadException {
        Path testFile = tempDir.resolve("duplicates.txt");
        try (FileWriter writer = new FileWriter(testFile.toFile(), UTF_8)) {
            writer.write("абзац\n");
            writer.write("абзац\n");
            writer.write("абрис\n");
            writer.write("абрис\n");
            writer.write("автор\n");
        }

        WordleDictionary dictionary = loader.loadDictionaryFromFile(testFile.toString());

        assertNotNull(dictionary);
        assertEquals(3, dictionary.getSizeDictionary());
        assertTrue(dictionary.isDictionaryContainsWord("абзац"));
        assertTrue(dictionary.isDictionaryContainsWord("абрис"));
        assertTrue(dictionary.isDictionaryContainsWord("автор"));
    }

    @Test
    void testLogCreation() throws IOException, DictionaryLoadException {
        Path testFile = tempDir.resolve("test.txt");
        try (FileWriter writer = new FileWriter(testFile.toFile(), UTF_8)) {
            writer.write("абзац\n");
            writer.write("абрис\n");
        }

        loader.loadDictionaryFromFile(testFile.toString());
        log.flush();
        String logContent = logWriter.toString();
        
        assertTrue(logContent.contains("Загрузка словаря из файла:"));
    }
}
