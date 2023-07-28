package cn.ccsu.chatbot.api.domain.zsxq;

import cn.ccsu.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;

import java.io.IOException;

public interface IZxsqApi {

    UnAnsweredQuestionaggregates queryUnAnsweredQuestionsTopicID(String groupId, String cookie) throws IOException;

    boolean answer(String groupId,String cookie,String topicId,String text,boolean silenced) throws IOException;
}
