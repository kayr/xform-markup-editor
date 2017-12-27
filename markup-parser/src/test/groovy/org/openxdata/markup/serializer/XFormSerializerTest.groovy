package org.openxdata.markup.serializer

import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.DifferenceListener
import org.custommonkey.xmlunit.IgnoreTextAndAttributeValuesDifferenceListener
import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.*
import org.openxdata.markup.deserializer.MarkupDeserializer

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/4/13
 * Time: 8:59 PM
 * To change this template use File | Settings | File Templates.
 */
class XFormSerializerTest extends XMLTestCase {

    private Study           study
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
        def studyXml = Converter.fromFormTo(FORMAT.STUDY_XML, study.firstForm, FLAGS.of(FLAGS.OXD_GENERATE_VIEW))

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


    void testSerializingWithBindXpath() {

        /*
         todo bind attributes on xml
         [x] Mkp -> OXD
         [x] MKP -> ODK
         [ ] ODK -> Mkp
         [ ] OXD -> MKP
         */
        def formText = '''### Study
                      |@bindxpath generex
                      |## Form
                      |@id name
                      |Name
                      |
                      |@id endtime
                      |@time
                      |@bind:generex concat($name,' ') 
                      |End time'''.stripMargin()

        assertEquals '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <name />
        <endtime />
      </study_form_v1>
    </instance>
    <bind id="name" nodeset="/study_form_v1/name" generex="" type="xsd:string" />
    <bind id="endtime" nodeset="/study_form_v1/endtime" generex="concat(/study_form_v1/name,' ')" type="xsd:time" />
  </model>
  <group id="1" isSynthetic="true">
    <label>Page1</label>
    <input bind="name">
      <label>Name</label>
    </input>
    <input bind="endtime">
      <label>End time</label>
    </input>
  </group>
</xforms>''', Converter.markup2Oxd(formText)

        def odk = Converter.to(FORMAT.ODK)
                           .from(FORMAT.MARKUP)
                           .convert(formText)

        assertEquals '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Form</h:title>
    <model>
      <instance>
        <study_form_v1 id="0" name="Form">
          <name />
          <endtime />
        </study_form_v1>
      </instance>
      <bind id="name" nodeset="/study_form_v1/name" generex="" type="string" />
      <bind id="endtime" nodeset="/study_form_v1/endtime" generex="concat(/study_form_v1/name,' ')" type="time" />
    </model>
  </h:head>
  <h:body>
    <input ref="/study_form_v1/name">
      <label>Name</label>
    </input>
    <input ref="/study_form_v1/endtime">
      <label>End time</label>
    </input>
  </h:body>
</h:html>''', odk


    }


}
