package br.ufrn.imd.cubo.geral.thread;

/**
 * Thread de execução do cubo 3x3.
 * 
 * @author Renan
 */
public class ExecucaoCubo3Thread extends ExecucaoCodigoThread {

	private static ExecucaoCodigoThread instance;
	
	protected static void reiniciarInstancia() {
		instance = new ExecucaoCubo3Thread();
	}
	
	@Override
	public void run() {
		executar(3);
		reiniciarInstancia();
	}
	
	public static ExecucaoCodigoThread getInstance() {
		if (instance == null)
			reiniciarInstancia();
		
		return instance;
	}
	
}
