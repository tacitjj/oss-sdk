package com.dmhxm.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;

/**
 * @author jinyingxin
 * @since 2021/10/12 11:01
 */
public interface DxmOSSBuilder {

    OSS build(String accessKeyId, String accessKeySecret, String endPoint, String bucketName, ClientConfiguration conf);

    OSS build(String accessKeyId, String accessKeySecret, String endPoint, String bucketName, String roleArn,ClientConfiguration conf);

}
