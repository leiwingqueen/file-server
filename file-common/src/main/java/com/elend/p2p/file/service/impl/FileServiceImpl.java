package com.elend.p2p.file.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elend.p2p.Result;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.file.FilePartition;
import com.elend.p2p.file.Partition;
import com.elend.p2p.file.RoundRobinFilePathStrategy;
import com.elend.p2p.file.exception.FileNotFoundException;
import com.elend.p2p.file.facade.FileManageFacade;
import com.elend.p2p.file.facade.vo.FileInfo;
import com.elend.p2p.file.mapper.FileManageMapper;
import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.service.FileService;
import com.elend.p2p.file.util.EncryptFileUtil;
import com.elend.p2p.file.util.FileUtil;
import com.elend.p2p.file.util.MD5FileUtil;
import com.elend.p2p.file.vo.FileManageVO;
import com.elend.p2p.util.ShortURL;

@Service
public class FileServiceImpl implements FileService {
    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private FileManageMapper mapper;

    @Autowired
    private FilePartition partition;
    @Autowired
    private Partition par;
    @Autowired
    private RoundRobinFilePathStrategy stratgey;
    @Autowired
    private FileManageFacade fileManageFacade;

    @Override
    public Result<FileUploadInfo> upload(String appId, InputStream in,
            String oriFileName, FileType fileType) {
        /**
         * 1.参数校验
         */
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(oriFileName)){
            return new Result<FileService.FileUploadInfo>(ResultCode.FAILURE, null, "appId,oriFileName不能为空");
        }
        if(in==null){
            return new Result<FileService.FileUploadInfo>(ResultCode.FAILURE, null, "文件不能为空");
        }
        if(fileType==null){
            return new Result<FileService.FileUploadInfo>(ResultCode.FAILURE, null, "文件类型不能为空");
        }
        /**
         * 2.文件拷贝
         */
        String path = stratgey.getPath(appId, fileType);
        String fileName = ShortURL.MD5Encode(UUID.randomUUID().toString())
                + EncryptFileUtil.getFilePrex(oriFileName);
        String md5 = "";
        int size = 0;
        try {
            size = in.available();
            FileUtil.copyFile(in, path, fileName);
            md5 = MD5FileUtil.getFileMD5String(path + fileName);
        } catch (Exception e) {
            logger.error("文件拷贝失败...失败原因:" + e.getMessage(), e);
            return new Result<FileUploadInfo>(ResultCode.FAILURE, null,
                                              "文件上传失败");
        }
        logger.info("file:{},md5:{}", path + fileName, md5);
        /**
         * 3.写入文件索引
         */
        FileManage file = new FileManage();
        file.setAppId(appId);
        file.setOrgName(oriFileName);
        file.setCreator(appId);
        file.setFileMd5(md5);
        file.setFileName(fileName);
        //只保存相对路径
        String relativePath=path.substring(partition.getBasePath().length());
        file.setPath(relativePath);
        file.setSize(size);
        file.setPartitionId(0);
        file.setEncrypt((short) 0);
        file.setFileType(fileType.name());
        mapper.insert(file);
        logger.info("保存文件索引成功...file:{}", file);
        return new Result<FileUploadInfo>(ResultCode.SUCCESS,
                                          new FileUploadInfo(file, fileType,
                                                             par.getFileServer()));
    }
    
    @Override
    public Result<FileDownloadInfo> download(String appId, long fileId) {
        /**
         * 1.参数校验
         */
        if(StringUtils.isBlank(appId)){
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "appId不能为空");
        }
        if(fileId<=0){
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "fileId不能小于等于0");
        }
        /**
         * 2.查找文件索引
         */
        FileManageVO vo=mapper.get(fileId);
        if (vo == null) {
            logger.error("找不到对应的数据库文件索引...fileId:{}",fileId);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "找不到对应的文件,fileId:"+fileId);
        }
        /**
         * 3.验证文件的归属账号是否一致
         */
        if(FileType.PRIVATE.name().toLowerCase().equals(vo.getFileType().toLowerCase())&&!appId.equals(vo.getAppId())){
            logger.error("文件归属账号不一致...fileId:{},appId:{},用户输入的appId:{}",fileId,vo.getAppId(),appId);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "找不到对应的文件,fileId:"+fileId);
        }
        /**
         * 4.旧的文件下载兼容
         */
        String fullPath=partition.getBasePath()+vo.getPath() + vo.getFileName();//文件存放的完整路径
        if(FileType.OLD.name().toLowerCase().equals(vo.getFileType().toLowerCase())){
            logger.info("旧文件下载兼容...fileId:{},appId:{},用户输入的appId:{}",fileId,vo.getAppId(),appId);
            try {
                FileInfo oldFile=fileManageFacade.download(appId, fileId, appId);
                fullPath=oldFile.getFullPath();
            } catch (FileNotFoundException |IOException e) {
                logger.error("旧文件下载失败...失败原因:"+e.getMessage(),e);
                return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "文件下载失败,"+e.getMessage());
            } 
        }
        /**
         * 5.读取文件
         */
        File file=new File(fullPath);
        if(!file.exists()){
            logger.error("找不到对应的文件...fullPath:{}", fullPath);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "找不到对应的文件,fileId:"+fileId);
        }
        FileDownloadInfo info=new FileDownloadInfo();
        info.setFile(file);
        info.setFileId(fileId);
        info.setOriFileName(StringUtils.isBlank(vo.getOrgName())?"":vo.getOrgName());
        return new Result<FileDownloadInfo>(ResultCode.SUCCESS, info);
    }
}
