package ru.yandex.practicum.exceptions;

public class GameAlreadyFinishedException extends GameException {
    public GameAlreadyFinishedException() {
        super("Игра уже завершена");
    }
}
