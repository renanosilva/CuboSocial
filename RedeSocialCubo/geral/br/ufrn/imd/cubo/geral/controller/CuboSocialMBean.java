package br.ufrn.imd.cubo.geral.controller;

import javax.faces.bean.ManagedBean;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.CuboSocialUtils;

/** 
 * MBean para métodos genéricos.
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
public class CuboSocialMBean extends AbstractController {
	
	/** Retorna a URL da foto do perfil do usuário logado. */
	public String getUrlFotoUsuario(){
		return "/verArquivo?"
				+ "idFoto=" + getUsuarioLogado().getIdFoto()
				+"&key=" + CriptografiaUtils.criptografarMD5(String.valueOf(getUsuarioLogado().getIdFoto())) 
				+ "&salvar=false"; 
	}
	
	public String getContext(){
		return CuboSocialUtils.getContext();
	}
	
	public String getContextBarras(){
		return CuboSocialUtils.getContextBarras();
	}
	
}
