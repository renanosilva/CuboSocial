package br.ufrn.imd.cubo.geral.thread;

/**
 * Thread de execução do cubo 4x4.
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
		executar(5);
		reiniciarInstancia();
	}
	
	public static ExecucaoCodigoThread getInstance() {
		if (instance == null)
			reiniciarInstancia();
		
		return instance;
	}
	
}
