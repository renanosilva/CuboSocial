package br.ufrn.imd.cubo.geral.thread;

/**
 * Thread de execu��o do cubo 4x4.
 * 
 * @author Renan
 */
public class ExecucaoCubo5Thread extends ExecucaoCodigoThread {

	private static ExecucaoCodigoThread instance;
	
	protected static void reiniciarInstancia() {
		instance = new ExecucaoCubo5Thread();
	}
	

	@Override
	public void run() {
		while (!filaCodigos.isEmpty()) {
			
			//TODO: executar thread
			
			/* TODO: se houver sucesso, esperar 30s at� executar a pr�xima; se n�o,
			 * executar a pr�xima imediatamente. */
			
			try {
				filaCodigos.peek();
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