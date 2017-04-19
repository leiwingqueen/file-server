package com.elend.p2p.file.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.elend.p2p.BaseController;
import com.elend.p2p.ResponseUtils;
import com.elend.p2p.Result;
import com.elend.p2p.ServiceException;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.file.facade.FileAppFacade;
import com.elend.p2p.file.facade.FileFacade;
import com.elend.p2p.file.service.FileService.FileDownloadInfo;
import com.elend.p2p.file.service.FileService.FileType;
import com.elend.p2p.file.service.FileService.FileUploadInfo;
import com.elend.p2p.gson.JSONUtils;
import com.elend.p2p.resource.ClassResourceDesc;
import com.google.gson.reflect.TypeToken;
/**
 * 文件服务2.0
 * @author liyongquan 2016年11月23日
 *
 */
@ClassResourceDesc(firstCate = "文件服务2.0")
@Controller
@Scope("prototype")
public class FileServerContrller extends BaseController{
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private FileAppFacade fileAppFacade;
    @Autowired
    private FileFacade fileFacade;
    /**
     * 文件服务上传接口（image.gzdai.com）
     * 
     * @param request
     * @param response
     * @param appId
     *            系统ID
     * @param timeStamp
     *            时间戳
     * @param sign
     *            签名
     * @param fileType
     *  文件类型
     * @return
     */
    @RequestMapping(value = "/fileServer/upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<FileUploadInfo>> fileUpload(HttpServletRequest request,
            HttpServletResponse response, String appId, String timeStamp,
            String sign,String fileType) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> iter = multipartRequest.getFileNames();
        if (iter == null || !iter.hasNext()) {
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,
                                            "上传文件不能为空");
        }
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(timeStamp)
                || StringUtils.isBlank(sign)||StringUtils.isBlank(fileType)) {
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,
                                            "参数异常.appId,timeStamp,sign,fileType不能为空");
        }
        long timeStampLong = 0L;
        try {
            timeStampLong = Long.parseLong(timeStamp);
        } catch (NumberFormatException e1) {
            logger.error("timeStamp格式错误");
        }
        Result<String> validateResult = fileAppFacade.validate(appId,
                                                               timeStampLong,
                                                               sign);
        if (!validateResult.isSuccess()) {
            logger.error("文件上传签名验证失败...appId:{},timeStamp:{},sign:{}",appId,timeStamp,sign);
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,
                                            validateResult.getMessage());
        }
        InputStream in = null;
        List<FileUploadInfo> list = new ArrayList<FileUploadInfo>();
        try {
            while (iter.hasNext()) {
                String param = (String) iter.next();
                CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(param);
                in = file.getFileItem().getInputStream();
                logger.info("文件大小..." + file.getFileItem().getSize());
                if (file.getFileItem().getSize() >= 1024L * 1024*5) {
                    logger.error("文件上传大小超过5M，请处理后再上传...");
                    throw new ServiceException("文件上传大小超过1M，请处理后再上传...");
                }
                String oriFileName = file.getOriginalFilename();
                Result<FileUploadInfo> uploadResult=fileFacade.upload(appId, in, oriFileName, FileType.from(fileType));
                if(!uploadResult.isSuccess()||uploadResult.getObject()==null){
                    logger.error("上传失败...失败原因:{}",uploadResult.getMessage());
                    return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, uploadResult.getMessage());
                }
                list.add(uploadResult.getObject());
            }
            return new Result<List<FileUploadInfo>>(ResultCode.SUCCESS, list);
        } catch (Exception e) {
            logger.error("文件上传失败");
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,
                                            e.getMessage());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                }
        }
    }
    
    /**
     * 文件下载
     * @param request
     * @param response
     * @param appId
     * appId
     * @param fileId
     * 文件ID
     * @param timeStamp
     * 时间戳
     * @param sign
     * 签名信息
     */
    @RequestMapping(value = "/fileServer/download.do")
    public void download(HttpServletRequest request,
            HttpServletResponse response, String appId,String fileId, String timeStamp,
            String sign) {
        /**
         * 1.参数校验
         */
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(timeStamp)||
                StringUtils.isBlank(sign)||StringUtils.isBlank(fileId)){
            renderErrorMessage(response, "参数异常.appId,timeStamp,sign,fileId不能为空", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long timeStampLong = 0L;
        try {
            timeStampLong = Long.parseLong(timeStamp);
        } catch (NumberFormatException e1) {
            logger.error("timeStamp格式错误");
            renderErrorMessage(response, "timeStamp格式错误", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        long fileIdLong = 0L;
        try {
            fileIdLong = Long.parseLong(fileId);
        } catch (NumberFormatException e1) {
            logger.error("fileId格式错误");
            renderErrorMessage(response, "fileId格式错误", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if(fileIdLong<=0){
            renderErrorMessage(response, "fileId不能小于等于0", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /**
         * 2.签名校验
         */
        Result<String> validateResult = fileAppFacade.validate(appId,
                                                               timeStampLong,
                                                               sign);
        if (!validateResult.isSuccess()) {
            logger.error("文件下载签名验证失败...appId:{},timeStamp:{},sign:{}",appId,timeStamp,sign);
            renderErrorMessage(response, "签名验证失败", HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        /**
         * 3.获取文件索引
         */
        Result<FileDownloadInfo> downloadResult=fileFacade.download(appId, fileIdLong);
        if(!downloadResult.isSuccess()||downloadResult.getObject()==null){
            logger.error("文件下载失败,失败原因:{}",downloadResult.getMessage());
            renderErrorMessage(response, downloadResult.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        /**
         * 4.输出到客户端
         */
        OutputStream stream = null;
        BufferedInputStream fif = null;
        try {
            File file = downloadResult.getObject().getFile();
            stream = response.getOutputStream();
            response.reset();
            response.setHeader("Content-Disposition", "attachment;filename="+downloadResult.getObject().getOriFileName());//告诉客户端下载文件和下载的文件名
            response.setContentType("application/octet-stream");//不指定具体的文件类型
            response.setHeader("Content-Length",
                               String.valueOf(file.length()));
            fif = new BufferedInputStream(new FileInputStream(file));
            int d;
            byte[] buf = new byte[10240];
            while ((d = fif.read(buf)) != -1) {
                stream.write(buf, 0, d);
            }
            stream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (stream != null) {
                    stream.close();
                }
                if (fif != null) {
                    fif.close();
                }
            } catch (Exception e11) {
            }
        }
    }
    
    /**
     * 返回错误信息
     * @param response
     * http response
     * @param message
     * 错误信息
     * @param statusCode
     * 返回的http错误码
     * 建议如果是客户端的参数错误或者签名错误返回400
     * 如果是服务器处理过程中出错返回500
     */
    private static void renderErrorMessage(HttpServletResponse response,String message,int statusCode){
        Type targetType = new TypeToken<Result<String>>() {}.getType();
        String json = JSONUtils.toJson(new Result<String>(ResultCode.FAILURE, null, message), targetType, false);
        //设置http错误码
        response.setStatus(statusCode);
        ResponseUtils.renderJson(response, json);
    }
}
