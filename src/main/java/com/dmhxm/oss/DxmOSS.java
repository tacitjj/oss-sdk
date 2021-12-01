package com.dmhxm.oss;

import com.aliyun.oss.model.CannedAccessControlList;
import com.dmhxm.oss.result.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

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
     * @param stream     文件
     * @param fileName 文件名（oos路径+文件名） eg ： video/123.jpg
     * @return json
     */
    ResponseResult<String> upLoad(MultipartFile file, String fileName);

    /**
     * 上传文件
     *
     * @param stream      文件
     * @param fileName  文件名（oos路径+文件名） eg ： video/123.jpg
     * @param cannedAcl 指定文件权限
     * @return json
     */
    ResponseResult<String> upLoad(MultipartFile file, String fileName, CannedAccessControlList cannedAcl);

}
