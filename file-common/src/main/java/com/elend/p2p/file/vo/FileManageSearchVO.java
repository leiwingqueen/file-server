package com.elend.p2p.file.vo;

import com.elend.p2p.util.vo.BaseSearchVO;

public class FileManageSearchVO extends BaseSearchVO{
	private String fileMd5;
	private long fileId;
	private String appId;

	public String getFileMd5() {
		return fileMd5;
	}

	public void setFileMd5(String fileMd5) {
		this.fileMd5 = fileMd5;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}
