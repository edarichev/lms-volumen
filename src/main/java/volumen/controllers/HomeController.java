package volumen.controllers;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.web.ui.CategoryNode;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController {
	
	private static final String VIEW_HOME = "home"; 
	
	@GetMapping("/")
	public ModelAndView home() {
		ArrayList<CategoryNode> categories = buildCategoriesTreeList();

		ModelAndView model = new ModelAndView(VIEW_HOME);
		model.addObject("categories", categories);
		model.addObject("courses", courseRepo.findAll());
		return model;
	}
}
