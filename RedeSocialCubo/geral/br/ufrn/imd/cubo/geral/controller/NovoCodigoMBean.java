package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Cubo;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCadastraCodigo;

/** 
 * MBean que controla operações relacionadas à criação de novos códigos.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class NovoCodigoMBean extends AbstractControllerCadastro<Codigo> {

	/** URL de streaming selecionada para visualização. */
	private String urlStreaming;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		obj.setCubo(new Cubo());
		dao = new GenericDAOImpl();
		
		urlStreaming = "";
	}
	
	/** Entra na tela de novo código. */
	public String entrar(){
		return Paginas.NOVO_CODIGO;
	}
	
	/** Carrega os dados do código, caso seja edição. */
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
			
			addMsgInfo(rascunho ? "Código salvo com sucesso!" : "Código publicado com sucesso!");
			
			return posCadastro();
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		}
	}
	
	public String executarCodigo(){
		try {
			salvar(obj.getId() == 0 ? true : !obj.isFinalizado());
			
			ExecutarCodigoMBean mBean = getMBean("executarCodigoMBean");
			mBean.executarCodigo(obj.getId());
			
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} 
		
		return null;
	}
	
	@Override
	protected String posCadastro() {
		if (obj.isFinalizado()){
			TimelineMBean bean = getMBean("timelineMBean");
			bean.setCodigos(null);
			return Paginas.PORTAL_INICIO;
		} else {
			return null;
		}
	}
	
	/** Carrega a URL de streaming do cubo, a partir do ID do cubo que o usuário selecionou. */
	public void carregarUrlStreaming(){
		int idCubo = obj.getCubo().getId();
		
		if (idCubo > 0){
			Cubo cubo = dao.findByPrimaryKey(idCubo, Cubo.class);
			urlStreaming = cubo.getUrl();
		}
	}

	public String getUrlStreaming() {
		return urlStreaming;
	}

	public void setUrlStreaming(String urlStreaming) {
		this.urlStreaming = urlStreaming;
	}
	
}
