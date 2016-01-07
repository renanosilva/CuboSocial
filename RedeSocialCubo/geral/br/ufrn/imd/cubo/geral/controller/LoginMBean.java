package br.ufrn.imd.cubo.geral.controller;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractController;
import br.ufrn.imd.cubo.arq.dominio.Pessoa;
import br.ufrn.imd.cubo.arq.dominio.TipoUsuario;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorLogin;

/**
 * MBean que controla o login no sistema. 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class LoginMBean extends AbstractController {
	
	/** Armazena os dados informados na tela de login. */
	private Usuario usuario;
	
	/** Armazena os dados iniciais de cadastro do usuário. */
	private Usuario usuarioCadastro;
	
	@PostConstruct
	private void init(){
		usuario = new Usuario();
		usuarioCadastro = new Usuario();
		usuarioCadastro.setPessoa(new Pessoa());
		usuarioCadastro.setTipoUsuario(TipoUsuario.COMUM);
	}
	
	/** Autentica o usuário e faz login no sistema. */
	public String autenticar(){
		ProcessadorLogin p = new ProcessadorLogin();
		p.setUsuario(usuario);
		
		try {
			usuario = (Usuario) p.execute();
			
			if (ValidatorUtil.isEmpty(usuario)){
				init();
				
				addMsgError("Usuário/Senha incorretos.");
				return null;
			}
			
			getCurrentSession().setAttribute("usuarioLogado", usuario);
			return Paginas.PORTAL_INICIO;
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		}
	}
	
	/** Autentica o usuário e faz login no sistema. */
	public String logoff(){
		getCurrentSession().invalidate();
		return Paginas.LOGIN_PAGE;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuarioCadastro() {
		return usuarioCadastro;
	}

	public void setUsuarioCadastro(Usuario usuarioCadastro) {
		this.usuarioCadastro = usuarioCadastro;
	}

}
