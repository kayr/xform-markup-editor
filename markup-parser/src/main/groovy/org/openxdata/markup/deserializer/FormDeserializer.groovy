package org.openxdata.markup.deserializer

import org.openxdata.markup.Form
import org.openxdata.markup.IQuestion
import org.openxdata.markup.Page
import org.openxdata.markup.SingleSelectQuestion

/**
 * Created by kay on 6/7/14.
 */
class FormDeserializer {

    String xml
    def xforms
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
        def instance = xforms.model.instance[0]
        form.id = instance.@id
        form.name = instance.'*'[0].@name
        addPages()
        return form
    }

    List<String> getFieldNames() {
        //get the last element inside the first child tag
        def names = xforms.model.instance[0].'*'.'**'
        return names.collect { it.name() }
    }

    List<String> getFieldLabels() {
        def names = xforms.model.instance.'*'.'*'
        return names.collect { it.name() }
    }

    List<String> iterateFieldLabels() {
        def names = xforms.group.'*'
        return names.collect { it.name() }
    }

    List<Page> addPages() {
        def groups = xforms.group
        groups.each {
            def page = toPage(it)
            form.addPage(page)
        }
    }

    Page toPage(def elem) {
        def p = new Page(name: elem.label.text())
        addQuestions(p, elem)
        return p
    }

    List<IQuestion> addQuestions(Page p, def elem) {
        def qnElems = elem.findAll { it.name() != 'label' }
        qnElems.each {
            def name = it.name()
        }
        return qnElems.collect { it.name() }
    }

    IQuestion process_select1(Page page, def elem) {
        def qn = new SingleSelectQuestion()
        addMetaInfo(qn)
    }

    IQuestion process_select(Page page, def elem) {

    }

    IQuestion process_input(Page page, def elem) {

    }

    IQuestion process_group(Page page, def elem) {

    }

    IQuestion addMetaInfo(IQuestion qn, def elem) {
        qn.binding = elem.@id
//        qn.
    }

    String getType(def element) {

    }


    def parseXml() {
        xforms = new XmlSlurper().parseText(xml)
        return this
    }


}
