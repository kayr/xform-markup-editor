package org.openxdata.markup.deserializer

import org.openxdata.markup.*

/**
 * Created by kay on 6/7/14.
 */
class XFormDeserializer {

    String xml
    def xForm
    Form form

    XFormDeserializer() {
    }

    XFormDeserializer(String xml) {
        this.xml = xml
    }

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
        def dbId = instance.children()[0].@id
        form.dbId = dbId
        addDynamicInstances()
        addPages()
        form.allQuestions.each {
            addBehaviourInfo(it)
        }
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
            dynamicOption.bind = it.@id.text()
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

    static IQuestion process_select1(HasQuestions page, def elem) {

        if (isDynamicElement(elem))
            return process_Dynamic(page, elem)

        def qn = addIdMetaInfo new SingleSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    static IQuestion process_select(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new MultiSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    IQuestion process_input(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    IQuestion process_upload(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    IQuestion process_group(HasQuestions page, def elem) {
        def qn = new RepeatQuestion(parent: page)
        //add questions after u have set the parent form
        addQuestions(qn, elem.repeat)
        addIdMetaInfo(qn, page, elem)
        return qn
    }

    static IQuestion process_Dynamic(HasQuestions page, def elem) {
        def qn = addIdMetaInfo(new DynamicQuestion(), page, elem) as DynamicQuestion
        String nodeSet = elem.itemset.@nodeset.text()
        qn.parentQuestionId = getDynamicParentInstanceId(nodeSet)
        qn.dynamicInstanceId = getDynamicChildInstanceId(nodeSet)
        return qn

    }

    static boolean isDynamicElement(def elem) {
        return elem.itemset.size() > 0
    }

    static IQuestion addIdMetaInfo(IQuestion qn, HasQuestions parent, def elem) {
        qn.binding = elem.@bind
        qn.text = elem.label.text()
        qn.comment = elem.hint.text()
        parent.addQuestion(qn)
        return qn
    }

    /**
     * Simply add the formulas and messages without any validation and processing
     * @param qn the question
     * @param elem the bind element
     * @return the passed question
     */
    IQuestion addBehaviourInfo(IQuestion qn) {
        def bindNode = getBindNode(qn.binding)
        /*todo implement the notion of readonly and not enabled. Readonly question can be populated with data as opposed to disabled*/
        mayBeMakeLocked(bindNode, qn)
        mayBeMakeReadOnly(bindNode, qn)
        mayBeMakeInvisible(bindNode, qn)
        mayBeMakeRequired(bindNode, qn)

        mayBeAddSkipLogic(bindNode, qn)
        mayBeAddValidationLogic(bindNode, qn)
        mayBeAddCalculation(bindNode, qn)

        return qn
    }

    private void mayBeAddCalculation(bindNode, IQuestion qn) {
        String calculate = bindNode.@calculate.text()
        if (calculate) {
            qn.calculation = getXPathFormula(calculate)
        }
    }

    private void mayBeAddValidationLogic(bindNode, IQuestion qn) {
        String validationLogic = bindNode.@constraint.text()
        if (validationLogic) {
            qn.validationLogic = getXPathFormula(validationLogic)
            qn.message = bindNode.@message.text()
        }
    }

    private void mayBeAddSkipLogic(bindNode, IQuestion qn) {
        String skipLogic = bindNode.@relevant.text()
        if (skipLogic) {
            qn.skipLogic = getXPathFormula(skipLogic)
            qn.skipAction = bindNode.@action.text()
        }
    }

    private static void mayBeMakeRequired(bindNode, IQuestion qn) {
        String required = bindNode.@required.text()
        if (required && required.contains('true')) {
            qn.required = true
        }
    }

    private static void mayBeMakeInvisible(bindNode, IQuestion qn) {
        String visible = bindNode.@visible.text()
        if (visible && visible.contains('false')) {
            qn.visible = false
        }
    }

    private static void mayBeMakeReadOnly(bindNode, IQuestion qn) {
        String enabled = bindNode.@readonly.text()
        if (enabled && enabled.contains('true')) {
            qn.readOnly = true;
        }
    }

    private static String mayBeMakeLocked(bindNode, IQuestion qn) {
        String readonly = bindNode.@locked.text()
        if (readonly && readonly.contains('true')) {
            qn.readOnly = true
        }
        return readonly
    }

    String getXPathFormula(String xpath) {
        if (!xpath) return null

        try {
            def builder = new StringBuilder(xpath)
            def paths = new XPathUtil(xpath).getPathVariables()

            //todo do some caching to improve performance
            paths.inject(0) { Integer offset, Map path ->
                processPath(xpath, builder, path, offset)
            }
            return builder.toString()
        } catch (Exception x) {
            System.err.println("!!!!: Failed to process xpath: [$xpath]: [$x]")
            x.printStackTrace()
            return xpath
        }
    }

    private int processPath(String xpath, StringBuilder builder, Map path, int offset) {
        String pathArea = xpath.substring(path.start, path.end)
        String newPath = getReference(pathArea)
        if (pathArea != newPath) {
            builder.replace(path.start - offset, path.end - offset, newPath)
        }
        return pathArea.size() - newPath.size() + offset
    }

    private String getReference(String reference) {
        int idx = reference.lastIndexOf('/')
        if (idx < 0) return reference
        //todo do not resolve binding that have conflicts
        def id = XPathUtil.getNodeName(reference)
        if (Form.findQuestionWithBinding(id, form))
            return '$' + id
        return reference

    }

    String resolveType(IQuestion qn) {
        def binding = getBindNode(qn.binding)

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

    private Object getBindNode(binding) {
        return xForm.model.bind.find { it.@id == binding }
    }

    static private String resolveMediaType(format) {
        return format == 'image' ? 'picture' : format
    }

    private static String getDynamicChildInstanceId(String nodeset) {
        if (!nodeset) return null

        int pos1 = nodeset.indexOf("'")
        if (pos1 < 0) return null

        int pos2 = nodeset.indexOf("'", pos1 + 1)
        if (pos2 < 0 || (pos1 == pos2)) return null

        return nodeset.substring(pos1 + 1, pos2)
    }

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
