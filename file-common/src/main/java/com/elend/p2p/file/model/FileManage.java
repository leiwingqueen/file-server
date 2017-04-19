package com.elend.p2p.file.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FileManage implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 8231746164189500632L;

    /** 流水号，文件id */
    private long fileId;

    /** 文件分区ID */
    /**
     * 文件服务改造后这个字段没有再使用
     */
    @Deprecated
    private int partitionId;

    /** 文件名称，这个是程序处理后生成的uuid名称，与硬件存放对应 */
    private String fileName;

    /** 文件md5 */
    private String fileMd5;

    /** 用户上传原文件名称，主要用于显示时使用 */
    private String orgName;

    /** 文件path, 如基本path+年+月+日,如/2012/09/02/ */
    private String path;

    /** 接入文件服务器的系统ID */
    private String appId;

    /** 创建时间 */
    private String createTime;

    /** 创建人 */
    private String creator;

    /** 文件大小 */
    private long size;

    /** 0:正常,1:已删除 */
    private int del;

    /** 最后修改时间 */
    private Date lastUpdate;

    /** 0 未加密，1已加密 */
    private short encrypt;
    /**
     * 文件类型
     * OLD--旧文件、PUBLIC--对外开放文件,PRIVATE--内部访问的隐私文件
     */
    private String fileType;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public int getPartitionId() {
        return partitionId;
    }

    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDel() {
        return del;
    }

    public void setDel(int del) {
        this.del = del;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public short getEncrypt() {
        return encrypt;
    }

    public void setEncrypt(short encrypt) {
        this.encrypt = encrypt;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
