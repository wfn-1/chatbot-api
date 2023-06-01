package com.example.chatbot.api;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;

public class ApiTest {
    @Test
    public void query_unanswered_question() throws IOException {

    }

    @Test
    public void answer() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post=new HttpPost("https://api.zsxq.com/topics/fds/answer");
        post.addHeader("cookie","__cuid=27a7421450bd494ba5a0c732e4890c02; amp_fef1e8=d1c444a3-55e3-4720-adb3-568187681e13R...1h1jjo2fp.1h1jjo2fu.1.1.2; zsxq_access_token=B1D0A0FA-C1BB-6F00-9C86-D3254AA2500F_CD446F4DB4D98229; abtest_env=product; zsxqsessionid=67068b870241849ee764ee651282e83e; sensorsdata2015jssdkcross={\"distinct_id\":\"815842452181122\",\"first_id\":\"18409842cdf922-0a87f8b21c83c2-7b555472-1327104-18409842ce0ff\",\"props\":{\"$latest_traffic_source_type\":\"直接流量\",\"$latest_search_keyword\":\"未取到值_直接打开\",\"$latest_referrer\":\"\"},\"identities\":\"eyIkaWRlbnRpdHlfY29va2llX2lkIjoiMTg0MDk4NDJjZGY5MjItMGE4N2Y4YjIxYzgzYzItN2I1NTU0NzItMTMyNzEwNC0xODQwOTg0MmNlMGZmIiwiJGlkZW50aXR5X2xvZ2luX2lkIjoiODE1ODQyNDUyMTgxMTIyIn0=\",\"history_login_id\":{\"name\":\"$identity_login_id\",\"value\":\"815842452181122\"},\"$device_id\":\"18409842cdf922-0a87f8b21c83c2-7b555472-1327104-18409842ce0ff\"}");
        post.addHeader("Content-Type","application/json;chartset=utf-8");
        //回答的内容-
        String paramJson="{\n" +
                "  \"req_data\": {\n" +
                "    \"text\": \"我不会\\n\",\n" +
                "    \"image_ids\": []\n" +
                "    \"silence\":false"+
                "  }\n" +
                "}";
        //将回答的内容转换为json类型返回
        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(post);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(res);
        }
        else{
            System.out.println(response.getStatusLine().getStatusCode());
        }
    }

    @Test
    public void test_chatGPT() throws IOException {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        //使用代理解决超时问题
        HttpPost post=new HttpPost("https://open.aiproxy.xyz/v1/chat/completions");
        post.addHeader("Content-type","application/json");
        post.addHeader("Authorization","Bearer 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");
        String paramJson="{\n" +
                "     \"model\": \"gpt-3.5-turbo\",\n" +
                "     \"messages\": [{\"role\": \"user\", \"content\": \"帮我写一个冒泡排序\"}],\n" +
                "     \"temperature\": 0.7\n" +
                "   }";
        StringEntity stringEntity = new StringEntity(paramJson, ContentType.create("text/json", "UTF-8"));
        post.setEntity(stringEntity);
        CloseableHttpResponse response = httpClient.execute(post);
        if(response.getStatusLine().getStatusCode()== HttpStatus.SC_OK){
            String res = EntityUtils.toString(response.getEntity());
            System.out.println(res);
        }
        else{
            System.out.println(response.getStatusLine().getStatusCode());
        }
    }
}
