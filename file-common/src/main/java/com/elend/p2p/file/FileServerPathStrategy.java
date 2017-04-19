package com.elend.p2p.file;

import com.elend.p2p.file.service.FileService.FileType;

/**
 * 文件写入策略
 * @author liyongquan 2016年11月24日
 *
 */
public interface FileServerPathStrategy {
    /**
     * 获取写入的文件路径
     * @param appId
     * appId
     * @param fileType
     * 文件类型
     * @return
     * 写入的文件路径
     */
    String getPath(String appId,FileType fileType);
}
