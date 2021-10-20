package com.dmhxm.oss.internal;

import com.aliyun.oss.OSS;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.StorageClass;
import com.dmhxm.oss.configuration.properties.OSSProperties;

import java.io.File;

/**
 * @author jinyingxin
 * @since 2021/10/12 17:55
 */
public class DxmSimpleUploadOperation {

    public String simpleUpLoad(File file, String fileName, CannedAccessControlList cannedAcl, OSS oss, OSSProperties ossProperties) {

        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), fileName, file);
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
    }
}
