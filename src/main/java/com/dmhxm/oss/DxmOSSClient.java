package com.dmhxm.oss;

import com.aliyun.oss.ClientConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.CannedAccessControlList;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import com.dmhxm.oss.internal.DxmDownLoadOperation;
import com.dmhxm.oss.internal.DxmPartUploadOperation;
import com.dmhxm.oss.internal.DxmSimpleUploadOperation;
import com.dmhxm.oss.internal.OSSClientFactory;
import com.dmhxm.oss.result.ResponseResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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

    private OSSProperties ossProperties;

    @Override
    public ResponseResult<String> upLoad(MultipartFile file, String fileName) {
        return this.upLoad(file, fileName, null);
    }

    @Override
    public ResponseResult<String> upLoad(MultipartFile file, String fileName, CannedAccessControlList cannedAcl) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        //判断文件大小
        long fileSize = 0;
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
            fileSize = inputStream.available();
        } catch (IOException e) {
            log.error("读取文件大小错误！");
            return ResponseResult.fail();
        } finally {
            if (inputStream != null) {
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
            if (StringUtils.isBlank(upLoad)) {
                return ResponseResult.fail();
            }
            return ResponseResult.success(upLoad);
        }
        //分片
        DxmPartUploadOperation dxmPartUploadOperation = new DxmPartUploadOperation();
        return dxmPartUploadOperation.partUpLoad(file, fileSize, fileName, cannedAcl, oss, ossProperties);
    }

    @Override
    public void coverDownLoad(String fileName, HttpServletResponse response) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        DxmDownLoadOperation.coverDownLoad(oss, ossProperties, fileName, response);
    }

    @Override
    public void fileDownLoad(String fileName, HttpServletResponse response) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        DxmDownLoadOperation.simpleDownLoad(oss, ossProperties, fileName, response);
    }

    @Override
    public void fileDownLoad(String fileName, Long expiration, HttpServletResponse response) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        String realFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        DxmDownLoadOperation.accessDownLoad(oss, ossProperties, fileName, realFileName, expiration, response, 0);
    }

    @Override
    public String getFileUrl(String fileName) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        return DxmDownLoadOperation.fileUrl(oss, ossProperties, fileName, 0);
    }

    @Override
    public String getFileUrl(String fileName, Long expiration) {
        OSS oss = OSSClientFactory.getInstance(ossProperties);
        return DxmDownLoadOperation.fileUrl(oss, ossProperties, fileName, expiration);
    }
}
