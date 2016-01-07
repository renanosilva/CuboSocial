package br.ufrn.imd.cubo.geral.negocio;

import java.util.UUID;

import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorComando;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.UsuarioDAO;

/** 
 * Processador que gera uma nova senha aleat�ria para um usu�rio, como maneira
 * de recuperar sua senha.
 * @author Renan
 */
public class ProcessadorRecuperacaoSenha extends ProcessadorComando {
	
	/** 
	 * Usu�rio para o qual ser� gerada nova senha.
	 */
	private Usuario usuario;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		UsuarioDAO dao = new UsuarioDAO();
		usuario.setSenhaReal(gerarSenhaAleatoria());
		usuario.setSenha(CriptografiaUtils.criptografarMD5(usuario.getSenhaReal()));
		dao.updateField(Usuario.class, usuario.getId(), "senha", usuario.getSenha());
	}

	@Override
	protected void validar() throws NegocioException {
		if (ValidatorUtil.isEmpty(usuario)){
			throw new NegocioException("Usu�rio n�o informado.");
		}
	}
	
	private String gerarSenhaAleatoria(){
		 return UUID.randomUUID().toString().substring(0, 8);
	}

	@Override
	protected Object getResult() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
