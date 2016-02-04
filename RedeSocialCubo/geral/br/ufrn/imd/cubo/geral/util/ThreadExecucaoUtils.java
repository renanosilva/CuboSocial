package br.ufrn.imd.cubo.geral.util;

import br.ufrn.imd.cubo.geral.dominio.Cubo;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCodigoThread;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCubo2Thread;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCubo3Thread;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCubo4Thread;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCubo5Thread;

/**
 * Métodos úteis referentes às threads de execução de códigos.
 * 
 * @author Renan
 */
public class ThreadExecucaoUtils {

	/** 
	 * Retorna a thread de execução referente ao cubo passado como parâmetro.
	 * */
	public static ExecucaoCodigoThread getThreadExecucao(Cubo c){
		if (c.is2x2x2()){
			return ExecucaoCubo2Thread.getInstance();
		} else if (c.is3x3x3()){
			return ExecucaoCubo3Thread.getInstance();
		} else if (c.is4x4x4()){
			return ExecucaoCubo4Thread.getInstance();
		} else if (c.is5x5x5()){
			return ExecucaoCubo5Thread.getInstance();
		} else {
			return null;
		}
	}
	
}
