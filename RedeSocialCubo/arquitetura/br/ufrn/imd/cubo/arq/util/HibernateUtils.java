package br.ufrn.imd.cubo.arq.util;

import org.hibernate.exception.ConstraintViolationException;

/**
 * Classe com métodos úteis referentes ao Hibernate. 
 * @author Renan
 */
public class HibernateUtils {
	
	/**
	 * Verifica se uma exceção foi gerada devido a um erro de violação de
	 * restrição de chave estrangeira.
	 *
	 * @param e
	 * @return
	 */
	public static boolean isFKConstraintError(Exception e) {
		ConstraintViolationException cve = null;
		
		if (e.getCause() != null && e.getCause() instanceof ConstraintViolationException) {
			cve = (ConstraintViolationException) e.getCause();
		} else if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof ConstraintViolationException){
			cve = (ConstraintViolationException) e.getCause().getCause();
		}

		if (cve != null){
			if (cve.getSQLException() != null) {
				String msg = cve.getSQLException().getMessage();
				if (msg.contains("viola restrição de chave estrangeira"))
					return true;
				else {
					if (cve.getSQLException().getNextException() != null) {
						msg = cve.getSQLException().getNextException().toString();
						if (msg.contains("viola restrição de chave estrangeira"))
							return true;
					}
				}
			}
		}
		return false;
	}
	
	/** 
	 * Transforma um array de inteiros em uma String que pode ser usada
	 * em uma cláusula IN de SQL.
	 */
	public static String gerarStringIn(int[] ids) {
		StringBuilder in = new StringBuilder();
		in.append(" ( ");

		int t = ids.length;
		for (int i = 0; i < t; i++) {
			in.append(ids[i]);
			if (i < (t - 1)) {
				in.append(",");
			}
		}
		in.append(" )");
		return in.toString();
	}
	
}
