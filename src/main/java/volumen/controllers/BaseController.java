package volumen.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;

import volumen.data.CourseCategoryRepository;
import volumen.data.CourseRepository;
import volumen.data.UsersRepository;
import volumen.exceptions.CategoryNotFoundException;
import volumen.exceptions.ChapterNotFoundException;
import volumen.exceptions.CircularCategoryReferenceException;
import volumen.exceptions.CourseNotFoundException;
import volumen.exceptions.LectureNotFoundException;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.TestQuestion;
import volumen.model.dto.IdNamePair;
import volumen.web.ui.CategoryNode;
import volumen.web.ui.CategoryTreeBuilder;

public class BaseController {
	
	protected static final String INDENT = "\u00A0\u00A0";

	protected static final String[] EDITOR_ROLES = {"ADMIN", "TEACHER"};
	@Autowired
	protected CourseCategoryRepository categoryRepo;
	
	@Autowired
	protected CourseRepository courseRepo;
	
	@Autowired
	protected UsersRepository usersRepo;

	@Autowired
    protected MessageSource messageSource;
	
	protected ArrayList<CategoryNode> buildCategoriesTreeList() {
		return buildCategoriesList(null);
	}

	protected ArrayList<CategoryNode> buildCategoriesList(CourseCategory parentCategory) {
		List<CourseCategory> target = new ArrayList<>();
		categoryRepo.findAll().forEach(target::add);
		CategoryNode rootCat = null;
		ArrayList<CategoryNode> categories = null;
		try {
			if (parentCategory == null)
				rootCat = CategoryTreeBuilder.buildTree(target);
			else
				rootCat = CategoryTreeBuilder.buildTree(target, parentCategory);
			categories = rootCat == null ? null : rootCat.getItems();
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
		}
		if (categories == null)
			categories = new ArrayList<CategoryNode>();
		return categories;
	}
	protected ArrayList<CourseCategory> getCategoriesList() {
		var categoriesList = new ArrayList<CourseCategory>();
		for (var cat : categoryRepo.findAll())
			categoriesList.add(cat);
		return categoriesList;
	}

	protected List<IdNamePair<Long>> buildCategoryListForSelectElement(boolean includeEmptyRoot, String indent) {
		ArrayList<CourseCategory> list = getCategoriesList();
		List<IdNamePair<Long>> items;
		try {
			items = CategoryTreeBuilder.buildStringItems(list, indent);
			// add empty item (NULL)
			if (includeEmptyRoot)
				items.add(0, new IdNamePair<Long>(0L, getMessage("category.root")));
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
			items = new ArrayList<IdNamePair<Long>>();//return an empty list
		}
		return items;
	}
	
	protected String getMessage(String key) {
		Locale locale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(key, null, locale);
	}
	
	protected CourseCategory findCategoryOrThrow(Long categoryId) {
		return categoryRepo.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
	}

	protected Course findCourseOrThrow(Long id) {
		var course = courseRepo.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
		return course;
	}

	protected Course getCourseOrThrow(Chapter chapter) {
		Course course = chapter.getCourse();
		if (course == null)
			throw new CourseNotFoundException();
		return course;
	}

	protected CourseCategory getCategoryOrThrow(Course course) {
		var category = course.getCategory();
		if (category == null)
			throw new CategoryNotFoundException();
		return category;
	}
	
	protected Chapter getChapterOrThrow(Lecture lecture) {
		Chapter chapter = lecture.getChapter();
		if (chapter == null)
			throw new ChapterNotFoundException();
		return chapter;
	}

	protected Lecture getLectureOrThrow(LectureTest test) {
		Lecture lecture = test.getLecture();
		if (lecture == null)
			throw new LectureNotFoundException();
		return lecture;
	}
	
	protected LectureTest getLectureTestOrThrow(TestQuestion question) {
		LectureTest test = question.getLectureTest();
		if (test == null)
			throw new TestNotFoundException();
		return test;
	}
	
	protected LectureTest getLectureTestOrThrow(Lecture lecture) {
		LectureTest test = lecture.getLectureTest();
		if (test == null)
			throw new TestNotFoundException();
		return test;
	}

	protected volumen.User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken)) {
		    String currentUserName = authentication.getName();
		    return usersRepo.findByUsername(currentUserName);
		}
		return null;
	}
	
	protected boolean isCurrentUserAllowEditResource() {
		volumen.User user = getCurrentUser();
		if (user == null)
			return false;
		return user.hasAnyRole(EDITOR_ROLES);
	}
	
	protected void addRoleAttributes(ModelAndView model) {
		model.addObject("user", this.getCurrentUser());
		model.addObject("editCategoryEnabled", this.isCurrentUserAllowEditResource());
	}
	
	protected void addRoleAttributes(Model model) {
		model.addAttribute("user", this.getCurrentUser());
		model.addAttribute("editCategoryEnabled", this.isCurrentUserAllowEditResource());
	}
}
