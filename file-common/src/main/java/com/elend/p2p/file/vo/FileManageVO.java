package com.elend.p2p.file.vo;

import java.io.InputStream;

import com.elend.p2p.file.model.FileManage;

public class FileManageVO extends FileManage {
    /**
	 * 
	 */
    private static final long serialVersionUID = 4344559494552418819L;

    private InputStream inputStream;

    /** 文件完整路径 */
    private String fullPath;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

}
