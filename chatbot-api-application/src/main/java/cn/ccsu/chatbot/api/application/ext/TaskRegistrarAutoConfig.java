package cn.ccsu.chatbot.api.application.ext;

import cn.ccsu.chatbot.api.application.job.ChatBotTask;
import cn.ccsu.chatbot.api.common.PropertyUtil;
import cn.ccsu.chatbot.api.domain.ai.IOpenAI;
import cn.ccsu.chatbot.api.domain.zsxq.IZxsqApi;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @description: 任务注册服务，支持多组任务配置，解析配置文件
 * @author：wufengning
 * @date: 2023/7/28
 */
@Configuration
@EnableScheduling
public class TaskRegistrarAutoConfig implements EnvironmentAware, SchedulingConfigurer {

    private Logger logger= LoggerFactory.getLogger(TaskRegistrarAutoConfig.class);

    //任务配置组
    Map<String, Map<String, Object>> taskGroup=new HashMap<>();

    @Resource //优先按名称注册
    private IZxsqApi zxsqApi;

    @Resource
    private IOpenAI openAI;

    @Override
    public void setEnvironment(Environment environment) {
        //设置前缀
        String prefix="chatbot-api.";
        //获取执行列表
        String launchListStr = environment.getProperty(prefix + "launchList");
        if(StringUtils.isEmpty(launchListStr)){
            return;
        }
        for (String key : launchListStr.split(",")) {
            Map<String,Object> taskGroupProps = PropertyUtil.handle(environment, prefix + key, Map.class);
            taskGroup.put(key,taskGroupProps);
        }
    }

    //实现接口与使用注解的方式@Scheduled(cron = "0/5 * * * * ?")效果是相同的
    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        Set<String> taskGroups = taskGroup.keySet();
        for (String groupKey : taskGroups) {
            Map<String, Object> map = taskGroup.get(groupKey);
            String groupName=map.get("groupName").toString();
            String groupId=map.get("groupId").toString();
            String cookie=map.get("cookie").toString();
            String openAiKey=map.get("openAiKey").toString();
            //此处如果使用的是Base64加密好的表达式，那么需要使用Base64解码
            //String cronExpressionBase64 = taskGroup.get("cronExpression").toString();
            //String cronExpression = new String(Base64.getDecoder().decode(cronExpressionBase64), StandardCharsets.UTF_8);
            String  cronExpression =map.get("cronExpression").toString();
            logger.info("创建任务 groupName：{} groupId：{} cronExpression：{}", groupName, groupId, cronExpression);
            scheduledTaskRegistrar.addCronTask(new ChatBotTask(groupName,groupId,cookie,openAiKey,zxsqApi,openAI),cronExpression);
        }
    }
}
