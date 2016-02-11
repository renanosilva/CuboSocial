package br.ufrn.imd.cubo.geral.thread;

/**
 * Thread de execução do cubo 4x4.
 * 
 * @author Renan
 */
public class ExecucaoCubo4Thread extends ExecucaoCodigoThread {

	private static ExecucaoCodigoThread instance;
	
	protected static void reiniciarInstancia() {
		instance = new ExecucaoCubo4Thread();
	}
	

	@Override
	public void run() {
		executar(4);
		reiniciarInstancia();
	}
	
	public static ExecucaoCodigoThread getInstance() {
		if (instance == null)
			reiniciarInstancia();
		
		return instance;
	}
	
}
