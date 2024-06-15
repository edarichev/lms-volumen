package volumen.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import volumen.model.Answer;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.TestQuestion;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    static {
        try {
        	Class<?>[] cl = {Answer.class, Chapter.class, Course.class, CourseCategory.class,
        			Lecture.class, LectureTest.class,
        			QuestionType.class, TestQuestion.class};
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            for (var c : cl)
            	configuration.addAnnotatedClass(c);
            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static Session getSession() {
    	return sessionFactory.openSession();
    }
}