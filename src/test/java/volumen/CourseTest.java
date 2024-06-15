package volumen;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import volumen.data.ChaptersRepository;
import volumen.data.CourseCategoryRepository;
import volumen.data.CourseRepository;
import volumen.data.LectureTestRepository;
import volumen.data.LecturesRepository;
import volumen.model.Answer;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.TestQuestion;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Order(1)
public class CourseTest {

	private CourseCategoryRepository repoCategories;
	private CourseRepository repoCourses;
	private ChaptersRepository repoChapters;
	private LecturesRepository repoLectures;
	private LectureTestRepository repoTests;

	Long firstCourseCategoryId = -1L;
	Long secondCourseCategoryId = -1L;
	Long firstCourseId = -1L;
	Long firstChapterId = -1L;
	Long firstLectureId = -1L;
	Long firstTestId = -1L;

	@BeforeAll
	public void setUp() {
		repoCategories = new CourseCategoryRepository();
		repoCourses = new CourseRepository();
		repoChapters = new ChaptersRepository();
		repoLectures = new LecturesRepository();
		repoTests = new LectureTestRepository();

		repoCourses.deleteAll();
	}

	@AfterAll
	public void tearDown() {
		// Clean up

	}
	
	private static final String CATEGORY1_NAME = "Science";
	private static final String CATEGORY2_NAME = "Medical";
	private static final String CATEGORY1_1_NAME = "Mathematics";
	private static final String CATEGORY1_2_NAME = "Physics";
	private static final String CATEGORY2_1_NAME = "Zhong Yi";
	
	@Test
	@Order(1)
	public void testCreateCourseCategories() {
		CourseCategory cat1 = new CourseCategory();
		cat1.setName(CATEGORY1_NAME);
		var c1_1 = new CourseCategory();
		c1_1.setName(CATEGORY1_1_NAME);
		var c1_2 = new CourseCategory();
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
		assertEquals(0, repoCourses.getCoursesCount());
		Course course = new Course();
		var cat1 = repoCategories.findById(firstCourseCategoryId);
		assertNotNull(cat1);
		course.setCategory(cat1);
		course.setName(COURSE1_NAME);
		course.setDescription(COURSE1_DESCRIPTION);
		repoCourses.save(course);

		Long id = course.getId();
		assertEquals(1, repoCourses.getCoursesCount());

		Course c1 = repoCourses.findById(id);
		assertNotNull(c1);
		assertEquals(COURSE1_NAME, c1.getName());
		assertEquals(COURSE1_DESCRIPTION, c1.getDescription());
		assertEquals(0, repoCourses.getChaptersCount());

		var chapters = c1.getChapters();
		assertEquals(0, chapters.size());

		// save the identifier
		firstCourseId = id;
	}

	@Test
	@Order(12)
	public void testAddDifferentCourses() {
		Course course = new Course();
		var cat2 = repoCategories.findById(secondCourseCategoryId);
		assertNotNull(cat2);
		course.setCategory(cat2);
		course.setName(COURSE2_NAME);
		course.setDescription(COURSE2_DESCRIPTION);
		repoCourses.save(course);
		assertEquals(2, repoCourses.getCoursesCount());

		course = new Course();
		course.setCategory(cat2);
		course.setName(COURSE3_NAME);
		course.setDescription(COURSE3_DESCRIPTION);
		repoCourses.save(course);
		assertEquals(3, repoCourses.getCoursesCount());
	}

	@Test
	@Order(13)
	public void testAddChapter1ToCourse1() {
		assertEquals(3, repoCourses.getCoursesCount());
		Course c1 = repoCourses.findById(firstCourseId);
		assertNotNull(c1);
		assertEquals(COURSE1_NAME, c1.getName());
		assertEquals(0, repoCourses.getChaptersCount());

		Chapter chapter = new Chapter();
		chapter.setName(COURSE1_CHAPTER1_NAME);
		chapter.setDescription(COURSE1_CHAPTER1_DESCRIPTION);
		chapter.setCourse(c1);
		c1.getChapters().add(chapter);
		repoCourses.save(c1);
		Long chapId1 = chapter.getId();
		assertEquals(1, repoCourses.getChaptersCount());

		var chapters = c1.getChapters();
		assertEquals(1, chapters.size());

		Chapter chap1 = repoChapters.findById(chapId1);
		assertNotNull(chap1);
		assertEquals(COURSE1_CHAPTER1_NAME, chap1.getName());
		assertEquals(COURSE1_CHAPTER1_DESCRIPTION, chap1.getDescription());

		firstChapterId = chap1.getId();
	}

	@Test
	@Order(14)
	public void testAddMoreChaptersToCourse1() {
		Course c1 = repoCourses.findById(firstCourseId);
		assertNotNull(c1);
		assertEquals(COURSE1_NAME, c1.getName());
		assertEquals(1, repoCourses.getChaptersCount());

		Chapter chapter = new Chapter();
		chapter.setName(COURSE1_CHAPTER2_NAME);
		chapter.setDescription(COURSE1_CHAPTER2_DESCRIPTION);
		chapter.setCourse(c1);
		c1.getChapters().add(chapter);
		repoCourses.save(c1);
		assertEquals(2, repoCourses.getChaptersCount());

		var chapters = c1.getChapters();
		assertEquals(2, chapters.size());

		Chapter chap2 = repoChapters.findById(chapter.getId());
		assertNotNull(chap2);
		assertEquals(COURSE1_CHAPTER2_NAME, chap2.getName());
		assertEquals(COURSE1_CHAPTER2_DESCRIPTION, chap2.getDescription());

		chapter = new Chapter();
		chapter.setName(COURSE1_CHAPTER3_NAME);
		chapter.setDescription(COURSE1_CHAPTER3_DESCRIPTION);
		chapter.setCourse(c1);
		c1.getChapters().add(chapter);
		repoCourses.save(c1);
		assertEquals(3, repoCourses.getChaptersCount());

		chapters = c1.getChapters();
		assertEquals(3, chapters.size());

		Chapter chap3 = repoChapters.findById(chapter.getId());
		assertNotNull(chap3);
		assertEquals(COURSE1_CHAPTER3_NAME, chap3.getName());
		assertEquals(COURSE1_CHAPTER3_DESCRIPTION, chap3.getDescription());

		chapter = new Chapter();
		chapter.setName(COURSE1_CHAPTER4_NAME);
		chapter.setDescription(COURSE1_CHAPTER4_DESCRIPTION);
		chapter.setCourse(c1);
		c1.getChapters().add(chapter);
		repoCourses.save(c1);
		assertEquals(4, repoCourses.getChaptersCount());

		chapters = c1.getChapters();
		assertEquals(4, chapters.size());

		Chapter chap4 = repoChapters.findById(chapter.getId());
		assertNotNull(chap4);
		assertEquals(COURSE1_CHAPTER4_NAME, chap4.getName());
		assertEquals(COURSE1_CHAPTER4_DESCRIPTION, chap4.getDescription());

	}

	@Test
	@Order(15)
	public void testAddLecture1ToChpter1_1() {
		Chapter ch1 = repoChapters.findById(firstChapterId);
		assertNotNull(ch1);

		Lecture lecture = new Lecture();
		lecture.setName(COURSE1_CHAPTER1_LECTURE1_NAME);
		lecture.setDescription(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION);

		lecture.setContent(COURSE1_CHAPTER1_LECTURE1_CONTENT);

		lecture.setChapter(ch1);
		ch1.getLectures().add(lecture);
		repoChapters.save(ch1);
		assertThat(lecture.getId() >= 0);
		assertEquals(1, ch1.getLectures().size());

		Lecture lec1 = repoLectures.findById(lecture.getId());
		assertNotNull(lec1);

		assertEquals(COURSE1_CHAPTER1_LECTURE1_NAME, lec1.getName());
		assertEquals(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION, lec1.getDescription());

		assertEquals(COURSE1_CHAPTER1_LECTURE1_CONTENT, lec1.getContent());
		firstLectureId = lec1.getId();
	}

	@Test
	@Order(16)
	void testAddTest1ToLecture() {
		Lecture lec1 = repoLectures.findById(firstLectureId);
		assertNotNull(lec1);

		assertEquals(COURSE1_CHAPTER1_LECTURE1_NAME, lec1.getName());
		assertEquals(COURSE1_CHAPTER1_LECTURE1_DESCRIPTION, lec1.getDescription());

		LectureTest test = new LectureTest();
		test.setLecture(lec1);
		lec1.setLectureTest(test);
		repoLectures.save(lec1);

		firstTestId = lec1.getId();

		LectureTest test1 = repoTests.findById(firstTestId);
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
		LectureTest test1 = repoTests.findById(firstTestId);
		assertNotNull(test1);
		// Question #1
		TestQuestion question1 = new TestQuestion();
		question1.setText(QUESTION1_TEXT);
		question1.setQuestionType(QuestionType.SINGLE);
		question1.setScore(QUESTION1_SCORE);

		Answer a11 = new Answer();
		a11.setText(QUESTION1_ANSWER1_TEXT);
		a11.setValid(QUESTION1_ANSWER1_ISVALID);
		question1.getAnswers().add(a11);

		Answer a12 = new Answer();
		a12.setText(QUESTION1_ANSWER2_TEXT);
		a12.setValid(QUESTION1_ANSWER2_ISVALID);
		question1.getAnswers().add(a12);

		Answer a13 = new Answer();
		a13.setText(QUESTION1_ANSWER3_TEXT);
		a13.setValid(QUESTION1_ANSWER3_ISVALID);
		question1.getAnswers().add(a13);

		test1.getQuestions().add(question1);
		repoTests.save(test1);

		LectureTest t1 = repoTests.findById(firstTestId);
		assertEquals(1, t1.getQuestions().size());
		assertEquals(3, t1.getQuestions().get(0).getAnswers().size());

		// Question #2
		TestQuestion question2 = new TestQuestion();
		question2.setText(QUESTION2_TEXT);
		question2.setQuestionType(QuestionType.SINGLE);
		question2.setScore(QUESTION2_SCORE);

		Answer a21 = new Answer();
		a21.setText(QUESTION2_ANSWER1_TEXT);
		a21.setValid(QUESTION2_ANSWER1_ISVALID);
		question2.getAnswers().add(a21);

		Answer a22 = new Answer();
		a22.setText(QUESTION2_ANSWER2_TEXT);
		a22.setValid(QUESTION2_ANSWER2_ISVALID);
		question2.getAnswers().add(a22);

		Answer a23 = new Answer();
		a23.setText(QUESTION2_ANSWER3_TEXT);
		a23.setValid(QUESTION2_ANSWER3_ISVALID);
		question2.getAnswers().add(a23);

		Answer a24 = new Answer();
		a24.setText(QUESTION2_ANSWER4_TEXT);
		a24.setValid(QUESTION2_ANSWER4_ISVALID);
		question2.getAnswers().add(a24);

		test1.getQuestions().add(question2);
		repoTests.save(test1);

		t1 = repoTests.findById(firstTestId);
		assertEquals(2, t1.getQuestions().size());
		assertEquals(4, t1.getQuestions().get(1).getAnswers().size());
		
		assertEquals(QUESTION1_SCORE, t1.getQuestions().get(0).getScore());
		assertEquals(QUESTION2_SCORE, t1.getQuestions().get(1).getScore());
		
		assertEquals(QUESTION1_SCORE + QUESTION2_SCORE, t1.getFullScore());
	}
}