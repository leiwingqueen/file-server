package com.elend.p2p.file.facade;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.Result;
import com.elend.p2p.file.service.FileService;
import com.elend.p2p.file.service.FileService.FileDownloadInfo;
import com.elend.p2p.file.service.FileService.FileType;
import com.elend.p2p.file.service.FileService.FileUploadInfo;

@Component
public class FileFacade {
    @Autowired
    private FileService fileService;
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
    public Result<FileUploadInfo> upload(String appId, InputStream in,
            String oriFileName,FileType fileType){
        return fileService.upload(appId, in, oriFileName, fileType);
    }
    
    /**
     * 文件下载
     * @param appId
     * --系统ID
     * @param fileId
     * --文件ID
     * @return 
     * 需要下载的文件
     */
    public Result<FileDownloadInfo> download(String appId, long fileId){
        return fileService.download(appId, fileId);
    }
}
