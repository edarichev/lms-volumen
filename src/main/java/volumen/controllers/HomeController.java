package volumen.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.data.CourseCategoryRepository;
import volumen.exceptions.CircularCategoryReferenceException;
import volumen.model.CourseCategory;
import volumen.web.ui.CategoryNode;
import volumen.web.ui.CategoryTreeBuilder;

@Controller
@RequestMapping("/")
public class HomeController {
	
	@Autowired
	private CourseCategoryRepository categoryRepo;
	
	@GetMapping("/")
	public ModelAndView home() {
		ModelAndView model = new ModelAndView("home");
		List<CourseCategory> target = new ArrayList<>();
		categoryRepo.findAll().forEach(target::add);
		CategoryNode rootCat;
		try {
			rootCat = CategoryTreeBuilder.buildTree(target);
			model.addObject("categories", rootCat == null ? null : rootCat.getItems());
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
			model.addObject("categories", new ArrayList<CategoryNode>());
		}
		return model;
	}
}
