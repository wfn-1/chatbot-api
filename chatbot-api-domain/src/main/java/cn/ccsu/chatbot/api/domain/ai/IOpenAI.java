package cn.ccsu.chatbot.api.domain.ai;

import java.io.IOException;

//chatGPT Open AI接口
public interface IOpenAI {
    String doChatGPT(String openAIKey,String question) throws IOException;
}
