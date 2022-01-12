package com.dmhxm.oss.internal;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.dmhxm.oss.DxmOSSClientBuilder;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import org.apache.commons.lang3.StringUtils;

/**
 * @author jinyingxin
 * @since 2021/12/2 10:58
 */
public class OSSClientFactory {

    private static OSS ossClient = null;

    private OSSClientFactory() {
    }

    public static OSS getInstance(OSSProperties ossProperties) {
        // 可以使用ClientConfiguration对象设置代理服务器、最大重试次数等参数。
        //链接超时
        ClientConfiguration conf = new ClientConfiguration();
        conf.setIdleConnectionTime(5000);
        if (StringUtils.isBlank(ossProperties.getRoleArn())) {
            ossClient = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(), conf);
        } else {
            ossClient = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(), ossProperties.getRoleArn(), conf);
        }
        return ossClient;
    }
}
