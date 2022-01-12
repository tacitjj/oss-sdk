package com.dmhxm.oss.thread;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import com.dmhxm.oss.DxmOSSClient;
import com.dmhxm.oss.model.MyPartETag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * @author jinyingxin
 * @since 2021/10/20 11:33
 */
@Slf4j
public class UploadPartThread implements Callable<UploadPartThread>, Serializable {

    private static final long serialVersionUID = 1L;

    private final OSSClient client;
    private final MultipartFile uploadFile;
    private final String bucket;
    private final String object;
    private final long start;
    private final long size;
    private final int partId;
    private String uploadId;

    private MyPartETag myPartETag;

    public UploadPartThread(OSSClient client, String bucket, String object,
                            MultipartFile uploadFile, String uploadId, int partId,
                            long start, long partSize) {
        this.client = client;
        this.uploadFile = uploadFile;
        this.bucket = bucket;
        this.object = object;
        this.start = start;
        this.size = partSize;
        this.partId = partId;
        this.uploadId = uploadId;
    }

    @Override
    public UploadPartThread call() {
        InputStream inputStream = null;
        try {
            inputStream = uploadFile.getInputStream();
            long skip = inputStream.skip(start);
            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucket);
            uploadPartRequest.setKey(object);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(inputStream);
            uploadPartRequest.setPartSize(size);
            uploadPartRequest.setPartNumber(partId);
            UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
            log.warn("分片============" + partId + "完成！");
            //MyPartETag是对uploadPartResult.getPartETag()的返回值PartETag的封装
            myPartETag = new MyPartETag(uploadPartResult.getPartETag());
        } catch (Exception e) {
            log.warn("上传失败！" + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.warn("关闭文件流异常");
                }
            }
        }
        return this;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public MyPartETag getMyPartETag() {
        return myPartETag;
    }

    public void setMyPartETag(MyPartETag myPartETag) {
        this.myPartETag = myPartETag;
    }
}
