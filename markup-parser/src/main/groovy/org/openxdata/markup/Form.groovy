package org.openxdata.markup

import org.openxdata.markup.exception.ValidationException
import org.openxdata.xpath.XPathParser

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Form implements HasQuestions {

    String name
    String id
    Study study

    List<Page> pages = []

    Map<String, List<DynamicOption>> dynamicOptions = [:]

    Form(String name) {
        this.name = name
    }

    List<IQuestion> getQuestions() {
        def questions = []
        pages.each {
            it.questions.each {questions << it}
        }
        return questions
    }

    void addPage(Page page) {
        def dupPage = pages.find {it.name.equalsIgnoreCase(page.name)}
        if (dupPage != null)
            throw new ValidationException("Duplicate pages[$page.name] found in form [$name]");
        page.setForm(this)
        pages << page
    }

    void addQuestion(IQuestion question) {
        if (pages.isEmpty()) {
            def page = new Page("Page1")
            addPage(page)
        }
        question.setParent(this)
        pages[0].addQuestion(question)
    }

    void validate(){
        allQuestions.each {
            validateSkipLogic(it)
            validateCalculation(it)
            validateValidationLogic(it)
            if(it instanceof DynamicQuestion)
                it.validate()
        }
    }

    public static String getFullBindingXPath(String xpath, IQuestion question, String logicType = 'XPATH') {
        def variableRegex = /[$][a-z][a-z0-9_]*/

        xpath = xpath.replaceAll(variableRegex) {
            def tmpQn = findQuestionWithBinding(it - '$', question.parent)
            if (!tmpQn)
                throw new ValidationException("$logicType Logic for [$question.text] has an unknown variable $it")

            return tmpQn.fullBinding
        }
        xpath = xpath.replace('$.',question.fullBinding)
        return xpath
    }

    static IQuestion findQuestionWithBinding(String binding, HasQuestions hasQuestions) {

        if (hasQuestions instanceof IQuestion)
            hasQuestions = hasQuestions.parent

        def dupeQuestion = findQuestion(binding, hasQuestions)

        return dupeQuestion
    }

    static IQuestion findQuestion(String binding, HasQuestions hasQuestions) {
        def dupeQuestion = hasQuestions.questions.findResult {

            if (it.binding == binding)
                return it

            if (it instanceof HasQuestions)
                return findQuestion(binding, it)

            return null
        }
        return dupeQuestion
    }

    List<IQuestion> getAllQuestions() {
        def allQuestions = extractQuestion(this)
        return allQuestions
    }

    List<IQuestion> extractQuestion(HasQuestions questions) {
        def allQuestions = []
        questions.questions.each {
            allQuestions.add(it)
            if (it instanceof RepeatQuestion) {
                def moreQuestions = extractQuestion(it)
                allQuestions.addAll(moreQuestions)
            }
        }
        return allQuestions
    }

    static void validateCalculation(IQuestion iQuestion) {
        if (!iQuestion.calculation)
            return

        validateXpath(iQuestion.calculation, iQuestion, 'Calculation')
    }

    static void validateValidationLogic(IQuestion question) {

        if (!question.validationLogic)
            return

        if (!question.message)
            throw new ValidationException("Validation message has not been set on question [$question.text]")

        validateXpath(question.validationLogic, question, 'Validation')

    }

    static void validateSkipLogic(IQuestion question) {

        if (!question.skipLogic)
            return

        if (!question.skipAction)
            question.skipAction = "enable"

        validateXpath(question.skipLogic, question, 'Skip')
    }

    static String validateXpath(String xpath, IQuestion question, String logicType) {
        xpath = getFullBindingXPath(xpath, question, logicType)
        try {
            XPathParser parser = Util.createXpathParser(xpath)
            parser.eval()
        } catch (Exception e) {
            throw new ValidationException("Error parsing XPATH[$xpath] $logicType logic for \n [$question.text] \n $e.message", e)
        }
    }

    public String getBinding() {
        if(id == null)
            id = Util.getBindName("${study.name}_${name}_v1")
        return id
    }

    @Override
    String getFullBinding() {
        return '/' + binding
    }

    @Override
    Form getParentForm() {
        return this
    }
}
