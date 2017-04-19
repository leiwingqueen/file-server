package com.elend.p2p.file.service.impl;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.elend.p2p.file.FileEncrypt;
import com.elend.p2p.file.FilePathStrategy;
import com.elend.p2p.file.Partition;
import com.elend.p2p.file.exception.FileDecryptException;
import com.elend.p2p.file.exception.FileNotFoundException;
import com.elend.p2p.file.exception.PatitionNotFoundException;
import com.elend.p2p.file.mapper.FileManageMapper;
import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.service.FileManageService;
import com.elend.p2p.file.service.FileService.FileType;
import com.elend.p2p.file.util.EncryptFileUtil;
import com.elend.p2p.file.util.MD5FileUtil;
import com.elend.p2p.file.vo.FileManageVO;
import com.elend.p2p.util.ImgMarkLogoByIconUtil;

@Service
public class FileManageServiceImpl implements FileManageService {
    protected final static Logger logger = LoggerFactory.getLogger(FileManageServiceImpl.class);
    
    @Autowired
    private FileManageMapper mapper;

    @Autowired
    private FilePathStrategy filePathStrategy;

    @Autowired
    private Partition partition;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public FileManage upload(String appId, InputStream in,
            String oriFileName, String username, String des3Key)
            throws IOException, PatitionNotFoundException {
        String path = "";
        int partitionId = 0;
        int size = in.available();
        /**
         * 查找符合条件的分区
         */
        for (partitionId = 0; partitionId < partition.getPartitionList().size(); partitionId++) {
            path = filePathStrategy.getPath(appId, partitionId);
            String lastChar = String.valueOf(path.charAt(path.length() - 1));
            if (!lastChar.equals(File.separator)) {
                path += File.separator;
            }
            if (getPatitionFreeSpace(partitionId) > size)
                break;
        }
        if (path.equals("")) {
            throw new PatitionNotFoundException("找不到合适的合适的分区!");
        }
        /**文件拷贝*/
        String fileName = UUID.randomUUID().toString()
                + EncryptFileUtil.getFilePrex(oriFileName);
        FileEncrypt encrypt=FileEncrypt.NO;
        if(StringUtils.isNotBlank(des3Key)){//des3Key不为空时进行加密
            EncryptFileUtil.copyFileEncrypt(in, path, fileName,des3Key);
            encrypt=FileEncrypt.YES;
        }else{
            EncryptFileUtil.copyFile(in, path, fileName);
        }
        String md5 = MD5FileUtil.getFileMD5String(path + fileName);
        logger.info("file:{},md5:{}",path+fileName,md5);
        /** 文件去重，相同的文件只返回之前上传的文件 */
        /*
        FileManageSearchVO svo = new FileManageSearchVO();
        svo.setSize(1);
        svo.setFileMd5(md5);
        svo.setAppId(appId);
        List<FileManageVO> list = mapper.list(svo);
        if (list != null && list.size() > 0) {
            System.gc();// 这里一定要强制做一次gc，不然删除文件会失败
            boolean flag = EncryptFileUtil.deleteFile(path + fileName);
            if (!flag)
                logger.warn("删除失败");
            logger.info("file" + oriFileName + " already exsit!");
            return list.get(0);
        }*/
        FileManage file = new FileManage();
        file.setAppId(appId);
        file.setOrgName(oriFileName);
        file.setCreator(username);
        file.setFileMd5(md5);
        file.setFileName(fileName);
        //修改为只保存相对路径
        String relativePath=path.substring(partition.getBasePath().length());
        file.setPath(relativePath);
        file.setSize(size);
        file.setPartitionId(partitionId);
        file.setEncrypt(encrypt.getValue());
        file.setFileType(FileType.OLD.name());
        mapper.insert(file);
        return file;
    }
    
    
    
    
   
    /**上传带水印的图片
     * @param appId
     * @param in
     * @param oriFileName
     * @param username
     * @param des3Key
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public FileManage uploadIconImage(String appId, InputStream in,
            String oriFileName, String username, String des3Key)
            throws IOException, PatitionNotFoundException {
        String path = "";
        int partitionId = 0;
        int size = in.available();
              
        /**
         * 查找符合条件的分区
         */
        for (partitionId = 0; partitionId < partition.getPartitionList().size(); partitionId++) {
            path = filePathStrategy.getPath(appId, partitionId);
            String lastChar = String.valueOf(path.charAt(path.length() - 1));
            if (!lastChar.equals(File.separator)) {
                path += File.separator;
            }
            if (getPatitionFreeSpace(partitionId) > size)
                break;
        }
        if (path.equals("")) {
            throw new PatitionNotFoundException("找不到合适的分区!");
        }
        /**文件拷贝*/
        String uuid=UUID.randomUUID().toString();
        
        String fileName = 
        		uuid+ EncryptFileUtil.getFilePrex(oriFileName);
        
        String iconFileName = uuid
               +"Icon"+ EncryptFileUtil.getFilePrex(oriFileName);
             
        FileEncrypt encrypt=FileEncrypt.NO;
        if(StringUtils.isNotBlank(des3Key)){//des3Key不为空时进行加密
            EncryptFileUtil.copyFileEncrypt(in, path, fileName,des3Key);
            encrypt=FileEncrypt.YES;
        }else{
            EncryptFileUtil.copyFile(in, path, fileName);
        }
        String md5 = MD5FileUtil.getFileMD5String(path + fileName);
        logger.info("file:{},md5:{}",path+fileName,md5);
        /** 文件去重，相同的文件只返回之前上传的文件 */
        /*
        FileManageSearchVO svo = new FileManageSearchVO();
        svo.setSize(1);
        svo.setFileMd5(md5);
        svo.setAppId(appId);
        List<FileManageVO> list = mapper.list(svo);
        if (list != null && list.size() > 0) {
            System.gc();// 这里一定要强制做一次gc，不然删除文件会失败
            boolean flag = EncryptFileUtil.deleteFile(path + fileName);
            if (!flag)
                logger.warn("删除失败");
            logger.info("file" + oriFileName + " already exsit!");
            return list.get(0);
        }*/
        FileManage file = new FileManage();
        file.setAppId(appId);
        file.setOrgName(oriFileName);
        file.setCreator(username);
        file.setFileMd5(md5);
        file.setFileName(iconFileName);
        //修改为只保存相对路径       
        String relativePath=path.substring(partition.getBasePath().length());               
        //打水印       
     
        
        String iconPath=FileManageServiceImpl.class.getResource("/iconImage/icon.png").getPath();//	
            
        String srcFile=path+fileName;
        
        //判断是否为图片
        BufferedImage bi = ImageIO.read(new File(srcFile));
        if(bi == null){
           logger.error("需要加水印的文件   {} 不为图片文件  ",srcFile);
        }else {
        	   String iconFile=path+iconFileName;      
               ImgMarkLogoByIconUtil.markImageByIcon(iconPath,srcFile,iconFile );    
		}     
        
        file.setPath(relativePath);
        file.setSize(size);
        file.setPartitionId(partitionId);
        file.setEncrypt(encrypt.getValue());
        mapper.insert(file);
        return file;
    }
    
    

    @Override
    @Transactional(readOnly = true)
    public FileManageVO download(String appId, long id, String username,String des3Key)
            throws FileNotFoundException,FileDecryptException,IOException {
        FileManageVO vo=mapper.get(id);
        if (vo == null) {
            throw new FileNotFoundException();
        }
        /**
         * 如果文件没加密，直接返回改文件的输入流即可，如果文件已加密，则会在临时目录生成一个解密后的文件返回
         */
        //TODO 解密后的这些临时文件必须定时清除,上线后要做一个定时清理脚本
        FileEncrypt encrypt=FileEncrypt.from(vo.getEncrypt());
        File file=null;
        String fullPath=partition.getBasePath()+vo.getPath() + vo.getFileName();//文件存放的完整路径
        if(encrypt==FileEncrypt.NO){
            file = new File(fullPath);
        }else{//解密文件后存放到临时目录
            if(des3Key==null){
                logger.error("file has been encrypt,des3Key can not be null");
                throw new FileDecryptException();
            }
            File encryptFile = new File(fullPath);
            InputStream encryptFileIn;
            try {
                encryptFileIn = new FileInputStream(encryptFile);
            } catch (java.io.FileNotFoundException e) {
                logger.error(fullPath+" file not found");
                throw new FileNotFoundException();
            }
            String newFileName = UUID.randomUUID().toString()
                    + EncryptFileUtil.getFilePrex(vo.getOrgName());
            EncryptFileUtil.copyFileDecrypt(encryptFileIn, partition.getTempPath(), newFileName, des3Key);
            file=new File(partition.getTempPath()+File.separator+newFileName);
            //文件完整路径修改
            fullPath=partition.getTempPath()+File.separator+newFileName;
        }
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (java.io.FileNotFoundException e) {
            throw new FileNotFoundException();
        }
        vo.setInputStream(in);
        vo.setFullPath(fullPath);
        return vo;
    }

    @Override
    @Transactional(readOnly = true)
    public FileManage getInfo(String appId, long id, String username) {
        return mapper.get(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void delete(String appId, long id, String username)
            throws FileNotFoundException, IOException {
        FileManageVO fileManage = mapper.get(id);
        if (fileManage == null) {
            throw new FileNotFoundException();
        }
        String path = filePathStrategy.getDelPath(appId);
        File file = new File(partition.getBasePath()+fileManage.getPath() + fileManage.getFileName());
        InputStream in = null;
        try {
            in = new FileInputStream(file);
        } catch (IOException e) {
            throw new FileNotFoundException();
        }
        EncryptFileUtil.copyFile(in, path, fileManage.getFileName());
        EncryptFileUtil.deleteFile(partition.getBasePath()+fileManage.getPath() + fileManage.getFileName());
        fileManage.setPath(path);
        mapper.logicDelete(fileManage);
    }

    /**
     * 获取分区可用空间
     * 
     * @param partitionId
     * @return
     */
    private long getPatitionFreeSpace(int partitionId) {
        String par = partition.getPartitionList().get(partitionId);
        File f = new File(par);
        return f.getFreeSpace();
    }
    
    
    public static void main(String[] args) {
		System.out.println(FileManageServiceImpl.class.getResource("/"));
	}
    @Override
    @Transactional(readOnly = true)
    public List<List<String>> parsing(long id, String des3Key, String charset) throws FileNotFoundException,FileDecryptException,IOException {
        
        FileManageVO vo=mapper.get(id);
        if (vo == null) {
            throw new FileNotFoundException();
        }
        /**
         * 如果文件没加密，直接返回改文件的输入流即可，如果文件已加密，则会在临时目录生成一个解密后的文件返回
         */
        //TODO 解密后的这些临时文件必须定时清除,上线后要做一个定时清理脚本
        FileEncrypt encrypt=FileEncrypt.from(vo.getEncrypt());
        File file=null;
        String fullPath=partition.getBasePath()+vo.getPath() + vo.getFileName();//文件存放的完整路径
        if(encrypt==FileEncrypt.NO){
            file = new File(fullPath);
        }else{//解密文件后存放到临时目录
            if(des3Key==null){
                logger.error("file has been encrypt,des3Key can not be null");
                throw new FileDecryptException();
            }
            File encryptFile = new File(fullPath);
            InputStream encryptFileIn;
            try {
                encryptFileIn = new FileInputStream(encryptFile);
            } catch (java.io.FileNotFoundException e) {
                logger.error(fullPath+" file not found");
                throw new FileNotFoundException();
            }
            String newFileName = UUID.randomUUID().toString()
                    + EncryptFileUtil.getFilePrex(vo.getOrgName());
            EncryptFileUtil.copyFileDecrypt(encryptFileIn, partition.getTempPath(), newFileName, des3Key);
            file=new File(partition.getTempPath()+File.separator+newFileName);
            //文件完整路径修改
            fullPath=partition.getTempPath()+File.separator+newFileName;
        }
        BufferedReader reader = null; 
        List<List<String>> list = new ArrayList<List<String>>();
        try {
            //in = new FileReader(file);
            InputStreamReader read = new InputStreamReader(
                                                           new FileInputStream(
                                                                               file),
                                                           "gbk");
            reader = new BufferedReader(read);
            
            if (reader != null) {
                
                String bytesRead = null;
                while ((bytesRead = reader.readLine()) != null) {
                    // 保存
                    list.add(Arrays.asList(bytesRead.split(",")));
                }
                //reader.close();               
            }
            read.close();
            
        } catch (java.io.FileNotFoundException e) {
            throw new FileNotFoundException();
        } finally {
            if (reader != null) {
                reader.close();
            }
            
        }        
        return list;
    }
    @Override
    @Transactional(readOnly = true)
    public List<List<String>> parsing(long id, String des3Key)
                throws FileNotFoundException,FileDecryptException,IOException {
        return parsing(id, des3Key, "utf-8");
    }
    /*@Override
    @Transactional(readOnly = true)
    public List<List<String>> parsing(long id, String des3Key)
    		throws FileNotFoundException,FileDecryptException,IOException {
//    		String appId, InputStream in, String oriFileName,
//            String username, String des3Key) throws IOException,
//            PatitionNotFoundException{
    	//解析
//    	logger.info("{},{},{},{}",applyId, id, username,des3Key);
    	
    	FileManageVO vo=mapper.get(id);
    	if (vo == null) {
            throw new FileNotFoundException();
        }
        *//**
         * 如果文件没加密，直接返回改文件的输入流即可，如果文件已加密，则会在临时目录生成一个解密后的文件返回
         *//*
        //TODO 解密后的这些临时文件必须定时清除,上线后要做一个定时清理脚本
        FileEncrypt encrypt=FileEncrypt.from(vo.getEncrypt());
        File file=null;
        String fullPath=partition.getBasePath()+vo.getPath() + vo.getFileName();//文件存放的完整路径
        if(encrypt==FileEncrypt.NO){
            file = new File(fullPath);
        }else{//解密文件后存放到临时目录
            if(des3Key==null){
                logger.error("file has been encrypt,des3Key can not be null");
                throw new FileDecryptException();
            }
            File encryptFile = new File(fullPath);
            InputStream encryptFileIn;
            try {
                encryptFileIn = new FileInputStream(encryptFile);
            } catch (java.io.FileNotFoundException e) {
                logger.error(fullPath+" file not found");
                throw new FileNotFoundException();
            }
            String newFileName = UUID.randomUUID().toString()
                    + EncryptFileUtil.getFilePrex(vo.getOrgName());
            EncryptFileUtil.copyFileDecrypt(encryptFileIn, partition.getTempPath(), newFileName, des3Key);
            file=new File(partition.getTempPath()+File.separator+newFileName);
            //文件完整路径修改
            fullPath=partition.getTempPath()+File.separator+newFileName;
        }
        Reader in = null; 
        List<List<String>> list = new ArrayList<List<String>>();
        try {
            in = new FileReader(file);
            if (in != null) {
                BufferedReader reader = new BufferedReader(in);
                String bytesRead = null;
                while ((bytesRead = reader.readLine()) != null) {
                    // 保存
                    list.add(Arrays.asList(bytesRead.split(",")));
                }
                reader.close();               
            }
        } catch (java.io.FileNotFoundException e) {
            throw new FileNotFoundException();
        } finally {
            if (in != null) {
                in.close();
            }
            
        }        
    	return list;
    }*/
}
