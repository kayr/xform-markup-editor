package org.openxdata.markup

import org.openxdata.markup.exception.DuplicateQuestionException

import static org.openxdata.markup.Form.*

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Page implements HasQuestions {

    String name
    Study study
    Form form

    List<IQuestion> questions = []

    Page(){}

    Page(String name) {
        this.name = name
    }

    void addQuestion(IQuestion question) {
        question.setParent(form)
        validate(question)
        questions.add(question)
    }

    private void validate(IQuestion question) {
        IQuestion qn = findQuestionWithBinding(question.binding, question.parent)
        if (qn != null) {
            throw new DuplicateQuestionException(question1: question, question2: qn)
        }

        validateSkipLogic(question)
        validateValidationLogic(question)
        validateCalculation(question)
    }



    public String getBinding() {
        return null
    }

    @Override
    String getFullBinding() {
        return null
    }
}
