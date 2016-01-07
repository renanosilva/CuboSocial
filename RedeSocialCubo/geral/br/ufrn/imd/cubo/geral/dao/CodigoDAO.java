package br.ufrn.imd.cubo.geral.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.PagingInformation;
import br.ufrn.imd.cubo.arq.exception.DAOException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
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
	
	public List<Codigo> findCodigoGeral(String titulo, String orderBy, Boolean finalizado, 
			Integer idCriadoPor, PagingInformation paginacao) throws Exception{
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
	
}
