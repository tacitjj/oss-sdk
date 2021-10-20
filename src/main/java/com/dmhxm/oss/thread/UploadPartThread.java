package com.dmhxm.oss.thread;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.UploadPartRequest;
import com.aliyun.oss.model.UploadPartResult;
import com.dmhxm.oss.model.MyPartETag;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * @author jinyingxin
 * @since 2021/10/20 11:33
 */
@Slf4j
public class UploadPartThread implements Callable<UploadPartThread>, Serializable {

    private static final long serialVersionUID = 1L;

    private final OSSClient client;
    private final File uploadFile;
    private final String bucket;
    private final String object;
    private final long start;
    private final long size;
    private final int partId;
    private String uploadId;

    private MyPartETag myPartETag;

    public UploadPartThread(OSSClient client, String bucket, String object,
                            File uploadFile, String uploadId, int partId,
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
        InputStream in = null;
        try {
            in = new FileInputStream(uploadFile);
            long skip = in.skip(start);

            UploadPartRequest uploadPartRequest = new UploadPartRequest();
            uploadPartRequest.setBucketName(bucket);
            uploadPartRequest.setKey(object);
            uploadPartRequest.setUploadId(uploadId);
            uploadPartRequest.setInputStream(in);
            uploadPartRequest.setPartSize(size);
            uploadPartRequest.setPartNumber(partId);
            UploadPartResult uploadPartResult = client.uploadPart(uploadPartRequest);
            //MyPartETag是对uploadPartResult.getPartETag()的返回值PartETag的封装
            myPartETag = new MyPartETag(uploadPartResult.getPartETag());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("==" + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    log.error("====关闭流失败：" + e.getMessage());
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
