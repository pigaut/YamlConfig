package io.github.pigaut.yaml.amount.config;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.map.*;
import org.jetbrains.annotations.*;

public class CasualAmountMapper implements ConfigMapper<CasualAmount> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SEQUENCE;
    }

    @Override
    public void mapToSequence(@NotNull ConfigSequence sequence, @NotNull CasualAmount casualAmount) {
        for (double value : casualAmount.values) {
            sequence.add(value);
        }
    }

}
