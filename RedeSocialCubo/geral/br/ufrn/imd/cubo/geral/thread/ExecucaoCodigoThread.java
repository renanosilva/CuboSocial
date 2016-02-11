package br.ufrn.imd.cubo.geral.thread;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.ufrn.imd.cubo.arq.util.Formatador;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.ConsoleArduino;
import br.ufrn.imd.cubo.geral.rest.ExecucaoREST;

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
	
	protected void executar(int idCubo){
		while (!filaCodigos.isEmpty()) {
			
			Codigo c = new Codigo();
			
			try {
				c = filaCodigos.peek();
				
				System.out.println("EXECUTANDO CODIGO: " + c.getTitulo());
				String resultado = ExecucaoREST.executarCodigo(c.getCodigo(), idCubo);
				System.out.println(resultado);
				
				ConsoleArduino.getInstance().adicionarTexto("\n\n" + 
						Formatador.getInstance().formatarDataHora(new Date()) + 
						": EXECUTANDO CODIGO: " + c.getTitulo() + "\n" + resultado);
				
				if (!possuiErro(resultado)){
					sleep(TEMPO_MIN_EXECUCAO_CODIGO);
				} 
				
			} catch (InterruptedException e) {
				e.printStackTrace();
				ConsoleArduino.getInstance().adicionarTexto(Formatador.getInstance().formatarDataHora(new Date()) + 
						": ERRO ao executar código: " + c.getTitulo() + ". Ocorreu um erro de sistema. "
						+ "Por favor, entre em contato com a administração.");
			} catch (Exception e){
				e.printStackTrace();
				ConsoleArduino.getInstance().adicionarTexto(Formatador.getInstance().formatarDataHora(new Date()) + 
						": ERRO ao executar código: " + c.getTitulo() + ". Ocorreu um erro de sistema. "
						+ "Por favor, entre em contato com a administração.");
			} finally {
				filaCodigos.poll();
			}
		}
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
	
	protected boolean possuiErro(String result){
		return result.contains("compilation terminated") ||
				result.contains("error") ||
				result.contains("fatal") ||
				result.contains("stop.");
	}
	
}
