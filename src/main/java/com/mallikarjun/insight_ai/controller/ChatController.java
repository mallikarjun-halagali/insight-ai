package com.mallikarjun.insight_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatModel chatModel;
    private final ChatClient chatClient;

    public ChatController(ChatModel chatModel, ChatClient.Builder builder) {
        this.chatModel = chatModel;
        this.chatClient = builder.build();
    }
    @GetMapping("/test")
    public String test(@RequestParam(defaultValue = "Hello, Gemini!") String message) {
        return chatModel.call("You are a helpful assistant. Respond to the following message: " + message);
    }

    @GetMapping("/ai/{test}")
    public String analyzeStock(@PathVariable String test) {
        String systemMessage = """
        You are an expert Wall Street Analyst.
        Analyze the following news text and decide if it is GOOD or BAD for the stock.
        Return your answer in this format:
        SENTIMENT: [Positive/Negative/Neutral]
        REASON: [Short explanation]
        """;

        // In Spring AI, you can combine a System Message and User Message
        return chatModel.call(systemMessage + "\n\nNews: " + test);
    }
    @GetMapping("/ai/current")
    public Object currentModel(@RequestParam(defaultValue = "AAPL") String symbol) {
        String msg = """
                        Analyse the current sentiment for the stock symbol
                        Return your answer in this format:
                        SENTIMENT: [Positive/Negative/Neutral]
                        REASON: [Short explanation]
                        """;

        String systemPrompt = """
        You are an institutional-grade Equity Research Analyst.
        Analyze the latest news and determine its IMPACT WEIGHT on the stock price.
        
        Assign an IMPACT SCORE from 1 to 10:
        - 1-3: Low Impact (Minor news, noise, already priced in)
        - 4-6: Moderate Impact (Short-term volatility, quarterly guidance shifts)
        - 7-10: High Impact (Structural changes, M&A, major earnings surprises)

        Return your analysis in this JSON format:
        {
          "sentiment": "Bullish/Bearish/Neutral",
          "impact_score": 1-10,
          "certainty": "Percentage %",
          "reasoning": "Explain why the score is high or low based on revenue impact."
        }
        """;
        return chatClient.prompt()
                .user(systemPrompt + "\n\nStock Symbol: " + symbol)
                .call().entity(Object.class);
//                .content();
    }

}
