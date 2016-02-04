package br.ufrn.imd.cubo.geral.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.ufrn.imd.cubo.geral.dominio.Codigo;

/**
 * Thread que controla a fila de execu��o de c�digos dos usu�rios 
 * do sistema.
 * 
 * @author Renan
 *
 */
public abstract class ExecucaoCodigoThread extends Thread {

	/** 
	 * Tempo m�nimo (em milisegundos) que cada c�digo ficar� sendo executado,
	 * caso haja mais c�digos na fila. 
	 * */
	public static final Integer TEMPO_MIN_EXECUCAO_CODIGO = 30000;
	
	/** Fila de c�digos a serem executados. */
	protected Queue<Codigo> filaCodigos;
	
	protected ExecucaoCodigoThread() {
		filaCodigos = new ConcurrentLinkedQueue<>();
	}
	
	/** Adiciona um c�digo � fila, apenas se ele n�o j� estiver nela. */
	public boolean adicionarAFila(Codigo c){
		if (filaCodigos.contains(c))
			return false;
		
		return filaCodigos.add(c);
	}
	
	public Integer getTamanhoFila(){
		return filaCodigos.size();
	}
	
	public Queue<Codigo> getFila(){
		return filaCodigos;
	}

}
