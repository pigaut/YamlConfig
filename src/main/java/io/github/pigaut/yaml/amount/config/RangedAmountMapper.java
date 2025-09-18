package io.github.pigaut.yaml.amount.config;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.amount.*;
import io.github.pigaut.yaml.configurator.*;
import io.github.pigaut.yaml.configurator.map.*;
import org.jetbrains.annotations.*;

public class RangedAmountMapper implements ConfigMapper<RangedAmount> {

    @Override
    public @NotNull FieldType getDefaultMappingType() {
        return FieldType.SECTION;
    }

    @Override
    public void mapToScalar(@NotNull ConfigScalar scalar, @NotNull RangedAmount value) {
        scalar.setValue(value.toString());
    }

    @Override
    public void mapToSection(@NotNull ConfigSection section, @NotNull RangedAmount rangedAmount) {
        section.set("min|minimum", rangedAmount.min);
        section.set("max|maximum", rangedAmount.max);
    }

}
