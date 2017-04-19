package com.elend.p2p.file.service;

import java.io.File;
import java.io.InputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elend.p2p.Result;
import com.elend.p2p.file.model.FileManage;

/**
 * 文件服务(文件服务2.0)
 * @author liyongquan
 *
 */
public interface FileService {
    public static Logger log = LoggerFactory.getLogger(FileService.class);
    /**
     * 文件上传
     * @param appId
     * --系统ID
     * @param in
     * --输入流
     * @param oriFileName
     * --原文件名
     * @param fileType
     * 文件上传类型
     * @return
     * 文件上传结果
     */
    Result<FileUploadInfo> upload(String appId, InputStream in,
            String oriFileName,FileType fileType);
    
    /**
     * 文件下载
     * @param appId
     * --系统ID
     * @param fileId
     * --文件ID
     * @return 
     * 需要下载的文件
     */
    Result<FileDownloadInfo> download(String appId, long fileId);       
    
    /**
     * 文件上传信息
     * @author liyongquan
     *
     */
    public static class FileUploadInfo{
        public FileUploadInfo(){}
        /**
         * 
         * @param file
         * 文件对象
         * @param type
         * 文件类型
         * @param serverName
         * 服务器域名
         */
        public FileUploadInfo(FileManage file,FileType type,String serverName){
            this.fileType=type;
            this.fileId=file.getFileId();
            this.downloadUrl="";
            this.md5=file.getFileMd5();
            if(FileType.PRIVATE!=type&&StringUtils.isNotBlank(file.getPath())){
                try {
                    //去掉public的前缀
                    int index=file.getPath().indexOf(type.name().toLowerCase());
                    if(index>=0){
                        index=index+type.name().length();
                    }
                    this.downloadUrl=serverName+file.getPath().substring(index)+file.getFileName();
                } catch (Exception e) {
                    log.error("获取下载地址失败...file:{},type:{},nginxRoot:{}",file,type,serverName);
                    downloadUrl="";
                }
            }
        }
        /**
         * 文件类型(文件类型为PRIVATE需要通过fileId进行下载)
         */
        private FileType fileType;
        /**
         * 文件ID
         */
        private long fileId;
        /**
         * 文件下载路径(如果是PUBLIC的文件才会返回这个字段，否则返回为空串)
         */
        private String downloadUrl;
        /**
         * 文件上传md5;
         */
        private String md5;
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        public FileType getFileType() {
            return fileType;
        }
        public void setFileType(FileType fileType) {
            this.fileType = fileType;
        }
        public long getFileId() {
            return fileId;
        }
        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
        public String getDownloadUrl() {
            return downloadUrl;
        }
        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
        public String getMd5() {
            return md5;
        }
        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
    
    /**
     * 文件下载信息
     * @author liyongquan 2016年11月24日
     *
     */
    public static class FileDownloadInfo{
        /**
         * 文件ID
         */
        private long fileId;
        /**
         * 原文件名
         */
        private String oriFileName;
        /**
         * 文件
         */
        private File file;
        public long getFileId() {
            return fileId;
        }
        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
        public String getOriFileName() {
            return oriFileName;
        }
        public void setOriFileName(String oriFileName) {
            this.oriFileName = oriFileName;
        }
        public File getFile() {
            return file;
        }
        public void setFile(File file) {
            this.file = file;
        }
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
    
    /**
     * 文件上传类型
     *  对外开放的文件存放到nginx能够直接访问的目录路径(root)。
     *  内部隐私文件存放到另外的目录，需要通过resin的签名验证才能获取(p2p_web上传的文件只能由p2p_web的账号进行获取)。
     * @author liyongquan
     *
     */
    public static enum FileType{
        /**
         * 表示改造前上传的文件(旧文件)
         */
        OLD,
        /**
         * 对外公开访问的文件
         */
        PUBLIC,
        /**
         * 内部访问的隐私文件
         */
        PRIVATE;
        /**
         * 根据名称转换成对应的枚举(忽略大小写)
         * @param name
         * @return
         */
        public static FileType from(String name){
            if(StringUtils.isBlank(name)){
                log.error("name为空");
                return FileType.PUBLIC;
            }
            for(FileType type:FileType.values()){
                if(type.name().toLowerCase().equals(name.toLowerCase())){
                    return type;
                }
            }
            log.error("找不到对应的文件类型，返回PUBLIC,name:{}",name);
            return FileType.PUBLIC;
        }
    }
}
