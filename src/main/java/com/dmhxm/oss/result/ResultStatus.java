package com.dmhxm.oss.result;


import lombok.Getter;

/**
 * 统一返回状态
 *
 * @author jinyingxin
 */
@Getter
public enum ResultStatus implements Status {

    /**
     * result enum
     */
    SUCCESS(200, "上传成功"),
    FAIL(10001, "上传失败");

    private final int code;

    private final String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
