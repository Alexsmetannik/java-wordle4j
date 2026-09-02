package ru.yandex.practicum.exceptions;

public class HintNotFoundException extends GameException {
    public HintNotFoundException() {
        super("Нет доступных подсказок");
    }
}
