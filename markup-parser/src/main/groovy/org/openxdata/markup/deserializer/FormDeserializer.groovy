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
        xForm = new XmlSlurper().parseText(xml)
        toForm()
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
            this."$method"(page, qnElem)
        }
    }

    IQuestion process_select1(HasQuestions page, def elem) {
        def qn = addMetaInfo new SingleSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    IQuestion process_select(HasQuestions page, def elem) {
        def qn = addMetaInfo new MultiSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    IQuestion process_input(HasQuestions page, def elem) {
        def qn = addMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    IQuestion process_upload(HasQuestions page, def elem) {
        def qn = addMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    IQuestion process_group(HasQuestions page, def elem) {
        def qn = new RepeatQuestion(parent: page)
        addQuestions(qn, elem.repeat)
        addMetaInfo(qn, page, elem)
        return qn
    }

    IQuestion addMetaInfo(IQuestion qn, HasQuestions parent, def elem) {
        qn.binding = elem.@bind
        qn.text = elem.label.text()
        qn.comment = elem.hint.text()
        parent.addQuestion(qn)
        return qn
    }

    String resolveType(IQuestion qn) {
        def binding = xForm.model.bind.find { it.@id == qn.binding }

        if (!binding) return 'string'

        def type = binding.@type.text()?.replaceFirst('xsd:', '')
        def format = binding.@format



        if (Attrib.types.contains(type)) {
            return format?.isEmpty() ? type : format;
        }

        switch (type) {
            case 'base64Binary':
                return resolveMediaType(format)
            case 'int':
                return 'number'
            default:
                return type ?: 'string'

        }
    }

    static private String resolveMediaType(format) {
        return format == 'image' ? 'picture' : format
    }


    static List<Option> getSelectOptions(def select) {
        def items = select.item
        return items.collect { new Option(it.label.text(), it.@id.text()) }
    }
}
