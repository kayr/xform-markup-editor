package org.openxdata.markup.deserializer

import groovy.json.JsonSlurper
import org.openxdata.markup.*

/**
 * Created by kay on 6/7/14.
 */
class XFormDeserializer {

    private String xml
    private def xForm
    private Form form
    private def model
    private final static def COMMON_BIND_ATTRIBUTES = ['locked', 'readonly', 'visible', 'required', 'relevant', 'constraint', 'calculate'] as HashSet


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

    private Form toForm() {
        def instance = xForm.model.instance[0]
        model = instance.'*'[0]

        form = new Form()
        form.id = instance.@id
        form.name = model.@name
        form.dbId = model.@id
        addDynamicInstances()
        addPages()
        form.allQuestions.each {
            addBehaviourInfo(it)
        }
        return form
    }

    private def addDynamicInstances() {
        def instances = xForm.model.instance
        instances.each {
            if (isDynamicInstanceNode(it)) {
                String instanceId = it.@id
                def dynamicElem = it.dynamiclist
                buildDynamicList(instanceId, dynamicElem)
            }
        }
    }

    private def buildDynamicList(String instanceId, def dynamicElem) {
        def options = dynamicElem.'*'.collect {
            def dynamicOption = new DynamicOption()
            dynamicOption.bind = it.@id.text()
            dynamicOption.parentBinding = it.@parent.text()
            dynamicOption.option = it.label.text()
            return dynamicOption
        }
        form.addDynamicOptions(instanceId, options)
    }

    private def addPages() {
        def groups = xForm.group
        groups.each {
            processPage(it)
        }
    }

    private static boolean isDynamicInstanceNode(def elem) {
        return elem.dynamiclist.size() > 0
    }

    private Page processPage(def group) {
        def page = new Page(name: group.label.text())
        form.addPage(page)
        addQuestions(page, group)
        return page
    }

    private List<IQuestion> addQuestions(HasQuestions page, def elem) {
        def qnElems = elem.'*'.findAll { it.name() != 'label' }
        qnElems.each {
            processQnElem(page, it)
        }
        page.allQuestions
    }

    private IQuestion processQnElem(HasQuestions page, def qnElem) {
        def tagName = qnElem.name()
        def method = "process_$tagName"

        if (this.respondsTo(method)) {
            this."$method"(page, qnElem)
        }
    }

    private IQuestion process_select1(HasQuestions page, def elem) {

        if (isDynamicElement(elem))
            return process_Dynamic(page, elem)

        def qn = addIdMetaInfo new SingleSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    private IQuestion process_select(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new MultiSelectQuestion(), page, elem
        qn.options.addAll(getSelectOptions(elem))
        return qn
    }

    private IQuestion process_input(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    private IQuestion process_upload(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn))
        return qn
    }

    private IQuestion process_group(HasQuestions page, def elem) {
        def qn = new RepeatQuestion(parent: page)
        //add questions after u have set the parent form
        addQuestions(qn, elem.repeat)
        addIdMetaInfo(qn, page, elem)
        return qn
    }

    private IQuestion process_Dynamic(HasQuestions page, def elem) {
        def qn = addIdMetaInfo(new DynamicQuestion(), page, elem) as DynamicQuestion
        String nodeSet = elem.itemset.@nodeset.text()
        qn.parentQuestionId = getDynamicParentInstanceId(nodeSet)
        qn.dynamicInstanceId = getDynamicChildInstanceId(nodeSet)
        return qn

    }

    private static boolean isDynamicElement(def elem) {
        return elem.itemset.size() > 0
    }

    private IQuestion addIdMetaInfo(IQuestion qn, HasQuestions parent, def elem) {
        //repeats do not have a bind attribute
        if (elem.name() == 'group')
            qn.binding = elem.@id
        else
            qn.binding = elem.@bind

        def value = model."$qn.binding".text()
        if (value)
            qn.value = value
        qn.text = elem.label.text()
        qn.comment = elem.hint.text()
        mayBeParseCommentAttribute(qn)


        parent.addQuestion(qn)
        mayBeAddLayoutAttributes(qn, elem)
        return qn
    }

    private static mayBeAddLayoutAttributes(IQuestion qn, def elem) {
        Map layoutAttributes = elem.attributes()
        if (qn instanceof RepeatQuestion) {
            layoutAttributes = elem.repeat[0].attributes()
        }

        layoutAttributes.remove('bind')
        qn.layoutAttributes.putAll(layoutAttributes)
    }

    private static mayBeParseCommentAttribute(IQuestion qn) {

        def jsonComment = qn.comment?.trim()

        if (!(jsonComment?.startsWith('json:'))) {
            return
        }

        jsonComment = Util.replaceFirst(jsonComment, 'json:', '')

        def json = new JsonSlurper().parseText(jsonComment)


        def comment = json.comment
        if (comment) {
            qn.comment = comment
        } else {
            qn.comment = null
        }

        def bindAttributes = json.bind
        if (bindAttributes instanceof Map) {
            for (e in bindAttributes) {
                if (qn.bindAttributes.containsKey(e.key)) continue
                qn.bindAttributes.put(e.key, e.value)
            }
        }

        def layoutAttributes = json.layout
        if (layoutAttributes instanceof Map) {
            for (e in layoutAttributes) {
                if (qn.layoutAttributes.containsKey(e.key)) continue
                qn.layoutAttributes.put(e.key, e.value)
            }
        }
    }

    /**
     * Simply add the formulas and messages without any validation and processing
     * @param qn the question
     * @param elem the bind element
     * @return the passed question
     */
    private IQuestion addBehaviourInfo(IQuestion qn) {
        def bindNode = getBindNode(qn.binding)
        /*todo implement the notion of readonly and not enabled. Readonly question can be populated with data as opposed to disabled*/
        mayBeMakeLocked(bindNode, qn)
        mayBeMakeReadOnly(bindNode, qn)
        mayBeMakeInvisible(bindNode, qn)
        mayBeMakeRequired(bindNode, qn)

        mayBeAddSkipLogic(bindNode, qn)
        mayBeAddValidationLogic(bindNode, qn)
        mayBeAddCalculation(bindNode, qn)

        Map bindAttributes = bindNode.attributes()

        for (kv in bindAttributes) {
            if (!COMMON_BIND_ATTRIBUTES.contains(kv.key)) {
                qn.bindAttributes[kv.key] = kv.value
            }
        }

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

    private String getXPathFormula(String xpath) {
        if (!xpath) return null

//        xpath = xpath.replace('$', '\\$')
        try {
            def builder = new StringBuilder(xpath)
            def paths = new XPathUtil(xpath).getXPathPathVariables()

            //todo do some caching to improve performance
            paths.inject(0) { Integer offset, Map path ->
                int oldSize = builder.size()
                processPath(xpath, builder, path, offset)
                return builder.size() - oldSize + offset
            }
            return builder.toString()
        } catch (Exception x) {
            System.err.println("!!!!: Failed to process xpath: [$xpath]: [$x]")
            x.printStackTrace()
            return xpath
        }
    }

    private void processPath(String xpath, StringBuilder builder, Map path, int offset) {
        String pathArea = xpath.substring(path.start, path.end)
        String newPath = getReference(pathArea)
        if (pathArea != newPath) {
            builder.replace(path.start + offset, path.end + offset, newPath)
        }
    }

    private String getReference(String reference) {
        int idx = reference.lastIndexOf('/')
        if (idx < 0) return reference
        //todo do not resolve binding that have conflicts
        def id = XPathUtil.getNodeName(reference)
        if (Form.findQuestionWithBinding(id, form))
            return reference.startsWith('/') ? '$' + id : '$:' + id
        return reference

    }

    private String resolveType(IQuestion qn) {
        def binding = getBindNode(qn.binding)

        if (!binding) return 'string'

        String type = binding.@type.text()?.replaceFirst('xsd:', '')
        def format = binding.@format

        if (Attrib.types.contains(type) || type?.equalsIgnoreCase('string')) {
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


    private static List<Option> getSelectOptions(def select) {
        def items = select.item
        return items.collect { new Option(it.label.text(), it.@id.text()) }
    }
}
