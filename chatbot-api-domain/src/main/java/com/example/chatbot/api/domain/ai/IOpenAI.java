package com.example.chatbot.api.domain.ai;

import java.io.IOException;

//chatGPT Open AI接口
public interface IOpenAI {
    String doChatGPT(String question) throws IOException;
}
