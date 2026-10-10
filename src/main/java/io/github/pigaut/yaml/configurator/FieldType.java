package io.github.pigaut.yaml.configurator;

public enum FieldType {

    SCALAR,
    SECTION,
    SEQUENCE;

    public boolean isBranch() {
        return this == SECTION || this == SEQUENCE;
    }

}
