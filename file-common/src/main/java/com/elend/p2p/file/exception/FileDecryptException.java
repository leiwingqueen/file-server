package com.elend.p2p.file.exception;

/**
 * 文件解密异常
 * @author liyongquan
 *
 */
public class FileDecryptException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6828405345879102005L;
	public FileDecryptException(){
		
	}
	public FileDecryptException(Exception e){
		super(e);
	}
	public FileDecryptException(String msg){
		super(msg);
	}
}
