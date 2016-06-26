package org.openxdata.markup

import groovy.transform.CompileStatic

@CompileStatic
trait HasQuestions implements IFormElement {

    //Map to speed up question look up
    private Map<String, Object> questionMap = [:]
    private List<IFormElement> questions = []
    private List<HasQuestions> hasQuestions = []


    List<IFormElement> getQuestions() {
        questions
    }

    void addQuestion(IFormElement question) {
        question.setParent(this)
        cacheQuestion(question)
        questions << question
    }

    private void cacheQuestion(IFormElement question) {
        def qnBinding = question.binding

        def existingEntity = questionMap[qnBinding]

        if (existingEntity) {
            if (existingEntity instanceof List) {
                (existingEntity as List) << question
            } else {
                questionMap[qnBinding] = [existingEntity] << question
            }
        } else {
            questionMap[qnBinding] = question
        }

        parentForm.questionMap[qnBinding] = question

        if (question instanceof HasQuestions) {
            (hasQuestions as List) << question as HasQuestions
        }
    }


    List<IQuestion> getAllQuestions() {
        def allQuestions = []
        questions.each {
            allQuestions.add(it)
            if (it instanceof HasQuestions) {
                def moreQuestions = it.getAllQuestions()
                allQuestions.addAll(moreQuestions)
            }
        }
        return allQuestions
    }


    IFormElement getQuestion(String binding) {
        def question = questionMap[binding]

        if (question instanceof List) {
            return (question as List)[0] as IQuestion
        }

        if (question) {
            return question as IQuestion
        }

        for (hq in hasQuestions) {
            def qn = hq.getQuestion(binding)
            if (qn) return qn
        }

        return null

    }

    List<IFormElement> getQuestions(String binding) {
        List<IFormElement> rt = []
        def question = questionMap[binding]

        if (question instanceof List<IFormElement>) {
            rt.addAll(question as List<IQuestion>)
        }

        if (question) {
            rt << (question as IQuestion)
        }

        for (hq in hasQuestions) {
            def qns = hq.getQuestions(binding)
            if (qns) rt.addAll(qns)
        }

        return rt.unique() as List<IQuestion>
    }


}
