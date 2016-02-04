package br.ufrn.imd.cubo.arq.negocio;

import javax.persistence.EntityManager;

import br.ufrn.imd.cubo.arq.dao.Database;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.HibernateUtils;

/**
 * Classe que representa um processador de dados. Deve ser estendido
 * por todos os outros processadores do sistema.
 * 
 * @author Renan
 *
 */
public abstract class ProcessadorComando {
	
	/** 
	 * Método principal que irá iniciar a execução dos processamentos 
	 * dos processadores filhos. É o método que deve ser chamado para 
	 * iniciar o processamento.
	 * 
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public final Object execute() throws ArqException, NegocioException {
		EntityManager em = null;
		
		try {
			em = Database.getInstance().getEntityManager();
			
			em.getTransaction().begin();
			
			validar();
			iniciarExecucao();
			
			em.getTransaction().commit();
			
			return getResult();
			
		} catch (NegocioException e){
			Database.getInstance().getEntityManager().getTransaction().rollback();
			throw new NegocioException(e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			
			em = Database.getInstance().getEntityManager();
			
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			
			if (HibernateUtils.isFKConstraintError(e)){
				throw new NegocioException("Esse registro não pode ser removido, pois está associado a outros registros da base de dados.");
			}
			
			throw new ArqException(e);
		} finally {
			//Limpando caches
//			if (em != null)
//				em.clear();
		}
	}
	
	/** Método que os processadores filhos devem implementar para realizar as operações necessárias. */
	protected abstract void iniciarExecucao() throws NegocioException, ArqException;
	
	/** Método que os processadores filhos devem implementar para validar seus dados. */
	protected abstract void validar() throws NegocioException;
	
	/** 
	 * Método que deve ser implementado pelos processadores filhos para retornar algum dado
	 * para quem chamou o processador. 
	 */
	protected abstract Object getResult();
	
}
