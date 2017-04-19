package com.elend.p2p.file.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.elend.p2p.Result;
import com.elend.p2p.file.service.FileService.FileDownloadInfo;
import com.elend.p2p.file.service.FileService.FileType;
import com.elend.p2p.file.service.FileService.FileUploadInfo;
import com.elend.p2p.util.encrypt.HMacSHA1;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class FileFacadeTest {
    @Autowired
    private FileFacade fileFacade;
    /**
     * 公开文件上传
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload1() throws FileNotFoundException {
        long timeStamp=new Date().getTime()/1000;
        String sign=HMacSHA1.getSignature("p2p_web"+timeStamp,"75kTAtPx9pxH0kms");
        Result<FileUploadInfo> result=fileFacade.upload("p2p_web", new FileInputStream(new File("D:/testUpload.txt")), "testUpload.txt", FileType.PUBLIC);
        System.out.println("result:"+result.toString());
        System.out.println("upload:"+result.getObject());
    }
    
    /**
     * 私有文件上传
     * @throws FileNotFoundException
     */
    @Test
    public void testUpload2() throws FileNotFoundException {
        long timeStamp=new Date().getTime()/1000;
        String sign=HMacSHA1.getSignature("p2p_web"+timeStamp,"75kTAtPx9pxH0kms");
        Result<FileUploadInfo> result=fileFacade.upload("p2p_web", new FileInputStream(new File("D:/testUpload.txt")), "testUpload.txt", FileType.PRIVATE);
        System.out.println("result:"+result.toString());
        System.out.println("upload:"+result.getObject());
    }
    
    /**
     * 签名生成
     */
    @Test
    public void testGenSign(){
        String appId="p2p_web";
        long timeStamp=new Date().getTime()/1000;
        String sign=HMacSHA1.getSignature(appId+timeStamp,"75kTAtPx9pxH0kms");
        System.out.println(String.format("appId=%s&timeStamp=%s&sign=%s", appId,timeStamp,sign));
    }
    
    @Test
    public void testDownload(){
        Result<FileDownloadInfo> result=fileFacade.download("p2p_web", 15177L);
        System.out.println("result:"+result.toString());
        System.out.println("upload:"+result.getObject());
    }

}
