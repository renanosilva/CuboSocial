package br.ufrn.imd.cubo.geral.controller;

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
 * MBean que controla operações relacionadas à criação/edição de usuários.<br/>
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
	
	/** Entra na tela de cadastro de usuários. */
	public String entrarCadastroUsuarios(){
		init();
		return Paginas.CADASTRAR_USUARIO;
	}
	
	/** Entra na tela de cadastro de usuários, já com alguns dados preenchidos. */
	public String entrarCadastroUsuarios(Usuario usuario){
		init();
		obj = usuario;
		return Paginas.CADASTRAR_USUARIO;
	}
	
	/** Entra na tela de edição de perfil de usuários. */
	public String entrarEditarPerfil(){
		init();
		obj = dao.findByPrimaryKey(getUsuarioLogado().getId(), Usuario.class);
		return Paginas.ALTERAR_PERFIL;
	}
	
	@Override
	public String salvar() throws InstantiationException,
			IllegalAccessException {
		
		IGenericDAO dao = new GenericDAOImpl();
		boolean erro = false;
		
		//Realizando validações
		
		cadastro = obj.getId() == 0 ? true : false;
		
		if (cadastro){	
			if (ValidatorUtil.isNotEmpty(dao.findByExactField("email", obj.getEmail(), Usuario.class))){
				addMsgError("Já existe um usuário com o email informado.");
				erro = true;
			}
			if (!obj.getSenha().equals(obj.getNovaSenhaConfirmacao())){
				addMsgError("A senha informada não confere com sua confirmação.");
				erro = true;
			}
		} else {
			if (ValidatorUtil.isNotEmpty(obj.getSenha()) && ValidatorUtil.isEmpty(obj.getNovaSenhaConfirmacao())
					|| (ValidatorUtil.isEmpty(obj.getSenha()) && ValidatorUtil.isNotEmpty(obj.getNovaSenhaConfirmacao()))){
				
				addMsgError("Informe a senha e sua confirmação.");
				erro = true;
			
			} else if (ValidatorUtil.isNotEmpty(obj.getSenha()) && ValidatorUtil.isNotEmpty(obj.getNovaSenhaConfirmacao()) 
					&& !obj.getSenha().equals(obj.getNovaSenhaConfirmacao())){
				
				addMsgError("A senha informada não confere com sua confirmação.");
				erro = true;
			}
		}
		
		if (!ValidatorUtil.validateEmail(obj.getEmail())){
			addMsgError("O email informado é inválido.");
			erro = true;
		}
		
		String nomeCompleto = obj.getPessoa().getNomeSobrenome().trim();
		
		if (!nomeCompleto.contains(" ")){
			addMsgError("Informe seu sobrenome.");
			erro = true;
		}
		
		if (erro)
			return null;
		
		int posEspaco = nomeCompleto.indexOf(" ");
		obj.getPessoa().setNome(nomeCompleto.substring(0, posEspaco));
		obj.getPessoa().setSobrenome(nomeCompleto.substring(posEspaco + 1));
		
		if (!cadastro && ValidatorUtil.isEmpty(obj.getSenha())){
			//Se for edição, só deve modificar a senha caso o usuário tenha digitado alguma coisa
			//no campo de senha, ou seja, caso a senha esteja vazia, ela não deve ser modificada (deve
			//permanecer a mesma do banco).
			
			dao.detach(obj);
			Usuario usuarioBanco = dao.findByPrimaryKey(obj.getId(), Usuario.class);
			obj.setSenha(usuarioBanco.getSenha()); //A senha do banco já está criptografada 
			
		} else {
			//Nos demais casos (cadastro ou edição com mudança de senha), a senha não está criptografada
			obj.setSenha(CriptografiaUtils.criptografarMD5(obj.getSenha()));
		}
		
		try {
			ProcessadorCadastraUsuario p = new ProcessadorCadastraUsuario();
			p.setObj(obj);
			p.execute();
			
			addMsgInfo((cadastro ? "Cadastro realizado" : "Alteração realizada") + " com sucesso!");
			
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
