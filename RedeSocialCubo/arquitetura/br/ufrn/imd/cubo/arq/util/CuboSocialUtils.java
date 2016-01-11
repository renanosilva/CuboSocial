package br.ufrn.imd.cubo.arq.util;

import java.util.Collection;

import br.ufrn.imd.cubo.arq.dominio.PersistDB;

/**
 * M�todos �teis relativos ao sistema.
 * 
 * @author Renan
 */
public class CuboSocialUtils {
	
	public static String getContext(){
		return "CuboSocial";
	}
	
	public static String getContextBarras(){
		return "/" + getContext() + "/";
	}
	
	/** 
	 * Transforma uma cole��o de entidades de dom�nio em um array com seus IDs.
	 */
	public static int[] persistDbToArray(Collection<? extends PersistDB> objs) {
		int[] array = new int[objs.size()];
		int i = 0;
		for (PersistDB p : objs)
			array[i++] = p.getId();

		return array;
	}

}
