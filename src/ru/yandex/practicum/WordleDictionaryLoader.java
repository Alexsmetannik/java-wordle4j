package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.DictionaryLoadException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary loadDictionaryFromFile(String filename) throws DictionaryLoadException {
        List<String> validWords = new ArrayList<>();

        log.println("Загрузка словаря из файла: " + filename);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String normalized = WordleDictionary.normalizeWord(line);
                if (normalized.length() == 5 && WordleDictionary.isValidRussianWord(normalized)) {
                    validWords.add(normalized);
                }
            }
        } catch (FileNotFoundException e) {
            log.println("Ошибка: файл словаря не найден: " + filename);
            throw new DictionaryLoadException("Файл словаря не найден: " + filename, e);
        } catch (IOException e) {
            log.println("Ошибка при чтении файла словаря: " + e.getMessage());
            throw new DictionaryLoadException("Ошибка при чтении файла словаря: " + e.getMessage(), e);
        }

        if (validWords.isEmpty()) {
            log.println("Ошибка: словарь не содержит подходящих слов (5 букв)");
            throw new DictionaryLoadException("Словарь не содержит подходящих слов (5 букв)");
        }

        Set<String> uniqueWords = new LinkedHashSet<>(validWords);
        log.println("Загружено " + uniqueWords.size() + " уникальных слов из " + validWords.size() + " строк");

        return new WordleDictionary(new ArrayList<>(uniqueWords));
    }
}
