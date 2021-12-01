package com.dmhxm.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import com.dmhxm.oss.internal.DxmPartUploadOperation;
import com.dmhxm.oss.internal.DxmSimpleUploadOperation;
import com.dmhxm.oss.result.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author jinyingxin
 * @since 2021/10/12 10:43
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class DxmOSSClient implements DxmOSS {

    public static OSS oss;

    private OSSProperties ossProperties;

    @Override
    public ResponseResult<String> upLoad(MultipartFile file, String fileName) {
        return this.upLoad(file, fileName, null);
    }

    @Override
    public ResponseResult<String> upLoad(MultipartFile file, String fileName, CannedAccessControlList cannedAcl) {
        //链接超时
        ClientConfiguration conf = new ClientConfiguration();
        conf.setIdleConnectionTime(5000);
        if (ossProperties.getIsSTS()) {
            oss = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(),conf);
        } else {
            oss = new DxmOSSClientBuilder().build(ossProperties.getAccessKeyId(), ossProperties.getAccessKeySecret(), ossProperties.getEndPoint(), ossProperties.getBucketName(), ossProperties.getRoleArn(),conf);
        }
        //判断文件大小
        long fileSize = 0;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            fileSize = inputStream.available();
        } catch (IOException e) {
            log.error("读取文件大小错误！");
            return ResponseResult.fail();
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭文件流异常");
                }
            }
        }
        if (fileSize < (ossProperties.getMinPartFileSize() * org.apache.tomcat.jni.File.APR_FINFO_UPROT)) {
            DxmSimpleUploadOperation dxmSimpleUploadOperation = new DxmSimpleUploadOperation();
            String upLoad = dxmSimpleUploadOperation.simpleUpLoad(file, fileName, cannedAcl, oss, ossProperties);
            if(StringUtils.isBlank(upLoad)){
                return ResponseResult.fail();
            }
            return ResponseResult.success(upLoad);
        }
        //分片
        DxmPartUploadOperation dxmPartUploadOperation = new DxmPartUploadOperation();
        return dxmPartUploadOperation.partUpLoad(file,fileSize, fileName, cannedAcl, oss, ossProperties);
    }

}
