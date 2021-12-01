package com.dmhxm.oss.model;

import com.dmhxm.oss.thread.UploadPartThread;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author jinyingxin
 * @since 2021/10/20 11:39
 */
public class UploadPartObj implements Serializable {

    private static final long serialVersionUID = 1L;

    List<UploadPartThread> uploadPartThreads = Collections.synchronizedList(new ArrayList<>());

    public List<UploadPartThread> getUploadPartThreads() {
        return uploadPartThreads;
    }

    public void setUploadPartThreads(List<UploadPartThread> uploadPartThreads) {
        this.uploadPartThreads = uploadPartThreads;
    }
}
