package com.raindropcentral;


import org.jetbrains.annotations.Nullable;
import org.simpleyaml.configuration.file.YamlFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class YamlReader {

    @Nullable
    public static Map<String, String> loadYaml(String langFile) throws URISyntaxException {
        Path jarPath = Paths.get(YamlReader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        File yamlFile = jarPath.resolve(langFile).toFile();
        try {
            YamlFile config = new YamlFile(yamlFile);
            config.loadWithComments();
            Map<String, String> map = new HashMap<>();
            config.getValues(true).forEach((s, o) -> {
                if (o instanceof String string) {
                    if (string.equalsIgnoreCase("null")) {
                        return;
                    }
                    map.put(s, string);
                }
            });
            return map;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Map<String, Object> loadConfig() throws URISyntaxException {
        Path jarPath = Paths.get(YamlReader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
        File yamlFile = jarPath.resolve("config.yml").toFile();

        // Check if the config.yml file exists
        if (!yamlFile.exists()) {
            // If not, copy the default config.yml from resources
            try (InputStream inputStream = YamlReader.class.getResourceAsStream("/config.yml")) {
                if (inputStream == null) {
                    throw new IOException("Default config.yml not found in resources");
                }
                Files.copy(inputStream, yamlFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        try {
            YamlFile config = new YamlFile(yamlFile);
            config.loadWithComments();
            return new HashMap<>(config.getValues(true));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}