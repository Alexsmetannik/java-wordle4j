package ru.yandex.practicum.exceptions;

public class EmptyWordException extends GameException {
    public EmptyWordException() {
        super("Слово не может быть пустым");
    }
}
