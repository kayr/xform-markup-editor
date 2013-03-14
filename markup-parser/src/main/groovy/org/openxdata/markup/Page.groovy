package org.openxdata.markup

import org.openxdata.markup.exception.DuplicateQuestionException
import org.openxdata.markup.exception.ValidationException
import org.openxdata.xpath.XPathParser

import static org.openxdata.markup.Form.getFullBindingXPath
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

    void validateCalculation(IQuestion iQuestion) {
        if (!iQuestion.calculation)
            return

        validateXpath(iQuestion.calculation, iQuestion, 'Calculation')
    }

    void validateValidationLogic(IQuestion question) {

        if (!question.validationLogic)
            return

        if (!question.message)
            throw new ValidationException("Validation message has not been set on question [$question.text]")

        validateXpath(question.validationLogic, question, 'Validation')

    }

    void validateSkipLogic(IQuestion question) {

        if (!question.skipLogic)
            return

        if (!question.skipAction)
            question.skipAction = "enable"

        validateXpath(question.skipLogic, question, 'Skip')
    }

    String validateXpath(String xpath, IQuestion question, String logicType) {
        println "Validating XPATH [$xpath]"
        xpath = getFullBindingXPath(xpath, question, logicType)
        println "Resolved XPATH [$xpath]"

        println "Parsing XPATH for validation"

        try {
            XPathParser parser = Util.createXpathParser(xpath)
            parser.eval()
        } catch (Exception e) {
            throw new ValidationException("Error parsing XPATH[$xpath] $logicType logic for \n [$question.text] \n $e.message", e)
        }
    }

    public String getBinding() {
        return null
    }

    @Override
    String getFullBinding() {
        return null
    }
}
