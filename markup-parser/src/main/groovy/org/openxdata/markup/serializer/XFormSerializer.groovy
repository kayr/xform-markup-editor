package org.openxdata.markup.serializer

import groovy.json.JsonOutput
import groovy.xml.MarkupBuilder
import groovy.xml.XmlUtil
import org.openxdata.markup.*

import static java.lang.System.err

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializer {


    boolean numberQuestions = false
    boolean numberBindings = false
    boolean putExtraAttributesInComments = false
    boolean generateView = true

    Map<Form, String> xforms = [:]
    def studyXML

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
                        if (generateView)
                            xml.layout(toLayout(form))
                    }


                }
            }
        }
        println "========== Done converting study [${study?.name}] ${new Date()}"
        studyXML = printWriter.toString()
        return studyXML
    }

    public Map<String, String> getFormImports() {

        def imports = [:]

        def studyNode = new XmlParser().parseText(studyXML)

        studyNode.form.each { Node node ->
            def formName = node.'@name'
            imports[formName + ".form"] = XmlUtil.serialize(node)

            def versionName = node.version[0].'@name'
            imports["$formName-${versionName}.version"] = XmlUtil.serialize(node.version[0])
        }

        return imports
    }

    private String toLayout(Form form) {
        def laySer = new LayoutSerializer(numberText: numberQuestions, numberBindings: numberBindings)
        def layout = laySer.generateLayout(form)
        layout
    }

    public String toXForm(Form form) {
        def printWriter = new StringWriter();
        def xml = new MarkupBuilder(printWriter)
        xml.doubleQuotes = true
        checkBindLength(form.binding)
        def attrs = [:]
        if (form.layoutAttributes) {
            attrs += form.layoutAttributes.collectEntries { ["layout:$it.key", it.value] }
            attrs["xmlns:layout"] = 'https://github.com/kayr/xform-markup-editor#layout'
        }

        xml.xforms(attrs) {
            xml.model {
                xml.instance(id: form.binding) {
                    xml."$form.binding"(id: form.dbId ?: 0, name: form.name, formKey: form.binding) {
                        buildInstance(xml, form)
                    }
                }
                buildDynamicModel(xml, form)
                form.allElementsWithIds.each {
                    addBindNode(xml, it)
                }
            }

            form.elements.each { element ->
                buildLayout(xml, element)
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
            return Form.getIndexedAbsoluteBindingXPath(xPath, question)
        return Form.getAbsoluteBindingXPath(xPath, question)
    }

    private String absoluteBinding(IFormElement question) {
        if (numberBindings)
            return question.indexedAbsoluteBinding
        return question.absoluteBinding
    }

    private static String checkBindLength(String bind) {
        if (bind?.length() > 63)
            err.println "Binding: [$bind] is too long"
        return bind
    }

    private static String vb(String bind) {
        checkBindLength(bind)
    }

    private void addBindNode(MarkupBuilder xml, IFormElement element) {

        def map = [id: binding(element), nodeset: absoluteBinding(element)] + element.bindAttributes

        if (element instanceof IQuestion) {
            def type = getQuestionType(element)
            if (type.type) map['type'] = type.type

            if (type.format) map['format'] = type.format

            if (element.isRequired()) map['required'] = "true()"

            if (element.isReadOnly()) map['locked'] = "true()"
        }


        if (!element.isVisible()) map['visible'] = "false()"

        if (element.skipLogic) {
            def xpath = getAbsoluteBindingXPath(element.skipLogic, element)
            map['relevant'] = xpath
            map['action'] = element.skipAction
        }

        if (element.validationLogic) {
            def xpath = getAbsoluteBindingXPath(element.validationLogic, element)
            map['constraint'] = xpath
            map['message'] = element.message
        }

        if (element instanceof IQuestion && element.calculation) {
            def xpath = getAbsoluteBindingXPath(element.calculation, element)
            map['calculate'] = xpath
        }

        xml.bind(map)
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


    void buildLayout(MarkupBuilder xml, IQuestion question) {
        def qnType = getQuestionType(question)
        if (qnType.type == 'xsd:base64Binary') {
            def map = [bind: binding(question), mediatype: "${qnType.format}/*"] + question.layoutAttributes
            xml.upload(map) {
                buildQuestionLabelAndHint(xml, question)
            }
        } else {
            def map = [bind: binding(question)] + question.layoutAttributes
            xml.input(map) {
                buildQuestionLabelAndHint(xml, question)
            }
        }
    }

    void buildQuestionLabelAndHint(MarkupBuilder xml, IFormElement question) {

        def label = question.getText(numberQuestions)
        xml.label(label)
        def comment = question instanceof IQuestion ? question.comment : null

        if (putExtraAttributesInComments) {
            def commentMap = [:]

            if (question.bindAttributes) {
                commentMap['bind'] = question.bindAttributes
            }

            if (question.layoutAttributes) {
                commentMap['layout'] = question.layoutAttributes
            }

            if (commentMap) {

                if (comment) commentMap['comment'] = comment

                comment = "json:${JsonOutput.toJson(commentMap)}"
            }

        }

        if (comment) {
            xml.hint(comment)
        }
    }

    void buildLayout(MarkupBuilder xml, DynamicQuestion question) {

        def map = [bind: binding(question)] + question.layoutAttributes
        xml.select1(map) {
            //"instance('district')/item[@parent=instance('brent_study_fsdfsd_v1')/country]
            buildQuestionLabelAndHint(xml, question)
            xml.itemset(nodeset: "instance('$question.dynamicInstanceId')/item[@parent=instance('$question.parentForm.binding')/${getDynamicParentQnId(question)}]") {
                xml.label(ref: 'label')
                xml.value(ref: 'value')
            }

        }
    }

    private String getDynamicParentQnId(DynamicQuestion question) {
        if (numberBindings)
            return question.indexedParentQuestionId
        return question.parentQuestionId
    }

    void buildLayout(MarkupBuilder xml, ISelectionQuestion question) {

        def selectRef = question instanceof SingleSelectQuestion ? '1' : ''
        def map = [bind: binding(question)] + question.layoutAttributes
        xml."select$selectRef"(map) {
            buildQuestionLabelAndHint(xml, question)
            question.options.each { option ->
                xml.item(id: option.bind) {
                    xml.label(option.text)
                    xml.value(option.bind)
                }
            }

        }
    }


    void buildLayout(MarkupBuilder xml, RepeatQuestion question) {

        xml.group(id: binding(question)) {
            buildQuestionLabelAndHint(xml, question)


            def map = [bind: binding(question)] + question.layoutAttributes
            xml.repeat(map) {
                question.elements.each { e ->
                    buildLayout(xml, e)
                }
            }
        }
    }

    void buildLayout(MarkupBuilder xml, Page page) {
        def pageId = page.id ?: page.parentForm.elements.indexOf(page) + 1

        def map = [id: pageId] + page.layoutAttributes

        if (page.id) {
            map['bind'] = pageId
        }

        xml.group(map) {
            buildQuestionLabelAndHint(xml, page)
            page.elements.each { e ->
                buildLayout(xml, e)
            }

        }
    }


    static Map getQuestionType(IQuestion question) {
        switch (question.type) {
            case 'video':
                return [type: 'xsd:base64Binary', format: 'video']
            case 'picture':
                return [type: 'xsd:base64Binary', format: 'image']
            case 'audio':
                return [type: 'xsd:base64Binary', format: 'audio']
            case 'number':
                return [type: 'xsd:int']
            case 'gps':
                return [type: 'xsd:string', format: 'gps']
            case 'repeat':
                return [:]
            case 'longtext':
                return [type: 'xsd:string']
            default:
                return [type: "xsd:$question.type"]

        }
    }


}
