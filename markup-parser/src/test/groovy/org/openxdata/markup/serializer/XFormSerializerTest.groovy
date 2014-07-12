package org.openxdata.markup.serializer

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.DifferenceListener
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener
import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study
import org.openxdata.markup.Util

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializerTest extends XMLTestCase {

    private Study study
    XFormSerializer serializer = new XFormSerializer();

    void setUp() {

        def parser = Util.createParser(Fixtures.normalPurcform)
        study = parser.study()

    }

    void testBuildStudy() {

    }

    void testToXForm() {
        def form = study.forms[0]

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.expectedXForm, xml
    }

    void testToXFormWithNumberedLabels() {
        def form = study.forms[0]

        serializer.numberQuestions = true

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.expectedXFormWithNumberedLabels, xml
    }


    void testTodStudy() {
        def studyXml = serializer.toStudyXml(study)

        DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener();
        Diff myDiff = new Diff(Fixtures.snvStudyXML, studyXml);
        myDiff.overrideDifferenceListener(myDifferenceListener);
        assertTrue("test XML matches control skeleton XML", myDiff.similar());
        //assertEquals Fixtures.snvStudyXML, studyXml
    }

    void testToXformWithDataTypes() {
        def parser = Util.createParser(Fixtures.formWithAttribs)

        def xml = serializer.toXForm(parser.study().forms[0])
        assertEquals Fixtures.xformWithAttribsXML, xml
    }

    void testToXFormWithSkipLogic() {
        def parser = Util.createParser(Fixtures.formWithSkipLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithSkipLogicXML, xml
    }

    void testToXFormWithValidationLogic() {
        def parser = Util.createParser(Fixtures.formWithValidationLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xfromWithValidationLogicXML, xml
    }

    void testRepeatWithAttributes() {
        def parser = Util.createParser(Fixtures.formRepeatWithAttributesOnRepeats)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithRepeatAttributesXML, xml

    }

    void testFormWithId() {
        def form = Util.createParser(Fixtures.formWithId).study().forms[0]

        assertEquals 'form_v5', form.id
        assertEquals '97', form.dbId

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xmlFormWithId, xml
    }

    void testFormWithPages() {
        def parser = Util.createParser(Fixtures.formWithMultiplePage)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.formWithMultiPageXML, xml
    }

    void testFormWithDynamicInstance() {
        def parser = Util.createParser(Fixtures.formWithDynamicInstanceReferences)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xmlFormWithDynamicInstanceIds, xml
    }

    void testToXFormWithNumbering() {
        def form = study.forms[0]

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(form)

        assertEquals NumberedXMLs.expectedXForm, xml


    }


    void testToXFormWithSkipLogicNumbered() {
        def parser = Util.createParser(Fixtures.formWithSkipLogic)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(parser.study().forms[0])


        assertEquals NumberedXMLs.xformWithSkipLogicXML, xml


    }

    void testToXFormWithValidationLogicNumbered() {
        def parser = Util.createParser(Fixtures.formWithValidationLogic)
        serializer.numberBindings = true
        serializer.numberQuestions = true
        def form = parser.study().forms[0]
        def xml = serializer.toXForm(form)

        assertEquals NumberedXMLs.xfromWithValidationLogicXML, xml

        serializer.numberBindings = false

        xml = serializer.toXForm(form)

        assertEquals NumberedXMLs.xfromWithValidationLogicXMLUnNumberedBindings, xml
    }

    void testRelativePathInVariableNames() {
        def form = Util.createParser(Fixtures.formUsingRelativeBinds).study().forms[0]

        def qn = form.questionMap.two

        def xml = serializer.toXForm(form)

        assertEquals xml, Fixtures.xmlWithRelativeBindings

        serializer.numberBindings = true
        serializer.numberQuestions = true

        xml = serializer.toXForm(form)

        assertEquals xml, NumberedXMLs.xmlWithRelativeBindings

    }

    void testFormWithEndTime() {

        def form = Util.createParser(Fixtures.formWithEndtime).study().forms[0]

        def question = form.questionMap['endtime']

        assertEquals '_1endtime', question.getBinding(true)

        assertEquals 'endtime', question.getBinding(false)

        question.setType('dateTime')

        assertEquals 'endtime', question.getBinding(true)

        assertEquals 'endtime', question.getBinding(false)

        question.setType('time')

        assertEquals 'endtime', question.getBinding(true)

        assertEquals 'endtime', question.getBinding(false)

    }


    void testFormImports() {
        def study = Util.createParser(Fixtures.multipleForms).study()

        serializer.toStudyXml(study)

        def formImports = serializer.formImports



        assertEquals 6, formImports.size()

        assertTrue formImports.values().every { !it.isEmpty() }
    }

    void testToXFormWithAbsoluteId() {
        def parser = Util.createParser(Fixtures.formWithAbsoluteId)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.absoluteIdXML, xml
    }


}
