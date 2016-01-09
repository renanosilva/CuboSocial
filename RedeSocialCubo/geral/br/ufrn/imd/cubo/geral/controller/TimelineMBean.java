package br.ufrn.imd.cubo.geral.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dao.PagingInformation;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCurtirPublicacao;

/** 
 * MBean que controla opera��es relacionadas � timeline
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class TimelineMBean extends AbstractControllerCadastro<Codigo> {

	/** Quantidade de c�digos a serem exibidos por p�gina. */
	private static final int QTD_CODIGOS = 10;
	
	/** C�digos que ser�o exibidos na timeline. */
	private List<Codigo> codigos;
	
	/** T�tulo pelo qual o usu�rio deseja filtrar as postagens. */
	private String tituloBusca;
	
	/** Tipos de op��es existentes de ordena��o de publica��es na timeline. */
	private enum OpcaoOrdenar {
		QUALQUER, MAIS_RECENTES, MAIS_ANTIGAS, MAIS_CURTIDAS, MENOS_CURTIDAS,
		MAIS_COMENTADAS, MENOS_COMENTADAS, MELHORES_AVALIADAS, PIORES_AVALIADAS;
	}
	
	/** 
	 * Tipos existentes de timeline.<br/>
	 * NORMAL: exibe todas as publica��es existentes, de todos os usu�rios.<br/>
	 * MEUS_CODIGOS_PUBLICADOS: exibe apenas os c�digos do usu�rio logado que est�o publicados.<br/>
	 * MEUS_RASCUNHOS: exibe apenas os c�digos do usu�rio logado que n�o foram publicados ainda.
	 * */
	private enum TipoTimeline {
		NORMAL, MEUS_CODIGOS_PUBLICADOS, MEUS_RASCUNHOS;
	}
	
	/** Op��o selecionada pelo usu�rio */
	private OpcaoOrdenar opcao;
	
	/** Armazena o tipo de timeline selecionado no momento. */
	private TipoTimeline tipoTimeline;
	
	/** Armazena as op��es de pagina��o de consulta a c�digos. */
	private PagingInformation paginacao;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new CodigoDAO();
		codigos = null;
		
		paginacao = new PagingInformation(0, QTD_CODIGOS);
		tipoTimeline = TipoTimeline.NORMAL;
	}
	
	/** Entra na tela da timeline. */
	public String inicio(){
		return Paginas.PORTAL_INICIO;
	}
	
	public String buscar(){
		//Setando c�digos para null para poder buscar novamente, via getCodigos()
		codigos = null;
		
		return null;
	}
	
	/** Curtir uma publica��o. */
	public void curtirPublicacao(ActionEvent evt) {
		IGenericDAO dao = null;
		
		try {
			dao = new GenericDAOImpl();
			
			Codigo publicacao = dao.findByPrimaryKey(getParameterInt("idPublicacao"), Codigo.class);

			ProcessadorCurtirPublicacao p = new ProcessadorCurtirPublicacao();
			p.setObj(publicacao);
			p.execute();
			
			//Atualizando registro
			
			publicacao = dao.findByPrimaryKey(publicacao.getId(), Codigo.class);
			int pos = codigos.indexOf(publicacao);
			codigos.set(pos, publicacao);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Avaliar uma publica��o. */
	public void avaliarPublicacao(ActionEvent evt) {
		IGenericDAO dao = null;
		evt.getComponent().getChildren();
//		try {
//			dao = new GenericDAOImpl();
//			
//			Codigo publicacao = dao.findByPrimaryKey(getParameterInt("idPublicacao"), Codigo.class);
//
//			ProcessadorAvaliarPublicacao p = new ProcessadorAvaliarPublicacao();
//			p.setObj(publicacao);
//			p.execute();
//			
//			//Atualizando registro
//			
//			publicacao = dao.findByPrimaryKey(publicacao.getId(), Codigo.class);
//			int pos = codigos.indexOf(publicacao);
//			codigos.set(pos, publicacao);
//			
//		} catch (ArqException e) {
//			tratamentoErroPadrao(e);
//		} catch (NegocioException e) {
//			tratamentoNegocioException(e);
//		}
	}
	
	/**
	 * Mude a p�gina atual da pagina��o de acordo com o par�metro informado, referente � listagem de publica��es.
	 * 
	 * @param event
	 */
	public String changePage(){
		int pagina = getParameterInt("pagina");
		paginacao.setPaginaAtual(pagina - 1);
		codigos = null;
		return null;
	}
	
	/**
	 * M�todo chamado para redirecionar para a pr�xima p�gina da pagina��o, referente � listagem de publica��es.
	 */
	public String next(){
		paginacao.nextPage(null);
		codigos = null;
		return null;
	}
	
	/**
	 * M�todo chamado para redirecionar para a p�gina anterior da pagina��o, referente � listagem de publica��es.
	 */
	public String previous(){
		paginacao.previousPage(null);
		codigos = null;
		return null;
	}
	
	public List<Codigo> getCodigos() {
		return codigos;
	}
	
	public String getCarregarCodigos(){
		String meusCodigos = getParameter("meusCodigos");
		
		if (meusCodigos != null && meusCodigos.equals("true")){
			tipoTimeline = TipoTimeline.MEUS_CODIGOS_PUBLICADOS;
		}
		
		if (codigos == null || meusCodigos != null){
			CodigoDAO dao = (CodigoDAO) this.dao;
			
			try {
				String ordenar = opcao == OpcaoOrdenar.QUALQUER ? null :
									opcao == OpcaoOrdenar.MAIS_ANTIGAS ? "criadoEm ASC" : 
									opcao == OpcaoOrdenar.MAIS_RECENTES ? "criadoEm DESC" :
									opcao == OpcaoOrdenar.MAIS_CURTIDAS ? "qtdCurtidas DESC" :
									opcao == OpcaoOrdenar.MENOS_CURTIDAS ? "qtdCurtidas ASC" :
									opcao == OpcaoOrdenar.MENOS_CURTIDAS ? "qtdCurtidas ASC" : null;
				
				Boolean finalizados = (tipoTimeline == TipoTimeline.NORMAL || tipoTimeline == TipoTimeline.MEUS_CODIGOS_PUBLICADOS) ? true : 
										tipoTimeline == TipoTimeline.MEUS_RASCUNHOS ? false : null;
				
				Integer criadoPor = tipoTimeline == TipoTimeline.NORMAL ? null : 
										(tipoTimeline == TipoTimeline.MEUS_CODIGOS_PUBLICADOS 
											|| tipoTimeline == TipoTimeline.MEUS_RASCUNHOS) ? getUsuarioLogado().getId() : null;
				
				codigos = dao.findCodigoGeral(tituloBusca, ordenar, 
						finalizados, 
						criadoPor, 
						paginacao);
			} catch (Exception e) {
				codigos = new ArrayList<>();
				tratamentoErroPadrao(e);
			}
		}
		
		return null;
	}

	public void setCodigos(List<Codigo> codigos) {
		this.codigos = codigos;
	}

	public String getTituloBusca() {
		return tituloBusca;
	}

	public void setTituloBusca(String tituloBusca) {
		this.tituloBusca = tituloBusca;
	}

	public OpcaoOrdenar getOpcao() {
		return opcao;
	}

	public void setOpcao(OpcaoOrdenar opcao) {
		this.opcao = opcao;
	}
	
	public OpcaoOrdenar getOrdenarQualquer() {
		return OpcaoOrdenar.QUALQUER;
	}
	
	public OpcaoOrdenar getOrdenarMaisRecentes() {
		return OpcaoOrdenar.MAIS_RECENTES;
	}
	
	public OpcaoOrdenar getOrdenarMaisAntigas() {
		return OpcaoOrdenar.MAIS_ANTIGAS;
	}
	
	public OpcaoOrdenar getOrdenarMaisCurtidas() {
		return OpcaoOrdenar.MAIS_CURTIDAS;
	}
	
	public OpcaoOrdenar getOrdenarMenosCurtidas() {
		return OpcaoOrdenar.MENOS_CURTIDAS;
	}

	public PagingInformation getPaginacao() {
		return paginacao;
	}

	public void setPaginacao(PagingInformation paginacao) {
		this.paginacao = paginacao;
	}

	public TipoTimeline getTipoTimeline() {
		return tipoTimeline;
	}

	public void setTipoTimeline(TipoTimeline tipoTimeline) {
		this.tipoTimeline = tipoTimeline;
	}
	
	public TipoTimeline getTipoTimelineNormal() {
		return TipoTimeline.NORMAL;
	}
	
	public TipoTimeline getTipoTimelineCodigosPublicados() {
		return TipoTimeline.MEUS_CODIGOS_PUBLICADOS;
	}
	
	public TipoTimeline getTipoTimelineRascunhos() {
		return TipoTimeline.MEUS_RASCUNHOS;
	}
	
	public boolean isTimelineNormal(){
		return tipoTimeline == getTipoTimelineNormal();
	}
	
	public boolean isMeusCodigosPublicados(){
		return tipoTimeline == getTipoTimelineCodigosPublicados();
	}
	
	public boolean isMeusRascunhos(){
		return tipoTimeline == getTipoTimelineRascunhos();
	}
	
}
