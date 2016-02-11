package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.geral.dominio.Cubo;

/** 
 * Controla o caso de uso de Visualizar Cubo.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class VisualizarCuboMBean extends AbstractControllerCadastro<Cubo> {
	
	@PostConstruct
	private void init() {
		obj = new Cubo();
		dao = new GenericDAOImpl();
	}
	
	public String entrar() {
		return Paginas.VISUALIZAR_CUBO;
	}
	
	/** Carrega a URL de streaming do cubo, a partir do ID do cubo que o usuário selecionou. */
	public void carregarUrlStreaming(){
		int idCubo = obj.getId();
		
		if (idCubo > 0){
			obj = dao.findByPrimaryKey(idCubo, Cubo.class);
			dao.detach(obj);
		} else {
			obj = new Cubo();
		}
	}
	
}
