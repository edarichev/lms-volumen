package volumen.data;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;

import volumen.util.HibernateUtil;

@Service
public abstract class RepositoryBase<TEntity> implements AutoCloseable {

	protected final Session session;
	
	protected final Class<TEntity> entityClass;
	
	public RepositoryBase(Class<TEntity> eclass) {
		this.entityClass = eclass;
		session = HibernateUtil.getSession();
	}
	
	public Transaction startTransaction() {
		Transaction tx = session.beginTransaction();
		return tx;
	}
	
	@Override
	public void close() throws Exception {
		if (session != null)
			session.close();
	}
	
	public TEntity findById(Long id) {
		Transaction transaction = null;
		TEntity course = null;
        try {
            transaction = session.beginTransaction();
            course = session.find(entityClass, id);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
		return course;
	}
	
	public void save(TEntity obj) {
		Transaction transaction = null; 
        try {
            transaction = session.beginTransaction();
            session.persist(obj);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }
}
