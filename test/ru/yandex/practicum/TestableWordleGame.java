package ru.yandex.practicum;

import java.io.PrintWriter;

public class TestableWordleGame extends WordleGame {
    private final String fixedAnswer;

    public TestableWordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        super(dictionary, log);
        this.fixedAnswer = answer;
    }

    @Override
    public String getAnswer() {
        return fixedAnswer;
    }
}
