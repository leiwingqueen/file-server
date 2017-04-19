package com.elend.p2p.file.facade.vo;

import java.io.InputStream;
import java.util.Date;

import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.vo.FileManageVO;

/**
 * 对外返回的文件信息
 * 
 * @author liyongquan
 */
public class FileInfo {
    
    public static String IMAGE_PATH="/file/imgFileView/";
    
    private long fileId;

    /** 文件路径 */
    private String path;

    /** 原文件名 */
    private String orgName;

    /** 文件名 */
    private String fileName;

    /**
     * 下载的时候会反正一个文件的输入流
     */
    private InputStream inputStream;

    /** 文件完整路径 */
    private String fullPath;
    /**文件的MD5*/
    private String fileMd5;
    /**查看图片地址*/
    private String imageUrl;
    /**最后更新时间*/
    private Date updateTime;

    public FileInfo(FileManage fileManage,String fileServer) {
        this.fileId = fileManage.getFileId();
        this.path = fileManage.getPath();
        this.orgName = fileManage.getOrgName();
        this.fileName = fileManage.getFileName();
        this.fileMd5=fileManage.getFileMd5();
        this.imageUrl=fileServer+IMAGE_PATH+fileManage.getFileId()+".jpg";
        this.updateTime=fileManage.getLastUpdate();
    }

    public FileInfo(FileManageVO vo,String fileServer) {
        this((FileManage) vo,fileServer);
        this.inputStream = vo.getInputStream();
        this.fullPath = vo.getFullPath();
        this.updateTime=vo.getLastUpdate();
    }

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getFileMd5() {
        return fileMd5;
    }

    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
