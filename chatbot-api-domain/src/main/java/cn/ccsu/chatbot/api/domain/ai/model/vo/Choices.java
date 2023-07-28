package cn.ccsu.chatbot.api.domain.ai.model.vo; /**
 * Copyright 2023 json.cn
 */


/**
 * Auto-generated: 2023-06-01 15:57:15
 *
 * @author json.cn (i@json.cn)
 * @website http://www.json.cn/java2pojo/
 */
public class Choices {

    private Message message;
    private String finish_reason;
    private int index;
    public void setMessage(Message message) {
        this.message = message;
    }
    public Message getMessage() {
        return message;
    }

    public void setFinish_reason(String finish_reason) {
        this.finish_reason = finish_reason;
    }
    public String getFinish_reason() {
        return finish_reason;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }

}