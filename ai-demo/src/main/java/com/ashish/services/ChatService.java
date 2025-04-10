package com.ashish.services;

import com.ashish.util.Tools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier(value = "ollamaChatModel")
public class ChatService {

    private ChatClient chatClient;

    public ChatService(OllamaChatModel chatModel) {
        this.chatClient = ChatClient.create(chatModel);
    }

    public String query(String prompt){
          return chatClient.prompt( new Prompt(prompt))
                  .tools(new Tools())
                  .call()
                  .content();
    }
}
