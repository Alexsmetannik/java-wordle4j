package ru.yandex.practicum.exceptions;

public class InvalidCharactersException extends GameException {
    public InvalidCharactersException() {
        super("Слово должно содержать только русские буквы");
    }
}
