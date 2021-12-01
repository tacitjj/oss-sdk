package com.dmhxm.oss.internal;

import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jinyingxin
 * @since 2021/10/12 17:55
 */
@Slf4j
public class DxmSimpleUploadOperation {

    public String simpleUpLoad(MultipartFile file, String fileName, CannedAccessControlList cannedAcl, OSS oss, OSSProperties ossProperties) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), fileName, inputStream);
            if (cannedAcl != null) {
                // 如果需要上传时设置存储类型与访问权限
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
                metadata.setObjectAcl(cannedAcl);
                putObjectRequest.setMetadata(metadata);
            }
            oss.putObject(putObjectRequest);
            // 关闭OSSClient。
            oss.shutdown();
            return fileName;
        } catch (IOException e) {
            log.warn("简单上传失败！");
        } finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭文件流异常");
                }
            }
        }
        return null;
    }
}
