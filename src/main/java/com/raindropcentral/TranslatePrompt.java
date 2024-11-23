package com.raindropcentral;

import dev.langchain4j.model.input.structured.StructuredPrompt;

@StructuredPrompt({
        "Translate the {{message}} into the following {{language}}.",
        "Do not translate or change any placeholders wrapped in % symbols.",
        "Do not include the {{language}} in the response.",
        "Structure your answer in the following way:",

        "...",
})
public class TranslatePrompt {
    protected String message;
    protected String language;
}
