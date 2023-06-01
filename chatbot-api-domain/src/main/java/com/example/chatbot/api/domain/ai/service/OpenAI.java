package com.example.chatbot.api.domain.ai.service;

import com.alibaba.fastjson.JSON;
import com.example.chatbot.api.domain.ai.IOpenAI;
import com.example.chatbot.api.domain.ai.model.aggregates.AIAnswer;
import com.example.chatbot.api.domain.ai.model.vo.Choices;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
@Service
public class OpenAI implements IOpenAI {
    private Logger logger= LoggerFactory.getLogger(OpenAI.class);
    @Value("${chatbot-api.openAIKey}")
    private String openAIKey;
    @Override
    public String doChatGPT(String question) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //使用代理解决超时问题
        HttpPost post=new HttpPost("https://open.aiproxy.xyz/v1/chat/completions");
        post.addHeader("Content-type","application/json");
        post.addHeader("Authorization","Bearer "+openAIKey);
        String paramJson="{\n" +
                "     \"model\": \"gpt-3.5-turbo\",\n" +
                "     \"messages\": [{\"role\": \"user\", \"content\": \""+question+"\"}],\n" +
                "     \"temperature\": 0.7\n" +
                "   }";
        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(post);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String jsonStr = EntityUtils.toString(response.getEntity());
            StringBuilder answers=new StringBuilder();
            AIAnswer aiAnswer = JSON.parseObject(jsonStr, AIAnswer.class);
            List<Choices> choices = aiAnswer.getChoices();
            for(Choices choice : choices){
                answers.append(choice.getMessage().getContent());
            }
            return answers.toString();
        }
        else{
            throw new RuntimeException("queryUnAnswereQuestionsTopicId Err Code is"+response.getStatusLine().getStatusCode());
        }
    }
}
