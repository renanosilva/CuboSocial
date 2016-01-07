package br.ufrn.imd.cubo.arq.dominio;

/**
 * Enum que armazena os poss�veis tipos de usu�rios do sistema.
 * 
 * @author Renan
 */
public enum TipoUsuario {
	
	/* ATEN��O: ESSES NOMES N�O PODEM SER MODIFICADOS. */
	
	/** Usu�rio comum. */
	COMUM,
	
	/** Administrador do sistema */
	ADMINISTRADOR;
	
	public String toString() {
		if (this == TipoUsuario.ADMINISTRADOR){
			return "Administrador";
		} else if (this == TipoUsuario.ADMINISTRADOR){
			return "Administrador";
		} else {
			return "N�o identificado";
		}
	}
	
}
