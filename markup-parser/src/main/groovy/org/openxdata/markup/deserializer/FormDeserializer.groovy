package org.openxdata.markup.deserializer

import groovy.transform.CompileStatic
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
        addDynamicInstances()
        addPages()
        return form
    }

    def addDynamicInstances() {
        def instances = xForm.model.instance
        instances.each {
            if (isDynamicInstanceNode(it)) {
                String instanceId = it.@id
                def dynamicElem = it.dynamiclist
                buildDynamicList(instanceId, dynamicElem)
            }
        }
    }

    def buildDynamicList(String instanceId, def dynamicElem) {
        def options = dynamicElem.'*'.collect {
            def dynamicOption = new DynamicOption()
            dynamicOption.bind =it.@id.text()
            dynamicOption.parentBinding = it.@parent.text()
            dynamicOption.option = it.label.text()
            return dynamicOption
        }
        form.addDynamicOptions(instanceId, options)
    }

    def addPages() {
        def groups = xForm.group
        groups.each {
            processPage(it)
        }
    }

    static boolean isDynamicInstanceNode(def elem) {
        return elem.dynamiclist.size() > 0
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

        if (isDynamicElement(elem))
            return process_Dynamic(page, elem)

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

    IQuestion process_Dynamic(HasQuestions page, def elem) {
        def qn = addMetaInfo(new DynamicQuestion(), page, elem) as DynamicQuestion
        String nodeSet = elem.itemset.@nodeset.text()
        qn.parentQuestionId = getDynamicParentInstanceId(nodeSet)
        qn.dynamicInstanceId = getDynamicChildInstanceId(nodeSet)
        return qn

    }

    static boolean isDynamicElement(def elem) {
        return elem.itemset.size() > 0
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

    @CompileStatic
    private static String getDynamicChildInstanceId(String nodeset) {
        if (!nodeset) return null

        int pos1 = nodeset.indexOf("'")
        if (pos1 < 0) return null

        int pos2 = nodeset.indexOf("'", pos1 + 1)
        if (pos2 < 0 || (pos1 == pos2)) return null

        return nodeset.substring(pos1 + 1, pos2)
    }

    @CompileStatic
    private static String getDynamicParentInstanceId(String nodeset) {
        if (!nodeset) return null

        int pos1 = nodeset.lastIndexOf('/')
        if (pos1 < 0) return null

        int pos2 = nodeset.lastIndexOf(']')
        if (pos2 < 0 || (pos1 == pos2)) return null

        return nodeset.substring(pos1 + 1, pos2)
    }


    static List<Option> getSelectOptions(def select) {
        def items = select.item
        return items.collect { new Option(it.label.text(), it.@id.text()) }
    }
}
