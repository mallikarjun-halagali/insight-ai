//package com.mallikarjun.insight_ai.controller;
//
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class MultiModelController {
//
//    private final ChatClient flashClient;
//    private final ChatClient proClient;
//
//    public MultiModelController(ChatClient flashClient, @Qualifier("proClient") ChatClient proClient) {
//        this.flashClient = flashClient;
//        this.proClient = proClient;
//    }
//
//    @GetMapping("/fast")
//    public String fast(String msg) { return flashClient.prompt(msg).call().content(); }
//
//    @GetMapping("/slow")
//    public String slow(String msg) { return proClient.prompt(msg).call().content(); }
//}
