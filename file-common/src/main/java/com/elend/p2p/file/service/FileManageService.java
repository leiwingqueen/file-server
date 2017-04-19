package com.elend.p2p.file.service;

import java.io.IOException;
import java.io.InputStream;

import com.elend.p2p.file.exception.FileDecryptException;
import com.elend.p2p.file.exception.FileNotFoundException;
import com.elend.p2p.file.exception.PatitionNotFoundException;
import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.vo.FileManageVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件管理service
 * @author liyongquan
 *
 */
@Deprecated
public interface FileManageService {
    /**
     * 文件上传
     * @param appId--系统ID
     * @param in--输入流
     * @param oriFileName--原文件名
     * @param username--操作用户
     * @param des3Key--为空时不进行加密，不为空按传入的key进行加密
     * @return
     * @throws IOException
     * @throws PatitionNotFoundException
     */
    FileManage upload(String appId, InputStream in, String oriFileName,
            String username, String des3Key) throws IOException,
            PatitionNotFoundException;
    
    
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
     FileManage uploadIconImage(String appId, InputStream in,
            String oriFileName, String username, String des3Key)
            throws IOException, PatitionNotFoundException ;

    /**
     * 文件下载
     * @param appId--系统ID
     * @param id--文件ID
     * @param username--操作用户
     * @param des3Key--为空时不进行加密，不为空按传入的key进行加密
     * @return
     * @throws FileNotFoundException
     */
    FileManageVO download(String appId, long id, String username,String des3Key)
            throws FileNotFoundException,FileDecryptException,IOException;
    

    /**
     * 获取文件信息
     * @param appId--系统ID
     * @param id--文件ID
     * @param username--操作用户
     * @return
     */
    FileManage getInfo(String appId, long id, String username);

    /**
     * 文件删除
     * @param appId--系统ID
     * @param id--文件ID
     * @param username--操作用户
     * @throws FileNotFoundException
     * @throws IOException
     */
    void delete(String appId, long id, String username)
            throws FileNotFoundException, IOException;

    /**
     * 将csv文件转换为list，默认按utf-8字符集读取
     * @param id
     * @param des3Key
     * @return
     */
    List<List<String>> parsing(long id, String des3Key) throws FileNotFoundException,
            FileDecryptException, IOException;
    /**
     * 将csv文件转换为list
     * @param id
     * @param des3Key
     * @param charset
     * @return
     */
    List<List<String>> parsing(long id, String des3Key, String charset) throws FileNotFoundException,
            FileDecryptException, IOException;
}
