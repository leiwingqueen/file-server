package com.elend.p2p.file.mapper;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.vo.FileManageSearchVO;
import com.elend.p2p.file.vo.FileManageVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:config/spring/*.xml" })
public class FileManageMapperTest {
	@Autowired
	private FileManageMapper mapper;
	
	
	//@Test
	public void testGet(){
		long id=1;
		FileManage vo=mapper.get(id);
		System.out.println(vo.getFileId());
		System.out.println(vo.getPath());
		System.out.println(vo.getFileName());
	}
	
	@Test
	public void testList(){
		long id=12;
		FileManageSearchVO svo=new FileManageSearchVO();
		svo.setAppId("image_test");
		svo.setSize(1);
		svo.setFileId(id);
		List<FileManageVO> list=mapper.list(svo);
		System.out.println(list.get(0).getFileId());
		System.out.println(list.get(0).getPath()+list.get(0).getFileName());
	}
}
