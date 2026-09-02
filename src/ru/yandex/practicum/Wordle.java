package ru.yandex.practicum;

import ru.yandex.practicum.exceptions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new OutputStreamWriter(new FileOutputStream("wordle.log"), UTF_8))) {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(log);

            WordleDictionary dictionary = loader.loadDictionaryFromFile("words_ru.txt");
            log.println("Словарь загружен, размер: " + dictionary.getSizeDictionary());

            WordleGame game = new WordleGame(dictionary, log);

            try (Scanner scanner = new Scanner(System.in, UTF_8)) {
                System.out.println("Добро пожаловать в игру Wordle!");
                System.out.println("Начинайте игру");

                while (!game.isFinished()) {
                    System.out.println("Попытка " + (game.getSteps() + 1) + " из " + game.getMaxSteps());
                    System.out.print("Введите слово (или Enter для подсказки): ");
                    String input = scanner.nextLine().trim();
                    if (input.isEmpty()) {
                        handleHintRequest(game);
                        continue;
                    }

                    try {
                        String result = game.makeStep(input);
                        System.out.println(input);
                        System.out.println(result);
                        if (game.isWon()) {
                            System.out.println("Поздравляем! Вы угадали слово: " + game.getAnswer());
                            System.out.println("Количество попыток: " + game.getSteps());
                            break;
                        }
                    } catch (WordNotFoundException e) {
                        System.out.println("Слово '" + input + "' не найдено в словаре. Попробуйте другое.");
                    } catch (InvalidWordLengthException e) {
                        System.out.println("Слово должно содержать ровно 5 букв. Попробуйте снова.");
                    } catch (EmptyWordException e) {
                        System.out.println("Слово не может быть пустым. Попробуйте снова.");
                    } catch (InvalidCharactersException e) {
                        System.out.println("Слово должно содержать только русские буквы. Попробуйте снова.");
                    } catch (GameAlreadyFinishedException e) {
                        System.out.println("Игра уже завершена. Начните новую игру.");
                        break;
                    } catch (GameException e) {
                        System.out.println("Ошибка игры: " + e.getMessage());
                        log.println("Ошибка игры: " + e.getMessage());
                    }
                }
                if (!game.isWon() && game.isFinished()) {
                    System.out.println("Игра окончена. Загаданное слово: " + game.getAnswer());
                }
            }
            log.println("Игра завершена");

        } catch (DictionaryLoadException e) {
            System.err.println("Ошибка загрузки словаря: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Ошибка создания лог-файла: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void handleHintRequest(WordleGame game) {
        try {
            String hint = game.getHint();
            int available = game.getAvailableHintCount();
            System.out.println("Подсказка: " + hint);
            if (available > 1) {
                System.out.println("Доступно еще " + (available - 1) + " слов-подсказок");
            }
        } catch (HintNotFoundException e) {
            System.out.println("Нет доступных подсказок.");
        } catch (GameException e) {
            System.out.println("Ошибка при получении подсказки: " + e.getMessage());
        }
    }
}
