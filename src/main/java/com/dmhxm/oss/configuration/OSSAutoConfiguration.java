package com.dmhxm.oss.configuration;

import com.dmhxm.oss.DxmOSSClient;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * oss自动配置类
 * </p>
 *
 * @author jinyingxin
 * @since 2021/10/11 18:17
 */
@Configuration
@EnableConfigurationProperties(OSSProperties.class)
@ConditionalOnProperty(
        prefix = "oss",
        name = "isOpen",
        havingValue = "true"
)
@ConditionalOnWebApplication
public class OSSAutoConfiguration {

    private final OSSProperties ossProperties;

    public OSSAutoConfiguration(OSSProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean(name = "dxmOSSClient")
    public DxmOSSClient dxmOSSClient() {
        return new DxmOSSClient(ossProperties);
    }




















//    @Bean(name = "ossClientFactory")
//    public DxmOSS ossClientFactory() {
//        OSSClientFactoryBean ossClientFactoryBean = new OSSClientFactoryBean();
//        ossClientFactoryBean.setIsSTS(ossProperties.getIsSTS());
//        if (StringUtils.isNotBlank(ossProperties.getEndPoint())) {
//            ossClientFactoryBean.setEndPoint(ossProperties.getEndPoint());
//        }
//        if (StringUtils.isNotBlank(ossProperties.getAccessKeyId())) {
//            ossClientFactoryBean.setAccessKeyId(ossProperties.getAccessKeyId());
//        }
//        if (StringUtils.isNotBlank(ossProperties.getAccessKeySecret())) {
//            ossClientFactoryBean.setAccessKeySecret(ossProperties.getAccessKeySecret());
//        }
//        if (StringUtils.isNotBlank(ossProperties.getRoleArn())) {
//            ossClientFactoryBean.setRoleArn(ossProperties.getRoleArn());
//        }
//        if (StringUtils.isNotBlank(ossProperties.getBucketName())) {
//            ossClientFactoryBean.setBucketName(ossProperties.getBucketName());
//        }
//        if (ossProperties.getMinPartFileSize() != null && ossProperties.getMinPartFileSize() > 0) {
//            ossClientFactoryBean.setMinPartFileSize(ossProperties.getMinPartFileSize());
//        }
//        if(ossProperties.getSinglePartFileSize() != null && ossProperties.getSinglePartFileSize() > 0){
//            ossClientFactoryBean.setSinglePartFileSize(ossProperties.getSinglePartFileSize());
//        }
//
//        if(ossProperties.getIsSTS()){
////            OSSClientSTSFactory
//        }
//        return new DxmOSSClient();
//    }
}
