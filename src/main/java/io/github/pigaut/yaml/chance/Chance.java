package io.github.pigaut.yaml.chance;

import io.github.pigaut.yaml.util.*;

public class Chance {

    private final double percentage;

    public Chance(double percentage) {
        Preconditions.checkArgument(percentage < 0 || percentage > 1,
                "Chance must be between 0 and 1");
        this.percentage = percentage;
    }

    public boolean test() {
        double randomValue = Math.random();
        return randomValue <= percentage;
    }

}
