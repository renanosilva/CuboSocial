package br.ufrn.imd.cubo.geral.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RateEvent;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dao.PagingInformation;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.CuboSocialUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.AvaliacaoCodigo;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorAvaliarPublicacao;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCurtirPublicacao;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorExcluiCodigo;

/** 
 * MBean que controla opera��es relacionadas � timeline
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
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
	 * MEUS_CODIGOS: exibe apenas os c�digos do usu�rio logado.<br/>
	 * */
	private enum TipoTimeline {
		NORMAL, MEUS_CODIGOS, DEMO;
	}
	
	/** 
	 * Tipos existentes de c�digo.<br/>
	 * MEUS_CODIGOS_PUBLICADOS: exibe apenas os c�digos do usu�rio logado que est�o publicados.<br/>
	 * MEUS_RASCUNHOS: exibe apenas os c�digos do usu�rio logado que n�o foram publicados ainda.
	 * */
	private enum TipoCodigo {
		MEUS_CODIGOS_PUBLICADOS, MEUS_RASCUNHOS;
	}
	
	/** Op��o selecionada pelo usu�rio */
	private OpcaoOrdenar opcao;
	
	/** Armazena o tipo de timeline selecionado no momento. */
	private TipoTimeline tipoTimeline;
	
	/** Armazena o tipo de timeline selecionada na busca do usu�rio. */
	private TipoCodigo tipoCodigoBusca;
	
	/** Armazena o ID do cubo pelo qual o usu�rio deseja filtrar a busca. */
	private Integer idTipoCubo;
	
	/** Armazena as op��es de pagina��o de consulta a c�digos. */
	private PagingInformation paginacao;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new CodigoDAO();
		codigos = null;
		paginacao = new PagingInformation(0, QTD_CODIGOS);
		
		tipoTimeline = TipoTimeline.NORMAL;
		tipoCodigoBusca = null;
		tituloBusca = null;
		opcao = null;
		idTipoCubo = null;
	}
	
	/** Entra na tela da timeline. */
	public String inicio(){
		init();
		
		String meusCodigos = getParameter("meusCodigos");
		
		if (meusCodigos != null && meusCodigos.equals("true")){
			tipoTimeline = TipoTimeline.MEUS_CODIGOS;
		}
		
		return Paginas.PORTAL_INICIO;
	}
	
	/** Entra na tela de demonstra��o. */
	public String entrarDemo(){
		init();
		tipoTimeline = TipoTimeline.DEMO;
		return Paginas.PORTAL_INICIO;
	}
	
	/** Volta � timeline, sem reset�-la. */
	public String voltarTimeline(){
		return Paginas.PORTAL_INICIO;
	}
	
	public String buscar(){
		//Setando c�digos para null para poder buscar novamente, via getCodigos()
		codigos = null;
		paginacao = new PagingInformation(0, QTD_CODIGOS);
		
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
			
			//publicacao = dao.findByPrimaryKey(publicacao.getId(), Codigo.class);
			dao.refresh(publicacao);
			int pos = codigos.indexOf(publicacao);
			codigos.set(pos, publicacao);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Atribuir uma nota a uma publica��o. */
	public void avaliarPublicacao(RateEvent rateEvent) {
		avaliarPublicacao();
	}
	
	/** Cancela (remove) uma avalia��o de c�digo. */
	public void cancelarAvaliacaoPublicacao() {
		avaliarPublicacao();
    }
	
	/** Opera��es necess�rias para avaliar uma publica��o de c�digo. */
	private void avaliarPublicacao(){
		IGenericDAO dao = null;
		
		try {
			dao = new GenericDAOImpl();
			
			int idPublicacao = getParameterInt("idPublicacao");
			
			Codigo c = new Codigo();
			c.setId(idPublicacao);
			
			Codigo codigoLista = codigos.get(codigos.indexOf(c));
			
			Codigo publicacaoBanco = dao.findByPrimaryKey(idPublicacao, Codigo.class);
			publicacaoBanco.setNotaUsuarioLogado(codigoLista.getNotaUsuarioLogado());

			ProcessadorAvaliarPublicacao p = new ProcessadorAvaliarPublicacao();
			p.setObj(publicacaoBanco);
			p.execute();
			
			//Atualizando registro
			
			publicacaoBanco = dao.findByPrimaryKey(publicacaoBanco.getId(), Codigo.class);
			
			int pos = codigos.indexOf(publicacaoBanco);
			codigos.set(pos, publicacaoBanco);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Exclui uma publica��o de c�digo. */
	public String excluirPublicacao() {
		int idPublicacao = getParameterInt("idPublicacao", -1);
		
		if (idPublicacao != -1){
			try {
				boolean isUltimaPagina = (paginacao.getPaginaAtual() + 1) == paginacao.getTotalPaginas();
				boolean reduzirPagina = isUltimaPagina && codigos.size() == 1;
				
				Codigo c = dao.findByPrimaryKey(idPublicacao, Codigo.class);
				
				ProcessadorExcluiCodigo p = new ProcessadorExcluiCodigo();
				p.setObj(c);
				p.execute();
				
				//For�ando recarregamento dos c�digos
				codigos = null;
				
				if (reduzirPagina){
					paginacao.previousPage(null);
				}
				
				getCarregarCodigos();
				
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			} catch (NegocioException e) {
				tratamentoErroPadrao(e);
			}
		}
		
		return null;
		
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
		
		if (codigos == null || meusCodigos != null){
			CodigoDAO dao = (CodigoDAO) this.dao;
			
			try {
				String ordenar = opcao == OpcaoOrdenar.QUALQUER ? null :
									opcao == OpcaoOrdenar.MAIS_ANTIGAS ? "criadoEm ASC" : 
									opcao == OpcaoOrdenar.MAIS_RECENTES ? "criadoEm DESC" :
									opcao == OpcaoOrdenar.MAIS_CURTIDAS ? "qtdCurtidas DESC" :
									opcao == OpcaoOrdenar.MENOS_CURTIDAS ? "qtdCurtidas ASC" :
									opcao == OpcaoOrdenar.MAIS_COMENTADAS ? "qtdComentarios DESC" :
									opcao == OpcaoOrdenar.MENOS_COMENTADAS ? "qtdComentarios ASC" :
									opcao == OpcaoOrdenar.MELHORES_AVALIADAS ? "nota DESC" :
									opcao == OpcaoOrdenar.PIORES_AVALIADAS ? "nota ASC" : null;
				
				Boolean finalizados = null;
				Integer criadoPor = null;
				
				if (tipoTimeline != TipoTimeline.DEMO){
					if (tipoTimeline == TipoTimeline.NORMAL || 
							(tipoTimeline == TipoTimeline.MEUS_CODIGOS && tipoCodigoBusca == TipoCodigo.MEUS_CODIGOS_PUBLICADOS)){
						finalizados = true;
					} else if (tipoCodigoBusca == TipoCodigo.MEUS_RASCUNHOS){
						finalizados = false;
					}
					
					criadoPor = tipoTimeline == TipoTimeline.NORMAL ? null : 
						tipoTimeline == TipoTimeline.MEUS_CODIGOS ? getUsuarioLogado().getId() : null;
				}
				
				codigos = dao.findCodigoGeral(tituloBusca, ordenar, 
						finalizados, 
						criadoPor,
						idTipoCubo,
						isDemo() ? true : null,
						paginacao);
				
				//Buscando quais dos c�digos encontrados o usu�rio avaliou
				
				if (ValidatorUtil.isNotEmpty(codigos)){
					int[] idsCodigos = CuboSocialUtils.persistDbToArray(codigos);
					
					Map<Codigo, AvaliacaoCodigo> map = dao.findAvaliacoesCodigosByIdsCodigos(idsCodigos, getUsuarioLogado().getId());
					
					if (map != null){
						for (Codigo c : map.keySet()){
							AvaliacaoCodigo ava = map.get(c);
							
							//Setando a nota que o usu�rio logado atribuiu � publica��o em quest�o
							codigos.get(codigos.indexOf(c)).setNotaUsuarioLogado(ava.getNota());;
						}
					}
				}
				
				
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
	
	public OpcaoOrdenar getOrdenarMaisComentadas() {
		return OpcaoOrdenar.MAIS_COMENTADAS;
	}
	
	public OpcaoOrdenar getOrdenarMenosComentadas() {
		return OpcaoOrdenar.MENOS_COMENTADAS;
	}
	
	public OpcaoOrdenar getOrdenarMelhoresAvaliacoes() {
		return OpcaoOrdenar.MELHORES_AVALIADAS;
	}
	
	public OpcaoOrdenar getOrdenarPioresAvaliacoes() {
		return OpcaoOrdenar.PIORES_AVALIADAS;
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
	
	public TipoTimeline getTipoTimelineMeusCodigos() {
		return TipoTimeline.MEUS_CODIGOS;
	}
	
	public TipoTimeline getTipoTimelineDemo() {
		return TipoTimeline.DEMO;
	}
	
	public boolean isTimelineNormal(){
		return tipoTimeline == getTipoTimelineNormal();
	}
	
	public boolean isMeusCodigos(){
		return tipoTimeline == getTipoTimelineMeusCodigos();
	}
	
	public boolean isDemo(){
		return tipoTimeline == getTipoTimelineDemo();
	}

	public Integer getIdTipoCubo() {
		return idTipoCubo;
	}

	public void setIdTipoCubo(Integer idTipoCubo) {
		this.idTipoCubo = idTipoCubo;
	}

	public TipoCodigo getTipoCodigoBusca() {
		return tipoCodigoBusca;
	}

	public void setTipoCodigoBusca(TipoCodigo tipoCodigoBusca) {
		this.tipoCodigoBusca = tipoCodigoBusca;
	}
	
	public TipoCodigo getTipoCodigosPublicados() {
		return TipoCodigo.MEUS_CODIGOS_PUBLICADOS;
	}
	
	public TipoCodigo getTipoCodigosRascunhos() {
		return TipoCodigo.MEUS_RASCUNHOS;
	}
	
}
