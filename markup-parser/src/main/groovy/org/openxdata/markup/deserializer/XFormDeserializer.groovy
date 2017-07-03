package org.openxdata.markup.deserializer

import groovy.json.JsonSlurper
import groovy.util.slurpersupport.GPathResult
import groovy.util.slurpersupport.NamespaceAwareHashMap
import org.openxdata.markup.*
import org.openxdata.markup.util.TextParser

/**
 * Created by kay on 6/7/14.
 */
class XFormDeserializer {

    private String      xml
    private GPathResult xForm
    private Form        form
    private def         model
    private final
    static
    def                 COMMON_BIND_ATTRIBUTES = ['locked', 'message', 'id', 'readonly', 'visible', 'action', 'required', 'nodeset', 'type', 'format', 'relevant', 'constraint', 'calculate'] as HashSet
    static def          LAYOUT_NS              = '{https://github.com/kayr/xform-markup-editor#layout}'


    XFormDeserializer() {
    }

    XFormDeserializer(String xml) {
        this.xml = xml
    }

    Form parse() {
        if (form)
            return form
        xForm = new XmlSlurper(false, true).parseText(xml)
        toForm()
    }

    private Form toForm() {
        def instance = xForm.model.instance[0]
        model = instance.'*'[0]



        form = new Form()
        form.id = instance.@id
        form.name = model.@name
        form.dbId = model.@id

        addLayoutAttributes()
        addDynamicInstances()
        addPages()
        form.allElementsWithIds.each {
            addBehaviourInfo(it)
        }
        return form
    }

    private void addLayoutAttributes() {
        def layoutAttributes = (xForm.attributes() as NamespaceAwareHashMap).findResults { k, v ->
            if (k.startsWith(LAYOUT_NS)) {
                return new MapEntry(k.replace(LAYOUT_NS, ''), v)
            }
            return null
        }

        if (layoutAttributes)
            form.layoutAttributes.putAll(layoutAttributes)
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

        if (groups.size() == 1 && groups.@isSynthetic.text()) {
            addQuestions(form, groups[0])
        } else {
            groups.each {
                processPage(form, it)
            }
        }
    }

    private static boolean isDynamicInstanceNode(def elem) {
        return elem.dynamiclist.size() > 0
    }

    private Page processPage(HasQuestions parent, def group) {
        def page = new Page(name: group.label.text())
        addIdMetaInfo(page, parent, group)
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

    private IFormElement processQnElem(HasQuestions page, def qnElem) {
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

    private IFormElement process_group(HasQuestions page, def elem) {

        if (isRepeatGroup(elem)) {
            def qn = new RepeatQuestion(parent: page)
            //add questions after u have set the parent form
            addQuestions(qn, elem.repeat)
            addIdMetaInfo(qn, page, elem)
            return qn
        } else {
            processPage(page, elem)
        }

    }

    private static boolean isRepeatGroup(def elem) {
        return elem.'*'.find { it.name() == 'repeat' }
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

    private <T extends IFormElement> T addIdMetaInfo(T qn, HasQuestions parent, def elem) {
        //repeats do not have a bind attribute
        if (isRepeatGroup(elem))
            qn.binding = elem.@id
        else
            qn.binding = elem.@bind

        if (qn instanceof IQuestion) {

            if (!(qn instanceof RepeatQuestion)) {
                def value = findDataNode(qn)?.text()
                if (value)
                    qn.value = value
            }

            qn.text = elem.label.text()
            qn.comment = elem.hint.text()
            mayBeParseCommentAttribute(qn)
        }


        parent.addElement(qn)
        mayBeAddLayoutAttributes(qn, elem)
        return qn
    }


    def findDataNode(IQuestion qn) {
        def steps = []


        qn.firstInstanceParent
        IFormElement p = qn
        while (p = p.parent) {
            if (p.id && !(p instanceof Form)) steps << p.id
        }

        steps = steps.reverse()
        steps << qn.binding

        def finalNode = steps.inject(model) { xAcc, val -> xAcc."$val" }

        return finalNode
    }

    private static mayBeAddLayoutAttributes(IFormElement qn, def elem) {
        Map layoutAttributes = elem.attributes()
        if (qn instanceof RepeatQuestion) {
            layoutAttributes = elem.repeat[0].attributes()
        }

        if (qn instanceof Page) {
            layoutAttributes.remove('id')
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
    private IFormElement addBehaviourInfo(IFormElement qn) {
        def bindNode = getBindNode(qn.binding)
        /*todo implement the notion of readonly and not enabled. Readonly question can be populated with data as opposed to disabled*/
        if (qn instanceof IQuestion) {
            mayBeMakeLocked(bindNode, qn)
            mayBeMakeReadOnly(bindNode, qn)
            mayBeAddCalculation(bindNode, qn)
        }
        mayBeMakeInvisible(bindNode, qn)
        mayBeMakeRequired(bindNode, qn)

        mayBeAddSkipLogic(bindNode, qn)
        mayBeAddValidationLogic(bindNode, qn)

        mayBeProcessHintAndText(qn)

        Map bindAttributes = bindNode.attributes()

        nameSpaceAwareCopyInto(qn.bindAttributes, bindAttributes, COMMON_BIND_ATTRIBUTES)
//        for (kv in bindAttributes) {
//            if (!COMMON_BIND_ATTRIBUTES.contains(kv.key)) {
//                qn.bindAttributes[kv.key] = kv.value
//            }
//        }

        return qn
    }

    private void mayBeProcessHintAndText(IFormElement qn) {

        if (qn.text) {
            qn.name = getMarkupTemplateText(qn.text)

        }

        if (qn instanceof IQuestion && qn.comment) {
            qn.comment = getMarkupTemplateText(qn.comment)
        }


    }

    private String getMarkupTemplateText(String txt) {
        def tokens = TextParser.parseOxdToken(txt)

        StringBuilder b = new StringBuilder()
        for (t in tokens) {

            if (t.type == TextParser.TextToken.Type.EXPRESSION) {
                b << '{{' + getXPathFormula(t.innerText) + '}}'
            } else {
                b << t.text
            }
        }

        return b.toString()

    }

    def nameSpaceAwareCopyInto(Map target, Map<String, Object> source, Collection excludes, Map keyConversion = [:]) {
//        def hints = xForm.@namespaceTagHints.collectEntries { [it.value, it.key] }

        def hintsField = GPathResult.getDeclaredField('namespaceTagHints')
        hintsField.setAccessible(true)
        def hints = hintsField.get(xForm) as Map<String, String>

        for (kv in source) {
            def finalKey = kv.key
            if (kv.key.startsWith('{')) {
                for (h in hints) {
                    finalKey = finalKey.replace("{${hints[h.key]}}", "$h.key:")
                }
            }

            if (!excludes.contains(finalKey))
                target[keyConversion[finalKey] ?: finalKey] = kv.value


        }
    }

    private void mayBeAddCalculation(bindNode, IQuestion qn) {
        String calculate = bindNode.@calculate.text()
        if (calculate) {
            qn.calculation = getXPathFormula(calculate)
        }
    }

    private void mayBeAddValidationLogic(bindNode, IFormElement qn) {
        String validationLogic = bindNode.@constraint.text()
        if (validationLogic) {
            qn.validationLogic = getXPathFormula(validationLogic)
            qn.message = bindNode.@message.text()
        }
    }

    private void mayBeAddSkipLogic(bindNode, IFormElement qn) {
        String skipLogic = bindNode.@relevant.text()
        if (skipLogic) {
            qn.skipLogic = getXPathFormula(skipLogic)
            qn.skipAction = bindNode.@action.text()
        }
    }

    private static void mayBeMakeRequired(bindNode, IFormElement qn) {
        String required = bindNode.@required.text()
        if (required && required.contains('true')) {
            qn.required = true
        }
    }

    private static void mayBeMakeInvisible(bindNode, IFormElement qn) {
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
        if (form.getElement(id))
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
