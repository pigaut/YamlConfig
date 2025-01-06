package io.github.pigaut.yaml.itemstack.attribute;

import io.github.pigaut.yaml.*;
import io.github.pigaut.yaml.configurator.loader.*;
import org.jetbrains.annotations.*;

public class AttributeLoader implements ConfigLoader<Attribute> {

    @Override
    public @NotNull String getProblemDescription() {
        return "Could not load attribute";
    }

    @Override
    public @NotNull Attribute loadFromSection(@NotNull ConfigSection section) throws InvalidConfigurationException {
        final String attribute = section.getString("type|attribute");
        final String name = section.getOptionalString("name").orElse("");
        final double amount = section.getDouble("amount");
        final String slot = section.getString("slot");
        final int operation = section.getInteger("operation");
        return new Attribute(attribute, name, amount, slot, operation);
    }

    @Override
    public @NotNull Attribute loadFromSequence(@NotNull ConfigSequence sequence) throws InvalidConfigurationException {
        String attribute;
        String name = "";
        double amount;
        String slot;
        int operation;
        switch (sequence.size()) {
            case 4 -> {
                attribute = sequence.getString(0);
                amount = sequence.getDouble(1);
                slot = sequence.getString(2);
                operation = sequence.getInteger(3);
            }
            case 5 -> {
                attribute = sequence.getString(0);
                name = sequence.getString(1);
                amount = sequence.getDouble(2);
                slot = sequence.getString(3);
                operation = sequence.getInteger(4);
            }
            default -> throw new InvalidConfigurationException(sequence, "is not a valid attribute");
        }
        return new Attribute(attribute, name, amount, slot, operation);
    }

}
