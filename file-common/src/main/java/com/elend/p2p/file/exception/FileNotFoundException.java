package com.elend.p2p.file.exception;

/**
 * 找不到文件异常
 * @author liyongquan
 *
 */
public class FileNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6828405345879102005L;
	public FileNotFoundException(){
		
	}
	public FileNotFoundException(Exception e){
		super(e);
	}
	public FileNotFoundException(String msg){
		super(msg);
	}
}
