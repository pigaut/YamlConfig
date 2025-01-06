package io.github.pigaut.yaml.node;

import io.github.pigaut.yaml.*;

public interface KeylessField extends ConfigField {

    int getIndex();

    void setIndex(int index);

}
