package br.ufrn.imd.cubo.arq.dominio;

/**
 * Enum que armazena os possíveis tipos de usuários do sistema.
 * 
 * @author Renan
 */
public enum TipoUsuario {
	
	/* ATENÇÃO: ESSES NOMES NÃO PODEM SER MODIFICADOS. */
	
	/** Usuário comum. */
	COMUM,
	
	/** Administrador do sistema */
	ADMINISTRADOR;
	
	public String toString() {
		if (this == TipoUsuario.ADMINISTRADOR){
			return "Administrador";
		} else if (this == TipoUsuario.ADMINISTRADOR){
			return "Administrador";
		} else {
			return "Não identificado";
		}
	}
	
}
