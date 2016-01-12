package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.event.RateEvent;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.AvaliacaoCodigo;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Comentario;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorAvaliarPublicacao;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCadastrarComentario;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCurtirComentario;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCurtirPublicacao;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorExcluiComentario;

/** 
 * MBean que permite a visualização de detalhes de uma publicação de código.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class VisualizarCodigoMBean extends AbstractControllerCadastro<Codigo> {
	
	/** 
	 * Utilizado para salvar os dados da timeline, enquanto o usário estiver na visualização
	 * de detalhes de código. Necessário para o caso de o usuário querer voltar à timeline.  
	 * */
	private TimelineMBean timelineMBean;
	
	/** Comentário que está sendo inserido no momento. */
	private Comentario comentario;
	
	/** Indica se, ao carregar a página, o scroll deve ser rolado até os comentários. */
	private boolean irParaComentarios;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new CodigoDAO();
		comentario = new Comentario();
		irParaComentarios = false;
	}
	
	/** Entra na tela de visualização. */
	public String entrar(){
		return Paginas.VISUALIZAR_CODIGO;
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
			
			obj = dao.findByPrimaryKey(publicacao.getId(), Codigo.class);
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
			
			Codigo publicacaoBanco = dao.findByPrimaryKey(obj.getId(), Codigo.class);
			publicacaoBanco.setNotaUsuarioLogado(obj.getNotaUsuarioLogado());

			ProcessadorAvaliarPublicacao p = new ProcessadorAvaliarPublicacao();
			p.setObj(publicacaoBanco);
			p.execute();
			
			//Atualizando registro
			
			obj = dao.findByPrimaryKey(publicacaoBanco.getId(), Codigo.class);
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Curtir um comentário. */
	public void curtirComentario(ActionEvent evt) {
		IGenericDAO dao = null;
		
		try {
			dao = new GenericDAOImpl();
			
			Comentario comentario = dao.findByPrimaryKey(getParameterInt("idComentario"), Comentario.class);

			ProcessadorCurtirComentario p = new ProcessadorCurtirComentario();
			p.setObj(comentario);
			p.execute();
			
			//Atualizando registro
			
			comentario = dao.findByPrimaryKey(comentario.getId(), Comentario.class);
			obj.getComentarios().set(obj.getComentarios().indexOf(comentario), comentario);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Publicar um comentário. */
	public void publicarComentario(ActionEvent evt) {
		try {
			boolean cadastro = comentario.getId() == 0;
			
			if (cadastro){
				comentario.setPublicacao(obj);
			}
			
			ProcessadorCadastrarComentario p = new ProcessadorCadastrarComentario();
			p.setObj(comentario);
			p.execute();
			
			comentario = new Comentario();
			
			recarregarPublicacaoCodigo(obj.getId());
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
		}
	}
	
	/** Inicia a edição de um comentário. */
	public void editarComentario(ActionEvent evt) {
		int idComentario = getParameterInt("idComentario", -1);
		
		if (idComentario != -1){
			comentario = dao.findByPrimaryKey(idComentario, Comentario.class);
		}
		
	}
	
	/** Excluir um comentário. */
	public void excluirComentario(ActionEvent evt) {
		int idComentario = getParameterInt("idComentario", -1);
		
		if (idComentario != -1){
			try {
				Comentario c = dao.findByPrimaryKey(idComentario, Comentario.class);
				
				ProcessadorExcluiComentario p = new ProcessadorExcluiComentario();
				p.setObj(c);
				p.execute();
				
				recarregarPublicacaoCodigo(obj.getId());
				
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			} catch (NegocioException e) {
				tratamentoErroPadrao(e);
			}
		}
	}
	
	public String getCarregarDadosPagina(){
		if (ValidatorUtil.isEmpty(obj)){
			int idCodigo = getParameterInt("idCodigo", -1);
			
			if (idCodigo != -1)
				recarregarPublicacaoCodigo(idCodigo);
		}
		
		String irParaComentarios = getParameter("irParaComentarios");
		
		if (irParaComentarios != null && irParaComentarios.equals("true"))
			this.irParaComentarios = true;
		
		return null;
	}
	
	private void recarregarPublicacaoCodigo(int idCodigo){
		obj = dao.findByPrimaryKey(idCodigo, Codigo.class);
		obj.setComentarios(dao.findByExactField("publicacao.id", obj.getId(), "criadoEm ASC", Comentario.class));
		
		//Carregando avaliação do usuário logado para o código em questão
		AvaliacaoCodigo ava = dao.findByExactFields(new String[]{"criadoPor.id", "publicacao.id"}, 
								new Object[]{getUsuarioLogado().getId(), obj.getId()}, 
								true, 
								AvaliacaoCodigo.class);
		
		if (ValidatorUtil.isNotEmpty(ava)){
			obj.setNotaUsuarioLogado(ava.getNota());
		}
	}

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}

	public boolean isIrParaComentarios() {
		return irParaComentarios;
	}

	public void setIrParaComentarios(boolean irParaComentarios) {
		this.irParaComentarios = irParaComentarios;
	}

	public TimelineMBean getTimelineMBean() {
		return timelineMBean;
	}

	public void setTimelineMBean(TimelineMBean timelineMBean) {
		this.timelineMBean = timelineMBean;
	}
	
}
