package cn.ccsu.chatbot.api;

import com.alibaba.fastjson.JSON;
import cn.ccsu.chatbot.api.domain.ai.IOpenAI;
import cn.ccsu.chatbot.api.domain.zsxq.IZxsqApi;
import cn.ccsu.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;
import cn.ccsu.chatbot.api.domain.zsxq.model.vo.Topics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootRunTest {

    private Logger logger= LoggerFactory.getLogger(SpringBootRunTest.class);
    @Value("${chatbot-api.group01.groupId}")
    private String groupId;
    @Value("${chatbot-api.group01.cookie}")
    private String cookie;
    @Value("${chatbot-api.group01.openAiKey}")
    private String openAiKey;

    @Resource
    private IZxsqApi zxsqApi;
    @Resource
    private IOpenAI openAI;
    @Test
    public void test_zxsq() throws IOException {
        UnAnsweredQuestionaggregates unAnsweredQuestionaggregates = zxsqApi.queryUnAnsweredQuestionsTopicID(groupId, cookie);
        logger.info("测试结果：{}", JSON.toJSONString(unAnsweredQuestionaggregates));
        List<Topics> topics = unAnsweredQuestionaggregates.getResp_data().getTopics();
//        logger.info("话题信息：{}",JSON.toJSONString(topics));
        for(Topics topic:topics){
            String topic_id=topic.getTopic_id();
            String text=topic.getQuestion().getText();
            logger.info("topic_id:{} text:{}",topic_id,text);
        }
        Topics topic = topics.get(0);
        zxsqApi.answer(groupId,cookie,topic.getTopic_id(),"我不会",false);
    }

    @Test
    public void test_openai() throws IOException {
        String result = openAI.doChatGPT(openAiKey,"帮我写一个冒泡排序");
        logger.info("测试结果：{}", result);

    }
}
