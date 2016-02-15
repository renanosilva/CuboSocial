package br.ufrn.imd.cubo.geral.thread;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import br.ufrn.imd.cubo.arq.util.Formatador;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.ConsoleArduino;
import br.ufrn.imd.cubo.geral.rest.ExecucaoREST;

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
	
	protected void executar(int idCubo){
		while (!filaCodigos.isEmpty()) {
			
			Codigo c = new Codigo();
			String resultado = "";
			
			try {
				c = filaCodigos.peek();
				
				System.out.println("EXECUTANDO CODIGO: " + c.getId() + " - " +  c.getTitulo());
				resultado = ExecucaoREST.executarCodigo(c.getCodigo(), idCubo);
				System.out.println(resultado);
				System.out.println("TERMINADA EXECU��O DE: " + c.getId() + " " +  c.getTitulo());
				
//				ConsoleArduino.getInstance().adicionarTexto("\n\n" + 
//						Formatador.getInstance().formatarDataHora(new Date()) + 
//						": EXECUTANDO CODIGO: " + c.getId() + " - " + c.getTitulo() + "\n" + 
//						resultado + "\n" +
//						"TERMINADA EXECU��O DE: " + c.getId() + " " +  c.getTitulo());
				
				c.getExecucao().setResultado(Formatador.getInstance().formatarDataHora(new Date()) + 
						": EXECUTANDO CODIGO: " + c.getTitulo() + "\n" + 
						resultado + "\n");
				
				ConsoleArduino.getInstance().addExecucao(c.getExecucao());
				
				if (!possuiErro(resultado)){
					sleep(TEMPO_MIN_EXECUCAO_CODIGO);
				} 
				
			} catch (InterruptedException e) {
				e.printStackTrace();
//				ConsoleArduino.getInstance().adicionarTexto(Formatador.getInstance().formatarDataHora(new Date()) + 
//						": TERMINADA EXECU��O DE: " + c.getId() + " " +  c.getTitulo() + ". Ocorreu um erro de sistema. "
//						+ "Por favor, entre em contato com a administra��o.");
				
				c.getExecucao().setResultado(Formatador.getInstance().formatarDataHora(new Date()) + 
						": Ocorreu um erro de sistema. Por favor, entre em contato com a administra��o.");
				ConsoleArduino.getInstance().addExecucao(c.getExecucao());
				
			} catch (Exception e){
				e.printStackTrace();
//				ConsoleArduino.getInstance().adicionarTexto("EXECUTANDO CODIGO: " + c.getId() + " - " +  c.getTitulo() + " ");
//				ConsoleArduino.getInstance().adicionarTexto(Formatador.getInstance().formatarDataHora(new Date()) + 
//						": Ocorreu um erro de sistema. Por favor, entre em contato com a administra��o. " +
//						"TERMINADA EXECU��O DE: " + c.getId() + " " +  c.getTitulo());
				c.getExecucao().setResultado(Formatador.getInstance().formatarDataHora(new Date()) + 
						": Ocorreu um erro de sistema. Por favor, entre em contato com a administra��o.");
				ConsoleArduino.getInstance().addExecucao(c.getExecucao());
			} finally {
				filaCodigos.poll();
			}
		}
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
	
	protected boolean possuiErro(String result){
		return result.contains("compilation terminated") ||
				result.contains("error") ||
				result.contains("fatal") ||
				result.contains("stop.");
	}
	
}
