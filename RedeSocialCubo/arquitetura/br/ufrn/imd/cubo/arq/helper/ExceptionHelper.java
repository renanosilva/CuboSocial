package br.ufrn.imd.cubo.arq.helper;

/** 
 * Contém métodos auxiliares para tratamento de exceções.
 * 
 * @author Renan
 */
public class ExceptionHelper {

	public static String getStackTrace(Throwable t){
		String msg = t.toString() + "<br/>";
		
		if (t.getStackTrace() != null){
			for (StackTraceElement s : t.getStackTrace()){
				msg += s.toString() + "<br/>";
			}
		}
		
		if (t.getCause() != null){
			msg += "Caused by: " + t.getCause().toString() + "<br/>";
			
			if (t.getCause().getStackTrace() != null){
				for (StackTraceElement s : t.getCause().getStackTrace()){
					msg += s.toString() + "<br/>";
				}
			}
		}
		
		
		return msg;
	}
	
}
