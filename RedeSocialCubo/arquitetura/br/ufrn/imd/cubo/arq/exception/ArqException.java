package br.ufrn.imd.cubo.arq.exception;

@SuppressWarnings("serial")
public class ArqException extends Exception {
	
	public ArqException() {
		super();
	}
	
	public ArqException(Exception e) {
		super(e);
	}
	
	public ArqException(String msg) {
		super(msg);
	}

}
