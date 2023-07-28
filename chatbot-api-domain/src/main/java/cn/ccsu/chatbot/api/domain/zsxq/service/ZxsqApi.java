package cn.ccsu.chatbot.api.domain.zsxq.service;

import cn.ccsu.chatbot.api.domain.zsxq.IZxsqApi;
import cn.ccsu.chatbot.api.domain.zsxq.model.aggregates.UnAnsweredQuestionaggregates;
import cn.ccsu.chatbot.api.domain.zsxq.model.req.AnswerReq;
import cn.ccsu.chatbot.api.domain.zsxq.model.req.ReqData;
import cn.ccsu.chatbot.api.domain.zsxq.model.res.AnswerRes;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class ZxsqApi implements IZxsqApi {
    private Logger logger= LoggerFactory.getLogger(ZxsqApi.class);
    @Override
    public UnAnsweredQuestionaggregates queryUnAnsweredQuestionsTopicID(String groupId, String cookie) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet get=new HttpGet("https://api.zsxq.com/v2/groups/"+groupId+"/topics?scope=unanswered_questions&count=20");
        get.addHeader("cookie",cookie);
        get.addHeader("Content-Type","application/json;chartset=utf-8");
        CloseableHttpResponse response = httpClient.execute(get);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String jsonStr = EntityUtils.toString(response.getEntity());
            logger.info("拉取的问题数据，uid:{} jsonStr:{}",groupId,jsonStr);
            return JSON.parseObject(jsonStr,UnAnsweredQuestionaggregates.class);
        }
        else{
            throw new RuntimeException("queryUnAnswereQuestionsTopicId Err Code is"+response.getStatusLine().getStatusCode());
        }
    }

    @Override
    public boolean answer(String groupId, String cookie, String topicId, String text, boolean silenced) throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post=new HttpPost("https://api.zsxq.com/v2/topics/"+topicId+"/answer");
        post.addHeader("cookie",cookie);
        post.addHeader("Content-Type","application/json;chartset=utf-8");
        post.addHeader("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36 Edg/113.0.1774.57");
        //回答的内容-
//        String paramJson="{\n" +
//                "  \"req_data\": {\n" +
//                "    \"text\": \"我不会\\n\",\n" +
//                "    \"image_ids\": []\n" +
//                "    \"silence\":false"+
//                "  }\n" +
//                "}";
        AnswerReq answerReq=new AnswerReq(new ReqData(text,silenced));
        String paramJson = JSONObject.fromObject(answerReq).toString();
        //将回答的内容转换为json类型通过post请求返回
        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(post);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            //获取执行post请求后的返回学校
            String jsonStr = EntityUtils.toString(response.getEntity());
            logger.info("回答星球的问题，uid:{} topicId:{} jsonStr:{}",groupId,topicId,jsonStr);
            AnswerRes answerRes = JSON.parseObject(jsonStr, AnswerRes.class);
            return answerRes.isSucceeded();
        }
        else{
            throw new RuntimeException("answer Err Code is"+response.getStatusLine().getStatusCode());
        }
    }
}
