package com.elend.p2p.file;

import org.apache.commons.lang3.StringUtils;

/**
 * 文件分区(文件服务2.0)
 * 
 * @author liyongquan
 */
public class FilePartition {
    /**文件服务器挂载目录 /data1/file_manage*/
    private String basePath;
    /**
     * 分区列表字符串，使用逗号分隔
     */
    private String partitions;

    public String[] getPartitionList() {
        //00 为默认的分区
        if(StringUtils.isBlank(partitions)||partitions.split(",")==null||partitions.split(",").length<=0)return new String[]{"00"};
        return partitions.split(",");
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getPartitions() {
        return partitions;
    }

    public void setPartitions(String partitions) {
        this.partitions = partitions;
    }
}
