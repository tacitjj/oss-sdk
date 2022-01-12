package com.dmhxm.oss.internal;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.dmhxm.oss.configuration.properties.OSSProperties;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @author jinyingxin
 * @since 2021/12
 */
@Slf4j
public class DxmDownLoadOperation {

    public static void simpleDownLoad(OSS oss, OSSProperties ossProperties, String fileName, HttpServletResponse response) {
        String realFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        accessDownLoad(oss, ossProperties, fileName, realFileName, 3600L * 1000 * 24 * 365 * 10, response, 0);

    }

    public static void accessDownLoad(OSS oss, OSSProperties ossProperties, String fileName, String realFileName, Long expiration, HttpServletResponse response, int isCover) {
        // 设置签名URL过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        OutputStream out = null;
        InputStream inputStream = null;
        try {
            // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), fileName, HttpMethod.GET);
            if (isCover == 1) {
                request.setProcess("video/snapshot,t_1000,m_fast,f_jpg");
            }
            request.setExpiration(expirationDate);
            URL url = oss.generatePresignedUrl(request);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //得到输入流
            inputStream = conn.getInputStream();
            response.reset();
            response.setContentType("application/octet-stream;charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(realFileName, "utf-8"));
            int len = 0;
            //创建数据缓冲区
            byte[] buffer = new byte[1024];
            out = response.getOutputStream();
            while ((len = inputStream.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            log.warn("文件下载异常！" + e);
        } finally {
            try {
                // 关闭OSSClient。
                oss.shutdown();
                if (out != null) {
                    out.close();
                }

                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                log.warn("关闭流异常！" + e.getMessage(), e);
            }

        }
    }

    public static String fileUrl(OSS oss, OSSProperties ossProperties, String fileName, long expiration) {
        String realFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
        // 设置签名URL过期时间
        Date expirationDate = null;
        if (expiration == 0) {
            expirationDate = new Date(System.currentTimeMillis() + 3600L * 1000 * 24 * 365 * 10 * 1000);
        } else {
            expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        }

        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), fileName, HttpMethod.GET);
        request.setExpiration(expirationDate);
        URL url = oss.generatePresignedUrl(request);
        if (url != null) {
            return url.toString();
        }
        return null;

    }

    public static void coverDownLoad(OSS oss, OSSProperties ossProperties, String fileName, HttpServletResponse response) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(ossProperties.getBucketName(), fileName, HttpMethod.GET);
        request.setProcess("video/snapshot,t_1000,m_fast,f_jpg");
        String realFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")) + ".jpg";
        // 设置URL过期时间为10年  3600l* 1000*24*365*10
        accessDownLoad(oss, ossProperties, fileName, realFileName, 3600L * 1000 * 24 * 365 * 10, response, 1);
    }


}
