package com.elend.p2p.file.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.elend.p2p.Result;
import com.elend.p2p.file.service.FileAppService;
import com.elend.p2p.file.vo.FileAppVO;

/**
 * 文件服务appId管理，后期文件服务迁移出来再统一迁出来
 * @author liyongquan 2016年9月20日
 *
 */
@Component
public class FileAppFacade {
    @Autowired
    private FileAppService service;
    /**
     * 根据主键appId获取单条记录
     * 
     * @param appId
     * @return
     */
    public Result<FileAppVO> get(String appId){
        return service.get(appId);
    }
    
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
    public Result<String> validate(String appId,long timeStamp,String sign){
        return service.validate(appId, timeStamp, sign);
    }
}
