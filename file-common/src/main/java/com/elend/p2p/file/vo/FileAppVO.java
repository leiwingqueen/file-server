package com.elend.p2p.file.vo;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.elend.p2p.file.model.FileAppPO;

public class FileAppVO extends FileAppPO {
    public FileAppVO(FileAppPO po) {
        this.id = po.getId();
        this.appId = po.getAppId();
        this.appKey = po.getAppKey();
        this.createTime = po.getCreateTime();
        this.updateTime = po.getUpdateTime();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this,
                                                  ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
