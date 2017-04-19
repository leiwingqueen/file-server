package com.elend.p2p.file.mapper;

import java.util.List;

import com.elend.p2p.file.model.FileManage;
import com.elend.p2p.file.vo.FileManageSearchVO;
import com.elend.p2p.file.vo.FileManageVO;
import com.elend.p2p.mapper.SqlMapper;

public interface FileManageMapper extends SqlMapper {
	
	/**
	 * 根据搜索条件返回列表
	 * @param svo
	 * @return
	 */
	public List<FileManageVO> list(FileManageSearchVO svo);
	
	/**
	 * 根据搜索条件返回列表总数
	 * @param svo
	 * @return
	 */
	public int count(FileManageSearchVO svo);
	
	/**
	 * 根据主键id获取单条记录
	 * @param id
	 * @return
	 */
	public FileManageVO get(long file_id);
	
	/**
	 * 插入记录
	 * @param vo
	 */
	public void insert(FileManage vo);
	
	/**
	 * 更新记录
	 * @param vo
	 */
	public void update(FileManage vo);
	
	/**
	 * 根据主键id删除记录
	 * @param id
	 */
	public void delete(long id);
	
	/**
	 * 逻辑删除
	 * @param id
	 */
	public void logicDelete(FileManage vo);
}
