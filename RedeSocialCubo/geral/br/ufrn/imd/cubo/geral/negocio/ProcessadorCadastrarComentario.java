package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Comentario;

/** 
 * Classe responsável por cadastrar um comentário em uma publicação.
 * 
 * @author Renan
 */
public class ProcessadorCadastrarComentario extends ProcessadorCadastro {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Comentario obj = (Comentario) this.obj;
		Codigo codigo = obj.getPublicacao();
		CodigoDAO dao = null;
		
		try {
			dao = new CodigoDAO();
			
			super.iniciarExecucao();
			
			dao.flush();
			
			dao.updateTotalComentariosPublicacao(codigo.getId());
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
}
