package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.DAOException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorComando;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.UsuarioDAO;

/** 
 * Classe responsável por realizar processamentos relativos ao login no sistema.
 * @author Renan
 */
public class ProcessadorLogin extends ProcessadorComando {
	
	/** 
	 * Usuário com o login e senha com os quais está se tentando fazer login. 
	 * Ao final do processamento, armazena o usuário logado. 
	 */
	private Usuario usuario;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		try {
			UsuarioDAO dao = new UsuarioDAO();
			usuario = dao.findUsuarioByLoginSenha(usuario.getEmail(), CriptografiaUtils.criptografarMD5(usuario.getSenha()));
			
			if (!ValidatorUtil.isEmpty(usuario)){
				if (!usuario.isAtivo()){
					throw new NegocioException("Este usuário foi desabilitado e não possui mais acesso ao sistema.");
				}
				
				dao.updateUltimoAcesso(usuario.getId());
			}
		} catch (DAOException e) {
			throw new ArqException(e);
		}
	}

	@Override
	protected void validar() throws NegocioException {
		if (usuario == null || (ValidatorUtil.isEmpty(usuario.getEmail()) && 
				ValidatorUtil.isEmpty(usuario.getSenha()))){
			throw new NegocioException("Usuário/senha não informados.");
		}
		
		if (ValidatorUtil.isEmpty(usuario.getEmail())){
			throw new NegocioException("Usuário: campo obrigatório não informado.");
		}
		
		if (ValidatorUtil.isEmpty(usuario.getSenha())){
			throw new NegocioException("Senha: campo obrigatório não informado.");
		}
	}

	@Override
	protected Object getResult() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
