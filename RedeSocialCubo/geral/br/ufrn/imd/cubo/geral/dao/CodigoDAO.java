package br.ufrn.imd.cubo.geral.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.PagingInformation;
import br.ufrn.imd.cubo.arq.exception.DAOException;
import br.ufrn.imd.cubo.arq.util.HibernateUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dominio.AvaliacaoCodigo;
import br.ufrn.imd.cubo.geral.dominio.Codigo;

/**
 * DAO com métodos relativos à entidade {@link Codigo}.
 * @author Renan
 */
public class CodigoDAO extends GenericDAOImpl {

	/**
	 * Faz update da quantidade de curtidas de uma publicação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public void updateTotalCurtidasCodigo(int idPublicacao) throws DAOException {
		
		String sql = "UPDATE geral.codigo " +
					 "SET qtd_curtidas = " +
							" (SELECT count(*) FROM geral.curtida cc " +
							" WHERE cc.id_publicacao = " + idPublicacao + ") " +
					 "WHERE id_codigo = " + idPublicacao;
		
		update(sql);
	}
	
	/**
	 * Faz update da nota média de uma publicação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public void updateNotaMediaCodigo(int idPublicacao) throws DAOException {
		
		String sql = "UPDATE geral.codigo " +
					 "SET nota = " +
							" (SELECT COALESCE(avg(nota),0.00) FROM geral.avaliacao_codigo ac " +
							" WHERE ac.id_publicacao = " + idPublicacao + ") " +
					 "WHERE id_codigo = " + idPublicacao;
		
		update(sql);
	}
	
	/**
	 * Faz update da quantidade de curtidas de um comentário.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public void updateTotalCurtidasComentario(int idComentario) throws DAOException {
		
		String sql = "UPDATE geral.comentario " +
					 "SET qtd_curtidas = " +
							" (SELECT count(*) FROM geral.curtida c " +
							" WHERE c.id_comentario = " + idComentario + ") " +
					 "WHERE id_comentario = " + idComentario;
		
		update(sql);
	}
	
	/**
	 * Faz update da quantidade de comentários de uma publicação de código.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public void updateTotalComentariosPublicacao(int idPublicacao) throws DAOException {
		
		String sql = "UPDATE geral.codigo " +
					 "SET qtd_comentarios = " +
							" (SELECT count(*) FROM geral.comentario c " +
							" WHERE c.id_publicacao = " + idPublicacao + ") " +
					 "WHERE id_codigo = " + idPublicacao;
		
		update(sql);
	}
	
	/** Busca geral de publicações de código. */
	public List<Codigo> findCodigoGeral(String titulo, String orderBy, Boolean finalizado, 
			Integer idCriadoPor, Integer idTipoCubo, PagingInformation paginacao) throws Exception{
		EntityManager em = getEm();
		
		String hql = " FROM Codigo c WHERE 1=1 ";
		
		if (ValidatorUtil.isNotEmpty(titulo)){
			hql += " AND upper(c.titulo) like :titulo ";
		}
		if (finalizado != null){
			hql += " AND c.finalizado = :finalizado ";
		}
		if (ValidatorUtil.isNotEmpty(idCriadoPor)){
			hql += " AND c.criadoPor.id = :idCriadoPor ";
		}
		if (ValidatorUtil.isNotEmpty(idTipoCubo)){
			hql += " AND c.cubo.id = :idTipoCubo ";
		}
		if (ValidatorUtil.isNotEmpty(orderBy)){
			hql += " order by " + orderBy;
		}
		
		Query q = em.createQuery(hql);
		
		if (ValidatorUtil.isNotEmpty(titulo)){
			q.setParameter("titulo", "%" + titulo.toUpperCase() + "%");
		}
		if (finalizado != null){
			q.setParameter("finalizado", finalizado);
		}
		if (ValidatorUtil.isNotEmpty(idCriadoPor)){
			q.setParameter("idCriadoPor", idCriadoPor);
		}
		if (ValidatorUtil.isNotEmpty(idTipoCubo)){
			q.setParameter("idTipoCubo", idTipoCubo);
		}
		
		try {
			if (paginacao != null) {
				String hqlPag = hql;
				int posOrder = (hqlPag.indexOf("order by") > 0) ? hqlPag.indexOf("order by") : hqlPag.length();
				int posSelect = (hqlPag.indexOf("select") >= 0) ? hqlPag.indexOf("from") : 0;
				
				Query qPaginacao = getEm().createQuery("select count(*) " + hqlPag.substring(posSelect, posOrder));
				
				if (ValidatorUtil.isNotEmpty(titulo)){
					qPaginacao.setParameter("titulo", "%" + titulo.toUpperCase() + "%");
				}
				if (finalizado != null){
					qPaginacao.setParameter("finalizado", finalizado);
				}
				if (ValidatorUtil.isNotEmpty(idCriadoPor)){
					qPaginacao.setParameter("idCriadoPor", idCriadoPor);
				}
				if (ValidatorUtil.isNotEmpty(idTipoCubo)){
					qPaginacao.setParameter("idTipoCubo", idTipoCubo);
				}
				
				paginacao.setTotalRegistros((int) ((Long) qPaginacao.getSingleResult()).longValue());
				q.setFirstResult(paginacao.getPaginaAtual() * paginacao.getTamanhoPagina());
				q.setMaxResults(paginacao.getTamanhoPagina());
			}
			
			@SuppressWarnings("unchecked")
			List<Codigo> codigos = q.getResultList();
			return codigos;
		} catch (NoResultException e){
			return new ArrayList<>();
		}
	}
	
	/**
	 * Retorna quais das publicações de códigos, cujos IDs são passados como parâmetros, um
	 * determinado usuário avaliou.
	 */
	@SuppressWarnings("unchecked")
	public Map<Integer, AvaliacaoCodigo> findAvaliacoesCodigosByIdsCodigos(int[] idsCodigos, 
			Integer idUsuario) throws DAOException{
		
		if (ValidatorUtil.isEmpty(idsCodigos))
			return null;
		
		try {
			EntityManager em = getEm();
			
			StringBuilder hql = new StringBuilder();
			
			hql.append(" SELECT c.id, av.nota ");
			hql.append(" FROM Codigo c, AvaliacaoCodigo av ");
			hql.append(" WHERE ");
			hql.append(" av.publicacao.id = c.id ");
			hql.append(" AND av.criadoPor.id = :idUsuario ");
			hql.append(" AND c.id IN " + HibernateUtils.gerarStringIn(idsCodigos));

			Query q = em.createQuery(hql.toString());
			q.setParameter("idUsuario", idUsuario);

			List<Object[]> objects = q.getResultList();
			Map<Integer, AvaliacaoCodigo> result = new HashMap<Integer, AvaliacaoCodigo>();
			
			for (int i=0; i< objects.size(); i++) {
				Object[] obj = objects.get(i);
				
				Integer idCodigo = (Integer) obj[0];
				
				AvaliacaoCodigo ava = new AvaliacaoCodigo();
				ava.setNota((Integer) obj[1]); 
				
				result.put(idCodigo, ava);
			}
			
			return result;
		} catch (NoResultException e){
			return null;
		}
	}
	
}
