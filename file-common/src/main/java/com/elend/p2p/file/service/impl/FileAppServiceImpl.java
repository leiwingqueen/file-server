package com.elend.p2p.file.service.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.elend.p2p.Result;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.file.mapper.FileAppMapper;
import com.elend.p2p.file.model.FileAppPO;
import com.elend.p2p.file.service.FileAppService;
import com.elend.p2p.file.vo.FileAppVO;
import com.elend.p2p.util.encrypt.DesPropertiesEncoder;
import com.elend.p2p.util.encrypt.HMacSHA1;

/**
 * 这个类已经不建议继续使用，重构后的类参考FileService
 * @author liyongquan
 *
 */
@Deprecated
@Service
public class FileAppServiceImpl implements FileAppService {

    @Autowired
    private FileAppMapper mapper;

    @Override
    public Result<FileAppVO> get(String appId) {
        FileAppPO po = mapper.get(appId);
        if (po != null) {
            return new Result<FileAppVO>(ResultCode.SUCCESS,
                                         new FileAppVO(po));
        }
        return new Result<FileAppVO>(ResultCode.FAILURE, null);
    }

    @Override
    public Result<String> validate(String appId, long timeStamp,
            String sign) {
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(sign)){
            return new Result<String>(ResultCode.FAILURE, null, "appId和签名不能为空");
        }
        if(Math.abs(new Date().getTime()/1000-timeStamp)>5L*60*1000){
            return new Result<String>(ResultCode.FAILURE, null, "timeStamp已过期，请重新发起上传...");
        }
        FileAppPO po=mapper.get(appId);
        if(po==null){
            return new Result<String>(ResultCode.FAILURE, null, "appId不存在");
        }
        String key=new DesPropertiesEncoder().decode(po.getAppKey());
        String plainText=appId+timeStamp;
        if(sign.equals(HMacSHA1.getSignature(plainText, key))){
            return new Result<String>(ResultCode.SUCCESS, "");
        }
        return new Result<String>(ResultCode.FAILURE, null,"签名验证失败");
    }

}
