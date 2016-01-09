package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.AvaliacaoCodigo;
import br.ufrn.imd.cubo.geral.dominio.Codigo;

/** 
 * Classe responsável por avaliar uma publicação.
 * @author Renan
 */
public class ProcessadorAvaliarPublicacao extends ProcessadorCadastro {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Codigo obj = (Codigo) this.obj;
		CodigoDAO dao = null;
		
		try {
			dao = new CodigoDAO();
			
			//Verificando se o usuário já avaliou a publicação em questão
			AvaliacaoCodigo avaliacaoBanco = dao.findByExactFields( 
					new String[]{"criadoPor.id", "publicacao.id"}, 
					new Object[]{UsuarioUtil.getUsuarioLogado().getId(), obj.getId()},
					true,
					AvaliacaoCodigo.class);
			
			if (ValidatorUtil.isEmpty(avaliacaoBanco)){
				//O usuário não avaliou a publicação ainda. Deve ser criado um novo registro.
				AvaliacaoCodigo avaliacao = new AvaliacaoCodigo();
				avaliacao.setPublicacao(obj);
				dao.create(avaliacao);
			} else {
				//O usuário já avaliou a publicação. A ação é para atualizar a avaliação.
				
				Float nota = obj.getNotaUsuarioLogado() > 0 ? obj.getNotaUsuarioLogado() : null; 
				
				if (nota == null){
					//O usuário removeu a avaliação. Deve-se remover o registro do banco.
					dao.delete(avaliacaoBanco);
				} else {
					avaliacaoBanco.setNota(nota);
					dao.update(avaliacaoBanco);
				}
			}
			
			dao.flush();
			
			//Atualizando a nota média da publicação...
			dao.updateNotaMediaCodigo(obj.getId());
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
}
