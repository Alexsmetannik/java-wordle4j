package ru.yandex.practicum.exceptions;

public class InvalidWordLengthException extends GameException {
    public InvalidWordLengthException() {
        super("Слово должно содержать ровно 5 букв");
    }
}
