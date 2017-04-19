package com.elend.p2p.file.util;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class EncryptFileUtilTest {

	/**
	 * 加解密测试
	 */
	@Test
	public void testEncrypt() {
		String des3Key="123457jhik3256789abcdefghijklmnopqrstuvwxyz@#$kjoisldrgl54r65q4gu87843918u%%4r0-*1qlkfl;ads7454dmnmkwaeriql1r4g57fgjdu893w842872y7u$";
		String content="哈哈";
		String encrypt=EncryptFileUtil.encrypt(content, des3Key);
		String decrypt=EncryptFileUtil.decrypt(encrypt, des3Key);
		assertTrue(content.equals(decrypt));
	}

	@Test
	public void testDecrypt() {
		fail("Not yet implemented");
	}

	/**
	 * 加解密图片测试
	 */
	@Test
	public void testCopyImageEncrypt() {
		InputStream source=null;
		try {
			source = new FileInputStream("D:/test1.jpg");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String destDir="D:/";
		String newFileName="testEnc.jpg";
		String des3Key="123457jhik3256789abcdefghijklmnopqrstuvwxyz@#$kjoisldrgl54r65q4gu87843918u%%4r0-*1qlkfl;ads7454dmnmkwaeriql1r4g57fgjdu893w842872y7u$";
		try {
			EncryptFileUtil.copyFileEncrypt(source, destDir, newFileName, des3Key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String decryptFileName="testDec.jpg";
		try {
			EncryptFileUtil.copyFileDecrypt(new FileInputStream(destDir+newFileName), destDir, decryptFileName, des3Key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 文件加解密测试
	 */
	@Test
	public void testCopyFileEncrypt() {
		InputStream source=null;
		try {
			source = new FileInputStream("D:/testUpload.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String destDir="D:/";
		String newFileName="fileEncrypt.txt";
		String des3Key="123457jhik3256789abcdefghijklmnopqrstuvwxyz@#$kjoisldrgl54r65q4gu87843918u%%4r0-*1qlkfl;ads7454dmnmkwaeriql1r4g57fgjdu893w842872y7u$";
		try {
			EncryptFileUtil.copyFileEncrypt(source, destDir, newFileName, des3Key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String decryptFileName="fileDecrypt.txt";
		try {
			EncryptFileUtil.copyFileDecrypt(new FileInputStream(destDir+newFileName), destDir, decryptFileName, des3Key);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	@Test
	public void testCopyFileDecrypt() {
		fail("Not yet implemented");
	}

}
