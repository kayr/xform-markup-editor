package org.openxdata.markup.deserializer

import org.openxdata.markup.*

/**
 * Created by kay on 6/7/14.
 */
class FormDeserializer {

    String xml
    def xForm
    Form form

    Form parse() {
        if (form)
            return form
        parseXml()
        toForm()
        return form
    }

    Form toForm() {
        form = new Form()
        def instance = xForm.model.instance[0]
        form.id = instance.@id
        form.name = instance.'*'[0].@name
        addPages()
        return form
    }

    def addPages() {
        def groups = xForm.group
        groups.each {
            processPage(it)
        }
    }

    Page processPage(def group) {
        def page = new Page(name: group.label.text())
        form.addPage(page)
        addQuestions(page, group)
        return page
    }

    List<IQuestion> addQuestions(HasQuestions page, def elem) {
        def qnElems = elem.'*'.findAll { it.name() != 'label' }
        qnElems.each {
            processQnElem(page, it)
        }
        page.allQuestions
    }

    IQuestion processQnElem(HasQuestions page, def qnElem) {
        def tagName = qnElem.name()
        def method = "process_$tagName"

        if (this.respondsTo(method)) {
            def qn = this."$method"(page, qnElem)
        }
    }

    IQuestion process_select1(HasQuestions page, def elem) {
        def qn = addMetaInfo new SingleSelectQuestion(), page, elem
        return qn
    }

    IQuestion process_select(HasQuestions page, def elem) {
        def qn = addMetaInfo new MultiSelectQuestion(), page, elem
        return qn
    }

    IQuestion process_input(HasQuestions page, def elem) {
        def qn = addMetaInfo new TextQuestion(), page, elem
        return qn
    }

    IQuestion process_group(HasQuestions page, def elem) {
        def qn = new RepeatQuestion(parent: page)
        addQuestions(qn, elem.repeat)
        addMetaInfo qn, page, elem
        return qn
    }

    IQuestion addMetaInfo(IQuestion qn, HasQuestions parent, def elem) {
        qn.binding = elem.@id
        qn.text = elem.label.text()
        qn.comment = elem.hint.text()
        parent.addQuestion(qn)
        return qn
    }

    String getType(def element) {

    }


    def parseXml() {
        xForm = new XmlSlurper().parseText(xml)
        return this
    }


}
