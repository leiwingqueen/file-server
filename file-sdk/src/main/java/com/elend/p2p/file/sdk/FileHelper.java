package com.elend.p2p.file.sdk;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;

import com.elend.p2p.Result;
import com.elend.p2p.constant.ResultCode;
import com.elend.p2p.gson.JSONUtils;
import com.elend.p2p.util.encrypt.HMacSHA1;
import com.google.gson.reflect.TypeToken;

/**
 * 文件上传工具类
 * @author liyongquan 2016年9月20日
 *
 */
public class FileHelper {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(FileHelper.class);
    /**
     * 默认超时时间
     */
    public static final int DEFAULT_TIME_OUT=3000; 
    /**
     * 文件上传(输入流)
     * @param url
     * 上传url
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param fileList
     * 文件列表
     * @param fileType
     * 文件类型
     * @return
     */
    public static Result<List<FileUploadInfo>> uploadInputStream(String url,String appId,String appKey,List<FilePair> fileList,FileType fileType){
        return uploadInputStream(url, appId, appKey,DEFAULT_TIME_OUT, fileList,fileType);
    }
    /**
     * 文件上传(输入流)
     * @param url
     * 上传url
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param timeOut
     * 超时时间
     * @param fileList
     * 文件列表
     * @param fileType
     * 文件类型
     * @return
     */
    public static Result<List<FileUploadInfo>> uploadInputStream(String url,String appId,String appKey,int timeOut,List<FilePair> fileList,FileType fileType){
        if(fileList==null||fileList.size()==0){
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, "文件不能为空");
        }
        MultipartEntityBuilder multipartEntityBuilder=MultipartEntityBuilder.create();
        for(int i=0;i<fileList.size();i++){
            multipartEntityBuilder.addPart("file"+i,new InputStreamBody(fileList.get(i).getInputStream(), ContentType.MULTIPART_FORM_DATA, fileList.get(i).getFileName()));
        }
        return upload(url, appId, appKey, timeOut, multipartEntityBuilder,fileType);
    }
    
    /**
     * 文件上传(本地文件上传)
     * @param url
     * 上传地址
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param fileList
     * 文件列表(具体路径)
     * @param fileType
     * 文件类型
     * @return
     */
    public static Result<List<FileUploadInfo>> uploadLocalFile(String url,String appId,String appKey,List<String> fileList,FileType fileType){
        return uploadLocalFile(url, appId, appKey, DEFAULT_TIME_OUT, fileList,fileType);
    }
    /**
     * 文件上传(本地文件上传)
     * @param url
     * 上传地址
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param timeOut
     * 超时时间
     * @param fileList
     * 文件列表(具体路径)
     * @param fileType
     * 文件类型
     * @return
     */
    public static Result<List<FileUploadInfo>> uploadLocalFile(String url,String appId,String appKey,int timeOut,List<String> fileList,FileType fileType){
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(appKey)){
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, "app_id和app_key不能为空");
        }
        if(fileList==null||fileList.size()==0){
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, "文件不能为空");
        }
        MultipartEntityBuilder multipartEntityBuilder=MultipartEntityBuilder.create();
        for(int i=0;i<fileList.size();i++){
            File file=new File(fileList.get(i));
            if(!file.isFile()){
                return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, "文件"+fileList.get(i)+"不存在");
            }
            multipartEntityBuilder.addPart("file"+i,new FileBody(file, ContentType.MULTIPART_FORM_DATA, file.getName()));
        }
        return upload(url, appId, appKey, timeOut, multipartEntityBuilder,fileType);
    }
    
    /**
     * 文件上传
     * @param url
     * 上传地址
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param timeOut
     * 超时时间
     * @param multipartEntityBuilder
     * @param fileType
     * 文件类型
     * 
     * @return
     */
    private static Result<List<FileUploadInfo>> upload(String url,String appId,String appKey,int timeOut,MultipartEntityBuilder multipartEntityBuilder,FileType fileType){
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(appKey)){
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null, "app_id和app_key不能为空");
        }
        String timeStamp=new Date().getTime()/1000+"";
        String sign=HMacSHA1.getSignature(appId+timeStamp,appKey);
        // 设置为浏览器兼容模式  
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);  
        // 设置请求的编码格式  
        multipartEntityBuilder.setCharset(Charset.forName("UTF-8"));  
        multipartEntityBuilder.addPart("appId",new StringBody(appId,ContentType.DEFAULT_TEXT));    
        multipartEntityBuilder.addPart("timeStamp",new StringBody(timeStamp,ContentType.DEFAULT_TEXT)); 
        multipartEntityBuilder.addPart("sign",new StringBody(sign,ContentType.DEFAULT_TEXT));
        multipartEntityBuilder.addPart("fileType",new StringBody(fileType.name(),ContentType.DEFAULT_TEXT));
        
        HttpEntity httpEntity = multipartEntityBuilder.build();
        HttpPost request = new HttpPost(url);
        //设置超时时间
        RequestConfig config=RequestConfig.custom().setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).setSocketTimeout(timeOut).build();
        System.out.println("config:"+config.toString());
        request.setConfig(config);
        request.setEntity(httpEntity);  
          
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();  
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (ClientProtocolException e) {
            logger.error("上传失败..."+e.getMessage(),e);
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,"文件上传失败");
        } catch (IOException e) {
            logger.error("上传失败..."+e.getMessage(),e);
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,"文件上传失败");
        }  
          
        InputStream is;
        try {
            is = response.getEntity().getContent();
        } catch (IllegalStateException | IOException e) {
            logger.error("读取服务器返回失败..."+e.getMessage(),e);
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,"读取服务器返回失败");
        }  
        BufferedReader in = new BufferedReader(new InputStreamReader(is));  
        StringBuffer buffer = new StringBuffer();  
        String line = "";  
        try {
            while ((line = in.readLine()) != null) {  
                buffer.append(line);  
            }
        } catch (IOException e) {
            logger.error("读取服务器返回失败..."+e.getMessage(),e);
            return new Result<List<FileUploadInfo>>(ResultCode.FAILURE, null,"读取服务器返回失败");
        }
        logger.info("发送消息收到的返回："+buffer.toString());
        Result<List<FileUploadInfo>> result=JSONUtils.fromJson(buffer.toString(), new TypeToken<Result<List<FileUploadInfo>>>(){});
        return result;
    }
    
    /**
     * 文件下载
     * @param url
     * 请求url
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param fileId
     * 文件ID
     * @return
     * 下载文件信息
     */
    public static Result<FileDownloadInfo> download(String url,String appId,String appKey,long fileId){
        return download(url, appId, appKey, fileId, DEFAULT_TIME_OUT);
    }
    /**
     * 文件下载
     * @param url
     * 请求url
     * @param appId
     * appId
     * @param appKey
     * appKey
     * @param fileId
     * 文件ID
     * @param timeOut
     * 请求超时
     * @return
     * 下载文件信息
     */
    public static Result<FileDownloadInfo> download(String url,String appId,String appKey,long fileId,int timeOut){
        /**
         * 1.参数校验
         */
        if(StringUtils.isBlank(appId)||StringUtils.isBlank(appKey)){
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "appId和appKey不能为空");
        }
        if(fileId<=0){
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "fileId不能小于等于0");
        }
        /**
         * 2.发送请求到服务器
         */
        String timeStamp=new Date().getTime()/1000+"";
        String sign=HMacSHA1.getSignature(appId+timeStamp,appKey);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("appId", appId));
        nvps.add(new BasicNameValuePair("timeStamp", timeStamp));
        nvps.add(new BasicNameValuePair("sign", sign));
        nvps.add(new BasicNameValuePair("fileId", fileId+""));
        HttpPost request = new HttpPost(url);
        //设置超时时间
        RequestConfig config=RequestConfig.custom().setConnectTimeout(timeOut).setConnectionRequestTimeout(timeOut).setSocketTimeout(timeOut).build();
        System.out.println("config:"+config.toString());
        request.setConfig(config);
        try {
            request.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            logger.error("编码类型错误...",e1);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null, "编码类型错误");
        }  
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient httpClient = httpClientBuilder.build();  
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(request);
        } catch (ClientProtocolException e) {
            logger.error("上传失败..."+e.getMessage(),e);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null,"文件上传失败");
        } catch (IOException e) {
            logger.error("上传失败..."+e.getMessage(),e);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null,"文件上传失败");
        }
        InputStream is;
        try {
            is = response.getEntity().getContent();
        } catch (IllegalStateException | IOException e) {
            logger.error("读取服务器返回失败..."+e.getMessage(),e);
            return new Result<FileDownloadInfo>(ResultCode.FAILURE, null,"读取服务器返回失败");
        }
        /**
         * 3.服务器返回码不为200，则获取错误提示
         */
        if(response.getStatusLine().getStatusCode()!=200){
            BufferedReader in = new BufferedReader(new InputStreamReader(is));  
            StringBuffer buffer = new StringBuffer();  
            String line = "";  
            try {
                while ((line = in.readLine()) != null) {  
                    buffer.append(line);  
                }
            } catch (IOException e) {
                logger.error("读取服务器返回失败..."+e.getMessage(),e);
                return new Result<FileDownloadInfo>(ResultCode.FAILURE, null,"读取服务器返回失败");
            }
            logger.info("发送消息收到的返回："+buffer.toString());
            return JSONUtils.fromJson(buffer.toString(), new TypeToken<Result<FileDownloadInfo>>(){});
        }
        /**
         * 4.返回成功，获取文件名并返回
         */
        String fileName="undefined";//默认文件名
        Header[] headers=response.getHeaders("Content-Disposition");
        if(headers!=null&&headers.length>0&&StringUtils.isNotBlank(headers[0].getValue())){
            int index=headers[0].getValue().indexOf("filename=");
            if(index>=0){
                fileName=headers[0].getValue().substring(index+"filename=".length());
            }
        }
        FileDownloadInfo downloanInfo=new FileDownloadInfo();
        downloanInfo.setFileId(fileId);
        downloanInfo.setOriFileName(fileName);
        downloanInfo.setInputStream(is);
        return new Result<FileHelper.FileDownloadInfo>(ResultCode.SUCCESS, downloanInfo);
    }
    
    /**
     * 上传文件输入流
     * @author liyongquan 2016年9月20日
     *
     */
    public static class FilePair{
        public FilePair(){}
        public FilePair(String fileName,InputStream inputStream){
            this.fileName=fileName;
            this.inputStream=inputStream;
            
        }
        /**
         * 文件名称(原文件名)
         */
        private String fileName;
        /**
         * 输入流
         */
        private InputStream inputStream;
        public String getFileName() {
            return fileName;
        }
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        public InputStream getInputStream() {
            return inputStream;
        }
        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }
    /**
     * 文件类型
     * @author liyongquan 2016年11月23日
     *
     */
    public static enum FileType{
        /**
         * 对外公开访问的文件
         */
        PUBLIC,
        /**
         * 内部访问的隐私文件
         */
        PRIVATE;
    }
    
    /**
     * 文件上传信息
     * @author liyongquan
     *
     */
    public static class FileUploadInfo{
        /**
         * 文件类型(文件类型为PRIVATE需要通过fileId进行下载)
         */
        private FileType fileType;
        /**
         * 文件ID
         */
        private long fileId;
        /**
         * 文件下载路径(如果是PUBLIC的文件才会返回这个字段，否则返回为空串)
         */
        private String downloadUrl;
        /**
         * 文件md5
         */
        private String md5;
        
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
        public FileType getFileType() {
            return fileType;
        }
        public void setFileType(FileType fileType) {
            this.fileType = fileType;
        }
        public long getFileId() {
            return fileId;
        }
        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
        public String getDownloadUrl() {
            return downloadUrl;
        }
        public void setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
        public String getMd5() {
            return md5;
        }
        public void setMd5(String md5) {
            this.md5 = md5;
        }
    }
    
    /**
     * 文件下载信息
     * @author liyongquan 2016年11月24日
     *
     */
    public static class FileDownloadInfo{
        /**
         * 文件ID
         */
        private long fileId;
        /**
         * 原文件名
         */
        private String oriFileName;
        /**
         * 文件输入流
         */
        private InputStream inputStream;
        public long getFileId() {
            return fileId;
        }
        public void setFileId(long fileId) {
            this.fileId = fileId;
        }
        public String getOriFileName() {
            return oriFileName;
        }
        public void setOriFileName(String oriFileName) {
            this.oriFileName = oriFileName;
        }
        public InputStream getInputStream() {
            return inputStream;
        }
        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
        }
    }
}
