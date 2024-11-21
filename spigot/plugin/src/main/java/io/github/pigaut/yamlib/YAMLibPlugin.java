package io.github.pigaut.yamlib;

import io.github.pigaut.yamlib.action.*;
import io.github.pigaut.yamlib.config.*;
import org.bukkit.plugin.java.*;

import java.io.*;
import java.util.*;

public class YAMLibPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        File file = new File(getDataFolder(), "config.yml");

        Config config = new FileConfig(file, new TestConfigurator());

        config.load();

        List<Action> actions = config.getAll("set", Action.class).toList();

        actions.forEach(Action::run);

        config.save();
    }

}
