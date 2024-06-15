package volumen.data;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.CriteriaQuery;
import volumen.model.Chapter;
import volumen.model.Course;

@Service
public class CourseRepository extends RepositoryBase<Course> {

	public CourseRepository() {
		super(Course.class);
	}

	public void deleteAll() {
		Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM Course", null).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
	}
	
	public Long getChaptersCount() {
		Transaction transaction = null;
		Long count = 0L;
		try {
            transaction = session.beginTransaction();
			CriteriaQuery<Long> c = session.getCriteriaBuilder().createQuery(Long.class);
			c.select(session.getCriteriaBuilder().count(c.from(Chapter.class)));
            count = session.createQuery(c).getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
		return count;
	}

	public Long getCoursesCount() {
		Transaction transaction = null;
		Long count = 0L;
		try {
            transaction = session.beginTransaction();
			CriteriaQuery<Long> c = session.getCriteriaBuilder().createQuery(Long.class);
			c.select(session.getCriteriaBuilder().count(c.from(Course.class)));
            count = session.createQuery(c).getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
		return count;
	}
}
