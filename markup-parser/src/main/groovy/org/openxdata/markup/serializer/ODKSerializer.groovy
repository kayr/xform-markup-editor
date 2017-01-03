package org.openxdata.markup.serializer

import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder
import org.openxdata.markup.*
import org.openxdata.markup.deserializer.XFormDeserializer

import static java.lang.System.err

/**
 * Created by kay on 7/13/14.
 */
//todo add an extra attribute value e.g appearance numbers @+apperaance + for layout numbers @+ for binds
//todo support for notes @note
//todo support for yes,no, or other
//todo support for ${TEMPLATES}
//todo support for
class ODKSerializer {

    boolean numberQuestions = false
    boolean numberBindings = false
    boolean oxdConversion = false
    boolean addMetaInstanceId = false
    Map<Form, String> xforms = [:]
    Study study
    def studyXML

    ODKSerializer() {}

    ODKSerializer(boolean oxdConversion) {
        this.oxdConversion = oxdConversion
    }

    public String toStudyXml(Study study) {
        def printWriter = new StringWriter();
        def xml = new MarkupBuilder(printWriter)
        xml.setDoubleQuotes(true)

        println "========== Converting study [${study?.name}] to XML"
        xml.study(name: study.name) {

            study.forms.each { form ->
                xml.form(name: form.name) {
                    xml.version(name: 'v1') {
                        xml.xform(toXForm(form))
                    }
                }
            }
        }
        println "========== Done converting study [${study?.name}] ${new Date()}"
        studyXML = printWriter.toString()
        return studyXML
    }

    public String toXForm(Form form) {
        def printWriter = new StringWriter()
        def x = new MarkupBuilder(printWriter)
        x.doubleQuotes = true
        vb(form.binding)

        x.'h:html'(xmlns: 'http://www.w3.org/2002/xforms',
                'xmlns:h': 'http://www.w3.org/1999/xhtml',
                'xmlns:ev': 'http://www.w3.org/2001/xml-events',
                'xmlns:xsd': 'http://www.w3.org/2001/XMLSchema',
                'xmlns:jr': 'http://openrosa.org/javarosa') {

            x.'h:head' {
                'h:title'(form.name)
                x.model {

                    //INSTANCE
                    x.instance {
                        x."${vb form.binding}"(id: form.dbId ?: 0, name: form.name) {
                            buildInstance(x, form)
                            if (addMetaInstanceId && !form.getElement('instanceID')) {
                                x.meta {
                                    x.instanceID()
                                }
                            }
                        }
                    }

                    //DYNAMIC MODEL
                    buildDynamicModel(x, form)

                    //BINDINGS
                    form.allElementsWithIds.each {
                        addBindNode(x, it)
                    }

                    if (addMetaInstanceId) {
                        x.bind(calculate: "concat('uuid:', uuid())", nodeset: "/$form.binding/meta/instanceID", readonly: "true()", type: "string")
                    }
                }
            }

            // CONTROLS AND WIDGETS
            def formAttrs = [:]
            if (form.layoutAttributes) {
                formAttrs += form.layoutAttributes
                if (formAttrs['style'])
                    formAttrs['class'] = formAttrs.remove('style')
            }
            x.'h:body'(formAttrs) {
                form.elements.each {
                    mayBeBuildLayout(x, it)
                }

            }
        }


        def xFormXml = printWriter.toString()
        xforms[form] = xFormXml
        return xFormXml
    }

    private buildInstance(def x, HasQuestions parent) {
        parent.elements.each { q ->
            def _bind = vb binding(q)

            if (!_bind && q instanceof HasQuestions) {
                buildInstance(x, q)
                return
            }

            if (q instanceof HasQuestions) {
                x."$_bind" {
                    buildInstance(x, q)
                }
            } else {
                x."$_bind"(q.value)
            }
        }
    }

    private String binding(IFormElement question) {

        if (!question.binding) return null

        if (numberBindings)
            return question.getBinding(numberBindings)
        return question.binding

    }

    private String getAbsoluteBindingXPath(String xPath, IFormElement question) {
        if (numberBindings)
            return makeXPathCompatible(Form.getIndexedAbsoluteBindingXPath(xPath, question, [allowRelativePath: false]), question)
        return makeXPathCompatible(Form.getAbsoluteBindingXPath(xPath, question, [allowRelativePath: false]), question)
    }

    def makeXPathCompatible(String xPath, IFormElement question) {
        try {
            if (oxdConversion)
                return ODKXpathUtil.makeODKCompatibleXPath(question.parentForm, xPath, numberBindings)
        } catch (Exception x) {
            //ignore
        }
        return xPath
    }

    private String absoluteBinding(IFormElement question) {

        if (!question.binding) return null

        if (numberBindings)
            return question.indexedAbsoluteBinding
        return question.absoluteBinding
    }

    /**
     * Validate a binding and print an error to the console
     * @param bind
     * @return
     */
    private static String vb(String bind) {
        if (bind?.length() > 63)
            err.println "Binding: [$bind] is too long"
        return bind
    }

    private void addBindNode(MarkupBuilder xml, IFormElement question) {

        def map = [id: binding(question), nodeset: absoluteBinding(question)] + question.bindAttributes

        if (question instanceof IQuestion) {
            def type = getQuestionType(question)


            if (type.type) map.type = type.type

            if (question.isRequired()) map.required = "true()"
            if (question.isReadOnly()) map.readonly = "true()"
        }
        //implement visibility in the layout body
        //if (!question.isVisible() ) map.readonly = "true()"

        if (question.skipLogic) {
            def xpath = getAbsoluteBindingXPath(question.skipLogic, question)

            //odk does not support actions so...
            //if we have action = enable and question is disabled remove the readonly Attr
            if (question instanceof IQuestion && question.isReadOnly() && (question.skipAction == 'enable'))
                map.remove('readonly')

            //odk does not have actions so hideif or disableif should be negated
            if (question.skipAction == 'disable' || question.skipAction == 'hide')
                xpath = "not($xpath)"

            map.relevant = xpath
        }

        if (question.validationLogic) {
            def xpath = getAbsoluteBindingXPath(question.validationLogic, question)
            map.constraint = xpath
            map.'jr:constraintMsg' = question.message
        }

        if (question instanceof IQuestion) {
            if (question.calculation) {
                def xpath = getAbsoluteBindingXPath(question.calculation, question)
                map.calculate = xpath
            }

            if (oxdConversion) {
                doPostProcessingForOxd(question, map)
            }
        }



        xml.bind(map)
    }

    static private doPostProcessingForOxd(IQuestion question, Map attr) {
        def binding = question.binding
        def type = question.type
        if (binding == 'endtime' && (type == 'dateTime' || type == 'time')) {
            attr['jr:preload'] = 'timestamp'
            attr['jr:preloadParams'] = 'end'
        }

        if (question instanceof RepeatQuestion && question.validationLogic) {
            def jrCount = ODKXpathUtil.getOXDJRCountOnRepeatValidation(attr.constraint)
            if (jrCount) {
                attr.constraint = "count(.) = $jrCount"
            }
        }

    }

    private void mayBeBuildLayout(MarkupBuilder xml, IFormElement q) {
        // implement visibility here
        // if a question is invisible and has no skipLogic do not render its layout
        if (shouldRenderLayout(q)) return
        buildLayout(xml, q)
    }

    private boolean shouldRenderLayout(HasQuestions q) {
        return q.allQuestions.any { shouldRenderLayout(it) }
    }

    private boolean shouldRenderLayout(IFormElement q) {
        return !q.visible && !q.skipLogic
    }


    private void buildDynamicModel(MarkupBuilder x, Form form) {
        def completeBinds = []
        form.allQuestions.each { question ->
            if (!(question instanceof DynamicQuestion))
                return

            if (completeBinds.contains(question.dynamicInstanceId))
                return

            x.instance(id: question.dynamicInstanceId) {
                completeBinds << question.dynamicInstanceId
                x.dynamiclist {

                    List<DynamicOption> options = question.options
                    options.each { option ->
                        x.item(id: option.bind, parent: option.parentBinding) {
                            x.label(option.child)
                            x.value(option.bind)
                        }
                    }
                }
            }
        }
    }

    private void buildLayout(MarkupBuilder x, IQuestion question) {
        def qnType = getQuestionType(question)
        def inputAttrs = [ref: absoluteBinding(question)] + question.layoutAttributes

        if (question.type == 'boolean') {
            x."select1"(inputAttrs) {
                buildQuestionLabelAndHint(x, question)
                x.item {
                    label('Yes')
                    value('true')
                }
                x.item {
                    label('No')
                    value('false')
                }
            }
        } else if (qnType.type == 'binary') {
            inputAttrs['mediatype'] = "${qnType.format}/*"
            x.upload(inputAttrs) {
                buildQuestionLabelAndHint(x, question)
            }
        } else {

            //add external app from the question hint
            def externalApp = mayBeExternalApp(question.comment)
            if (oxdConversion && externalApp) {
                inputAttrs['appearance'] = externalApp
            }
            x.input(inputAttrs) {
                buildQuestionLabelAndHint(x, question)
            }
        }
    }

    @CompileStatic
    static String mayBeExternalApp(String display) {
        if (display?.trim()?.startsWith('app:'))
            return display.replaceFirst('app:', '')
        return ""
    }


    private void buildLayout(MarkupBuilder xml, DynamicQuestion question) {
        def inputAttrs = [ref: absoluteBinding(question)] + question.layoutAttributes
        xml.select1(inputAttrs) {
            buildQuestionLabelAndHint(xml, question)
            xml.itemset(nodeset: "instance('$question.dynamicInstanceId')/dynamiclist/item[@parent=${getDynamicParentQnId(question)}]") {
                xml.value(ref: 'value')
                xml.label(ref: 'label')
            }
        }
    }

    private void buildLayout(MarkupBuilder xml, ISelectionQuestion question) {

        def inputAttrs = [ref: absoluteBinding(question)] + question.layoutAttributes
        def selectRef = question instanceof SingleSelectQuestion ? '1' : ''
        xml."select$selectRef"(inputAttrs) {
            buildQuestionLabelAndHint(xml, question)
            question.options.each { option ->
                xml.item {
                    xml.label(option.text)
                    xml.value(option.bind)
                }
            }
        }
    }

    private void buildLayout(MarkupBuilder xml, RepeatQuestion question) {

        if (!question.isVisible()) return

        def repeatAttributes = [nodeset: absoluteBinding(question)]
        if (oxdConversion) {
            //oxd uses validation to control size of repeat while odk uses jr:count
            def logic = question.getValidationLogic()
            if (logic) {
                logic = getAbsoluteBindingXPath(logic, question)
                def jrCount = ODKXpathUtil.getOXDJRCountOnRepeatValidation(logic)
                if (jrCount) repeatAttributes['jr:count'] = jrCount
            }

        }

        //serialize layout attributes to the group
        def layoutAttributes = ([:] + question.layoutAttributes)
        def manualJRCount = layoutAttributes.remove('jr:count')
        if (manualJRCount) {
            err.println("A manual jr:count($manualJRCount) was found on [$question.absoluteBinding] and therefore ignored")
        }
        layoutAttributes.remove('nodeset')
        String removedJR = layoutAttributes.remove('jrcount')

        if (removedJR) {
            repeatAttributes['jr:count'] = getAbsoluteBindingXPath(removedJR, question)
        }

        repeatAttributes.putAll(layoutAttributes)

        xml.group/*(groupLayoutAttributes)*/ { //do not add any layout attributes to the group we do not need them
            buildQuestionLabelAndHint(xml, question)
            xml.repeat(repeatAttributes) {
                for (child in question.elements) {
                    mayBeBuildLayout(xml, child)
                }
            }
        }
    }

    private void buildLayout(MarkupBuilder xml, Page page) {

        if (!page.isVisible()) return



        def attr = page.id ? [ref: absoluteBinding(page)] : [:]
        attr = attr + page.layoutAttributes

        xml.group(attr) {
            buildQuestionLabelAndHint(xml, page)
            page.elements.each { e ->
                mayBeBuildLayout(xml, e)
            }
        }
    }

    static String oxd2Odk(String oxdXform, boolean addMetaInstanceId = false) {
        def oxdFormObj = new XFormDeserializer(oxdXform).parse()
        ODKSerializer serializer = new ODKSerializer(true)
        serializer.addMetaInstanceId = addMetaInstanceId
        return serializer.toXForm(oxdFormObj)
    }


    private String getDynamicParentQnId(DynamicQuestion question) {
        if (numberBindings)
            return question.indexedAbsParentBinding
        return question.absParentBinding
    }

    private void buildQuestionLabelAndHint(MarkupBuilder xml, IFormElement element) {

        def label = element.getText(numberQuestions)
        xml.label(label)
        if (element instanceof IQuestion) {
            def hasExternalApp = oxdConversion && element.type == 'string' && mayBeExternalApp(element.comment)
            if (element.comment && !hasExternalApp)
                xml.hint(element.comment)
        }
    }

    private static Map getQuestionType(IQuestion question) {
        switch (question.type) {
            case 'video':
                return [type: 'binary', format: 'video']
            case 'picture':
                return [type: 'binary', format: 'image']
            case 'audio':
                return [type: 'binary', format: 'audio']
            case 'number':
                return [type: 'int']
            case 'gps':
                return [type: 'geopoint', format: 'gps']
            case 'repeat':
                return [:]
            case 'longtext':
                return [type: 'string']
            case 'boolean':
                return [type: 'string']
            default:
                return [type: "$question.type"]

        }
    }


}
