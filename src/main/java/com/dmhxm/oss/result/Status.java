package com.dmhxm.oss.result;

/**
 * 状态
 *
 * @author jinyingxin
 * @since 2021/10/19 18:18
 */
public interface Status {

    /**
     * 状态码
     *
     * @return status code
     */
    int getCode();

    /**
     * 消息体
     *
     * @return status message
     */
    String getMessage();
}
