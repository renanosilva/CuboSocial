package br.ufrn.imd.cubo.geral.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
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
 * MBean que controla operações relacionadas à timeline
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class TimelineMBean extends AbstractControllerCadastro<Codigo> {

	/** Quantidade de códigos a serem exibidos por página. */
	private static final int QTD_CODIGOS = 10;
	
	/** Códigos que serão exibidos na timeline. */
	private List<Codigo> codigos;
	
	/** Título pelo qual o usuário deseja filtrar as postagens. */
	private String tituloBusca;
	
	/** Tipos de opções existentes de ordenação de publicações na timeline. */
	private enum OpcaoOrdenar {
		QUALQUER, MAIS_RECENTES, MAIS_ANTIGAS, MAIS_CURTIDAS, MENOS_CURTIDAS,
		MAIS_COMENTADAS, MENOS_COMENTADAS, MELHORES_AVALIADAS, PIORES_AVALIADAS;
	}
	
	/** 
	 * Tipos existentes de timeline.<br/>
	 * NORMAL: exibe todas as publicações existentes, de todos os usuários.<br/>
	 * MEUS_CODIGOS_PUBLICADOS: exibe apenas os códigos do usuário logado que estão publicados.<br/>
	 * MEUS_RASCUNHOS: exibe apenas os códigos do usuário logado que não foram publicados ainda.
	 * */
	private enum TipoTimeline {
		NORMAL, MEUS_CODIGOS_PUBLICADOS, MEUS_RASCUNHOS;
	}
	
	/** Opção selecionada pelo usuário */
	private OpcaoOrdenar opcao;
	
	/** Armazena o tipo de timeline selecionado no momento. */
	private TipoTimeline tipoTimeline;
	
	/** Armazena as opções de paginação de consulta a códigos. */
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
		//Setando códigos para null para poder buscar novamente, via getCodigos()
		codigos = null;
		
		return null;
	}
	
	/** Curtir uma publicação. */
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
	
	/** Atribuir uma nota a uma publicação. */
	public void avaliarPublicacao(RateEvent rateEvent) {
		avaliarPublicacao();
	}
	
	/** Cancela (remove) uma avaliação de código. */
	public void cancelarAvaliacaoPublicacao() {
		avaliarPublicacao();
    }
	
	/** Operações necessárias para avaliar uma publicação de código. */
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
	
	/** Exclui uma publicação de código. */
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
				
				//Forçando recarregamento dos códigos
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
	 * Mude a página atual da paginação de acordo com o parâmetro informado, referente à listagem de publicações.
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
	 * Método chamado para redirecionar para a próxima página da paginação, referente à listagem de publicações.
	 */
	public String next(){
		paginacao.nextPage(null);
		codigos = null;
		return null;
	}
	
	/**
	 * Método chamado para redirecionar para a página anterior da paginação, referente à listagem de publicações.
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
									opcao == OpcaoOrdenar.MAIS_COMENTADAS ? "qtdComentarios DESC" :
									opcao == OpcaoOrdenar.MENOS_COMENTADAS ? "qtdComentarios ASC" :
									opcao == OpcaoOrdenar.MELHORES_AVALIADAS ? "nota DESC" :
									opcao == OpcaoOrdenar.PIORES_AVALIADAS ? "nota ASC" : null;
				
				Boolean finalizados = (tipoTimeline == TipoTimeline.NORMAL || tipoTimeline == TipoTimeline.MEUS_CODIGOS_PUBLICADOS) ? true : 
										tipoTimeline == TipoTimeline.MEUS_RASCUNHOS ? false : null;
				
				Integer criadoPor = tipoTimeline == TipoTimeline.NORMAL ? null : 
										(tipoTimeline == TipoTimeline.MEUS_CODIGOS_PUBLICADOS 
											|| tipoTimeline == TipoTimeline.MEUS_RASCUNHOS) ? getUsuarioLogado().getId() : null;
				
				codigos = dao.findCodigoGeral(tituloBusca, ordenar, 
						finalizados, 
						criadoPor, 
						paginacao);
				
				//Buscando quais dos códigos encontrados o usuário avaliou
				
				if (ValidatorUtil.isNotEmpty(codigos)){
					int[] idsCodigos = CuboSocialUtils.persistDbToArray(codigos);
					
					Map<Integer, AvaliacaoCodigo> map = dao.findAvaliacoesCodigosByIdsCodigos(idsCodigos, getUsuarioLogado().getId());
					
					if (map != null){
						for (Integer id : map.keySet()){
							Codigo c = new Codigo();
							c.setId(id);
							
							AvaliacaoCodigo ava = map.get(id);
							
							//Setando a nota que o usuário logado atribuiu à publicação em questão
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
