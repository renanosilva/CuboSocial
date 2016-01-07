package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Curtida;

/** 
 * Classe responsável por curtir uma publicação.
 * @author Renan
 */
public class ProcessadorCurtirPublicacao extends ProcessadorCadastro {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Codigo obj = (Codigo) this.obj;
		CodigoDAO dao = null;
		
		try {
			dao = new CodigoDAO();
			
			//Verificando se o usuário já curtiu a publicação em questão
			Curtida curtidaBanco = dao.findByExactFields( 
					new String[]{"criadoPor.id", "publicacao.id"}, 
					new Object[]{UsuarioUtil.getUsuarioLogado().getId(), obj.getId()},
					true,
					Curtida.class);
			
			if (ValidatorUtil.isEmpty(curtidaBanco)){
				//O usuário não curtiu a publicação ainda. A ação é para curtir
				Curtida curtida = new Curtida();
				curtida.setPublicacao(obj);
				dao.create(curtida);
			} else {
				//O usuário já curtiu a publicação. A ação é para desfazer a curtida
				dao.delete(curtidaBanco);
			}
			
			dao.flush();
			
			//Atualizando total de curtidas da publicação...
			//É interessante atualizar o total de curtidas através de uma consulta, pois caso haja algum
			//erro na contagem, ele é corrigido nesse momento.
			dao.updateTotalCurtidasCodigo(obj.getId());
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
}
