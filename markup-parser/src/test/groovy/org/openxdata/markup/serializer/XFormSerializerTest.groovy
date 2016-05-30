package org.openxdata.markup.serializer

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.DifferenceListener
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener
import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study
import org.openxdata.markup.Util
import org.openxdata.markup.deserializer.MarkupDeserializer

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializerTest extends XMLTestCase {

    private Study study
    XFormSerializer serializer

    void setUp() {

        def parser = new MarkupDeserializer(Fixtures.normalPurcform)
        study = parser.study()
        serializer = new XFormSerializer()

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
        def parser = new MarkupDeserializer(Fixtures.formWithAttribs)

        def xml = serializer.toXForm(parser.study().forms[0])
        assertEquals Fixtures.xformWithAttribsXML, xml
    }

    void testToXFormWithSkipLogic() {
        def parser = new MarkupDeserializer(Fixtures.formWithSkipLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithSkipLogicXML, xml
    }

    void testToXFormWithValidationLogic() {
        def parser = new MarkupDeserializer(Fixtures.formWithValidationLogic)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xfromWithValidationLogicXML, xml
    }

    void testRepeatWithAttributes() {
        def parser = new MarkupDeserializer(Fixtures.formRepeatWithAttributesOnRepeats)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.xformWithRepeatAttributesXML, xml

    }

    void testFormWithId() {
        def form = new MarkupDeserializer(Fixtures.formWithId).study().forms[0]

        assertEquals 'form_v5', form.id
        assertEquals '97', form.dbId
        assertEquals 5, form.dbIdLine
        assertEquals 4, form.idLine
        assertEquals 4, form.startLine

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xmlFormWithId, xml
    }

    void testFormWithPages() {
        def parser = new MarkupDeserializer(Fixtures.formWithMultiplePage)

        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.formWithMultiPageXML, xml
    }

    void testFormWithDynamicInstance() {
        def parser = new MarkupDeserializer(Fixtures.formWithDynamicInstanceReferences)

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
        def parser = new MarkupDeserializer(Fixtures.formWithSkipLogic)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(parser.study().forms[0])


        assertEquals NumberedXMLs.xformWithSkipLogicXML, xml


    }

    void testToXFormWithValidationLogicNumbered() {
        def parser = new MarkupDeserializer(Fixtures.formWithValidationLogic)
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
        def form = new MarkupDeserializer(Fixtures.formUsingRelativeBinds).study().forms[0]

        def qn = form.questionMap.two

        def xml = serializer.toXForm(form)

        assertEquals xml, Fixtures.xmlWithRelativeBindings

        serializer.numberBindings = true
        serializer.numberQuestions = true

        xml = serializer.toXForm(form)

        assertEquals xml, NumberedXMLs.xmlWithRelativeBindings

    }

    void testFormWithEndTime() {

        def form = new MarkupDeserializer(Fixtures.formWithEndtime).study().forms[0]

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
        def study = new MarkupDeserializer(Fixtures.multipleForms).study()

        serializer.toStudyXml(study)

        def formImports = serializer.formImports



        assertEquals 6, formImports.size()

        assertTrue formImports.values().every { !it.isEmpty() }
    }

    void testToXFormWithAbsoluteId() {
        def parser = new MarkupDeserializer(Fixtures.formWithAbsoluteId)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(parser.study().forms[0])

        assertEquals Fixtures.absoluteIdXML, xml
    }

    void testNonOxdInCompatibleIDsAreAllowed() {

        Study.validateWithXML.set(true)
        def form = new MarkupDeserializer(ODKFixtures.formWithIncompatibleOXDId.form).study().forms[0]

        def xml = serializer.toXForm(form)

        assertEquals ODKFixtures.formWithIncompatibleOXDId.oxdXML, xml


    }


}
