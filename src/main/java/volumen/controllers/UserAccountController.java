package volumen.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.annotation.security.RolesAllowed;


@RolesAllowed({"USER", "ADMIN", "TEACHER"})
@RequestMapping("/userpage")
@Controller
public class UserAccountController extends BaseController {
	
	private static final String VIEW_USERPAGE_START = "userpage/home";

	@GetMapping
	public ModelAndView index() {
		ModelAndView model = new ModelAndView(VIEW_USERPAGE_START);
		this.addRoleAttributes(model);
		return model;
	}
}
