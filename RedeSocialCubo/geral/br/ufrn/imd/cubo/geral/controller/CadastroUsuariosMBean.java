package br.ufrn.imd.cubo.geral.controller;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.Pessoa;
import br.ufrn.imd.cubo.arq.dominio.TipoUsuario;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.UsuarioDAO;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorCadastraUsuario;

/** 
 * MBean que controla opera��es relacionadas � cria��o/edi��o de usu�rios.<br/>
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@SessionScoped
public class CadastroUsuariosMBean extends AbstractControllerCadastro<Usuario> {

	private boolean cadastro;
	
	@PostConstruct
	private void init() {
		obj = new Usuario();
		obj.setTipoUsuario(TipoUsuario.COMUM);
		obj.setPessoa(new Pessoa());

		dao = new UsuarioDAO();
		
		cadastro = false;
	}
	
	/** Entra na tela de cadastro de usu�rios. */
	public String entrarCadastroUsuarios(){
		init();
		return Paginas.CADASTRAR_USUARIO;
	}
	
	/** Entra na tela de cadastro de usu�rios, j� com alguns dados preenchidos. */
	public String entrarCadastroUsuarios(Usuario usuario){
		init();
		obj = usuario;
		return Paginas.CADASTRAR_USUARIO;
	}
	
	/** Entra na tela de edi��o de perfil de usu�rios. */
	public String entrarEditarPerfil(){
		init();
		obj = dao.findByPrimaryKey(getUsuarioLogado().getId(), Usuario.class);
		return Paginas.ALTERAR_PERFIL;
	}
	
	@Override
	public String salvar() throws InstantiationException,
			IllegalAccessException {
		
		if (ValidatorUtil.isEmpty(obj.getPessoa().getDataNascimento())){
			addMsgError("Campo Data de Nascimento Obrigat�rio");
			return null;
		}
		
		if (obj.getPessoa().getDataNascimento().after(new Date())){
			addMsgError("A data de nascimento n�o pode ser posterior � atual.");
			return null;
		}
		
		IGenericDAO dao = new GenericDAOImpl();
		boolean erro = false;
		
		//Realizando valida��es
		
		cadastro = obj.getId() == 0 ? true : false;
		
		if (cadastro){	
			if (ValidatorUtil.isNotEmpty(dao.findByExactField("email", obj.getEmail(), Usuario.class))){
				addMsgError("J� existe um usu�rio com o login informado.");
				erro = true;
			}
			if (ValidatorUtil.isNotEmpty(dao.findByExactField("cpf", obj.getPessoa().getCpf(), Pessoa.class))){
				addMsgError("J� existe um usu�rio com o CPF informado.");
				erro = true;
			}
			if (!obj.getSenha().equals(obj.getNovaSenhaConfirmacao())){
				addMsgError("A senha informada n�o confere com sua confirma��o.");
				erro = true;
			}
		} else {
			if (ValidatorUtil.isNotEmpty(obj.getSenha()) && ValidatorUtil.isEmpty(obj.getNovaSenhaConfirmacao())
					|| (ValidatorUtil.isEmpty(obj.getSenha()) && ValidatorUtil.isNotEmpty(obj.getNovaSenhaConfirmacao()))){
				
				addMsgError("Informe a senha e sua confirma��o.");
				erro = true;
			
			} else if (ValidatorUtil.isNotEmpty(obj.getSenha()) && ValidatorUtil.isNotEmpty(obj.getNovaSenhaConfirmacao()) 
					&& !obj.getSenha().equals(obj.getNovaSenhaConfirmacao())){
				
				addMsgError("A senha informada n�o confere com sua confirma��o.");
				erro = true;
			}
		}
		
		if (!ValidatorUtil.validateEmail(obj.getEmail())){
			addMsgError("O email informado � inv�lido.");
			return null;
		}

		if (erro)
			return null;
		
		if (!cadastro && ValidatorUtil.isEmpty(obj.getSenha())){
			//Se for edi��o, s� deve modificar a senha caso o usu�rio tenha digitado alguma coisa
			//no campo de senha, ou seja, caso a senha esteja vazia, ela n�o deve ser modificada (deve
			//permanecer a mesma do banco).
			
			dao.detach(obj);
			Usuario usuarioBanco = dao.findByPrimaryKey(obj.getId(), Usuario.class);
			obj.setSenha(usuarioBanco.getSenha()); //A senha do banco j� est� criptografada 
			
		} else {
			//Nos demais casos (cadastro ou edi��o com mudan�a de senha), a senha n�o est� criptografada
			obj.setSenha(CriptografiaUtils.criptografarMD5(obj.getSenha()));
		}
		
		try {
			ProcessadorCadastraUsuario p = new ProcessadorCadastraUsuario();
			p.setObj(obj);
			p.execute();
			
			addMsgInfo("Opera��o realizada com sucesso!");
			
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
		if (getUsuarioLogado() != null){
			Usuario user = dao.findByPrimaryKey(getUsuarioLogado().getId(), Usuario.class);
			getCurrentSession().setAttribute("usuarioLogado", user);
		}
		
		return cadastro ? Paginas.LOGIN_PAGE : Paginas.PORTAL_INICIO;
	}
	
}
