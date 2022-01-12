package com.dmhxm.oss;

import com.aliyun.oss.model.CannedAccessControlList;
import com.dmhxm.oss.result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.awt.im.InputContext;
import java.io.File;
import java.io.InputStream;

/**
 * @author jinyingxin
 * @since 2021/10/12 10:50
 */
public interface DxmOSS {

    /**
     * 上传文件
     *
     * @param file     文件
     * @param fileName 文件名（oos路径+文件名） eg ： video/123.jpg
     * @return json
     */
    ResponseResult<String> upLoad(MultipartFile file, String fileName);

    /**
     * 上传文件
     *
     * @param file      文件
     * @param fileName  文件名（oos路径+文件名） eg ： video/123.jpg
     * @param cannedAcl 指定文件权限
     * @return json
     */
    ResponseResult<String> upLoad(MultipartFile file, String fileName, CannedAccessControlList cannedAcl);

    /**
     * 下载封面
     *
     * @param fileName 文件名（oos路径+文件名） eg ： video/123.jpg
     * @param response resp
     */
    void coverDownLoad(String fileName, HttpServletResponse response);

    /**
     * 下载文件
     *
     * @param fileName 文件名（oos路径+文件名） eg ： video/123.jpg
     * @param response resp
     */
    void fileDownLoad(String fileName, HttpServletResponse response);

    /**
     * 下载文件
     *
     * @param fileName   文件名（oos路径+文件名） eg ： video/123.jpg
     * @param response   resp
     * @param expiration 文件私有需设置过期时间 eg : 60  (单位 秒s)
     */
    void fileDownLoad(String fileName, Long expiration, HttpServletResponse response);

    /**
     * 获取文件访问链接 可通过浏览器直接访问的URL
     *
     * @param fileName 文件名（oos路径+文件名） eg ： video/123.jpg
     * @param response resp
     * @return 访问链接
     */
    String getFileUrl(String fileName);

    /**
     * 获取文件访问链接 可通过浏览器直接访问的URL
     *
     * @param fileName   文件名（oos路径+文件名） eg ： video/123.jpg
     * @param response   resp
     * @param expiration 文件私有需设置过期时间 eg : 60  (单位 秒s)
     * @return 访问链接
     */
    String getFileUrl(String fileName, Long expiration);

}
