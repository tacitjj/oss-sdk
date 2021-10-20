package com.dmhxm.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.dmhxm.oss.util.AliSTSUtil;

import java.util.Map;

/**
 * @author jinyingxin
 * @since 2021/10/12 10:55
 */
public class DxmOSSClientBuilder implements DxmOSSBuilder {

    @Override
    public OSS build(String accessKeyId, String accessKeySecret, String endPoint, String bucketName,ClientConfiguration conf) {
        return new OSSClientBuilder().build(endPoint, accessKeyId, accessKeySecret);
    }

    @Override
    public OSS build(String accessKeyId, String accessKeySecret, String endPoint, String bucketName, String roleArn, ClientConfiguration conf) {
        Map<String, String> token = AliSTSUtil.getToken(accessKeyId, accessKeySecret, bucketName, roleArn);
        return new OSSClientBuilder().build(endPoint, token.get("AccessKeyId"), token.get("AccessKeySecret"), token.get("SecurityToken"));
    }
}
