package com.example.chatbot.api.application.job;

import com.alibaba.fastjson.JSON;
import com.example.chatbot.api.domain.ai.IOpenAI;
import com.example.chatbot.api.domain.zsxq.IZxsqApi;
import com.example.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;
import com.example.chatbot.api.domain.zsxq.model.vo.Topics;
import jdk.jfr.Enabled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * 调度问答任务
 */
@EnableScheduling//启动定时任务的注解
@Configuration
public class ChatbotSchedule {
    private Logger logger= LoggerFactory.getLogger(ChatbotSchedule.class);
    @Value("${chatbot-api.groupId}")
    private String groupId;
    @Value("${chatbot-api.cookie}")
    private String cookie;

    @Resource
    private IZxsqApi zxsqApi;
    @Resource
    private IOpenAI openAI;
    //表达的网址cron.qqe2.com
    @Scheduled(cron = "0/5 * * * * ?")
    public void run(){
        try{
            if(new Random().nextBoolean()){
                logger.info("随机打洋工");
                return;
            }
            GregorianCalendar calendar=new GregorianCalendar();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            if(hour>22||hour<7){
                logger.info("打烊时间不工作");
                return;
            }
            //1.检索问题
            UnAnsweredQuestionaggregates unAnsweredQuestionaggregates = zxsqApi.queryUnAnsweredQuestionsTopicID(groupId, cookie);
            logger.info("测试结果：{}", JSON.toJSONString(unAnsweredQuestionaggregates));
            List<Topics> topics = unAnsweredQuestionaggregates.getResp_data().getTopics();
            if(null==topics||topics.isEmpty()){
                logger.info("本次检索未查询到需要回答的问题");
                return;
            }
            //一次只回答一个，防止被检测
            //2.AI回答
            Topics topic = topics.get(0);
            String answer = openAI.doChatGPT(topic.getQuestion().getText().trim());
            //3.问题回复
            boolean status = zxsqApi.answer(groupId, cookie, topic.getTopic_id(), answer, false);
            logger.info("编号：{} 问题：{} 回答：{} 状态：{}",topic.getTopic_id(),topic.getQuestion().getText(),answer,status);
        }catch (Exception e){
            logger.error("自动回答问题异常");
        }
    }
}
