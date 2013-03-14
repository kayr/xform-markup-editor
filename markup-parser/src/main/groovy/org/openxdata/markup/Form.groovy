package org.openxdata.markup

import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Form implements HasQuestions {

    String name
    Study study

    List<Page> pages = []

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
        if (dupPage!=null)
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

    public static String getFullBindingXPath(String xpath, IQuestion question, String logicType = 'XPATH') {
        def variableRegex = /[$][a-z][a-z0-9_]*/

        xpath = xpath.replaceAll(variableRegex) {
            def tmpQn = findQuestionWithBinding(it - '$', question.parent)
            if (!tmpQn)
                throw new ValidationException("$logicType Logic for [$question.text] has an unknown variable $it")

            return tmpQn.fullBinding
        }
        return xpath
    }

    static IQuestion findQuestionWithBinding(String binding, HasQuestions hasQuestions) {

        def dupeQuestion = hasQuestions.questions.find {
            if (it.binding == binding)
                return true

            if (it instanceof HasQuestions)
                return findQuestionWithBinding(it.binding, it)

            return false

        }
        return dupeQuestion
    }

    public String getBinding() {
        return Util.getBindName("${study.name}_${name}_v1")
    }

    @Override
    String getFullBinding() {
        return '/' + binding
    }
}
