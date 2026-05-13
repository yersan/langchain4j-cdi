package dev.langchain4j.cdi.example.booking;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class ChatMemoryProducer {

    @Produces
    @ApplicationScoped
    @Named("chat-ai-service-memory")
    public ChatMemory chatAiServiceMemory() {
        return MessageWindowChatMemory.withMaxMessages(10);
    }

    @Produces
    @ApplicationScoped
    @Named("fraud-ai-service-memory")
    public ChatMemory fraudAiServiceMemory() {
        return MessageWindowChatMemory.withMaxMessages(5);
    }
}
