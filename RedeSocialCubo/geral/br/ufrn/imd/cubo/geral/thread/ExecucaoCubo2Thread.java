package br.ufrn.imd.cubo.geral.thread;

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
		executar(2);
		reiniciarInstancia();
	}
	
	public static ExecucaoCodigoThread getInstance() {
		if (instance == null)
			reiniciarInstancia();
		
		return instance;
	}
	
}
