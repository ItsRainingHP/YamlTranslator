package com.raindropcentral;

import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.implementation.snakeyaml.SnakeYamlImplementation;
import org.simpleyaml.configuration.implementation.snakeyaml.lib.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

public class YamlWriter {

    public static void write(String fileName, @NotNull Map<String, String> contents) {
        YamlConfiguration yamlConfig = new YamlConfiguration();
        contents.forEach((key, value) -> yamlConfig.set(key, value.trim()));

        try {
            Path jarPath = Paths.get(YamlWriter.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

            // Construct the path to the YAML file in the same directory as the JAR
            File yamlFile = jarPath.resolve(fileName + ".yml").toFile();
            SnakeYamlImplementation implementation = (SnakeYamlImplementation) yamlConfig.getImplementation();
            implementation.getDumperOptions().setSplitLines(false);
            implementation.getDumperOptions().setDefaultFlowStyle(DumperOptions.FlowStyle.FLOW);
            yamlConfig.save(yamlFile);
            System.out.println(fileName + " YAML file saved successfully.");
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
