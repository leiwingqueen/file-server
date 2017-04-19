package com.elend.p2p.file;

import com.elend.p2p.file.exception.PatitionNotFoundException;

//新上传使用 FileServerPathStrategy
@Deprecated 
public interface FilePathStrategy {
	String getPath(String appId,int partitionId)throws PatitionNotFoundException;
	
	String getDelPath(String appId);
}
