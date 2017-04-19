package com.elend.p2p.file.util;

import org.junit.Test;

public class FileUtilTest {
	@Test
	public void testDelete(){
		
		String path="D:\\data1\\test\\2014-10-28\\82a4be45-b1b9-4317-9599-9c16aad9c098.txt";
		boolean flag=FileUtil.deleteFile(path);
		if(!flag)
			System.out.println("fail");
	}
}
