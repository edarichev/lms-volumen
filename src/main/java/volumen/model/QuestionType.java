package volumen.model;

public enum QuestionType {
	SINGLE, // the user must select only 1 variant (may be radio buttons or drop-down list)
	MULTIPLE, // checkboxes
	REORDER, // the user must reorder the answers
	TEXT, // the user must enter text
}
