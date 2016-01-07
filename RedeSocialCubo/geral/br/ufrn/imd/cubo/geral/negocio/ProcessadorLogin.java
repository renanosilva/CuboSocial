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
 * Classe respons�vel por realizar processamentos relativos ao login no sistema.
 * @author Renan
 */
public class ProcessadorLogin extends ProcessadorComando {
	
	/** 
	 * Usu�rio com o login e senha com os quais est� se tentando fazer login. 
	 * Ao final do processamento, armazena o usu�rio logado. 
	 */
	private Usuario usuario;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		try {
			UsuarioDAO dao = new UsuarioDAO();
			usuario = dao.findUsuarioByLoginSenha(usuario.getEmail(), CriptografiaUtils.criptografarMD5(usuario.getSenha()));
			
			if (!ValidatorUtil.isEmpty(usuario)){
				if (!usuario.isAtivo()){
					throw new NegocioException("Este usu�rio foi desabilitado e n�o possui mais acesso ao sistema.");
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
			throw new NegocioException("Usu�rio/senha n�o informados.");
		}
		
		if (ValidatorUtil.isEmpty(usuario.getEmail())){
			throw new NegocioException("Usu�rio: campo obrigat�rio n�o informado.");
		}
		
		if (ValidatorUtil.isEmpty(usuario.getSenha())){
			throw new NegocioException("Senha: campo obrigat�rio n�o informado.");
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
