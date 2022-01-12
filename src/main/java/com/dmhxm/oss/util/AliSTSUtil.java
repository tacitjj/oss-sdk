package com.dmhxm.oss.util;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.auth.sts.AssumeRoleRequest;
import com.aliyuncs.auth.sts.AssumeRoleResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  sts工具类
 * </p>
 *
 * @author jinyingxin
 * @since 2021/10/12 10:29
 */
@Slf4j
public class AliSTSUtil {

    private static String STSAccessKeyId;
    private static String STSAccessKeySecret;
    private static String STSSecurityToken;
    private static LocalDateTime Expiration;

    public static Map<String, String> getToken(String accessKeyId,String accessKeySecret,String bucketName,String roleArn) {
        if (Expiration != null) {
            if (Expiration.isBefore(LocalDateTime.now())) {
                refreshSTS(accessKeyId,accessKeySecret,bucketName,roleArn);
            }
        } else {
            refreshSTS(accessKeyId,accessKeySecret,bucketName,roleArn);
        }
        Map<String, String> map = new HashMap<>();
        map.put("AccessKeyId", STSAccessKeyId);
        map.put("Expiration", Expiration.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        map.put("AccessKeySecret", STSAccessKeySecret);
        map.put("SecurityToken", STSSecurityToken);
        return map;
    }

    private static void refreshSTS(String accessKeyId,String accessKeySecret,String bucketName,String roleArn) {
        String endpoint = "sts.aliyuncs.com";
        String policy = "{\n" +
                "    \"Version\": \"1\", \n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"oss:*\"\n" +
                "            ], \n" +
                "            \"Resource\": [\n" +
                "                \"acs:oss:*:*:*\" \n" +
                "            ], \n" +
                "            \"Effect\": \"Allow\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        try {
            final AssumeRoleRequest request = new AssumeRoleRequest();
            request.setMethod(MethodType.POST);
            request.setRoleArn(roleArn);
            request.setRoleSessionName(bucketName);
            request.setPolicy(policy); // Optional
            request.setDurationSeconds(3600L); // Optional

            // 添加endpoint（直接使用STS endpoint，前两个参数留空，无需添加region ID）
            DefaultProfile.addEndpoint("", "", "Sts", endpoint);
            IClientProfile profile = DefaultProfile.getProfile(
                    "",
                    accessKeyId,
                    accessKeySecret);
            //用profile构造client
            DefaultAcsClient client = new DefaultAcsClient(profile);

            final AssumeRoleResponse response = client.getAcsResponse(request);
            Expiration = LocalDateTime.now().plusSeconds(2500L);
            STSAccessKeyId = response.getCredentials().getAccessKeyId();
            STSAccessKeySecret = response.getCredentials().getAccessKeySecret();
            STSSecurityToken = response.getCredentials().getSecurityToken();
        } catch (ClientException e) {
            log.info("Failed");
            log.info("ErrorCode" + e.getErrCode());
            log.info("ErrorMessage" + e.getErrMsg());
            log.info("RequestId" + e.getRequestId());
        }
    }

    public static void main(String[] args) {

    }
}
