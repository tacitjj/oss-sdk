package com.dmhxm.oss.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * OSS相关配置
 * </p>
 *
 * @author jinyingxin
 * @since 2021/10/11 18:21
 */
@ConfigurationProperties(prefix = "dxm.oss")
@Data
@Component
public class OSSProperties {

    /**
     * endPoint
     */
    private String endPoint;
    /**
     * 阿里云授权账号的accessKeyId
     */
    private String accessKeyId;

    /**
     * 阿里云授权账号的accessKeySecret
     */
    private String accessKeySecret;

    /**
     * 阿里云授权账号的roleArn
     */
    private String roleArn;

    /**
     * bucketName
     */
    private String bucketName;

    /**
     * 核心线程数
     */
    private Integer corePoolSize;
    /**
     * 最小分片文件大小阈值(单位M)
     */
    private Integer minPartFileSize;
}
