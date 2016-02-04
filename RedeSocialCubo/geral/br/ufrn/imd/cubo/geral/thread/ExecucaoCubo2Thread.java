package br.ufrn.imd.cubo.geral.thread;

import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.rest.ExecucaoREST;

/**
 * Thread de execu��o do cubo 2x2.
 * 
 * @author Renan
 */
public class ExecucaoCubo2Thread extends ExecucaoCodigoThread {

	private static ExecucaoCodigoThread instance;
	
	protected static void reiniciarInstancia() {
		instance = new ExecucaoCubo2Thread();
	}
	
	@Override
	public void run() {
		while (!filaCodigos.isEmpty()) {
			
			//TODO: executar thread
			
			/* TODO: se houver sucesso, esperar 30s at� executar a pr�xima; se n�o,
			 * executar a pr�xima imediatamente. */
			
			try {
				Codigo c = filaCodigos.peek();
				
				ExecucaoREST.executarCodigo(c.getCodigo(), 2);
				
				sleep(TEMPO_MIN_EXECUCAO_CODIGO);
				filaCodigos.poll();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		reiniciarInstancia();
	}
	
	public static ExecucaoCodigoThread getInstance() {
		if (instance == null)
			reiniciarInstancia();
		
		return instance;
	}
	
}
