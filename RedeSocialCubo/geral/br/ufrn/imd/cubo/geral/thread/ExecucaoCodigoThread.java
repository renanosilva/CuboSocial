package br.ufrn.imd.cubo.geral.thread;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.ufrn.imd.cubo.geral.dominio.Codigo;

/**
 * Thread que controla a fila de execução de códigos dos usuários 
 * do sistema.
 * 
 * @author Renan
 *
 */
public abstract class ExecucaoCodigoThread extends Thread {

	/** 
	 * Tempo mínimo (em milisegundos) que cada código ficará sendo executado,
	 * caso haja mais códigos na fila. 
	 * */
	public static final Integer TEMPO_MIN_EXECUCAO_CODIGO = 30000;
	
	/** Fila de códigos a serem executados. */
	protected Queue<Codigo> filaCodigos;
	
	protected ExecucaoCodigoThread() {
		filaCodigos = new ConcurrentLinkedQueue<>();
	}
	
	/** Adiciona um código à fila, apenas se ele não já estiver nela. */
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
