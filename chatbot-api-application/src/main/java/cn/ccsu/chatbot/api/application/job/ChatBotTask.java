package cn.ccsu.chatbot.api.application.job;

import cn.ccsu.chatbot.api.domain.ai.IOpenAI;
import cn.ccsu.chatbot.api.domain.zsxq.IZxsqApi;
import cn.ccsu.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;
import cn.ccsu.chatbot.api.domain.zsxq.model.vo.Topics;
import cn.ccsu.chatbot.api.domain.zsxq.service.ZxsqApi;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

/**
 * @description: 任务体，用于注册调度任务，也可以看作是配置文件中问答的实体类
 * @author：wufengning
 * @date: 2023/7/28
 */
public class ChatBotTask implements Runnable{
    private Logger logger= LoggerFactory.getLogger(ChatBotTask.class);
    //组名
    private String groupName;
    //组Id
    private String groupId;
    //cokkie
    private String cookie;
    //OpenAI key
    private String openAiKey;

    //知识星球接口
    private IZxsqApi zxsqApi;

    //OpenAI接口
    private IOpenAI openAI;

    public ChatBotTask( String groupName, String groupId, String cookie, String openAiKey, IZxsqApi zxsqApi, IOpenAI openAI) {
        this.groupName = groupName;
        this.groupId = groupId;
        this.cookie = cookie;
        this.openAiKey = openAiKey;
        this.zxsqApi = zxsqApi;
        this.openAI = openAI;
    }
    @Override
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
            String answer = openAI.doChatGPT(openAiKey,topic.getQuestion().getText().trim());
            //3.问题回复
            boolean status = zxsqApi.answer(groupId, cookie, topic.getTopic_id(), answer, false);
            logger.info("编号：{} 问题：{} 回答：{} 状态：{}",topic.getTopic_id(),topic.getQuestion().getText(),answer,status);
        }catch (Exception e){
            logger.error("自动回答问题异常");
        }
    }
}
