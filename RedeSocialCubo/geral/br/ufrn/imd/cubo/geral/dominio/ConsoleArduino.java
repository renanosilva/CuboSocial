package br.ufrn.imd.cubo.geral.dominio;

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

}
