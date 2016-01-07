package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCadastraCodigo;

/** 
 * MBean que controla opera��es relacionadas � cria��o de novos c�digos.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class NovoCodigoMBean extends AbstractControllerCadastro<Codigo> {

	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new GenericDAOImpl();
	}
	
	/** Entra na tela de novo c�digo. */
	public String entrar(){
		return Paginas.NOVO_CODIGO;
	}
	
	/** Carrega os dados do c�digo, caso seja edi��o. */
	public String getCarregarCodigo(){
		int idCodigo = getParameterInt("idCodigo", -1);
		
		if (idCodigo != -1){
			obj = dao.findByPrimaryKey(idCodigo, Codigo.class);
		}
		
		return null;
	}
	
	public String salvar(boolean rascunho) throws InstantiationException,
			IllegalAccessException {
		
		obj.setFinalizado(!rascunho);
		
		try {
			ProcessadorCadastraCodigo p = new ProcessadorCadastraCodigo();
			p.setObj(obj);
			p.execute();
			
			addMsgInfo(rascunho ? "C�digo salvo com sucesso!" : "C�digo publicado com sucesso!");
			
			return posCadastro();
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		}
	}
	
	@Override
	protected String posCadastro() {
		return Paginas.PORTAL_INICIO;
	}

}
