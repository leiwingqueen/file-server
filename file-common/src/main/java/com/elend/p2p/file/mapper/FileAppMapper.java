package com.elend.p2p.file.mapper;

import org.apache.ibatis.annotations.Param;

import com.elend.p2p.file.model.FileAppPO;
import com.elend.p2p.mapper.SqlMapper;

public interface FileAppMapper extends SqlMapper {
    /**
     * 根据appId获取单条记录
     * 
     * @param appId
     * @return
     */
    FileAppPO get(@Param("appId")String appId);
}
