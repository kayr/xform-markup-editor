package org.openxdata.markup.deserializer

import groovy.transform.CompileStatic as CS
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.*
import org.openxdata.markup.transformers.TransformerResolver

import static org.openxdata.markup.ParserUtils.each

//@CS
class MarkupDeserializer {


    CommonTree tree
    String     text
    boolean    validating = true

    MarkupDeserializer(String text) {
        this.text = text
    }

    MarkupDeserializer(String text, boolean validating) {
        this.text = text
        this.validating = validating
    }


    @CS
    Study parse() {
        tree = createAST(text)
        def study = constructStudy(tree, validating)
        return study
    }

    @CS
    Study study() {
        return parse()
    }

    @CS
    static CommonTree createAST(String markup) {
        return createXpathParser(markup).study().tree as CommonTree
    }


    @CS
    private static Study constructStudy(CommonTree tree, Boolean validating) {

        def study = new Study(validating: false)//let use do our own validation after we are done with transformations
        study.setName(tree.text)
        each(tree) { CommonTree it ->
            switch (it.type) {
                case XformParser.T_FORM:
                    def form = constructForm(it)
                    study.addForm(form)


                    if (validating)
                        TransformerResolver.instance.doTransformations(FLAGS.of(FLAGS.VALIDATE_FORM), form)
                    else {
                        TransformerResolver.instance.doTransformations(FLAGS.none(), form)
                    }

                    if (validating) {
                        form.validate()
                    }

                    form.buildIndex()
                    break

            }
        }
        return study

    }

    @CS
    private static Form constructForm(CommonTree tree) {

        def form = new Form()
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.ATTRIBUTE:
                    Attrib.addAttributeToForm(form, child.text, child.line)
                    break
                case XformParser.FORMNAME:
                    form.setName(child.text)
                    form.line = child.line
                    break

                case XformParser.T_PAGE:
                    def page = new Page()
                    page.line = child.line == 0 ? form.line : child.line
                    processPage(child, page)
                    form.addElement(page)
                    addQuestions(page, child)
                    break
                case XformParser.T_START_FORM:
                    addQuestions(form, child)
                    break
            }
        }


        return form
    }

    @CS
    private static def addQuestions(HasQuestions parent, CommonTree tree) {


        each(tree) { CommonTree child ->

            switch (child.type) {
                case XformParser.T_MULTI_QN:
                    def q = processSelect(child, new MultiSelectQuestion())
                    parent.addElement(q)
                    break

                case XformParser.T_SINGLE_QN:
                    def q = processSelect(child, new SingleSelectQuestion())
                    parent.addElement(q)
                    break

                case XformParser.T_DYNAMIC_QN:
                    def q = processDynamicQuestion(child, new DynamicQuestion())
                    parent.addElement(q)
                    break

                case XformParser.T_QN:
                    def q = addAttributes(child, new TextQuestion())
                    if (q.binding == 'endtime' && (q.type == 'dateTime' || q.type == 'time')) {
                        q.hasAbsoluteId = true
                    }
                    parent.addElement(q)
                    break

                case XformParser.T_DYNAMIC_INSTANCE:
                    def dq = processDynamicOptions(child, new DynamicBuilder(true))
                    dq.addQuestionsToForm(parent)
                    break

                case XformParser.T_DYNAMIC_OPTIONS:
                    def dq = processDynamicOptions(child, new DynamicBuilder())
                    dq.addQuestionsToForm(parent)
                    break

                case XformParser.CSVIMPORT:
                    def dq = new DynamicBuilder()
                    dq.line = child.line
                    dq.setCsvFilePath(child.text)
                    dq.addQuestionsToForm(parent)
                    break

                case XformParser.T_REPEAT_QN:
                    def q = new RepeatQuestion()
                    q.setParent(parent)
                    processRepeatQuestion(child, q)
                    parent.addElement(q)
                    addQuestions(q, child)
                    break

                case XformParser.PAGE:
                    def page = parent as Page
                    page.setName(child.text)
                    page.line = child.line == 0 ? parent.parentForm.line : child.line
                    break

                case XformParser.T_PAGE:
                    def group = new Page()
                    group.line = child.line
                    processPage(child, group)
                    parent.addElement(group)
                    addQuestions(group, child)
                    break


            }

        }

    }

    static Page processPage(CommonTree tree, Page p) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.ATTRIBUTE:
                    Attrib.addAttributeToPage(p, child.text, child.line)
                    break

                case XformParser.GROUP_MARKER:
                    p.name = child.text
                    p.line = child.line
                    break

            }
        }

    }

    @CS
    static <T extends ISelectionQuestion> T processSelect(CommonTree tree, T q) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.SINGLEOPTION:
                case XformParser.MULTIPLEOPTION:
                case XformParser.MULTIPLEOPTION_MULTI:
                case XformParser.SINGLEOPTION_MULTI:
                    q.addOption(new Option(child.text, child.line))
                    break
                case XformParser.T_QN:
                    addAttributes(child, q)
                    break
            }
        }

        return q
    }

    @CS
    private static <T extends IQuestion> T addAttributes(CommonTree tree, T qn) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.ATTRIBUTE:
                    Attrib.addAttribute(qn, child.text, child.line)
                    break
                case XformParser.LINECONTENTS:
                case XformParser.MULTILINE_TEXT:
                    qn.text = child.text
                    qn.line = child.line
                    break
            }
        }


        return qn
    }

    @CS
    private static DynamicQuestion processDynamicQuestion(CommonTree tree, DynamicQuestion qn) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.T_QN:
                    addAttributes(child, qn)
                    break

                case XformParser.DYNAMICOPTION:
                    qn.dynamicInstanceId = child.text
                    break
            }
        }
        return qn
    }

    @CS
    private static RepeatQuestion processRepeatQuestion(CommonTree tree, RepeatQuestion qn) {
        each(tree) { CommonTree child ->
            switch (child.type) {

                case XformParser.ATTRIBUTE:
                    Attrib.addAttribute(qn, child.text, child.line)
                    break

                case XformParser.BEGINREPEATMARKER:
                    qn.text = child.text
                    qn.line = child.line
                    break
            }
        }

        return qn

    }

    @CS
    private static DynamicBuilder processDynamicOptions(CommonTree tree, DynamicBuilder dn) {
        dn.line = tree.line
        each(tree) { CommonTree child ->
            if (child.type == XformParser.LINECONTENTS) {
                dn.appendLine(child.text)
            } else {
                throw new UnsupportedOperationException("Cannot add Line at $tree.line")
            }
        }
        return dn
    }

    @CS
    private static XformParser createXpathParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString)
        XformLexer lexer = new XformLexer(stream)
        CommonTokenStream tokens = new CommonTokenStream(lexer)
        XformParser parser = new XformParser(tokens)
        return parser
    }

}
