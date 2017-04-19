package com.elend.p2p.file.facade;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.file.Partition;
import com.elend.p2p.file.exception.FileDecryptException;
import com.elend.p2p.file.exception.FileNotFoundException;
import com.elend.p2p.file.exception.PatitionNotFoundException;
import com.elend.p2p.file.facade.vo.FileInfo;
import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.service.FileManageService;
import com.elend.p2p.file.vo.FileManageVO;

@Component
public class FileManageFacade {

    @Autowired
    private FileManageService service;
    
    @Autowired
    private Partition partition;

    /**
     * 文件上传(不加密)
     * 
     * @param appId
     *            --接入的系统ID
     * @param in
     *            --输入流
     * @param oriFileName
     *            --原文件名
     * @param username
     *            --操作人
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    public FileInfo upload(String appId, InputStream in, String oriFileName,
            String username) throws IOException, PatitionNotFoundException {
        return this.upload(appId, in, oriFileName, username, null);
    }

    /**
     * 文件上传
     * 
     * @param appId
     *            --接入的系统ID
     * @param in
     *            --输入流
     * @param oriFileName
     *            --原文件名
     * @param username
     *            --操作人
     * @param des3Key--解密key
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    public FileInfo upload(String appId, InputStream in, String oriFileName,
            String username, String des3Key) throws IOException,
            PatitionNotFoundException {
        FileManage fileManage = service.upload(appId, in, oriFileName,
                                               username, des3Key);
        return new FileInfo(fileManage,partition.getFileServer());
    }
    
    
    
    /**
     * 水印图片上传(不加密)
     * 
     * @param appId
     *            --接入的系统ID
     * @param in
     *            --输入流
     * @param oriFileName
     *            --原文件名
     * @param username
     *            --操作人
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    public FileInfo uploadIconImage(String appId, InputStream in, String oriFileName,
            String username) throws IOException, PatitionNotFoundException {
        return this.uploadIconImage(appId, in, oriFileName, username, null);
    }
    
    
    /**上传水印图片
     * @param appId
     * @param in
     * @param oriFileName
     * @param username
     * @param des3Key
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    public FileInfo uploadIconImage(String appId, InputStream in, String oriFileName,
            String username, String des3Key) throws IOException,
            PatitionNotFoundException {
        FileManage fileManage = service.uploadIconImage(appId, in, oriFileName, username, des3Key);
        return new FileInfo(fileManage,partition.getFileServer());
    }
    

    /**
     * 文件下载(非加密文件使用)
     * 
     * @param appId
     *            --系统ID
     * @param id
     *            --文件ID
     * @param username
     *            --操作人
     * @return
     * @throws FileNotFoundException
     */
    public FileInfo download(String appId, long id, String username)
            throws FileNotFoundException,IOException {
       return this.download(appId, id, username, null);
    }
    
    /**
     * 文件下载
     * @param appId
     * @param id
     * @param username
     * @param des3Key
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public FileInfo download(String appId, long id, String username,String des3Key)
            throws FileNotFoundException,IOException {
        FileManageVO vo = service.download(appId, id, username,des3Key);
        return new FileInfo(vo,partition.getFileServer());
    }

    /**
     * 获取文件基本信息
     * 
     * @param appId
     *            --系统ID
     * @param id
     *            --文件ID
     * @param username
     *            --操作人
     * @return
     */
    public FileInfo getInfo(String appId, long id, String username) {
        FileManage fileManage = service.getInfo(appId, id, username);
        return new FileInfo(fileManage,partition.getFileServer());
    }

    /**
     * 删除文件(会移除到删除路径上)
     * 
     * @param appId
     *            --系统ID
     * @param id
     *            --文件ID
     * @param username
     *            --操作人
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void delete(String appId, long id, String username)
            throws FileNotFoundException, IOException {
        service.delete(appId, id, username);
    }
    
    /**/
    /**
     * 文件解析
     * 
     * @param appId
     *            --接入的系统ID
     * @param in
     *            --输入流
     * @param oriFileName
     *            --原文件名
     * @param username
     *            --操作人
     * @param des3Key--解密key
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    public List<List<String>> parsing(long id, String des3Key)
    		throws FileNotFoundException,FileDecryptException,IOException {
        return service.parsing(id, des3Key);
//        return null;
    }
    
    /**
     * 将csv文件转换为list
     * @param id
     * @param des3Key
     * @param charset
     * @return
     */
    public List<List<String>> parsing(long id, String des3Key, String charset) throws FileNotFoundException,
            FileDecryptException, IOException {
        return service.parsing(id, des3Key, charset);
    }
    
}
