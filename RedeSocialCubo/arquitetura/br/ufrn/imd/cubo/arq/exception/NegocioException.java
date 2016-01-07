package br.ufrn.imd.cubo.arq.exception;

import java.util.List;

/**
 * Exce��o que ocorre ao ser um violada uma regra de neg�cio de algum caso
 * de uso. 
 * @author Renan
 */
@SuppressWarnings("serial")
public class NegocioException extends Exception {
	
	/** Utilizado quando se deseja adicionar mais de uma mensagem de erro. */
	private List<String> msgs;
	
	public NegocioException() {
		super();
	}
	
	public NegocioException(String msg) {
		super(msg);
	}
	
	public NegocioException(Exception e) {
		super(e);
	}
	
	public NegocioException(List<String> msgs){
		super();
		this.msgs = msgs;
	}

	public List<String> getMsgs() {
		return msgs;
	}
	
}
