package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Comentario;
import br.ufrn.imd.cubo.geral.dominio.Curtida;

/** 
 * Classe respons�vel por curtir um coment�rio de publica��o.
 * 
 * @author Renan
 */
public class ProcessadorCurtirComentario extends ProcessadorCadastro {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Comentario obj = (Comentario) this.obj;
		CodigoDAO dao = null;
		
		try {
			dao = new CodigoDAO();
			
			//Verificando se o usu�rio j� curtiu o coment�rio em quest�o
			Curtida curtidaBanco = dao.findByExactFields( 
					new String[]{"criadoPor.id", "comentario.id"}, 
					new Object[]{UsuarioUtil.getUsuarioLogado().getId(), obj.getId()},
					true,
					Curtida.class);
			
			if (ValidatorUtil.isEmpty(curtidaBanco)){
				//O usu�rio n�o curtiu o coment�rio ainda. A a��o � para curtir
				Curtida curtida = new Curtida();
				curtida.setComentario(obj);
				dao.create(curtida);
			} else {
				//O usu�rio j� curtiu o coment�rio. A a��o � para desfazer a curtida
				dao.delete(curtidaBanco);
			}
			
			dao.flush();
			
			//Atualizando total de curtidas do coment�rio...
			//� interessante atualizar o total de curtidas atrav�s de uma consulta, pois caso haja algum
			//erro na contagem, ele � corrigido nesse momento.
			dao.updateTotalCurtidasComentario(obj.getId());
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
}
