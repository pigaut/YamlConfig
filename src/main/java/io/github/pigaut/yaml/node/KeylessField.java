package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;

public interface KeylessField extends ConfigField {

    int getIndex();

    int getPosition();

    void setIndex(int index);

}
