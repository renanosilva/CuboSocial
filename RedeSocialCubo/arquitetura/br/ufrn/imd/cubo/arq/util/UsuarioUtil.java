package br.ufrn.imd.cubo.arq.util;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import br.ufrn.imd.cubo.arq.dominio.Usuario;

/**
 * Classe com métodos úteis relativos a usuários do sistema.
 * @author Renan
 */
public class UsuarioUtil {
	
	public static Usuario getUsuarioLogado(){
		if (FacesContext.getCurrentInstance() == null)
			return null;
		
		HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		return (Usuario) req.getSession().getAttribute("usuarioLogado");
	}

}
