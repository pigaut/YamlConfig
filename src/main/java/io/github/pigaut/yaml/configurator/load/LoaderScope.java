package io.github.pigaut.yaml.configurator.load;

import io.github.pigaut.yaml.*;
import org.jetbrains.annotations.*;

public class LoaderScope implements AutoCloseable {
    private final ConfigRoot root;
    private final ConfigLoader<?> previousLoader;

    public LoaderScope(@NotNull ConfigRoot root, @Nullable ConfigLoader<?> newLoader) {
        this.root = root;
        this.previousLoader = root.getActiveLoader();
        root.setActiveLoader(newLoader);
    }
    @Override
    public void close() {
        root.setActiveLoader(previousLoader);
    }
}