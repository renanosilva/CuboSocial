package br.ufrn.imd.cubo.geral.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dominio.Cubo;

/** 
 * MBean para métodos genéricos.
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class CuboMBean extends AbstractController {
	
	/** Cubos disponíveis no sistema. */
	private List<Cubo> cubos;
	
	/**
	 * Retorna combo box com lista de cubos do sistema.
	 */
	public List<SelectItem> getSelectCubos() {

		if (ValidatorUtil.isEmpty(cubos)){
			 IGenericDAO dao = new GenericDAOImpl();
			 cubos = dao.findAllAtivos(Cubo.class);
		}
		
		List<SelectItem> result = new ArrayList<SelectItem>();

		for (int i = 0; i < cubos.size(); i++){
			Cubo cubo = cubos.get(i);
			
			SelectItem item = new SelectItem(cubo.getId(), cubo.getNome());
			result.add(item);
		}
		
		return result;

	}
	
}
