package com.raindropcentral;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws URISyntaxException {

        Map<String, Object> config = YamlReader.loadConfig();
        if (config == null) {
            System.out.println("Failed to load configuration");
            return;
        }

        String apiKey = (String) config.get("Key");
        int rateLimit = (int) config.get("RateLimit");
        long delayBetweenRequestsMs = TimeUnit.MINUTES.toMillis(1) / rateLimit;

        String baseURL = (String) config.get("BaseURL");
        String modelName = (String) config.get("Model");

        String defaultFile = (String) config.get("DefaultFile");

        List<String> lang = new ArrayList<>();
        Object languagesObj = config.get("languages");
        if (languagesObj instanceof List<?>) {
            for (Object langObj : (List<?>) languagesObj) {
                if (langObj instanceof String) {
                    lang.add((String) langObj);
                }
            }
        }

        String systemPrompt = (String) config.get("System");
        String p = (String) config.get("Prompt");


        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(baseURL)
                .modelName(modelName)
                .apiKey(apiKey)
                .build();

        Map<String, String> english = YamlReader.loadYaml(defaultFile);
        if (english == null) {
            System.out.println("Failed to find file");
            return;
        }
        Map<String, Map<String, String>> translations = new HashMap<>();
        lang.forEach(s -> translations.put(s, new HashMap<>()));

        PromptTemplate promptTemplate = PromptTemplate.from(p);
        SystemMessage message = SystemMessage.from(systemPrompt);

        english.forEach((key, value) -> {
            final String original = value;
            lang.forEach(s -> {
                try {
                    Map<String, Object> variables = new HashMap<>();
                    variables.put("message", original);
                    variables.put("language", s);
                    Prompt prompt = promptTemplate.apply(variables);
                    AiMessage aiMessage = model.generate(message, prompt.toUserMessage()).content();
                    final String translation = aiMessage.text();
                    Map<String, String> languageTranslations = translations.get(s);
                    languageTranslations.put(key, translation);
                    translations.put(s, languageTranslations);
                    // Sleep to respect rate limit
                    Thread.sleep(delayBetweenRequestsMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, failed to complete operation");
                }
            });
        });
        translations.forEach(YamlWriter::write);
    }
}