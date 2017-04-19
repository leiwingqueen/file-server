package com.elend.p2p.file.facade;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junitx.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.elend.p2p.file.exception.FileNotFoundException;
import com.elend.p2p.file.exception.PatitionNotFoundException;
import com.elend.p2p.file.facade.vo.FileInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class FileManageFacadeTest {
	@Autowired
	private FileManageFacade facade;
	
	@Test
	public void testUpload() throws IOException,PatitionNotFoundException{
		String appId="p2p_web";
		String path="D:/data1/file_manage/partition2/p2p_web/2015/3/";
		String oriFileName="1.png";
		File file=new File(path+oriFileName);
		InputStream in = new FileInputStream(file);
		FileInfo fileInfo=facade.upload(appId, in, oriFileName,"liyongquan");
		System.out.println("path:"+fileInfo.getImageUrl());
		Assert.assertTrue(true);
	}
	
	@Test
	public void testUploadEncrypt()throws IOException,PatitionNotFoundException{
	    String des3Key = "123457jhik3256789abcdefghijklmnopqrstuvwxyz@#$kjoisldrgl54r65q4gu87843918u%%4r0-*1qlkfl;ads7454dmnmkwaeriql1r4g57fgjdu893w842872y7u$";
	    String appId="image_test";
            String oriFileName="testUpload.txt";
            File file=new File("D:\\"+oriFileName);
            InputStream in = new FileInputStream(file);
            FileInfo info=facade.upload(appId, in, oriFileName,"liyongquan",des3Key);
            System.out.println("file_id:"+info.getFileId());
            Assert.assertTrue(true);
	}
	
	@Test
	public void testDownload()throws FileNotFoundException, IOException{
		String appId="test";
		long id=4;
		FileInfo info=facade.download(appId, id, "liyongquan");
		InputStream source = info.getInputStream();
		BufferedOutputStream out = null;
		try {
			String destFileFullName = "D:\\testDownload\\"+info.getOrgName();
			out = new BufferedOutputStream(new FileOutputStream(
					destFileFullName));
			byte[] buffer = new byte[8192];
			int bytesRead = 0;
			while ((bytesRead = source.read(buffer, 0, 8192)) != -1) {
				out.write(buffer, 0, bytesRead);
			}

		} finally {
			if(source!=null){
				source.close();
			}
			if (out != null) {
				out.flush();
				out.close();
			}
		}
		Assert.assertTrue(true);
	}
	
	@Test
        public void testDownloadDecrypt()throws FileNotFoundException, IOException{
	        String des3Key = "123457jhik3256789abcdefghijklmnopqrstuvwxyz@#$kjoisldrgl54r65q4gu87843918u%%4r0-*1qlkfl;ads7454dmnmkwaeriql1r4g57fgjdu893w842872y7u$";
                String appId="image_test";
                long id=10165;
                FileInfo info=facade.download(appId, id, "liyongquan",des3Key);
                InputStream source = info.getInputStream();
                BufferedOutputStream out = null;
                try {
                        String destFileFullName = "D:\\testDownload\\"+info.getOrgName();
                        out = new BufferedOutputStream(new FileOutputStream(
                                        destFileFullName));
                        byte[] buffer = new byte[8192];
                        int bytesRead = 0;
                        while ((bytesRead = source.read(buffer, 0, 8192)) != -1) {
                                out.write(buffer, 0, bytesRead);
                        }

                } finally {
                        if(source!=null){
                                source.close();
                        }
                        if (out != null) {
                                out.flush();
                                out.close();
                        }
                }
                Assert.assertTrue(true);
        }
	
	@Test
	public void testDelete() throws FileNotFoundException, IOException{
		String appId="test";
		long id=5;
		facade.delete(appId, id, "liyongquan");
		Assert.assertTrue(true);
	}
	
	@Test
	public void testGetInfo(){
		String appId="test";
		long id=4;
		FileInfo info=facade.getInfo(appId,id,"liyongquan");
		System.out.println(info.getPath());
		Assert.assertTrue(true);
	}
	
	@Test
        public void testParsing() throws IOException,PatitionNotFoundException{
                String appId="p2p_web";
                String path="G:\\201606\\";
                String oriFileName="csv_test.csv";
                File file=new File(path+oriFileName);
                InputStream in = new FileInputStream(file);
                FileInfo fileInfo=facade.upload(appId, in, oriFileName,"liyongquan");
                System.out.println("path:"+fileInfo.getImageUrl());
                Assert.assertTrue(true);
                
                List<List<String>> list = facade.parsing(fileInfo.getFileId(), null, "gbk");
                System.out.println(list);
                
                oriFileName="csv_test_utf8.csv";
                file=new File(path+oriFileName);
                in = new FileInputStream(file);
                fileInfo=facade.upload(appId, in, oriFileName,"liyongquan");
                System.out.println("path:"+fileInfo.getImageUrl());
                Assert.assertTrue(true);
                
                list = facade.parsing(fileInfo.getFileId(), null, "utf-8");
                System.out.println(list);
                
                oriFileName="csv_test_unix.csv";
                file=new File(path+oriFileName);
                in = new FileInputStream(file);
                fileInfo=facade.upload(appId, in, oriFileName,"liyongquan");
                System.out.println("path:"+fileInfo.getImageUrl());
                Assert.assertTrue(true);
                
                list = facade.parsing(fileInfo.getFileId(), null, "utf-8");
                System.out.println(list);
                
        }
	
}
