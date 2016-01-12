package br.ufrn.imd.cubo.geral.controller;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractController;

/** 
 * MBean que contém métodos relacionados ao Estados brasileiros. 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@RequestScoped
public class MenuPrincipalMBean extends AbstractController {
	
	private enum ItemMenu {
		TIMELINE, NOVO_CODIGO, MEUS_CODIGOS, VISUALIZAR_CUBOS, ALTERAR_PERFIL;
	}
	
	public ItemMenu getMenuTimeline(){
		return ItemMenu.TIMELINE;
	}
	
	public ItemMenu getMenuNovoCodigo(){
		return ItemMenu.NOVO_CODIGO;
	}
	
	public ItemMenu getMenuMeusCodigos(){
		return ItemMenu.MEUS_CODIGOS;
	}
	
	public ItemMenu getMenuVisualizarCubos(){
		return ItemMenu.VISUALIZAR_CUBOS;
	}
	
	public ItemMenu getMenuAlterarPerfil(){
		return ItemMenu.ALTERAR_PERFIL;
	}
	
}
