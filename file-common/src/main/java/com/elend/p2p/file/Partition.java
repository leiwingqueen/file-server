package com.elend.p2p.file;

import java.util.List;

/**
 * 文件分区
 * 
 * @author liyongquan
 */
public class Partition {
    @Deprecated //新文件上传改用FilePartition
    /**文件服务器挂载目录*/
    private String basePath;
    @Deprecated //新文件上传改用FilePartition
    /**分区列表*/
    private List<String> partitionList;
    @Deprecated //新文件上传改用FilePartition
    /**删除文件目录*/
    private String delPath;
    @Deprecated //新文件上传改用FilePartition
    /**解密后文件的临时目录，脚本定时清理*/
    private String tempPath;
    /**文件服务地址 默认地址为image.gzdai.com*/
    private String fileServer;

    public List<String> getPartitionList() {
        return partitionList;
    }

    public void setPartitionList(List<String> partitionList) {
        this.partitionList = partitionList;
    }

    public String getDelPath() {
        return delPath;
    }

    public void setDelPath(String delPath) {
        this.delPath = delPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }

    public String getBasePath() {
        return basePath;
    }

    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }
}
