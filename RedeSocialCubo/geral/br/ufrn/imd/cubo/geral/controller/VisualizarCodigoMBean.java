package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Comentario;
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
	
	/** Comentário que está sendo inserido no momento. */
	private Comentario comentario;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new CodigoDAO();
		comentario = new Comentario();
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
	
	public String getCarregarCodigo(){
		if (ValidatorUtil.isEmpty(obj)){
			int idCodigo = getParameterInt("idCodigo", -1);
			
			if (idCodigo != -1)
				recarregarPublicacaoCodigo(idCodigo);
		}
	
		return null;
	}
	
	private void recarregarPublicacaoCodigo(int idCodigo){
		obj = dao.findByPrimaryKey(idCodigo, Codigo.class);
		obj.setComentarios(dao.findByExactField("publicacao.id", obj.getId(), "criadoEm ASC", Comentario.class));
	}

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}
	
}
