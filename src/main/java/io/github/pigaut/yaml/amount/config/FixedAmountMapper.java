package io.github.pigaut.yaml.amount.config;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.map.*;
import org.jetbrains.annotations.*;

public class FixedAmountMapper implements ConfigMapper<FixedAmount> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SCALAR;
    }

    @Override
    public void mapToScalar(@NotNull ConfigScalar scalar, @NotNull FixedAmount value) {
        scalar.setValue(value.toString());
    }

    @Override
    public void mapToSequence(@NotNull ConfigSequence sequence, @NotNull FixedAmount fixedAmount) {
        sequence.set(0, fixedAmount.value);
    }

}
