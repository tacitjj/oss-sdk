package com.dmhxm.oss.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author jinyingxin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {

    /**
     * 返回码 200成功
     */
    private Integer code;

    /**
     * 响应结果
     */
    private String message;

    /**
     * 返回值
     */
    private T data;

    public ResponseResult(ResultStatus resultStatus, T data) {
        this.code = resultStatus.getCode();
        this.message = resultStatus.getMessage();
        this.data = data;
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<T>(ResultStatus.SUCCESS, data);
    }

    public static <T> ResponseResult<T> fail() {
        return new ResponseResult<T>(ResultStatus.FAIL, null);
    }


}
