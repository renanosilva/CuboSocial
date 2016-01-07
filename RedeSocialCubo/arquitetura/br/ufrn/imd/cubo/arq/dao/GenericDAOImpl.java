package br.ufrn.imd.cubo.arq.dao;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;

import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.exception.DAOException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

public class GenericDAOImpl implements IGenericDAO {
	
	private void change(PersistDB c, OperacaoDatabase op){
		switch (op) {
			case INSERIR:
				getEm().persist(c);
				break;
			case ALTERAR:
				getEm().merge(c);
				break;
			case REMOVER:
				getEm().remove(c);
				break;
		}
	}

	@Override
	public EntityManager getEm() {
		return Database.getInstance().getEntityManager();
	}
	
	@Override
	public void create(PersistDB c){
		change(c, OperacaoDatabase.INSERIR);
	}

	@Override
	public void update(PersistDB c){
		change(c, OperacaoDatabase.ALTERAR);
	}
	
	@Override
	public void createOrUpdate(PersistDB c){
		if (c.getId() == 0)
			change(c, OperacaoDatabase.INSERIR);
		else
			change(c, OperacaoDatabase.ALTERAR);
	}
	
	@Override
	public void delete(PersistDB c){
		change(c, OperacaoDatabase.REMOVER);
	}
	
	@Override
	public void flush(){
		getEm().flush();
	}
	
	@Override
	public <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe){
		EntityManager em = getEm();
		T c = em.find(classe, id);
		return c;
	}
	
	@Override
	public <T extends PersistDB> List<T> findAll(Class<T> classe){
		EntityManager em = getEm();
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(classe);
		TypedQuery<T> typedQuery = em.createQuery(query.select(query.from(classe)));
		List<T> c = typedQuery.getResultList();
		return c;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistDB> List<T> findAllAtivos(Class<T> classe) {
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where ativo = :ativo ";
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		q.setParameter("ativo", true);
		List<T> retorno = q.getResultList();
		return retorno;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <T extends PersistDB> List<T> findAllLike(String coluna,String valor, String orderby, Class<T> classe){
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where upper("+coluna+") like upper(:valor)";
		
		if (ValidatorUtil.isNotEmpty(orderby)){
			jpql += " order by " + orderby;
		}
		
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		q.setParameter("valor", "%"+valor+"%");
		List<T> retorno = q.getResultList();
		return retorno;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistDB> List<T> findByExactField(String coluna, Object valor, Class<T> classe) {
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where "+coluna+" = :valor";
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		q.setParameter("valor", valor);
		List<T> retorno = q.getResultList();
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistDB> List<T> findByExactField(String coluna, Object valor, String orderBy, Class<T> classe) {
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where "+coluna+" = :valor";
		
		if (ValidatorUtil.isNotEmpty(orderBy)){
			jpql += " order by " + orderBy;
		}
		
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		q.setParameter("valor", valor);
		List<T> retorno = q.getResultList();
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistDB> List<T> findByExactFields(String[] colunas, Object[] valores, Class<T> classe) {
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where 1=1 ";
		
		for (int i = 0; i < colunas.length; i++){
			String coluna = colunas[i];
			jpql += " and " + coluna + " = :valor" + i + " ";
		}
		
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		
		for (int i = 0; i < valores.length; i++){
			q.setParameter("valor"+i, valores[i]);
		}
		
		List<T> retorno = q.getResultList();
		return retorno;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistDB> T findByExactFields(String[] colunas, Object[] valores, 
			boolean limit, Class<T> classe) {
		String tabela = classe.getSimpleName();
		String jpql = "from "+tabela+ " where 1=1 ";
		
		for (int i = 0; i < colunas.length; i++){
			String coluna = colunas[i];
			jpql += " and " + coluna + " = :valor" + i + " ";
		}
		
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		
		if (limit)
			q.setMaxResults(1);
		
		for (int i = 0; i < valores.length; i++){
			q.setParameter("valor"+i, valores[i]);
		}
		
		List<T> results = q.getResultList();
		
		return ValidatorUtil.isNotEmpty(results) ? results.get(0) : null;
	}
	
	@Override
	public <T extends PersistDB> void updateField(Class<T> classe, int id, String coluna, Object valor) {
		String tabela = classe.getSimpleName();
		String jpql = "update "+tabela+ " set " + coluna + " = :valor where id = :id ";
		EntityManager em = getEm();
		Query q = em.createQuery(jpql);
		q.setParameter("valor", valor);
		q.setParameter("id", id);
		q.executeUpdate();
	}

	@Override
	public void clear() {
		getEm().clear();	
	}

	@Override
	public void detach(PersistDB p) {
		getEm().detach(p);
	}
	
	@Override
	public void refresh(PersistDB p) {
		getEm().refresh(p);
	}
	
	@Override
	public void update(String sql) {
		Session session = (Session) Database.getInstance().getEntityManager().getDelegate();
		SQLQuery q = session.createSQLQuery(sql);
		q.executeUpdate();
	}

	/**
	 * Recupera o próximo valor da sequence
	 *
	 * @param sequence
	 * @return
	 * @throws SQLException
	 */
	public int getNextSeq(int numSeqAno, int ano) throws DAOException {
		return getNextSeq("public", "seq_" + numSeqAno + "_" + ano );
	}
	
	/**
	 * Recupera o próximo valor de uma sequência de esquema e nome passados como argumento.
	 * Se a sequência não existir, cria.
	 */
	public int getNextSeq(String esquema, String nomeSequencia) {
		String sequencia = esquema == null ? nomeSequencia : esquema + "." + nomeSequencia; 
		
		if (!isSequenciaExistente(esquema, nomeSequencia)){
			//Cria sequência. Ela ainda não existe
			criaSequencia( esquema, nomeSequencia );
		}

		Session session = (Session) Database.getInstance().getEntityManager().getDelegate();
		
		//Sequência já existe na base de dados. Retorna próximo valor
		BigInteger result = (BigInteger) session.createSQLQuery("select nextval('" + sequencia + "')").uniqueResult();
		return result.intValue();
	}
	
	/**
	 * Recupera o valor atual de uma sequência de esquema e nome passados como argumento.
	 * Se a sequência não existir, retorna 0 (zero).
	 */
	public int getCurrentSeq(String esquema, String nomeSequencia) {
		if (!isSequenciaExistente(esquema, nomeSequencia))
			return 0;

		//Sequencia já existe na base de dados. Retorna valor atual
		String sequencia = esquema == null ? nomeSequencia : esquema + "." + nomeSequencia;
		String sql = "SELECT last_value FROM " + sequencia; 
		
		Session session = (Session) Database.getInstance().getEntityManager().getDelegate();
		BigInteger result = (BigInteger) session.createSQLQuery(sql).uniqueResult();
		return result.intValue();
	}
	
	/**
	 * Verifica se a sequência definida pelos parâmetros informados existe ou não na base de dados.
	 * @param esquema
	 * @param nomeSequencia
	 * @return
	 */
	private boolean isSequenciaExistente(String esquema, String nomeSequencia){
		String sqlVerificaSequencia = verificarExistenciaSequencia(esquema, nomeSequencia);
		
		Session session = (Session) Database.getInstance().getEntityManager().getDelegate();
		BigInteger total = (BigInteger) session.createSQLQuery(sqlVerificaSequencia).uniqueResult();

		return total.intValue() > 0;
	}
	
	/** Verifica se uma determinada sequence existe no banco de dados. */
	private String verificarExistenciaSequencia(String esquema, String sequencia) {
		
		String schema = esquema == null ? "public" : esquema;
		
		String sqlVerificaSequencia = "SELECT count(distinct c.relnamespace) "
			+ "FROM pg_catalog.pg_class c, pg_catalog.pg_user u, pg_catalog.pg_namespace n "
			+ "WHERE c.relnamespace=n.oid AND c.relkind = 'S' AND "
			+ "n.nspname='" + schema + "' and  c.relname='" + sequencia + "'";
		
		return sqlVerificaSequencia;
	}
	
	/** Cria uma sequence no banco de dados. */
	private void criaSequencia(String esquema, String seqName){
		String sequencia = esquema == null ? seqName : esquema + "." + seqName;
			
		final String criaSequencia = "CREATE SEQUENCE  " + sequencia +
				" INCREMENT 1 " +
				" MINVALUE 1 " +
				" MAXVALUE 9223372036854775807 " +
				" START 1 " +
				" CACHE 1 ";
		
		Session session = (Session) Database.getInstance().getEntityManager().getDelegate();
		
		session.doWork(new Work() {
			@Override
			public void execute(Connection connection) throws SQLException {
				Statement stm = connection.createStatement();
				stm.execute(criaSequencia);
			}
		});
	}
	
	/**
	 * Prepara a query e 'configura' o Paging Information para a paginação de
	 * uma determinada consulta.
	 *
	 * @param paging
	 * @param q
	 * @throws Exception
	 */
	public void preparePaging(PagingInformation paging, Query q)
			throws Exception {
		if (paging != null) {
			paging.setTotalRegistros(count(q));
			q.setFirstResult(paging.getPaginaAtual() * paging.getTamanhoPagina());
			q.setMaxResults(paging.getTamanhoPagina());
		}
	}
	
	/**
	 * Elimina a clausula order by e os campos definidos no "FROM" e executa a
	 * consulta com um SELECT COUNT(*) e retorna a quantidade de resultados
	 * encontrados.
	 *
	 * @param q
	 * @return
	 * @throws DAOException
	 */
	public int count(Query q) throws DAOException {
		String query = q.unwrap(org.hibernate.Query.class).getQueryString();
		int posOrder = (query.indexOf("order by") > 0) ? query.indexOf("order by") : query.length();
		int posSelect = (query.indexOf("select") >= 0) ? query.indexOf("from") : 0;
		
		Query qTotal = getEm().createQuery("select count(*) " + query.substring(posSelect, posOrder));
		return (int) ((Long) qTotal.getSingleResult()).longValue();
	}

}

enum OperacaoDatabase {
	INSERIR,ALTERAR,REMOVER;
}

