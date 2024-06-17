package volumen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Date;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import volumen.data.AnswersRepository;
import volumen.data.ChaptersRepository;
import volumen.data.CourseCategoryRepository;
import volumen.data.CourseRepository;
import volumen.data.LectureTestRepository;
import volumen.data.LecturesRepository;
import volumen.data.TestQuestionRepository;
import volumen.data.UsersRepository;
import volumen.model.Answer;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.TestQuestion;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = Application.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application.yml")
@TestMethodOrder(OrderAnnotation.class)
@Order(1)
public class CourseTest {

	@Autowired
	private MockMvc mvc;
	@Autowired
	private CourseCategoryRepository repoCategories;
	@Autowired
	private CourseRepository repoCourses;
	@Autowired
	private ChaptersRepository repoChapters;
	@Autowired
	private LecturesRepository repoLectures;
	@Autowired
	private LectureTestRepository repoTests;
	@Autowired
	private TestQuestionRepository repoTestQuestions;
	@Autowired
	private AnswersRepository repoAnswers;
	@Autowired
	private UsersRepository repoUsers;
	@Autowired
	private TransactionTemplate transactionTemplate;

	Long firstCourseCategoryId = -1L;
	Long secondCourseCategoryId = -1L;
	Long firstCourseId = -1L;
	Long firstChapterId = -1L;
	Long firstLectureId = -1L;
	Long firstTestId = -1L;
	
	User user = null;

	@BeforeAll
	static public void setUp() {
		 
	}

	@AfterAll
	static public void tearDown() {
		// Clean up

	}

	private static final String CATEGORY1_NAME = "Science";
	private static final String CATEGORY2_NAME = "Medical";
	private static final String CATEGORY1_1_NAME = "Mathematics";
	private static final String CATEGORY1_2_NAME = "Physics";
	private static final String CATEGORY2_1_NAME = "Zhong Yi";
	
	void createUserIfNotExists() {
		var optUser = repoUsers.findById(1L);
		if (optUser.isPresent())
			user = optUser.get();
		else {
			user = new User();
			user.setUsername("user1");
			user.setPassword("password");
			repoUsers.save(user);
		}
	}

	@Test
	@Order(1)
	public void testCreateCourseCategories() {
		CourseCategory cat1 = new CourseCategory();
		cat1.setName(CATEGORY1_NAME);
		var c1_1 = new CourseCategory();
		c1_1.setParent(cat1);
		c1_1.setName(CATEGORY1_1_NAME);
		var c1_2 = new CourseCategory();
		c1_2.setParent(cat1);
		c1_2.setName(CATEGORY1_2_NAME);
		cat1.getCategories().add(c1_1);
		cat1.getCategories().add(c1_2);
		repoCategories.save(cat1);
		firstCourseCategoryId = c1_2.getId();
		var cat1a = repoCategories.findById(firstCourseCategoryId);
		assertNotNull(cat1a);

		CourseCategory cat2 = new CourseCategory();
		cat2.setName(CATEGORY2_NAME);
		var c2_1 = new CourseCategory();
		c2_1.setName(CATEGORY2_1_NAME);
		c2_1.setParent(cat2);
		cat2.getCategories().add(c2_1);
		repoCategories.save(cat2);
		secondCourseCategoryId = c2_1.getId();
	}

	static final String COURSE1_NAME = "Course 1";
	static final String COURSE1_DESCRIPTION = "Course 1 description";
	static final String COURSE2_NAME = "Course 2";
	static final String COURSE2_DESCRIPTION = "Course 2 description";
	static final String COURSE3_NAME = "Course 3";
	static final String COURSE3_DESCRIPTION = "Course 3 description";

	static final String COURSE1_CHAPTER1_NAME = "Chapter 1 of Course 1";
	static final String COURSE1_CHAPTER1_DESCRIPTION = "Chapter 1 of Course 1 description";
	static final String COURSE1_CHAPTER2_NAME = "Chapter 2 of Course 1";
	static final String COURSE1_CHAPTER2_DESCRIPTION = "Chapter 2 of Course 1 description";
	static final String COURSE1_CHAPTER3_NAME = "Chapter 3 of Course 1";
	static final String COURSE1_CHAPTER3_DESCRIPTION = "Chapter 3 of Course 1 description";
	static final String COURSE1_CHAPTER4_NAME = "Chapter 4 of Course 1";
	static final String COURSE1_CHAPTER4_DESCRIPTION = "Chapter 4 of Course 1 description";

	static final String COURSE1_CHAPTER1_LECTURE1_NAME = "Lecture 1 of Chapter 1 of Course 1";
	static final String COURSE1_CHAPTER1_LECTURE1_DESCRIPTION = "Lecture 1 of Chapter 1 of Course 1 description";
	static final String COURSE1_CHAPTER1_LECTURE1_CONTENT = "Content of Lecture 1 of Chapter 1 of Course 1: не-ASCII-текст: 看似面善，实则心黑";

	@Test
	@Order(11)
	public void testCreateCourse1() {
		createUserIfNotExists();
		Course course = new Course();
		var opt = repoCategories.findById(firstCourseCategoryId);
		assertThat(opt.isPresent());
		var cat1 = opt.get();
		assertNotNull(cat1);
		course.setCreatedBy(user);
		course.setCategory(cat1);
		course.setName(COURSE1_NAME);
		course.setDescription(COURSE1_DESCRIPTION);
		course.setCreatedAt(new Date());
		repoCourses.save(course);

		Long id = course.getId();

		Course c1 = repoCourses.findById(id).get();
		assertNotNull(c1);
		assertEquals(COURSE1_NAME, c1.getName());
		assertEquals(COURSE1_DESCRIPTION, c1.getDescription());

		// save the identifier
		firstCourseId = id;
	}

	@Test
	@Order(12)
	public void testAddDifferentCourses() {
		Course course = new Course();
		var opt = repoCategories.findById(secondCourseCategoryId);
		assertThat(opt.isPresent());
		var cat2 = opt.get();
		assertNotNull(cat2);
		course.setCategory(cat2);
		course.setName(COURSE2_NAME);
		course.setDescription(COURSE2_DESCRIPTION);
		repoCourses.save(course);

		course = new Course();
		course.setCategory(cat2);
		course.setName(COURSE3_NAME);
		course.setDescription(COURSE3_DESCRIPTION);
		repoCourses.save(course);
	}

	Chapter createChapter(String name, String desc, Course addToCourse) {
		Chapter chapter = new Chapter();
		chapter.setName(name);
		chapter.setDescription(desc);
		chapter.setCourse(addToCourse);
		repoChapters.save(chapter);
		return chapter;
	}

	@Test
	@Order(13)
	public void testAddChapter1ToCourse1() {
		assertEquals(3, repoCourses.count());
		var opt1 = repoCourses.findById(firstCourseId);
		Course c1 = opt1.get();
		assertNotNull(c1);
		assertEquals(COURSE1_NAME, c1.getName());

		Chapter chapter = createChapter(COURSE1_CHAPTER1_NAME, COURSE1_CHAPTER1_DESCRIPTION, c1);
		Long chapId1 = chapter.getId();

		var optch = repoChapters.findById(chapId1);
		assertThat(optch.isPresent());
		Chapter chap1 = optch.get();
		assertNotNull(chap1);
		assertEquals(COURSE1_CHAPTER1_NAME, chap1.getName());
		assertEquals(COURSE1_CHAPTER1_DESCRIPTION, chap1.getDescription());

		firstChapterId = chap1.getId();
	}

	@Test
	@Order(14)
	public void testAddMoreChaptersToCourse1() {
		var optCourse = repoCourses.findById(firstCourseId);
		assertThat(optCourse.isPresent());
		Course c1 = optCourse.get();
		assertEquals(COURSE1_NAME, c1.getName());

		Chapter chapter = createChapter(COURSE1_CHAPTER2_NAME, COURSE1_CHAPTER2_DESCRIPTION, c1);

		var optChap = repoChapters.findById(chapter.getId());
		assertThat(optChap.isPresent());
		Chapter chap2 = optChap.get();
		assertEquals(COURSE1_CHAPTER2_NAME, chap2.getName());
		assertEquals(COURSE1_CHAPTER2_DESCRIPTION, chap2.getDescription());

		chapter = createChapter(COURSE1_CHAPTER3_NAME, COURSE1_CHAPTER3_DESCRIPTION, c1);

		optChap = repoChapters.findById(chapter.getId());
		assertThat(optChap.isPresent());
		Chapter chap3 = optChap.get();
		assertNotNull(chap3);
		assertEquals(COURSE1_CHAPTER3_NAME, chap3.getName());
		assertEquals(COURSE1_CHAPTER3_DESCRIPTION, chap3.getDescription());

		chapter = createChapter(COURSE1_CHAPTER4_NAME, COURSE1_CHAPTER4_DESCRIPTION, c1);

		optChap = repoChapters.findById(chapter.getId());
		assertThat(optChap.isPresent());
		Chapter chap4 = optChap.get();
		assertEquals(COURSE1_CHAPTER4_NAME, chap4.getName());
		assertEquals(COURSE1_CHAPTER4_DESCRIPTION, chap4.getDescription());

	}

	@Test
	@Order(15)
	public void testAddLecture1ToChapter1_1() {
		var optChap = repoChapters.findById(firstChapterId);
		assertThat(optChap.isPresent());
		Chapter ch1 = optChap.get();

		Lecture lecture = new Lecture();
		lecture.setName(COURSE1_CHAPTER1_LECTURE1_NAME);
		lecture.setDescription(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION);

		lecture.setContent(COURSE1_CHAPTER1_LECTURE1_CONTENT);

		lecture.setChapter(ch1);
		repoLectures.save(lecture);
		assertThat(lecture.getId() >= 0);

		var optLec = repoLectures.findById(lecture.getId());
		assertThat(optLec.isPresent());
		Lecture lec1 = optLec.get();

		assertEquals(COURSE1_CHAPTER1_LECTURE1_NAME, lec1.getName());
		assertEquals(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION, lec1.getDescription());

		assertEquals(COURSE1_CHAPTER1_LECTURE1_CONTENT, lec1.getContent());
		firstLectureId = lec1.getId();
	}

	@Test
	@Order(16)
	void testAddTest1ToLecture() {
		var optLec = repoLectures.findById(firstLectureId);
		assertThat(optLec.isPresent());
		Lecture lec1 = optLec.get();
		assertNotNull(lec1);

		assertEquals(COURSE1_CHAPTER1_LECTURE1_NAME, lec1.getName());
		assertEquals(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION, lec1.getDescription());

		LectureTest test = new LectureTest();
		test.setLecture(lec1);
		repoTests.save(test);
		lec1.setLectureTest(test);
		repoLectures.save(lec1);

		firstTestId = lec1.getId();

		var optTest = repoTests.findById(firstTestId);
		assertThat(optTest.isPresent());
		LectureTest test1 = optTest.get();
		assertNotNull(test1);
	}

	private static final String QUESTION1_TEXT = "Question 1";
	private static final String QUESTION1_ANSWER1_TEXT = "Question 1 / Answer1";
	private static final String QUESTION1_ANSWER2_TEXT = "Question 1 / Answer2";
	private static final String QUESTION1_ANSWER3_TEXT = "Question 1 / Answer3";
	private static final Long QUESTION1_SCORE = 5L; // score per question
	private static final boolean QUESTION1_ANSWER1_ISVALID = false;
	private static final boolean QUESTION1_ANSWER2_ISVALID = true;
	private static final boolean QUESTION1_ANSWER3_ISVALID = false;

	private static final String QUESTION2_TEXT = "Question 2";
	private static final String QUESTION2_ANSWER1_TEXT = "Question 2 / Answer1";
	private static final String QUESTION2_ANSWER2_TEXT = "Question 2 / Answer2";
	private static final String QUESTION2_ANSWER3_TEXT = "Question 2 / Answer3";
	private static final String QUESTION2_ANSWER4_TEXT = "Question 2 / Answer4";
	private static final Long QUESTION2_SCORE = 3L; // score per question
	private static final boolean QUESTION2_ANSWER1_ISVALID = false;
	private static final boolean QUESTION2_ANSWER2_ISVALID = false;
	private static final boolean QUESTION2_ANSWER3_ISVALID = true;
	private static final boolean QUESTION2_ANSWER4_ISVALID = true;

	@Test
	@Order(17)
	void testAddQuestionsToTest1() {
		var optTest = repoTests.findById(firstTestId);
		assertThat(optTest.isPresent());
		LectureTest test1 = optTest.get();
		assertNotNull(test1);
		// Question #1
		TestQuestion question1 = new TestQuestion();
		question1.setText(QUESTION1_TEXT);
		question1.setQuestionType(QuestionType.SINGLE);
		question1.setScore(QUESTION1_SCORE);
		question1.setLectureTest(test1);
		repoTestQuestions.save(question1);
		test1.getQuestions().add(question1);
		
		Answer a11 = new Answer();
		a11.setTestQuestion(question1);
		a11.setText(QUESTION1_ANSWER1_TEXT);
		a11.setValid(QUESTION1_ANSWER1_ISVALID);
		repoAnswers.save(a11);
		question1.getAnswers().add(a11);

		Answer a12 = new Answer();
		a12.setTestQuestion(question1);
		a12.setText(QUESTION1_ANSWER2_TEXT);
		a12.setValid(QUESTION1_ANSWER2_ISVALID);
		repoAnswers.save(a12);
		question1.getAnswers().add(a12);

		Answer a13 = new Answer();
		a13.setTestQuestion(question1);
		a13.setText(QUESTION1_ANSWER3_TEXT);
		a13.setValid(QUESTION1_ANSWER3_ISVALID);
		repoAnswers.save(a13);
		question1.getAnswers().add(a13);

		optTest = repoTests.findById(firstTestId);
		assertThat(optTest.isPresent());
		LectureTest t1 = optTest.get();
		assertEquals(1, t1.getQuestions().size());
		assertEquals(3, t1.getQuestions().get(0).getAnswers().size());

		// Question #2
		TestQuestion question2 = new TestQuestion();
		question2.setText(QUESTION2_TEXT);
		question2.setQuestionType(QuestionType.SINGLE);
		question2.setScore(QUESTION2_SCORE);
		question2.setLectureTest(test1);
		repoTestQuestions.save(question2);
		test1.getQuestions().add(question2);

		Answer a21 = new Answer();
		a21.setTestQuestion(question2);
		a21.setText(QUESTION2_ANSWER1_TEXT);
		a21.setValid(QUESTION2_ANSWER1_ISVALID);
		repoAnswers.save(a21);
		question2.getAnswers().add(a21);

		Answer a22 = new Answer();
		a22.setTestQuestion(question2);
		a22.setText(QUESTION2_ANSWER2_TEXT);
		a22.setValid(QUESTION2_ANSWER2_ISVALID);
		repoAnswers.save(a22);
		question2.getAnswers().add(a22);

		Answer a23 = new Answer();
		a23.setTestQuestion(question2);
		a23.setText(QUESTION2_ANSWER3_TEXT);
		a23.setValid(QUESTION2_ANSWER3_ISVALID);
		repoAnswers.save(a23);
		question2.getAnswers().add(a23);

		Answer a24 = new Answer();
		a24.setTestQuestion(question2);
		a24.setText(QUESTION2_ANSWER4_TEXT);
		a24.setValid(QUESTION2_ANSWER4_ISVALID);
		repoAnswers.save(a24);
		question2.getAnswers().add(a24);

		optTest = repoTests.findById(firstTestId);
		assertThat(optTest.isPresent());
		t1 = optTest.get();
		assertEquals(2, t1.getQuestions().size());
		assertEquals(4, t1.getQuestions().get(1).getAnswers().size());

		assertEquals(QUESTION1_SCORE, t1.getQuestions().get(0).getScore());
		assertEquals(QUESTION2_SCORE, t1.getQuestions().get(1).getScore());

		assertEquals(QUESTION1_SCORE + QUESTION2_SCORE, t1.getFullScore());
	}
}
