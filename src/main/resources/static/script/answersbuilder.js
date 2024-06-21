/**
 * Answers builder for test question
 * 
 * Usage:
 * 
    var builder = new AnswersBuilder("answers_builder", 
        {'orderNumberFieldCSSClass' : "answer-order-number-field",
        'answerTextFieldCSSClass' : "answer-text-field",
        'trueFalseCheckBoxCSSClass' : "true-false-checkbox",
        'tableCSSClass' : "ansers-builder-table"
        }
        );
    builder.addAnswer(6, "The answer", true);
 */
class AnswersBuilder {
    constructor(containerId, parameters) {
        this.tableName = containerId + "_answers_builder_table";
        this.rowIdPrefix = this.tableName + "answer_row_";
        this.nextId = 0; // for id generation

        this.numberFieldPrefix = this.rowIdPrefix + "_order_number_";
        this.answerTextFieldPrefix = this.rowIdPrefix + "_answer_text_";
        this.trueFalseFieldPrefix = this.rowIdPrefix + "_true_false_";
        this.hiddenIdPrefix = this.rowIdPrefix + "_hidden_id_";

        // css
        this.orderNumberFieldCSSClass = "";
        this.answerTextFieldCSSClass = "";
        this.trueFalseCheckBoxCSSClass = "";
        this.tableCSSClass = "";


        this.getNextOrderNumber = function() {
            var tbodyRef = document.getElementById(this.tableName).getElementsByTagName('tbody')[0];
            var rows = tbodyRef.getElementsByTagName('tr');
            if (rows.length == 0)
                return 1;

            // find max of input=number fields and add 1
            var maxValue = 0; // or NaN, but we must always start from non-negative numbers
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                var numberCell = row.getElementsByTagName('td')[0];
                var inputField = numberCell.getElementsByTagName('input')[1];
                var num = Number.parseInt(inputField.value);
                var num = isNaN(num) ? 0 : num;
                if (maxValue < num)
                    maxValue = num;
            }
            return maxValue + 1;
        };

        this.addAnswer = function(aQuestionId, anOrderNumber, anAnswer, aTrue) {
            var tbodyRef = document.getElementById(this.tableName).getElementsByTagName('tbody')[0];
            this.nextId++;
            var nextNum = this.getNextOrderNumber();
            // row
            var rowId = this.rowIdPrefix + this.nextId;
            var newRow = tbodyRef.insertRow();
            newRow.id = rowId;
            // cells
            var cellNumber = newRow.insertCell();
            var cellAnswerText = newRow.insertCell();
            var cellTrue = newRow.insertCell();
            var cellAction = newRow.insertCell();
            // optional identifier
            var hiddenIdentifier = this.hiddenIdPrefix + this.nextId;
            var hiddenId = document.createElement('input');
            hiddenId.type = 'hidden';
            hiddenId.id = hiddenIdentifier;
            hiddenId.name = hiddenIdentifier;
            hiddenId.value = aQuestionId;
            cellNumber.appendChild(hiddenId);
            // add input fields
            // order number
            var orderNumberInputId = this.numberFieldPrefix + this.nextId;
            var inputOrderNumber = document.createElement("input");
            inputOrderNumber.type = "number";
            inputOrderNumber.name = orderNumberInputId;
            inputOrderNumber.id = orderNumberInputId;
            inputOrderNumber.className = this.orderNumberFieldCSSClass;
            inputOrderNumber.value = nextNum;
            cellNumber.appendChild(inputOrderNumber);
            if (anOrderNumber !== undefined)
                inputOrderNumber.value = anOrderNumber;

            // answer text
            var answerTextAreaId = this.answerTextFieldPrefix + this.nextId;
            var answerTextArea = document.createElement("textarea");
            answerTextArea.id = answerTextAreaId;
            answerTextArea.name = answerTextAreaId;
            answerTextArea.className = this.answerTextFieldCSSClass;
            cellAnswerText.appendChild(answerTextArea);
            if (anAnswer !== undefined)
                answerTextArea.value = anAnswer;

            // true/false
            var trueFalseInputId = this.trueFalseFieldPrefix + this.nextId;
            var trueFalse = document.createElement("input");
            trueFalse.type = "checkbox";
            trueFalse.name = trueFalseInputId;
            trueFalse.id = trueFalseInputId;
            trueFalse.className = this.trueFalseCheckBoxCSSClass;
            cellTrue.appendChild(trueFalse);
            if (aTrue != undefined)
                trueFalse.checked = aTrue;

            // "delete row" button
            var deleteRowButton = document.createElement("a");
            deleteRowButton.href = "#";
            deleteRowButton.innerText = "Удалить";
            deleteRowButton.onclick = function(e) {
                if (confirm("Удалить этот вариант?")) {
                    var ownerRow = this.parentNode.parentNode;
                    var ownerTableBody = ownerRow.parentNode;
                    ownerTableBody.removeChild(ownerRow);
                }
            };
            cellAction.appendChild(deleteRowButton);
        };


        this.getAnswers = function() {
            var tbodyRef = document.getElementById(this.tableName).getElementsByTagName('tbody')[0];
            var rows = tbodyRef.getElementsByTagName('tr');
            if (rows.length == 0)
                return "";
            var arr = new Array();
            for (var i = 0; i < rows.length; ++i) {
                var row = rows[i];
                var cells = row.getElementsByTagName('td');
                // order number
                var numberCell = cells[0];
                var inputField = numberCell.getElementsByTagName('input')[1];
                var orderNumber = Number.parseInt(inputField.value);
                var orderNumber = isNaN(orderNumber) ? 0 : orderNumber;
                // id in same cell
                var hiddenIdField = numberCell.getElementsByTagName('input')[0];
                var id = Number.parseInt(hiddenIdField.value);
                var id = isNaN(id) ? -1 : id;
                // answer text
                var answerCell = cells[1];
                var answerField = answerCell.getElementsByTagName('textarea')[0];
                var answer = answerField.value;
                // is true?
                var trueCell = cells[2];
                var trueField = trueCell.getElementsByTagName('input')[0];
                var isTrue = trueField.checked;
                var obj = { 'id' : id, 'sequenceNumber': orderNumber, 'answer': answer, 'valid': isTrue };
                arr.push(obj);
            }
            return arr;
        };
        
        this.serialize = function(questionId, questionText, questionType) {
            var questionDTO = {
                'id' : questionId,
                'text' : questionText,
                'questionType' : questionType,
                'answers' : this.getAnswers() 
            };
            return questionDTO;
        }

        // create content
        var table = document.createElement('table');
        table.id = this.tableName;
        table.name = this.tableName;
        if (parameters) {
            table.className = parameters['tableCSSClass'];
            this.answerTextFieldCSSClass = parameters['answerTextFieldCSSClass'];
            this.orderNumberFieldCSSClass = parameters['orderNumberFieldCSSClass'];
            this.tableCSSClass = parameters['tableCSSClass'];
            this.trueFalseCheckBoxCSSClass = parameters['trueFalseCheckBoxCSSClass']; 
        }
        // header
        var thead = table.createTHead();
        var headerRow = thead.insertRow(0);

        var th = document.createElement('th');
        headerRow.appendChild(th);
        th.style.whiteSpace = 'nowrap';
        th.innerText = "№ п/п";

        th = document.createElement('th');
        headerRow.appendChild(th);
        th.innerText = "Текст ответа";
        th.style.whiteSpace = 'nowrap';
        th.style.width = '90%';

        th = document.createElement('th');
        headerRow.appendChild(th);
        th.innerText = "Ответ верный?";
        th.style.whiteSpace = 'nowrap';

        th = document.createElement('th');
        headerRow.appendChild(th);
        th.innerText = "Действия";

        var tbody = table.createTBody();

        // footer
        var tfoot = table.createTFoot();
        var footRow = tfoot.insertRow();
        var footCell = footRow.insertCell();
        footCell.colSpan = 4;
        footCell.style.textAlign = 'center';

        var btnAddAnswer = document.createElement('input');
        btnAddAnswer.type = 'button';
        btnAddAnswer.value = 'Добавить вариант ответа';
        btnAddAnswer.builderObject = this;
        btnAddAnswer.onclick = function(e) {
            this.builderObject.addAnswer();
        };
        footCell.appendChild(btnAddAnswer);

        // add the result table
        document.getElementById(containerId).appendChild(table);
    }
} // AnswersBuilder
