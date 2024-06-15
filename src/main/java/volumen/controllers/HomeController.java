package volumen.controllers;

import org.hibernate.HibernateException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

	@GetMapping("/")
	public String viewBooks() {
		Configuration configuration = new AnnotationConfiguration().configure();
        var sessionFactory = configuration.buildSessionFactory();
        org.hibernate.Session session = null;
        try {
			session = sessionFactory.openSession();
			
		} catch (HibernateException e) {
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
		return "home";
	}
}
