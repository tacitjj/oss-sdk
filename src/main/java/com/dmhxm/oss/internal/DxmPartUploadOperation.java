package com.dmhxm.oss.internal;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.*;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import com.dmhxm.oss.model.UploadPartObj;
import com.dmhxm.oss.result.ResponseResult;
import com.dmhxm.oss.thread.UploadPartThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author jinyingxin
 * @since 2021/10/12 17:55
 */
@Slf4j
public class DxmPartUploadOperation {

//    private static ExecutorService pool;

    public ResponseResult<String> partUpLoad(MultipartFile file, long fileSize, String fileName, CannedAccessControlList cannedAcl, OSS oss, OSSProperties ossProperties) {
        //默认五十兆
        long bytePartSize = 5 * 1024L * 1024L;
        UploadPartObj uploadPartObj = new UploadPartObj();
        //初始化
        String uploadId = initMultipartUpload(oss, ossProperties.getBucketName(), fileName, "DigestUtil.md5Hex(stream)");
        //需要上传的文件分块数
        int partCount = (int) calPartCount(fileSize, bytePartSize);
        for (int i = 0; i < partCount; i++) {
            long start = bytePartSize * i;
            long curPartSize = Math.min(bytePartSize, fileSize - start);
            //构造上传线程，UploadPartThread是执行每个分块上传任务的线程
            uploadPartObj.getUploadPartThreads().add(new UploadPartThread((OSSClient) oss, ossProperties.getBucketName(), fileName, file, uploadId, i + 1, bytePartSize * i, curPartSize));
        }
        //多线程上传
        ThreadFactory ossExecutor = new ThreadFactoryBuilder().setDaemon(true).setNamePrefix("ossExecutor").build();
        ExecutorService pool = new ThreadPoolExecutor(ossProperties.getCorePoolSize(),
                ossProperties.getCorePoolSize(),
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), ossExecutor);
        for (int i = 0; i < uploadPartObj.getUploadPartThreads().size(); i++) {
            if (uploadPartObj.getUploadPartThreads().get(i).getMyPartETag() == null) {
                pool.submit(uploadPartObj.getUploadPartThreads().get(i));
            }
        }

        pool.shutdown();
        while (!pool.isTerminated()) {
            //循环检查线程池
            try {
                boolean b = pool.awaitTermination(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //判断上传结果
        for (UploadPartThread uploadPartThread : uploadPartObj.getUploadPartThreads()) {
            if (uploadPartThread.getMyPartETag() == null) {
                delPartFile(oss, ossProperties.getBucketName(), fileName, uploadId);
                return ResponseResult.fail();
            }
        }
        completeMultipartUpload(oss, ossProperties.getBucketName(), fileName, uploadPartObj, cannedAcl);
        return ResponseResult.success(fileName);
    }


    /**
     * 根据文件的大小和每个Part的大小计算需要划分的Part个数
     *
     * @param available 文件大小
     * @param partSize  partSize
     * @return 分片数目
     */
    private static long calPartCount(long available, long partSize) {
        long partCount = (available / partSize);
        if (available % partSize != 0) {
            partCount++;
        }
        return partCount;
    }

    private static String initMultipartUpload(OSS client, String bucketName, String key, String fileMD5Str) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
//        objectMetadata.getUserMetadata().put("x-oss-meta-my-md5", fileMD5Str);
        InitiateMultipartUploadRequest initUploadRequest = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult initResult = client.initiateMultipartUpload(initUploadRequest);
        return initResult.getUploadId();
    }

    private static void completeMultipartUpload(OSS client, String bucketName, String key, UploadPartObj uploadPartObj, CannedAccessControlList cannedAcl) {
        List<PartETag> eTags = new ArrayList<>();
        for (UploadPartThread uploadPartThread : uploadPartObj.getUploadPartThreads()) {
            eTags.add(new PartETag(uploadPartThread.getMyPartETag().getPartNumber(), uploadPartThread.getMyPartETag().getETag()));
        }
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadPartObj.getUploadPartThreads().get(0).getUploadId(), eTags);
        if (cannedAcl != null) {
            // 指定权限
            completeMultipartUploadRequest.setObjectACL(cannedAcl);
        }
        client.completeMultipartUpload(completeMultipartUploadRequest);
        client.shutdown();
    }

    private static void delPartFile(OSS client, String bucketName, String objectName, String uploadId) {
        // 取消分片上传。
        AbortMultipartUploadRequest abortMultipartUploadRequest =
                new AbortMultipartUploadRequest(bucketName, objectName, uploadId);
        client.abortMultipartUpload(abortMultipartUploadRequest);
        // 关闭OSSClient。
        client.shutdown();
    }
}
