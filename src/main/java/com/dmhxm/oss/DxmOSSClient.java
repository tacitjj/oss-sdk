package com.dmhxm.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.CannedAccessControlList;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import com.dmhxm.oss.internal.DxmPartUploadOperation;
import com.dmhxm.oss.internal.DxmSimpleUploadOperation;
import com.dmhxm.oss.result.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;

/**
 * @author jinyingxin
 * @since 2021/10/12 10:43
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DxmOSSClient implements DxmOSS {

    private OSSProperties ossProperties;

    @Override
    public ResponseResult<String> upLoad(File file, String fileName) {
        return this.upLoad(file, fileName, null);
    }

    @Override
    public ResponseResult<String> upLoad(File file, String fileName, CannedAccessControlList cannedAcl) {
        OSS oss;
        //链接超时
        ClientConfiguration conf = new ClientConfiguration();
        conf.setIdleConnectionTime(5000);
        if (ossProperties.getIsSTS()) {
            oss = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(),conf);
        } else {
            oss = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(), ossProperties.getRoleArn(),conf);
        }
        //判断文件大小
        if ((file.length()) < (ossProperties.getMinPartFileSize() * org.apache.tomcat.jni.File.APR_FINFO_UPROT)) {
            DxmSimpleUploadOperation dxmSimpleUploadOperation = new DxmSimpleUploadOperation();
            return ResponseResult.success(dxmSimpleUploadOperation.simpleUpLoad(file, fileName, cannedAcl, oss, ossProperties));
        }
        //分片
        DxmPartUploadOperation dxmPartUploadOperation = new DxmPartUploadOperation();
        return ResponseResult.success(dxmPartUploadOperation.partUpLoad(file, fileName, cannedAcl, oss, ossProperties));
    }

}
