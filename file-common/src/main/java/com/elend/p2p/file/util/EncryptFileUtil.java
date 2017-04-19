package com.elend.p2p.file.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elend.p2p.util.StringUtil;
import com.elend.p2p.util.encrypt.DES3;

/**
 * 扩展FileUtil，增加文件写入加密和解密
 * 
 * @author liyongquan
 */
public class EncryptFileUtil extends FileUtil {
    protected final static Logger logger = LoggerFactory.getLogger(EncryptFileUtil.class);

    /**
     * 加密Salt值长度
     */
    private static final int SALT_LENGTH = 5;

    /**
     * des3加密(加入简单的干扰手段)
     * 
     * @param content
     * @param des3Key
     * @return
     * @throws RuntimeException
     */
    public static String encrypt(String content, String des3Key)
            throws RuntimeException {
        if (content == null || des3Key == null) {
            throw new RuntimeException("des3encrypt, params is error");
        }
        String salt = StringUtil.getRandomString(SALT_LENGTH);

        String result = null;
        try {
            logger.info("before DES3.encrypt, content=" + content
                    + ", des3Key=" + des3Key + ", salt1=" + salt);
            result = DES3.encrypt(content + salt, des3Key + salt) + salt;
            logger.info("after DES3.encrypt, result=" + result);
        } catch (Exception e) {
            throw new RuntimeException("encrypt error, " + e.getMessage(), e);
        }
        return result;
    }
    
    

    /**
     * des3解密(加入简单的干扰手段)
     * 
     * @param content
     *            密文
     * @param des3Key
     *            DE3S的密钥
     * @return
     * @throws RuntimeException
     *             解密失败，或者密文长度不正确
     */
    public static String decrypt(String content, String des3Key)
            throws RuntimeException {
        if (content == null || des3Key == null) {
            throw new RuntimeException("decrypt, params is error");
        }
        // DES3解密
        if (content.length() < SALT_LENGTH) {
            throw new RuntimeException(
                                       "the length of the ciphertext is error");
        }
        String salt = content.substring(content.length() - SALT_LENGTH);
        content = content.substring(0, content.length() - SALT_LENGTH);
        try {
            content = DES3.decrypt(content, des3Key + salt);
            return content.substring(0, content.length() - SALT_LENGTH);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 拷贝文件(加密)
     * 
     * @param source
     * @param destDir
     * @param newFileName
     * @return
     * @throws IOException
     */
    public static String copyFileEncrypt(InputStream source, String destDir,
            String newFileName, String des3Key) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IOException("dest dir (" + destDir
                    + ") is not a folder");
        }
        String destFileFullName = null;
        BufferedOutputStream out = null;
        try {
            destFileFullName = destDir + File.separator + newFileName;
            out = new BufferedOutputStream(
                                           new FileOutputStream(
                                                                destFileFullName));
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = source.read(buffer, 0, 8192)) != -1) {
                //加密
            	try {
					byte[] fileEncrypt=DES3.encrypt(buffer, des3Key,0,bytesRead);
					out.write(fileEncrypt);
				} catch (Exception e) {
					logger.error("文件加密失败");
					break;
				}
            }

        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (source != null) {
                source.close();
            }
        }
        return destFileFullName;
    }
    
    /**
     * 拷贝文件(解密)
     * 
     * @param source
     * @param destDir
     * @param newFileName
     * @return
     * @throws IOException
     */
    public static String copyFileDecrypt(InputStream source, String destDir,
            String newFileName, String des3Key) throws IOException {
        File dir = new File(destDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!dir.isDirectory()) {
            throw new IOException("dest dir (" + destDir
                    + ") is not a folder");
        }
        String destFileFullName = null;
        BufferedOutputStream out = null;
        try {
            destFileFullName = destDir + File.separator + newFileName;
            out = new BufferedOutputStream(
                                           new FileOutputStream(
                                                                destFileFullName));
            byte[] buffer = new byte[8192];
            int bytesRead = 0;
            while ((bytesRead = source.read(buffer, 0, 8192)) != -1) {
                //解密
                //String bufferDecrypt=decrypt(new String(buffer,0,bytesRead), des3Key);
                //out.write(bufferDecrypt.getBytes(), 0, bufferDecrypt.getBytes().length);
            	byte[] fileDecrypt;
				try {
					fileDecrypt = DES3.decrypt(buffer, des3Key,0,bytesRead);
					out.write(fileDecrypt);
				} catch (Exception e) {
					logger.error("DES3解密失败");
				}
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
            if (source != null) {
                source.close();
            }
        }
        return destFileFullName;
    }
}
