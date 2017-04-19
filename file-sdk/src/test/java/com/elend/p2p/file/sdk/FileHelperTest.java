package com.elend.p2p.file.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.elend.p2p.Result;
import com.elend.p2p.file.sdk.FileHelper.FileDownloadInfo;
import com.elend.p2p.file.sdk.FileHelper.FilePair;
import com.elend.p2p.file.sdk.FileHelper.FileType;
import com.elend.p2p.file.sdk.FileHelper.FileUploadInfo;

/**
 * 文件上传测试类
 * @author liyongquan 2016年9月20日
 *
 */
public class FileHelperTest {
    /**
     * 本地上传测试
     */
    @Test
    public void testUpload() {
        List<String> fileList=new ArrayList<>();
        fileList.add("D:/14045.jpg");
        Result<List<FileUploadInfo>> result=FileHelper.uploadLocalFile("http://test.image.gzdai.com/fileServer/upload.do",
                                                                       "p2p_web", "75kTAtPx9pxH0kms", 3000, fileList,FileType.PUBLIC);
        System.out.println("result:"+result.toString());
        if(result.getObject()!=null){
            for(FileUploadInfo file:result.getObject()){
                System.out.println("file:"+file);
            }
        }
    }
    /**
     * inputStream上传测试
     * @throws FileNotFoundException
     */
    @Test
    public void testUploadInputSteam() throws FileNotFoundException {
        List<FilePair> fileList=new ArrayList<FilePair>();
        FilePair pair=new FilePair();
        pair.setFileName("testUpload.txt");
        pair.setInputStream(new FileInputStream(new File("D:/testUpload.txt")));
        fileList.add(pair);
        Result<List<FileUploadInfo>> result=FileHelper.uploadInputStream("http://test.image.gzdai.com/fileServer/upload.do",
                                                                         "p2p_web", "75kTAtPx9pxH0kms", 3000, fileList,FileType.PRIVATE);
        System.out.println("result:"+result.toString());
        if(result.getObject()!=null){
            for(FileUploadInfo file:result.getObject()){
                System.out.println("file:"+file);
            }
        }
    }
    
    /**
     * 文件下载测试
     */
    @Test
    public void testDownload(){
        Result<FileDownloadInfo> result=FileHelper.download("http://test.image.gzdai.com/fileServer/download.do", "p2p_web", "75kTAtPx9pxH0kms",15079L);
        System.out.println("result:"+result.toString());
        System.out.println("download:"+result.getObject());
        if(result.isSuccess()&&result.getObject()!=null){
            try {
                int size = result.getObject().getInputStream().available();
                FileUtil.copyFile(result.getObject().getInputStream(), "E:\\",result.getObject().getOriFileName());
                System.out.println("下载成功!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 旧文件兼容下载测试
     */
    @Test
    public void testDownload2(){
        Result<FileDownloadInfo> result=FileHelper.download("http://test.image.gzdai.com/fileServer/download.do", "p2p_web", "75kTAtPx9pxH0kms",15317L);
        System.out.println("result:"+result.toString());
        System.out.println("download:"+result.getObject());
        if(result.isSuccess()&&result.getObject()!=null){
            try {
                int size = result.getObject().getInputStream().available();
                FileUtil.copyFile(result.getObject().getInputStream(), "E:\\",result.getObject().getOriFileName());
                System.out.println("下载成功!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
