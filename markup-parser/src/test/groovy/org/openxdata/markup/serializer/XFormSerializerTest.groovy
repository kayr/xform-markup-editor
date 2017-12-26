package org.openxdata.markup.serializer

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.DifferenceListener
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener
import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.Converter
import org.openxdata.markup.FLAGS
import org.openxdata.markup.FORMAT
import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study
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
        def studyXml = Converter.fromFormTo(FORMAT.STUDY_XML, study.firstForm,FLAGS.of(FLAGS.OXD_GENERATE_VIEW))

        DifferenceListener myDifferenceListener = new IgnoreTextAndAttributeValuesDifferenceListener()
        Diff myDiff = new Diff(Fixtures.snvStudyXML, studyXml)
        myDiff.overrideDifferenceListener(myDifferenceListener)
        assertTrue("test XML matches control skeleton XML", myDiff.similar())
//        assertEquals Fixtures.snvStudyXML, studyXml
    }


    void testToXformWithDataTypes() {
        def form = Converter.markup2Form(Fixtures.formWithAttribs)
        def xml = serializer.toXForm(form)
        assertEquals Fixtures.xformWithAttribsXML, xml
    }

    void testToXFormWithSkipLogic() {
        def form = Converter.markup2Form(Fixtures.formWithSkipLogic)

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xformWithSkipLogicXML, xml
    }

    void testToXFormWithValidationLogic() {
        def form = Converter.markup2Form(Fixtures.formWithValidationLogic)

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xfromWithValidationLogicXML, xml
    }

    void testRepeatWithAttributes() {
        def form = Converter.markup2Form(Fixtures.formRepeatWithAttributesOnRepeats)

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xformWithRepeatAttributesXML, xml

    }

    void testFormWithId() {
        def form = Converter.markup2Form(Fixtures.formWithId)

        assertEquals 'form_v5', form.id
        assertEquals '97', form.dbId
        assertEquals 5, form.dbIdLine
        assertEquals 4, form.idLine
        assertEquals 4, form.startLine

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.xmlFormWithId, xml
    }

    void testFormWithPages() {
        def form = Converter.markup2Form(Fixtures.formWithMultiplePage)

        def xml = serializer.toXForm(form)

        assertEquals Fixtures.formWithMultiPageXML, xml
    }

    void testFormWithDynamicInstance() {
        def form = Converter.markup2Form(Fixtures.formWithDynamicInstanceReferences)

        def xml = serializer.toXForm(form)

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
        def form = Converter.markup2Form(Fixtures.formWithSkipLogic)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(form)


        assertEquals NumberedXMLs.xformWithSkipLogicXML, xml


    }

    void testToXFormWithValidationLogicNumbered() {
        def form = Converter.markup2Form(Fixtures.formWithValidationLogic)
        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(form)

        assertEquals NumberedXMLs.xfromWithValidationLogicXML, xml

        serializer.numberBindings = false

        xml = serializer.toXForm(form)

        assertEquals NumberedXMLs.xfromWithValidationLogicXMLUnNumberedBindings, xml
    }

    void testRelativePathInVariableNames() {
        def form = Converter.markup2Form(Fixtures.formUsingRelativeBinds)

        def qn = form.getElement('two')

        def xml = serializer.toXForm(form)

        assertEquals xml, Fixtures.xmlWithRelativeBindings

        serializer.numberBindings = true
        serializer.numberQuestions = true

        xml = serializer.toXForm(form)

        assertEquals xml, NumberedXMLs.xmlWithRelativeBindings

    }

    void testFormWithEndTimeThatIsNotADate() {

        def formText = '''### Study
                      |## Form
                      |@id endtime
                      |End time'''.stripMargin()
        def form = Converter.markup2Form(formText)
        def question = form.getElement('endtime')
        assertEquals '_1endtime', question.getBinding(true)
    }

    void testFormWithEndTimeThatIsADate() {

        def formText = '''### Study
                      |## Form
                      |@id endtime
                      |@date
                      |End time'''.stripMargin()
        def form = Converter.markup2Form(formText)
        def question = form.getElement('endtime')
        assertEquals '_1endtime', question.getBinding(true)
    }

    void testFormWithEndTimeThatIsADateTime() {

        def formText = '''### Study
                      |## Form
                      |@id endtime
                      |@datetime
                      |End time'''.stripMargin()
        def form = Converter.markup2Form(formText)
        def question = form.getElement('endtime')
        assertEquals 'endtime', question.getBinding(true)
    }

    void testFormWithEndTimeThatIsATime() {
        def formText = '''### Study
                      |## Form
                      |@id endtime
                      |@time
                      |End time'''.stripMargin()
        def form = Converter.markup2Form(formText)
        def question = form.getElement('endtime')
        assertEquals 'endtime', question.getBinding(true)

    }


    void testFormImports() {
        def study = Converter.markup2Form(Fixtures.multipleForms).study

        serializer.toStudyXml(study)

        def formImports = serializer.formImports



        assertEquals 6, formImports.size()

        assertTrue formImports.values().every { !it.isEmpty() }
    }

    void testToXFormWithAbsoluteId() {
        def form = Converter.markup2Form(Fixtures.formWithAbsoluteId)

        serializer.numberBindings = true
        serializer.numberQuestions = true
        def xml = serializer.toXForm(form)

        assertEquals Fixtures.absoluteIdXML, xml
    }

    void testInCompatibleOxdIDsAreAllowed() {

        Study.validateWithXML.set(true)
        def form = Converter.markup2Form(ODKFixtures.formWithIncompatibleOXDId.form)

        def xml = serializer.toXForm(form)

        assertEquals ODKFixtures.formWithIncompatibleOXDId.oxdXML, xml


    }

    void testLayoutAttributesAndBindAttributesAreSerialized() {
        def form = Converter.markup2Form(Fixtures.formWithLayoutAndBindAttributes)

        def xForm = new XFormSerializer().toXForm(form)
        assertEquals Fixtures.formWithLayoutAndBindAttributesXML, xForm

    }

    void testLayoutAttributesAndBindAttributesAreSerializedToComments() {
        def form = Converter.markup2Form(Fixtures.formWithLayoutAndBindAttributes)

        def xForm = new XFormSerializer(putExtraAttributesInComments: true).toXForm(form)
        assertEquals Fixtures.formWithLayoutAndBindAttributesToCommentsXML, xForm

    }

    void testSerializingMultilineQuestionsAndOptions() {
        def form = Converter.markup2Form(Fixtures.form_With_Multiline)

        def xForm = new XFormSerializer().toXForm(form)

        assertEquals Fixtures.form_With_Multiline_XML, xForm

    }


}
