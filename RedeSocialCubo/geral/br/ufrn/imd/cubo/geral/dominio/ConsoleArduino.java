package br.ufrn.imd.cubo.geral.dominio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton que armazenar� o console de execu��o do arduino.
 *  
 * @author Renan
 */
public class ConsoleArduino {
	
	/** Tamanho m�ximo (em n�mero de caracteres) do texto do console. */
	private static final int TAM_MAXIMO_CONSOLE = 3000000;
	
	private String console;
	
	private static ConsoleArduino instance;
	
	private List<ExecucaoCodigo> listaExecucao = Collections.synchronizedList(new ArrayList<ExecucaoCodigo>());
	
	private ConsoleArduino() {
		console = "";
	}
	
	public static ConsoleArduino getInstance(){
		if (instance == null)
			instance = new ConsoleArduino();
		
		return instance;
	}
	
	public void adicionarTexto(String texto){
		/* Limpando o console, para evitar que fique muito grande
		e tamb�m para evitar que o scroll do div que exibe o console
		n�o permane�a no mesmo local ap�s ser recarregado */
		
		if (console.length() > TAM_MAXIMO_CONSOLE){
			console = "";
		}
		
		console = console + texto;
		
//		console = console + texto;
//		
//		if (console.length() > TAM_MAXIMO_CONSOLE){
//			int dif = console.length() - TAM_MAXIMO_CONSOLE;
//			console = console.substring(dif);
//		}
	}
	
	public String getTextoConsole(){
		return console;
	}

	public ExecucaoCodigo getExecucao(ExecucaoCodigo e) {
		int idx = listaExecucao.indexOf(e);
		return idx != -1 ? listaExecucao.get(idx) : null; 
	}
	
	public void addExecucao(ExecucaoCodigo e){
		synchronized (listaExecucao) {
			listaExecucao.add(e);
		}
	}
	
	public void removerExecucao(ExecucaoCodigo e){
		synchronized (listaExecucao) {
			listaExecucao.remove(e);
		}
	}

}
