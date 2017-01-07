package org.openxdata.markup.deserializer

import groovy.xml.Namespace
import groovy.xml.QName
import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.*

/**
 * Created by kay on 1/1/2017.
 */
class ODKDeSerializer {


    private String xml
    private Node xForm
    private Form form
    private def xDataNode, xModel, xBody
    private final
    static
    def COMMON_BIND_ATTRIBUTES = ['jr:constraintMsg', 'id', 'readonly', 'visible', 'action', 'required', 'nodeset', 'type', 'format', 'relevant', 'constraint', 'calculate'] as HashSet
    static def XFORM_NS = new Namespace("http://www.w3.org/2002/xforms"),
               XHTML_NS = new Namespace("http://www.w3.org/1999/xhtml", 'h'),
               JR_NS = new Namespace("http://openrosa.org/javarosa", "jr")

    private Map<String, Map<String, String>> TRANSLATIONS = [:]


    ODKDeSerializer(String xml) {
        this.xml = xml
    }


    Form parse() {
        if (form)
            return form
        xForm = new XmlParser(false, true).parseText(xml)
        extractEssentNodes()
        buildLanguageDictionary()
        toForm()
    }

    private def buildLanguageDictionary() {
        def xITextList = xModel.itext
        if (!xITextList) return
        def xItext = xITextList[0]
        def translations = xItext.translation

        def xDefaultTranslation = translations.find { xItext.@lang?.toLowerCase() in ['english', 'en', 'eng'] }
        if (!xDefaultTranslation) {
            xDefaultTranslation = translations[0]
        }

        loadTranslations('_default', xDefaultTranslation)

        for (xTranslation in translations) {
            if (xTranslation.is(xDefaultTranslation)) continue
            loadTranslations(xTranslation.@lang, xTranslation)
        }
    }

    def loadTranslations(String name, def xTranslation) {
        def translation = [:]
        TRANSLATIONS[name] = translation
        for (xText in xTranslation.text) {
            translation[xText.@id] = xText.value.text()
        }
    }

    def resolveDefaultText(String id) {
        if (!id || !id?.trim()?.startsWith('jr:itext')) return id
        def finalId = id.find(~/('|").*('|")/).replaceAll(/'|"/, '')
        for (kv in TRANSLATIONS) {
            def translation = kv.value[finalId]
            if (translation) return translation
        }
        return id

    }

    private Object extractEssentNodes() {
        //xForm.head.model.instance[0].'*'[0]
        xModel = xForm[XHTML_NS.head].model
        def instance = xModel.instance[0]
        xDataNode = instance.'*'[0]
        xBody = xForm[XHTML_NS.body][0]
        return xDataNode
    }

    private Form toForm() {

        form = new Form()
        form.id = xDataNode.name().localPart
        form.name = xForm[XHTML_NS.head][XHTML_NS.title].text()
        form.dbId = xDataNode.@id

        addLayoutAttributes()
        addDynamicInstances()
        addElements()
        form.allElementsWithIds.each {
            addBehaviourInfo(it)
        }


        addHiddenQuestions()

        return form
    }


    private void addLayoutAttributes() {
        def attrs = xBody.attributes()
        if (attrs)
            form.layoutAttributes.putAll(attrs)
    }

    private def addDynamicInstances() {
        def instances = xModel.instance
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
            dynamicOption.bind = it.@id
            dynamicOption.parentBinding = it.@parent
            dynamicOption.option = it.label.text()
            return dynamicOption
        }
        form.addDynamicOptions(instanceId, options)
    }

    def addHiddenQuestions() {
        def previous = null
        for (bind in xModel.bind) {
            mayBeAddNode(previous, bind)
            previous = bind
        }

    }

    def mayBeAddNode(def previousBind, def bind) {
        String nodeset = bind.@nodeset

        String bindName = XPathUtil.getNodeName(nodeset)

        def element = form.getElement(bindName)

        if (element) return



        def steps = nodeset.split('/')
        if (nodeset.startsWith('/')) steps = steps[1..-1]

        def pathSteps = steps[0..-2]

        def finalParent = pathSteps.inject(form) { HasQuestions acc, String val ->
            if (acc instanceof Form && val == form.id) return form
            def accGroup = acc as HasQuestions
            def group = accGroup.getElement(val)
            if (!group) {
                group = new Page(binding: val, visible: false, name: '')
                accGroup.addElement(group)
                println("### created new group:  [$group.absoluteBinding]")
            }
            return group
        }

        if (finalParent instanceof HasQuestions) {

            def prevQnBindName = XPathUtil.getNodeName(previousBind?.@nodeset)
            def previousBindElement = finalParent.getElement(prevQnBindName)
            def question = createQuestionFromBind(bind)
            if (previousBindElement) {
                finalParent.addAfterElement(previousBindElement, question)
            } else {
                finalParent.addElementAt(0, question)
            }
        } else {
            System.err.println("Could not add [$nodeset] invalid finaParent=[$finalParent.debugString]")
        }
    }

    private IFormElement createQuestionFromBind(def bind) {
        IFormElement t
        if (bind.@type) {
            t = new TextQuestion('...')
        } else {
            t = new Page()
        }
        t.binding = XPathUtil.getNodeName(bind.@nodeset)
        if(t instanceof IQuestion)//set type after setting bing coz its used to get bind node
            t.setType(resolveType(t, null))
        t.setVisible(false)
        addBehaviourInfo(t)

        return t

    }

    private def addElements() {
        addQuestions(form, xBody)
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

        def tagName = qnElem.name().localPart
        def method = "process_$tagName"

        if (this.respondsTo(method)) {
            this."$method"(page, qnElem)
        } else {
            System.err.println("Cannot find tag handler for  [$tagName]")
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
        qn.setType(resolveType(qn, elem))
        return qn
    }

    private IQuestion process_trigger(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(XformType.TRIGGER.value)
        return qn
    }

    private IQuestion process_upload(HasQuestions page, def elem) {
        def qn = addIdMetaInfo new TextQuestion(), page, elem
        qn.setType(resolveType(qn, elem))
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
        return elem.repeat as boolean
    }

    private IQuestion process_Dynamic(HasQuestions page, def elem) {
        def qn = addIdMetaInfo(new DynamicQuestion(), page, elem) as DynamicQuestion
        String nodeSet = elem.itemset[0].@nodeset
        def (dynamicInstanceId, parentQuestionId) = getInstanceAndParentId(nodeSet)
        qn.dynamicInstanceId = dynamicInstanceId
        qn.parentQuestionId = parentQuestionId
        return qn

    }

    private static boolean isDynamicElement(def elem) {
        return elem.itemset.size() > 0
    }

    private def <T extends IFormElement> T addIdMetaInfo(T qn, HasQuestions parent, def elem) {

        //repeats do not have a bind attribute
        if (isRepeatGroup(elem))
            qn.binding = XPathUtil.getNodeName(elem.repeat[0].@nodeset)
        else
            qn.binding = XPathUtil.getNodeName(elem.@ref)




        parent.addElement(qn)

        if (qn instanceof IQuestion) {

            if (!(qn instanceof RepeatQuestion)) {
                def value = findDataNode(qn)?.text()
                if (value)
                    qn.value = value
            }

            qn.text = extractTranslation(elem.label) ?: '...'
            qn.comment = extractTranslation(elem.hint)

        }

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

        def finalNode = steps.inject(xDataNode) { xAcc, val -> xAcc."$val" }

        return finalNode
    }

    private static mayBeAddLayoutAttributes(IFormElement qn, def elem) {
        Map layoutAttributes = elem.attributes()
        if (qn instanceof RepeatQuestion) {
            layoutAttributes = elem.repeat[0].attributes()
        }
        def ignoreAttribs = ['nodeset', 'ref']
        if (elem.name().localPart == 'upload' && elem.@mediatype in ['image/*', 'video/*', 'audio/*'])//todo improve this and probably shift it to the actual question
            ignoreAttribs.add('mediatype')
        nameSpaceAwareCopyInto(qn.layoutAttributes, layoutAttributes, ignoreAttribs, ['jr:count': 'jrcount'])
    }

    /**
     * Simply add the formulas and messages without any validation and processing
     * @param qn the question
     * @param elem the bind element
     * @return the passed question
     */
    private IFormElement addBehaviourInfo(IFormElement qn) {
        def bindNode = getBindNode(qn.binding)
        if (!bindNode) return
        /*todo implement the notion of readonly and not enabled. Readonly question can be populated with data as opposed to disabled*/
        if (qn instanceof IQuestion) {
            mayBeMakeReadOnly(bindNode, qn)
            mayBeAddCalculation(bindNode, qn)
            mayBeMakeRequired(bindNode, qn)
        }

        mayBeAddSkipLogic(bindNode, qn)
        mayBeAddValidationLogic(bindNode, qn)

        Map bindAttributes = bindNode.attributes()
        def questionAttributes = qn.bindAttributes

        nameSpaceAwareCopyInto(questionAttributes, bindAttributes, COMMON_BIND_ATTRIBUTES, ['jr:count': 'jrcount'])

        return qn
    }

    static private void nameSpaceAwareCopyInto(Map questionAttributes, Map bindAttributes,
                                               Collection<String> exclude, Map convertMap = [:]) {
        for (kv in bindAttributes) {
            if (kv.key instanceof QName) {
                def qName = mayBeResolveDefaultQName(kv.key)
                kv = new MapEntry(qName.qualifiedName, kv.value)
            }
            if (!exclude.contains(kv.key)) {
                questionAttributes[convertMap[kv.key] ?: kv.key] = kv.value
            }
        }
    }

    /**
     * This method should help you get the QName with default prefix we use in the whole library.
     * May be in future we can look into changing the prefix at the parser level
     */
    static QName mayBeResolveDefaultQName(QName name) {
        def defaultNS = [XFORM_NS, JR_NS, XHTML_NS].find { it.uri == name.namespaceURI }
        if (defaultNS) {
            return defaultNS.get(name.localPart)
        }
        return name
    }

    private void mayBeAddCalculation(bindNode, IQuestion qn) {
        String calculate = bindNode.@calculate
        if (calculate) {
            qn.calculation = getXPathFormula(calculate)
        }
    }

    private void mayBeAddValidationLogic(bindNode, IFormElement qn) {
        String validationLogic = bindNode.@constraint
        if (validationLogic) {
            qn.validationLogic = getXPathFormula(validationLogic)
            qn.message = resolveDefaultText(bindNode.attributes()[JR_NS.constraintMsg])
        }
    }

    private void mayBeAddSkipLogic(bindNode, IFormElement qn) {
        String skipLogic = bindNode.@relevant
        if (skipLogic) {
            qn.skipLogic = getXPathFormula(skipLogic)
            qn.skipAction = 'show'
        }
    }

    private static void mayBeMakeRequired(bindNode, IQuestion qn) {
        String required = bindNode.@required
        if (required && required.contains('true')) {
            qn.required = true
        }
    }

    private static void mayBeMakeReadOnly(bindNode, IQuestion qn) {
        String enabled = bindNode.@readonly
        if (enabled && enabled.contains('true')) {
            qn.readOnly = true;
        }
    }


    private String getXPathFormula(String xpath) {
        if (!xpath) return null

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

        if (!reference) return reference

        int idx = reference.lastIndexOf('/')
        if (idx < 0) return reference
        def id = XPathUtil.getNodeName(reference)
        if (form.getElement(id))
            return reference.startsWith('/') ? '$' + id : '$:' + id
        return reference

    }

    private String resolveType(IFormElement qn, def layoutElem) {
        def binding = getBindNode(qn.binding)

        if (!binding) return 'string'

        String type = binding.@type?.replaceFirst('xsd:', '')

        switch (type) {
            case 'binary':
                def media = layoutElem?.@mediatype
                return resolveMediaType(media)
            case 'int':
                return XformType.NUMBER.value
            case 'geopoint':
                return XformType.GPS.value
            default:
                return type ?: 'string'
        }
    }

    private Object getBindNode(binding) {
        return xModel.bind.find { XPathUtil.getNodeName(it.@nodeset) == binding }
    }

    static private String resolveMediaType(mediaType) {
        switch (mediaType) {
            case 'image/*':
                return XformType.PICTURE.value
            case 'audio/*':
                return XformType.AUDIO.value
            case 'video/*':
                return XformType.VIDEO.value
            default:
                return XformType.BINARY.value

        }
    }

    private static List getInstanceAndParentId(String nodeset) {
        if (!nodeset) return null

        def ast = XPathUtil.createAST(nodeset)

        def tree = ParserUtils.find(ast) { CommonTree it -> it.type == XPathParser.LITERAL }

        def literalTree = tree.getChild(0) as CommonTree
        def instanceId = literalTree.text.replaceAll("'", '')


        def childPath = ParserUtils.find(ast) { CommonTree it -> it.type == XPathParser.ABSPATH }

        def parentId = XPathUtil.getNodeName(ParserUtils.emitTailString(childPath))

        return [instanceId, parentId]

    }


    private List<Option> getSelectOptions(def select) {
        def items = select.item
        return items.collect { new Option(extractTranslation(it.label) ?: '...', it.value.text()) }
    }

    private String extractTranslation(Object xLabel) {
        def embeddedText = xLabel.text()
        if (embeddedText) return embeddedText

        def ref = xLabel.@ref
        ref = xLabel instanceof List ? ref[0] : ref
        return resolveDefaultText(ref)

    }

}
