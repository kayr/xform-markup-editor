package org.openxdata.markup.serializer

import groovy.xml.MarkupBuilder
import org.openxdata.markup.*

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializer {

    boolean numberQuestions = false

    def xforms = [:]

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
        return printWriter.toString()
    }

    public String toXForm(Form form) {
        def printWriter = new StringWriter();
        def xml = new MarkupBuilder(printWriter)
        xml.doubleQuotes = true

        xml.xforms {
            xml.model {
                xml.instance(id: form.binding) {
                    xml."$form.binding"(id: 0, name: form.name, formKey: form.binding) {

                        form.questions.each {  question ->
                            def bind = question.binding
                            checkBindLength(bind)
                            xml."$bind" {//Improve and use recursion when you have time
                                if (question instanceof RepeatQuestion) {
                                    RepeatQuestion qn = question
                                    qn.questions.each { qnInRpt ->
                                        checkBindLength(qnInRpt.binding)
                                        xml."$qnInRpt.binding"()
                                    }
                                }
                            }
                        }
                    }
                }
                buildDynamicModel(xml, form)
                form.questions.each {
                    if (it instanceof RepeatQuestion) {    //We do not support recursion. repeats with in repeats
                        addBindNode(xml, it)
                        it.questions.each {
                            addBindNode(xml, it)
                        }
                    }
                    else
                        addBindNode(xml, it)
                }
            }

            form.pages.eachWithIndex { page, idx ->
                xml.group(id: idx + 1) {
                    xml.label(page.name)
                    buildQuestionsLayout(page, xml)

                }
            }
        }

        def xFormXml = printWriter.toString()
        xforms[form] = xFormXml
        return xFormXml
    }

    private void checkBindLength(String bind) {
        if (bind.length() > 63)
            System.err.println "Binding: [$bind] is too long"
    }

    private void addBindNode(MarkupBuilder xml, IQuestion question) {

        def type = getQuestionType(question)

        def map = [id: question.binding, nodeset: question.fullBinding]

        if (type.type) map.type = type.type

        if (type.format) map.format = type.format

        if (question.isRequired()) map.required = "true()"

        if (question.isReadOnly()) map.locked = "true()"

        if (!question.isVisible()) map.visible = "false()"

        if (question.skipLogic) {
            def xpath = Form.getFullBindingXPath(question.skipLogic, question)
            map.relevant = xpath
            map.action = question.skipAction
        }

        if (question.validationLogic) {
            def xpath = Form.getFullBindingXPath(question.validationLogic, question)
            map.constraint = xpath
            map.message = question.message
        }

        if (question.calculation) {
            def xpath = Form.getFullBindingXPath(question.calculation, question)
            map.calculate = xpath
        }

        xml.bind(map)
    }

    private void buildQuestionsLayout(HasQuestions page, MarkupBuilder xml) {
        page.questions.each {  question ->
            def questionClass = question.class

            switch (questionClass) {
                case RepeatQuestion.class:
                    buildRepeatLayout(xml, question, page)
                    break
                case MultiSelectQuestion.class:
                    buildSelectionLayout(xml, question)
                    break
                case SingleSelectQuestion.class:
                    buildSelectionLayout(xml, question)
                    break
                case DynamicQuestion.class:
                    buildDynamicLayout(xml, question, page)
                    break
                default:
                    buildQuestionLayout(xml, question)
                    break
            }
        }
    }



    void buildDynamicModel(MarkupBuilder xml, Form form) {
        form.allQuestions.each { question ->
            if (!(question instanceof DynamicQuestion))
                return

            xml.instance(id: question.binding) {
                xml.dynamiclist {

                    List<DynamicOption> options = question.options
                    options.each { option ->
                        xml.item(id: option.bind, parent: option.parent) {
                            xml.label(option.child)
                            xml.value(option.bind)
                        }
                    }
                }
            }
        }
    }


    void buildQuestionLayout(MarkupBuilder xml, IQuestion question) {
        def qnType = getQuestionType(question)
        if (qnType.type == 'xsd:base64Binary') {
            xml.upload(bind: question.binding, mediatype: "${qnType.format}/*") {
                buildQuestionLabelAndHint(xml, question)
            }
        }
        else
            xml.input(bind: question.binding) {
                buildQuestionLabelAndHint(xml, question)
            }
    }

    void buildQuestionLabelAndHint(MarkupBuilder xml, IQuestion question) {

        def label = numberQuestions ? "${question.questionIdx}. $question.text" : question.text
        xml.label(label)
        if (question.comment)
            xml.hint(question.comment)
    }

    void buildDynamicLayout(MarkupBuilder xml, DynamicQuestion question, HasQuestions page) {

        xml.select1(bind: question.binding) {
            //"instance('district')/item[@parent=instance('brent_study_fsdfsd_v1')/country]
            buildQuestionLabelAndHint(xml, question)
            xml.itemset(nodeset: "instance('$question.binding')/item[@parent=instance('$page.parentForm.binding')/$question.parentQuestionId]") {
                xml.label(ref: 'label')
                xml.value(ref: 'value')
            }

        }
    }

    void buildSelectionLayout(MarkupBuilder xml, ISelectionQuestion question) {

        def selectRef = question instanceof SingleSelectQuestion ? '1' : ''
        xml."select$selectRef"(bind: question.binding) {
            buildQuestionLabelAndHint(xml, question)
            question.options.each { option ->
                xml.item(id: option.bind) {
                    xml.label(option.text)
                    xml.value(option.bind)
                }
            }

        }
    }


    void buildRepeatLayout(MarkupBuilder xml, RepeatQuestion question, HasQuestions page) {

        xml.group(id: question.binding) {
            buildQuestionLabelAndHint(xml, question)

            xml.repeat(bind: question.binding) {

                buildQuestionsLayout(question, xml)

            }
        }
    }

    Map getQuestionType(IQuestion question) {
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
            default:
                return [type: "xsd:$question.type"]

        }
    }

}
