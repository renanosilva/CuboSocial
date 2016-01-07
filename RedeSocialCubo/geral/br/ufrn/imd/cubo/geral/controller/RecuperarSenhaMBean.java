package br.ufrn.imd.cubo.geral.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.mail.MessagingException;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.helper.MailHelper;
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
	
	/** CPF informado pelo usuário. */
	private String cpf;
	
	@PostConstruct
	private void init() {
		cpf = null;
	}
	
	public String entrarRecuperarSenha(){
		init();
		return Paginas.RECUPERAR_LOGIN_SENHA;
	}
	
	public String recuperar(){
		IGenericDAO dao = new GenericDAOImpl();
		List<Usuario> usuarios = dao.findByExactField("pessoa.cpf", cpf, Usuario.class);
		
		if (ValidatorUtil.isEmpty(usuarios)){
			addMsgError("Não existe usuário com o CPF informado.");
			return null;
		}
		
		Usuario usuario = usuarios.get(0);
		
		ProcessadorRecuperacaoSenha p = new ProcessadorRecuperacaoSenha();
		p.setUsuario(usuario);
		
		try {
			usuario = (Usuario) p.execute();
			
			String msg = "Caro usuário,<br/> uma nova senha foi gerada para sua conta, devido a uma solicitação de recuperação de senha. Seguem seus novos dados:";
			msg += "<br/><br/>Nova senha: " + usuario.getSenhaReal();
			msg += "<br/><br/>É importante que você altere a senha novamente na primeira utilização do sistema.";
			msg += "<br/><br/>Cubo Social - IMD/UFRN<br/>";
			
			MailHelper.enviarEmail(usuario.getEmail(), "Recuperação de Senha", msg);
			addMsgInfo("Dentro de instantes, será enviada uma mensagem para o email cadastrado para este usuário. Verifique sua caixa de mensagens para recuperar sua senha.");
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		} catch (MessagingException e){
			addMsgError("Ocorreu um erro ao enviar o email. Por favor, tente novamente mais tarde.");
			e.printStackTrace();
			return null;
		}
			
		return Paginas.LOGIN_PAGE;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
}