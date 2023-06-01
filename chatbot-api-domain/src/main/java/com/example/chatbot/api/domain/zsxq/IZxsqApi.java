package com.example.chatbot.api.domain.zsxq;

import com.example.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public interface IZxsqApi {

    UnAnsweredQuestionaggregates queryUnAnsweredQuestionsTopicID(String groupId, String cookie) throws IOException;

    boolean answer(String groupId,String cookie,String topicId,String text,boolean silenced) throws IOException;
}
