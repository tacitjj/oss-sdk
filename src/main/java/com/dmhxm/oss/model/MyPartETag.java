package com.dmhxm.oss.model;

import com.aliyun.oss.model.PartETag;
import lombok.Data;

import java.io.Serializable;

/**
 * @author jinyingxin
 * @since 2021/10/20 11:40
 */
@Data
public class MyPartETag implements Serializable {

    private static final long serialVersionUID = 1L;

    private int partNumber;

    private String eTag;

    public MyPartETag(PartETag partETag ) {
        super();
        this.partNumber = partETag.getPartNumber();
        this.eTag = partETag.getETag();
    }
}
