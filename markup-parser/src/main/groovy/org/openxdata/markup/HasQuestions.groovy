package org.openxdata.markup

import groovy.transform.CompileStatic
import groovy.transform.stc.ClosureParams
import groovy.transform.stc.FirstParam

@CompileStatic
trait HasQuestions implements IFormElement {

    //Map to speed up question look up
    private Map<String, Object> elementMap = [:]
    private List<IFormElement> elements = []
    private List<HasQuestions> hasQuestions = []
    XformType xformType


    List<IFormElement> getElements() {
        elements
    }

    List<IFormElement> getElementsWithIds() {
        elements.findAll { IFormElement it -> it.id != null && !it.id.isEmpty() } as List<IFormElement>
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

    void addAfterElement(IFormElement after, IFormElement toAdd) {
        if (after.firstInstanceParent != this) {//the after seems to belong to another instance
            addAfterElement(after.binding, toAdd)
        } else {
            HasQuestions parent = after.parent ?: this
            parent.addAfterElement(after.binding, toAdd)
        }
    }

    void addAfterElement(String after, IFormElement toAdd) {
        def idx = elements.findIndexOf { IFormElement e -> e.binding == after }
        if (idx == -1) {
            addElement(toAdd)
        } else {
            addElementAt(idx + 1, toAdd)
        }
    }


    void addElementAt(int idx, IFormElement question) {
        question.setParent(this)
        cacheQuestion(question)
        elements.add(idx, question)
    }

    List<IQuestion> getAllFirstLevelQuestions() {
        return allFirstLevelElements.findAll { it instanceof IQuestion } as List<IQuestion>

    }

    List<IFormElement> getAllFirstLevelElements() {
        def thisObject = this
        return getAllElements { IFormElement q -> q.firstInstanceParent == thisObject }

    }

    private void cacheQuestion(IFormElement question) {

        //first store the Container
        if (question instanceof HasQuestions) {
            (hasQuestions as List) << question as HasQuestions
        }

        //cache question for future reference
        def qnBinding = question.binding

        if (!qnBinding) return

        def existingEntity = elementMap[qnBinding]

        if (existingEntity) {
            if (existingEntity instanceof List) {
                (existingEntity as List) << question
            } else {
                elementMap[qnBinding] = [existingEntity] << question
            }
        } else {
            elementMap[qnBinding] = question
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

    IFormElement getFirstElementsWithId() {
        def all = getAllElements(true) { IFormElement e -> e.id != null && !e.id.isEmpty() }
        if (all) return all.first()
        return null
    }

    IFormElement getFirstElementsText() {
        def all = getAllElements(true) { IFormElement e -> e.text as boolean }
        if (all) return all.first()
        return null
    }

    List<IFormElement> getAllElementsWithIds() {
        getAllElements { IFormElement e -> e.id != null && !e.id.isEmpty() }
    }

    List<IQuestion> getAllElements(@ClosureParams(FirstParam.FirstGenericType) Closure filter) {
        getAllElements(false, filter)
    }

    List<IQuestion> getAllElements(boolean breakOnFirst, @ClosureParams(FirstParam.FirstGenericType) Closure filter) {
        def allQuestions = []
        for (IFormElement it in elements) {
            if (filter(it)) {
                allQuestions.add(it)
                if (breakOnFirst) break
            }
            if (it instanceof HasQuestions) {
                def moreQuestions = (it as HasQuestions).getAllElements(filter)
                allQuestions.addAll(moreQuestions)
            }
        }
        return allQuestions
    }


    IFormElement getElement(String binding) {
        def element = elementMap[binding]

        if (element instanceof List) {
            return (element as List)[0] as IFormElement
        }

        if (element) {
            return element as IFormElement
        }

        for (hq in hasQuestions) {
            def qn = hq.getElement(binding)
            if (qn) return qn
        }

        return null
    }

    IFormElement getAt(String binding) {
        getElement(binding)
    }

    List<IFormElement> getElements(String binding) {
        List<IFormElement> rt = []
        def question = elementMap[binding]

        if (question instanceof List<IFormElement>) {
            rt.addAll(question as List<IFormElement>)
        } else if (question) {
            rt << (question as IFormElement)
        }

        for (hq in hasQuestions) {
            def qns = hq.getElements(binding)
            if (qns) rt.addAll(qns)
        }

        return rt.unique() as List<IFormElement>
    }

    IFormElement getElementClosestToLine(int lineOfInterest) {
        def elements = getAllElements()

        if (lineOfInterest <= this.line) return this


        def lastElement = elements.last()

        if (lineOfInterest >= lastElement.line) return lastElement

        def elem = elements.find { IFormElement it -> lineOfInterest <= it.line/* > lineOfInterest */ }

        return elem
    }

    boolean isHoldingPagesOnly() { elements.every { it instanceof Page } }

}
