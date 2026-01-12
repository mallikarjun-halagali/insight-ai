//package com.mallikarjun.insight_ai.config;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//@Configuration
//public class AiModelConfig {
//    @Bean
//    @Primary // This will be the default one
//    public ChatClient flashClient(ChatClient.Builder builder) {
//        return builder
//                .defaultSystem("You are a fast news summarizer.")
//                .build();
//    }
//
//    @Bean(name = "proClient")
//    public ChatClient proClient(ChatClient.Builder builder) {
//        // We customize this builder to use the Pro model
//        return builder
//                .defaultSystem("You are a deep financial analyst.")
//                .defaultOptions(GoogleGenAiChatOptions.builder()
//                        .model("gemini-2.5-pro")
//                        .googleSearchRetrieval(true)
//                        .build())
//                .build();
//    }
//
//    @Bean(name = "analystClient")
//    public ChatClient analystClient(ChatClient.Builder builder) {
//        // This is the "Option 2" Persona
//        String systemRole = """
//            Role: Senior Finance Research Analyst.
//            Experience: 20+ years in Wall Street equity research.
//
//            Instructions:
//            1. Consolidate news into a structured report.
//            2. Differentiate between 'Market Noise' and 'Fundamental News'.
//            3. Provide an Impact Score (1-10) based on potential revenue shift.
//            4. Always cite specific sources if Grounding is used.
//            """;
//
//        return builder
//                .defaultSystem(systemRole)
//                .defaultOptions(GoogleGenAiChatOptions.builder()
//                        .model("gemini-2.5-flash") // Fast & cheap for analysis
//                        .temperature(0.2)        // Low temperature for factual accuracy
//                        .googleSearchRetrieval(true) // Enable live news search by default
//                        .build())
//                .build();
//    }
//
//    @Bean(name = "newsConsolidationClient")
//    public ChatClient newsConsolidationClient(ChatClient.Builder builder) {
//        String systemRole = """
//        Role: Senior Finance Research Analyst.
//        Instructions:
//        1. DO NOT simply list news articles.
//        2. CONSOLIDATE similar news into single thematic highlights.
//        3. For 'Market Noise', provide one single summary of the day's price action.
//        4. For 'Fundamental News', group related events (like rate cuts and margin impact) into one 'synthesis' block.
//        5. Return a final 'summary' paragraph that explains the net impact of all news.
//        """;
//
//        return builder
//                .defaultSystem(systemRole)
//                .defaultOptions(GoogleGenAiChatOptions.builder()
//                        .model("gemini-2.5-flash")
//                        .temperature(0.1) // Lower temperature = more precise consolidation
//                        .googleSearchRetrieval(true)
//                        .build())
//                .build();
//    }
//
//    @Bean(name = "analystShort")
//    public ChatClient analystShort(ChatClient.Builder builder) {
//        String systemRole = """
//        Role: Senior Finance Research Analyst.
//        Instructions:
//        1. CONSOLIDATE all findings into a SINGLE paragraph of exactly 2-3 lines.
//        2. Combine fundamental news and market noise into one cohesive narrative.
//        3. End the paragraph with a clear 'IMPACT: [Bullish/Bearish/Neutral]' and a 'SCORE: [1-10]'.
//        4. Focus on how the news directly affects the company's valuation or revenue.
//        5. Be concise and avoid unnecessary details.
//        6.Return in the JSON format:
//        {
//          "summary": "Consolidated analysis here.",
//          "impact": "Bullish/Bearish/Neutral",
//          "score": from -10 to 10
//        }
//        """;
//
//        return builder
//                .defaultSystem(systemRole)
//                .defaultOptions(GoogleGenAiChatOptions.builder()
//                        .model("gemini-2.5-flash")
//                        .temperature(0.1) // Lower temperature makes the summary more focused
//                        .googleSearchRetrieval(true)
//                        .build())
//                .build();
//    }
//}
package com.mallikarjun.insight_ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.retry.support.RetryTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class AiModelConfig {

    // 1. TIMEOUT CONFIGURATION (5 Minutes)
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        return restClientBuilder -> {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(60)) // Time to establish connection
                    .build();

            JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
            factory.setReadTimeout(Duration.ofMinutes(5)); // Wait up to 5 mins for AI response

            restClientBuilder.requestFactory(factory);
        };
    }

    // 2. RETRY CONFIGURATION
    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(2)                         // Reduce to 2 attempts for long requests
                .fixedBackoff(Duration.ofSeconds(100))   // Wait 10s between retries
                .retryOn(Exception.class)               // Retry on any failure
                .build();
    }

    @Bean
    @Primary
    public ChatClient flashClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a fast news summarizer.")
                .build();
    }

    @Bean(name = "proClient")
    public ChatClient proClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are a deep financial analyst.")
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.5-pro")
                        .googleSearchRetrieval(true)
                        .build())
                .build();
    }

    @Bean(name = "analystClient")
    public ChatClient analystClient(ChatClient.Builder builder) {
        String systemRole = """
            Role: Senior Finance Research Analyst.
            Experience: 20+ years in Wall Street equity research.
            
            Instructions:
            1. Consolidate news into a structured report.
            2. Differentiate between 'Market Noise' and 'Fundamental News'.
            3. Provide an Impact Score (1-10) based on potential revenue shift.
            4. Always cite specific sources if Grounding is used.
            """;

        return builder
                .defaultSystem(systemRole)
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.5-flash")
                        .temperature(0.2)
                        .googleSearchRetrieval(true)
                        .build())
                .build();
    }

    @Bean(name = "newsConsolidationClient")
    public ChatClient newsConsolidationClient(ChatClient.Builder builder) {
        String systemRole = """
            Role: Senior Finance Research Analyst (Institutional Equity Research).
            Experience: 20+ years in Wall Street equity research, specialized in fundamental analysis.
            
            Instructions:
            1. CONSOLIDATE findings into the following sections:
               - 'marketNoise': Transient events, social media trends, and daily price fluctuations without structural change.
               - 'fundamental': Earnings, M&A, regulatory shifts, macro-economic drivers, and infrastructure investments.
            
            2. LOGIC & REASONING (Chain of Thought):
               - Before assigning a score, evaluate if the news is "Priced In" (already expected) or a "Surprise".
               - Filter out duplicate headlines and prioritize Tier-1 sources (SEC Filings, Reuters, Bloomberg, CNBC).
               - If conflicting info exists, highlight the discrepancy in the 'detailedReport'.
            
            3. SCORING METRICS (Strict Impact Score -10 to 10):
               - [7 to 10]: High Conviction Bullish (Structural revenue growth, margin expansion, major M&A).
               - [4 to 6]: Moderate Bullish (Positive guidance, incremental market share gain).
               - [-3 to 3]: Neutral/Noise (Expected results, minor leadership changes, daily volatility).
               - [-7 to -4]: Moderate Bearish (Guidance miss, rising cost of capital, sector headwinds).
               - [-10 to -7]: High Conviction Bearish (Fraud, bankruptcy risk, catastrophic regulatory fines).
            
            4. SYNTHESIS & OUTPUT:
               - 'detailedReport': A professional 2-3 line executive summary. Focus on "The Bottom Line"—how these events specifically shift the company's valuation.
               - 'impact': A concise sentiment label (e.g., "Strong Bullish", "Cautiously Bearish").
               - 'newsSummary': Must include a 1-sentence "So What?" (Why this news matters for the stock price).
            
            JSON Requirement:
            {
              "ticker": "string",
              "impact": "string",
              "impactScore": number,
              "marketNoise": [{"headline": "...", "newsSummary": "..."}],
              "fundamental": [{"headline": "...", "newsSummary": "..."}],
              "detailedReport": "string"
            }
            
            Constraint: Do not include any text before or after the JSON. Return valid JSON only.
            """;

        return builder
                .defaultSystem(systemRole)
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.5-flash-lite")
                        .temperature(0.1) // Keep it low for structural consistency
                        .googleSearchRetrieval(true)
                        .build())
                .build();
    }

    @Bean(name = "analystShort")
    public ChatClient analystShort(ChatClient.Builder builder) {
        String systemRole = """
        Role: Senior Finance Research Analyst.
        Instructions:
        1. CONSOLIDATE all findings into a SINGLE paragraph of exactly 2-3 lines.
        2. Combine fundamental news and market noise into one cohesive narrative.
        3. End the paragraph with a clear 'IMPACT: [Bullish/Bearish/Neutral]' and a 'SCORE: [1-10]'.
        4. Focus on how the news directly affects the company's valuation or revenue.
        5. Be concise and avoid unnecessary details.
        6.Return in the JSON format:
        {
          "summary": "Consolidated analysis here.",
          "impact": "Bullish/Bearish/Neutral",
          "score": from -10 to 10
        }
        """;

        return builder
                .defaultSystem(systemRole)
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-2.5-flash-lite")
//                        .model("gemini-3-flash")
                        .temperature(0.1)
                        .googleSearchRetrieval(true)
                        .build())
                .build();
    }
}