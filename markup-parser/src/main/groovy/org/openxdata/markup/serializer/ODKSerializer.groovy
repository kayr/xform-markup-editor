package org.openxdata.markup.serializer

import groovy.xml.MarkupBuilder
import org.openxdata.markup.*

import static java.lang.System.err

/**
 * Created by kay on 7/13/14.
 */
//todo add an extra attribute value e.g appearance numbers @+apperaance numbers
class ODKSerializer {

    boolean numberQuestions = false
    boolean numberBindings = false
    Map<Form, String> xforms = [:]
    Study study
    def studyXML

    ODKSerializer() {}

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

            'h:head' {
                'h:title'(form.name)
                model {

                    // INSTANCE
                    instance {
                        x."${vb form.binding}"(id: form.dbId ?: 0, name: form.name) {
                            buildInstance(x, form)
                        }
                    }

                    //DYNAMIC MODEL
                    buildDynamicModel(x, form)

                    //BINDINGS
                    form.allQuestions.each {
                        addBindNode(x, it)
                    }
                }
            }

            // CONTROLS AND WIDGETS
            'h:body' {
                form.pages.eachWithIndex { page, idx ->
                    x.group {
                        x.label(page.name)
                        buildQuestionsLayout(page, x)
                    }
                }
            }
        }


        def xFormXml = printWriter.toString()
        xforms[form] = xFormXml
        return xFormXml
    }

    private buildInstance(def x, HasQuestions parent) {
        parent.questions.each { q ->
            def _bind = vb binding(q)
            if (q instanceof HasQuestions) {
                x."$_bind" {
                    buildInstance(x, q)
                }
            } else {
                x."$_bind"()
            }

        }
    }

    private String binding(IQuestion question) {
        if (numberBindings)
            return question.getBinding(numberBindings)
        return question.binding

    }

    private String getAbsoluteBindingXPath(String xPath, IQuestion question) {
        if (numberBindings)
            return Form.getIndexedAbsoluteBindingXPath(xPath, question)
        return Form.getAbsoluteBindingXPath(xPath, question)
    }

    private String absoluteBinding(IQuestion question) {
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
        if (bind.length() > 63)
            err.println "Binding: [$bind] is too long"
        return bind
    }

    private void addBindNode(MarkupBuilder xml, IQuestion question) {

        def type = getQuestionType(question)

        def map = [id: binding(question), nodeset: absoluteBinding(question)]

        if (type.type) map.type = type.type

        if (question.isRequired()) map.required = "true()"

        if (question.isReadOnly()) map.readonly = "true()"

        if (!question.isVisible()) map.visible = "false()"

        if (question.skipLogic) {
            def xpath = getAbsoluteBindingXPath(question.skipLogic, question)
            map.relevant = xpath
//            map.action = question.skipAction
        }

        if (question.validationLogic) {
            def xpath = getAbsoluteBindingXPath(question.validationLogic, question)
            map.constraint = xpath
            map.'jr:constraintMsg' = question.message
        }

        if (question.calculation) {
            def xpath = getAbsoluteBindingXPath(question.calculation, question)
            map.calculate = xpath
        }

        xml.bind(map)
    }

    private void buildQuestionsLayout(HasQuestions page, MarkupBuilder xml) {
        page.questions.each { q ->
            buildLayout(xml, q)
        }
    }

    void buildDynamicModel(MarkupBuilder xml, Form form) {
        def completeBinds = []
        form.allQuestions.each { question ->
            if (!(question instanceof DynamicQuestion))
                return

            if (completeBinds.contains(question.dynamicInstanceId))
                return

            xml.instance(id: question.dynamicInstanceId) {
                completeBinds << question.dynamicInstanceId
                xml.dynamiclist {

                    List<DynamicOption> options = question.options
                    options.each { option ->
                        xml.item(id: option.bind, parent: option.parentBinding) {
                            xml.label(option.child)
                            xml.value(option.bind)
                        }
                    }
                }
            }
        }
    }

    void buildLayout(MarkupBuilder x, IQuestion question) {
        def qnType = getQuestionType(question)
        if (question.type == 'boolean') {
            x."select1"(ref: absoluteBinding(question)) {
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
            x.upload(ref: absoluteBinding(question), mediatype: "${qnType.format}/*") {
                buildQuestionLabelAndHint(x, question)
            }
        } else {
            x.input(ref: absoluteBinding(question)) {
                buildQuestionLabelAndHint(x, question)
            }
        }
    }

    void buildLayout(MarkupBuilder xml, DynamicQuestion question) {

        xml.select1(ref: absoluteBinding(question)) {
            buildQuestionLabelAndHint(xml, question)
            xml.itemset(nodeset: "instance('$question.dynamicInstanceId')/dynamiclist/item[@parent=${getDynamicParentQnId(question)}]") {
                xml.value(ref: 'value')
                xml.label(ref: 'label')
            }
        }
    }

    void buildLayout(MarkupBuilder xml, ISelectionQuestion question) {

        def selectRef = question instanceof SingleSelectQuestion ? '1' : ''
        xml."select$selectRef"(ref: absoluteBinding(question)) {
            buildQuestionLabelAndHint(xml, question)
            question.options.each { option ->
                xml.item {
                    xml.label(option.text)
                    xml.value(option.bind)
                }
            }
        }
    }

    void buildLayout(MarkupBuilder xml, RepeatQuestion question) {

        xml.group(ref: absoluteBinding(question)) {
            buildQuestionLabelAndHint(xml, question)

            xml.repeat(bind: binding(question)) {

                buildQuestionsLayout(question, xml)

            }
        }
    }

    private String getDynamicParentQnId(DynamicQuestion question) {
        if (numberBindings)
            return question.indexedAbsParentBinding
        return question.absParentBinding
    }

    void buildQuestionLabelAndHint(MarkupBuilder xml, IQuestion question) {

        def label = question.getText(numberQuestions)
        xml.label(label)
        if (question.comment)
            xml.hint(question.comment)
    }

    static Map getQuestionType(IQuestion question) {
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
                return [type: 'string', format: 'gps']
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
