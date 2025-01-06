package io.github.pigaut.yaml.util;

import java.util.*;

public class Percentage {

    private final int percent;
    private final Random random = new Random();

    public Percentage(int percent) {
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public boolean test() {
        return random.nextInt(100) < percent;
    }

}
