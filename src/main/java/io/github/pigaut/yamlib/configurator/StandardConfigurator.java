package io.github.pigaut.yamlib.configurator;

import io.github.pigaut.yamlib.*;
import io.github.pigaut.yamlib.configurator.field.*;
import io.github.pigaut.yamlib.configurator.section.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class StandardConfigurator extends Configurator {

    public StandardConfigurator() {
        addMapper(Map.class, new SectionMapper());
        addMapper(Iterable.class, new SequenceMapper());
        addMapper(ConfigSection.class, new ConfigSectionMapper());
    }

    protected static class SectionMapper implements ConfigMapper<Map> {
        @Override
        public void map(ConfigSection section, Map map) {
            section.setKeyless(false);
            section.clear();

            map.forEach((key, value) -> {
                section.set(String.valueOf(key), value);
            });
        }
    }

    protected static class SequenceMapper implements ConfigMapper<Iterable> {
        @Override
        public void map(ConfigSection section, Iterable elements) {
            section.setKeyless(true);
            section.clear();

            for (Object element : elements) {
                section.add(element);
            }
        }
    }

    protected static class ConfigSectionMapper implements ConfigMapper<ConfigSection> {
        @Override
        public void map(ConfigSection section, ConfigSection sectionToMap) {
            if (sectionToMap.isKeyless()) {
                section.map(sectionToMap.toList());
            }
            else {
                section.map(sectionToMap.getNestedFields());
            }
        }
    }

}
