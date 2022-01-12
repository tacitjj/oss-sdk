package com.dmhxm.oss.configuration;

import com.dmhxm.oss.DxmOSSClient;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import lombok.extern.slf4j.Slf4j;
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
@ConditionalOnWebApplication
@Slf4j
public class OSSAutoConfiguration {

    private final OSSProperties ossProperties;

    public OSSAutoConfiguration(OSSProperties ossProperties) {
        this.ossProperties = ossProperties;
    }

    @Bean(name = "dxmOSSClient")
    public DxmOSSClient dxmOSSClient() {
        log.info("加载oss配置" + ossProperties);
        return new DxmOSSClient(ossProperties);
    }
}
