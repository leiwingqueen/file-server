package com.elend.p2p.file.service;

import com.elend.p2p.Result;
import com.elend.p2p.file.vo.FileAppVO;

public interface FileAppService {

    /**
     * 根据主键appId获取单条记录
     * 
     * @param appId
     * @return
     */
    Result<FileAppVO> get(String appId);
    
    /**
     * 签名校验
     * @param appId
     * @param timeStamp
     * 时间戳--精确到s
     * @param sign
     * 签名
     * @return
     * 验证结果
     */
    Result<String> validate(String appId,long timeStamp,String sign);
}
