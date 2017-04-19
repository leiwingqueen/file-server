package com.elend.p2p.file.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.elend.p2p.BaseController;
import com.elend.p2p.Result;
import com.elend.p2p.ServiceException;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.context.UserInfoContext;
import com.elend.p2p.file.facade.FileAppFacade;
import com.elend.p2p.file.facade.FileManageFacade;
import com.elend.p2p.file.facade.vo.FileInfo;
import com.elend.p2p.resource.ClassResourceDesc;
import com.elend.p2p.resource.MethodResourceDesc;
import com.elend.p2p.util.DateUtil;
import com.elend.p2p.util.SystemConstant;

/**
 * 老版本的上传和下载功能
 * @author liyongquan
 *
 */
@ClassResourceDesc(firstCate = "文件管理")
@Controller
@Scope("prototype")
@Deprecated //新上传和下载迁移到FileServerController
public class FileController extends BaseController {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    FileManageFacade fileManageFacade;
    @Autowired
    private FileAppFacade fileAppFacade;

    public static final String GET_CONTENT_URL = "/file/imgFileView";

    @MethodResourceDesc(name = "查看图片文件")
    @RequestMapping(value = "/file/imgFileView/{id}")
    public void imgFileView(HttpServletRequest request,
            HttpServletResponse response, @PathVariable("id") int id) {
        FileInfo fileInfo = null;
        try {
            fileInfo = fileManageFacade.download(SystemConstant.APP_ID, id,
                                                 UserInfoContext.getUserId()
                                                         + "");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
            return;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }
        OutputStream stream = null;
        BufferedInputStream fif = null;
        try {
            File file = new File(fileInfo.getFullPath());
            if (!file.exists()) {
                return;
            }
            stream = response.getOutputStream();
            response.reset();
            response.setContentType("image/jpeg");
            response.setHeader("Content-Length",
                               String.valueOf(file.length()));
            /**
             * 跨域支持
             */
            response.setHeader("Access-Control-Allow-Origin", "*");
            /**
             * 客户端缓存支持
             */
            response.setHeader("ETag", fileInfo.getFileMd5());
            response.setHeader("Last-Modified",
                               DateUtil.timeToRFC1123(fileInfo.getUpdateTime()));// 最后修改时间
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.DATE, 3);// 3天的过期时间
            response.setHeader("Expires",
                               DateUtil.timeToRFC1123(cal.getTime()));// 最后修改时间
            if (fileInfo.getFileMd5().equals(request.getHeader("If-None-Match"))) {
                response.setStatus(304);
                return;
            }
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
     * @return
     */
    @RequestMapping(value = "/file/upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Result<List<String>> fileUpload(HttpServletRequest request,
            HttpServletResponse response, String appId, String timeStamp,
            String sign) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Iterator<String> iter = multipartRequest.getFileNames();
        if (iter == null || !iter.hasNext()) {
            return new Result<List<String>>(ResultCode.FAILURE, null,
                                            "上传文件不能为空");
        }
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(timeStamp)
                || StringUtils.isBlank(sign)) {
            return new Result<List<String>>(ResultCode.FAILURE, null,
                                            "参数异常.appId,timeStamp,sign不能为空");
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
            return new Result<List<String>>(ResultCode.FAILURE, null,
                                            validateResult.getMessage());
        }
        InputStream in = null;
        List<String> list = new ArrayList<String>();
        try {
            while (iter.hasNext()) {
                String param = (String) iter.next();
                CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(param);
                in = file.getFileItem().getInputStream();
                logger.info("文件大小..." + file.getFileItem().getSize());
                if (file.getFileItem().getSize() >= 1024L * 1024) {
                    logger.error("文件上传大小超过1M，请处理后再上传...");
                    throw new ServiceException("文件上传大小超过1M，请处理后再上传...");
                }
                String oriFileName = file.getOriginalFilename();
                FileInfo fileInfo = fileManageFacade.upload(appId, in,
                                                            oriFileName, "");
                String path = fileInfo.getImageUrl();
                // 文件后缀替换为原来的文件后缀
                String suffix = ".do";
                int index = oriFileName.lastIndexOf(".");
                if (index >= 0) {
                    suffix = oriFileName.substring(index,
                                                   oriFileName.length());
                }
                if (path.lastIndexOf(".") >= 0) {
                    path = path.substring(0, path.lastIndexOf(".")) + suffix;
                }
                list.add(path);
            }
            return new Result<List<String>>(ResultCode.SUCCESS, list);
        } catch (Exception e) {
            logger.error("上传失败...失败原因:{}",e.getMessage());
            return new Result<List<String>>(ResultCode.FAILURE, null,
                                            e.getMessage());
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (Exception e) {
                }
        }
    }
}
