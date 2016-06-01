package org.openxdata.markup

trait HasQuestions implements HasIdentifier {

    //Map to speed up question look up
    private Map<String, Object> questionMap = [:]
    private List<IQuestion> questions = []
    private List<HasQuestions> hasQuestions = []


    List<IQuestion> getQuestions() {
        questions
    }

    void addQuestion(IQuestion question) {
        question.setParent(this)
        cacheQuestion(question)
        questions << question
    }

    private void cacheQuestion(IQuestion question) {
        def qnBinding = question.binding

        def existingEntity = questionMap[qnBinding]

        if (existingEntity) {
            if (existingEntity instanceof List) {
                existingEntity << question
            } else {
                questionMap[qnBinding] = [existingEntity] << question
            }
        } else {
            questionMap[qnBinding] = question
        }

        parentForm.questionMap[qnBinding] = question

        if (question instanceof HasQuestions) {
            hasQuestions << question
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


    IQuestion getQuestion(String binding) {
        def question = questionMap[binding]

        if (question instanceof List) {
            return question[0] as IQuestion
        }

        if (question) {
            return question as IQuestion
        }

        for (hq in hasQuestions) {
            def qn = hq.getQuestion(binding)
            if (qn) return qn
        }

    }

    List<IQuestion> getQuestions(String binding) {
        List<IQuestion> rt = []
        def question = questionMap[binding]

        if (question instanceof List) {
            rt.addAll(question)
        }

        if (question) {
            rt << (question as IQuestion)
        }

        for (hq in hasQuestions) {
            def qns = hq.getQuestions(binding)
            if (qns) rt.addAll(qns)
        }

        return rt.unique()
    }


}
