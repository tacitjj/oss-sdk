package com.dmhxm.oss.internal;

import cn.hutool.crypto.digest.DigestUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import com.dmhxm.oss.model.UploadPartObj;
import com.dmhxm.oss.result.ResultStatus;
import com.dmhxm.oss.thread.UploadPartThread;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jinyingxin
 * @since 2021/10/12 17:55
 */
public class DxmPartUploadOperation {

    private static ExecutorService pool;

    public String partUpLoad(File file, String fileName, CannedAccessControlList cannedAcl, OSS oss, OSSProperties ossProperties) {
        int partSize = 50;
        //需要上传的文件分块数
        int partCount = calPartCount(file, partSize * org.apache.tomcat.jni.File.APR_FINFO_UPROT);

        UploadPartObj uploadPartObj = new UploadPartObj();
        //初始化
        String uploadId = initMultipartUpload(oss, ossProperties.getBucketName(), fileName, DigestUtil.md5Hex(file));

        for (int i = 0; i < partCount; i++) {
            long start = (long) partSize * i;
            long curPartSize = partSize < file.length() - start ? partSize : file.length() - start;
            //构造上传线程，UploadPartThread是执行每个分块上传任务的线程
            uploadPartObj.getUploadPartThreads().add(new UploadPartThread((OSSClient) oss, ossProperties.getBucketName(), fileName, file, uploadId, i + 1, (long) partSize * i, curPartSize));
        }
        //多线程上传
        if (pool == null) {
            pool = Executors.newFixedThreadPool(ossProperties.getCorePoolSize());
        }
        int i = 0;
        while (!upLoad(uploadPartObj).isResult()) {
            if (++i == ossProperties.getRetry()) break;
        }
        if (!uploadPartObj.isResult()) {
            return String.valueOf(ResultStatus.FAIL.getCode());
        }

        completeMultipartUpload(oss, ossProperties.getBucketName(), fileName, uploadPartObj, cannedAcl);
        return fileName;
    }


    /**
     * 根据文件的大小和每个Part的大小计算需要划分的Part个数
     *
     * @param f        file
     * @param partSize partSize
     * @return 分片数目
     */
    private static int calPartCount(File f, Integer partSize) {

        int partCount = (int) (f.length() / partSize);
        if (f.length() % partSize != 0) {
            partCount++;
        }
        return partCount;
    }

    private static String initMultipartUpload(OSS client, String bucketName, String key, String fileMD5Str) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.getUserMetadata().put("x-oss-meta-my-md5", fileMD5Str);
        InitiateMultipartUploadRequest initUploadRequest = new InitiateMultipartUploadRequest(bucketName, key, objectMetadata);
        InitiateMultipartUploadResult initResult = client.initiateMultipartUpload(initUploadRequest);
        return initResult.getUploadId();
    }

    /**
     * 多线程上传文件
     *
     * @param uploadPartObj 上传文件分片集合
     * @return 分片集合
     */
    private static UploadPartObj upLoad(UploadPartObj uploadPartObj) {
        uploadPartObj.setResult(true);
        //向线程池中submit单个文件所有分块上传线程
        for (int i = 0; i < uploadPartObj.getUploadPartThreads().size(); i++) {
            if (uploadPartObj.getUploadPartThreads().get(i).getMyPartETag() == null)
                pool.submit(uploadPartObj.getUploadPartThreads().get(i));
        }
        pool.shutdown();
        //判断上传结果
        for (UploadPartThread uploadPartThread : uploadPartObj.getUploadPartThreads()) {
            if (uploadPartThread.getMyPartETag() == null) {
                uploadPartObj.setResult(false);
                return uploadPartObj;
            }
        }
        return uploadPartObj;
    }

    private static void completeMultipartUpload(OSS client, String bucketName, String key, UploadPartObj uploadPartObj, CannedAccessControlList cannedAcl) {
        List<PartETag> eTags = new ArrayList<PartETag>();
        for (UploadPartThread uploadPartThread : uploadPartObj.getUploadPartThreads()) {
            eTags.add(new PartETag(uploadPartThread.getMyPartETag().getPartNumber(), uploadPartThread.getMyPartETag().getETag()));
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                new CompleteMultipartUploadRequest(bucketName, key, uploadPartObj.getUploadPartThreads().get(0).getUploadId(), eTags);
        // 指定权限
        completeMultipartUploadRequest.setObjectACL(cannedAcl);
        client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private static void delPartFile() {

    }
}
