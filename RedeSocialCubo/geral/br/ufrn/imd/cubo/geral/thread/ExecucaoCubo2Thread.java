package br.ufrn.imd.cubo.geral.thread;

/**
 * Thread de execução do cubo 2x2.
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
			
			/* TODO: se houver sucesso, esperar 30s até executar a próxima; se não,
			 * executar a próxima imediatamente. */
			
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
