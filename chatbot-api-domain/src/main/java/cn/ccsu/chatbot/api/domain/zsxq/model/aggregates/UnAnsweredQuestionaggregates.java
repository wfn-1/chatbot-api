package cn.ccsu.chatbot.api.domain.zsxq.model.aggregates;

import cn.ccsu.chatbot.api.domain.zsxq.model.res.RespData;

/**
 * 返回未回答信息的聚合信息
 */
public class UnAnsweredQuestionaggregates {
    private boolean succeeded;
    private RespData resp_data;

    public boolean isSucceeded() {
        return succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        this.succeeded = succeeded;
    }

    public RespData getResp_data() {
        return resp_data;
    }

    public void setResp_data(RespData resp_data) {
        this.resp_data = resp_data;
    }
}
