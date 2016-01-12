package br.ufrn.imd.cubo.geral.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorRecuperacaoSenha;

/**
 * MBean utilizado para recuperação de logins e senhas.
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class RecuperarSenhaMBean extends AbstractController {
	
	/** Email informado pelo usuário. */
	private String email;
	
	@PostConstruct
	private void init() {
		email = null;
	}
	
	public String entrarRecuperarSenha(){
		init();
		return Paginas.RECUPERAR_LOGIN_SENHA;
	}
	
	public String recuperar(){
		IGenericDAO dao = new GenericDAOImpl();
		List<Usuario> usuarios = dao.findByExactField("email", email, Usuario.class);
		
		if (ValidatorUtil.isEmpty(usuarios)){
			addMsgError("Não existe usuário com o email informado.");
			return null;
		}
		
		Usuario usuario = usuarios.get(0);
		
		ProcessadorRecuperacaoSenha p = new ProcessadorRecuperacaoSenha();
		p.setUsuario(usuario);
		
		try {
			usuario = (Usuario) p.execute();
			
			addMsgInfo("Dentro de instantes, será enviada uma mensagem para o email informado. Verifique sua caixa de mensagens para recuperar sua senha.");
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		} 
			
		return Paginas.LOGIN_PAGE;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}