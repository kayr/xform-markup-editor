package org.openxdata.markup.deserializer

import groovy.transform.CompileStatic as CS
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CharStream
import org.antlr.runtime.CommonTokenStream
import org.antlr.runtime.tree.CommonTree
import org.openxdata.markup.*

import static org.openxdata.markup.ParserUtils.each

//@CS
class MarkupDeserializer {


    CommonTree tree
    String text

    MarkupDeserializer(String text) {
        this.text = text
    }


    @CS
    Study parse() {
        tree = createAST(text)
        def study = constructStudy(tree)
        return study
    }

    @CS
    Study study() {
        return parse()
    }

    @CS
    private static CommonTree createAST(String markup) {
        return createXpathParser(markup).study().tree as CommonTree
    }


    @CS
    private static Study constructStudy(CommonTree tree) {

        def study = new Study()
        study.setName(tree.text)
        each(tree) { CommonTree it ->
            switch (it.type) {
                case XformParser.T_FORM:
                    def form = constructForm(it)
                    study.addForm(form)
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
                    form.addPage(page)
                    addQuestions(page, child)
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
                    parent.addQuestion(q)
                    break

                case XformParser.T_SINGLE_QN:
                    def q = processSelect(child, new SingleSelectQuestion())
                    parent.addQuestion(q)
                    break

                case XformParser.T_DYNAMIC_QN:
                    def q = processDynamicQuestion(child, new DynamicQuestion())
                    parent.addQuestion(q)
                    break

                case XformParser.T_QN:
                    def q = addAttributes(child, new TextQuestion())
                    parent.addQuestion(q)
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
                    parent.addQuestion(q)
                    addQuestions(q, child)
                    break

                case XformParser.PAGE:
                    def page = parent as Page
                    page.setName(child.text)
                    break


            }

        }

    }

    @CS
    static def <T extends ISelectionQuestion> T processSelect(CommonTree tree, T q) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.SINGLEOPTION:
                case XformParser.MULTIPLEOPTION:
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
    private static def <T extends IQuestion> T addAttributes(CommonTree tree, T qn) {
        each(tree) { CommonTree child ->
            switch (child.type) {
                case XformParser.ATTRIBUTE:
                    Attrib.addAttribute(qn, child.text, child.line)
                    break
                case XformParser.LINECONTENTS:
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
        CharStream stream = new ANTLRStringStream(testString);
        XformLexer lexer = new XformLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XformParser parser = new XformParser(tokens);
        return parser;
    }


}
