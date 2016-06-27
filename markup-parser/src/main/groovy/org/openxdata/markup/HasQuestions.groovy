package org.openxdata.markup

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

@CompileStatic
trait HasQuestions implements IFormElement {

    //Map to speed up question look up
    private Map<String, Object> questionMap = [:]
    private List<IFormElement> elements = []
    private List<HasQuestions> hasQuestions = []


    List<IFormElement> getElements() {
        elements
    }

    List<IFormElement> getElementsWithIds() {
        elements.findAll { IFormElement it -> it.id != null && !it.id.isEmpty()} as List<IFormElement>
    }

    /**
     * Get the first level questions. This method is used mostly by the serializer
     * @return all fist level questions
     */
    List<IQuestion> getQuestions() {
        elements.findAll { it instanceof IQuestion } as List<IQuestion>
    }

    void addElement(IFormElement question) {
        question.setParent(this)
        cacheQuestion(question)
        elements << question
    }

    private void cacheQuestion(IFormElement question) {

        //first store the Container
        if (question instanceof HasQuestions) {
            (hasQuestions as List) << question as HasQuestions
        }

        //cache question for future reference
        def qnBinding = question.binding

        if (!qnBinding) return

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

        parentForm.elementCache[qnBinding] = question


    }


    List<IQuestion> getAllQuestions() {
        def allQuestions = getAllElements { it instanceof IQuestion }
        return allQuestions
    }


    List<IFormElement> getAllElements() {
        getAllElements(Closure.IDENTITY)
    }

    List<IFormElement> getAllElementsWithIds() {
        getAllElements{IFormElement e -> e.id != null && !e.id.isEmpty()}
    }

    List<IQuestion> getAllElements(@ClosureParams(FirstParam.FirstGenericType) Closure filter) {
        def allQuestions = []
        for (IFormElement it in elements) {
            if (filter(it))
                allQuestions.add(it)
            if (it instanceof HasQuestions) {
                def moreQuestions = (it as HasQuestions).getAllElements(filter)
                allQuestions.addAll(moreQuestions)
            }
        }
        return allQuestions
    }


    IFormElement getElement(String binding) {
        def question = questionMap[binding]

        if (question instanceof List) {
            return (question as List)[0] as IQuestion
        }

        if (question) {
            return question as IQuestion
        }

        for (hq in hasQuestions) {
            def qn = hq.getElement(binding)
            if (qn) return qn
        }

        return null
    }

    List<IFormElement> getElements(String binding) {
        List<IFormElement> rt = []
        def question = questionMap[binding]

        if (question instanceof List<IFormElement>) {
            rt.addAll(question as List<IQuestion>)
        }

        if (question) {
            rt << (question as IQuestion)
        }

        for (hq in hasQuestions) {
            def qns = hq.getElements(binding)
            if (qns) rt.addAll(qns)
        }

        return rt.unique() as List<IQuestion>
    }

}
