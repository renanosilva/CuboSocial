package br.ufrn.imd.cubo.geral.controller;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.model.SelectItem;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.geral.dominio.UF;

/** 
 * MBean que contém métodos relacionados ao Estados brasileiros. 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
public class UFMBean extends AbstractController {
	
	/** Retorna uma lista de SelectItem com todas as UFs brasileiras. */
	public List<SelectItem> getAllUFsCombo(){
		List<UF> ufs = UF.getAllUFsBrasileiras();
		List<SelectItem> result = new ArrayList<SelectItem>();
		
		for (UF uf : ufs){
			result.add(new SelectItem(uf.getSigla(), uf.getNome()));
		}
		
		return result; 
	}
	
}
