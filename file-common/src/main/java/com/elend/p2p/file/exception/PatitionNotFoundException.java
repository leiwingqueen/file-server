package com.elend.p2p.file.exception;

/**
 * 找不到分区异常
 * @author liyongquan
 *
 */
public class PatitionNotFoundException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6828405345879102005L;
	public PatitionNotFoundException(){
		
	}
	public PatitionNotFoundException(Exception e){
		super(e);
	}
	public PatitionNotFoundException(String msg){
		super(msg);
	}
}
