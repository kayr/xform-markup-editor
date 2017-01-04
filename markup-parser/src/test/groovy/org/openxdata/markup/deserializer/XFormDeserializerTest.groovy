package org.openxdata.markup.deserializer

import junit.framework.ComparisonFailure
import org.custommonkey.xmlunit.XMLTestCase
import org.openxdata.markup.*
import org.openxdata.markup.serializer.XFormSerializer

import static org.openxdata.markup.Fixtures.*
import static org.openxdata.markup.Form.extractQuestions
import static org.openxdata.markup.deserializer.DeSerializerFixtures.*
import static org.openxdata.markup.serializer.ODKFixtures.formWithIncompatibleOXDId
import static org.openxdata.markup.serializer.ODKFixtures.formWithInvisible

/**
 * Created by kay on 6/7/14.
 */
class XFormDeserializerTest extends XMLTestCase {

    def serializer = new XFormSerializer()

    void testToForm() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.name == 'Snv Form'
        assert form.id == 'snv_study_snv_form_v1'
    }

    void testAddPages() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.pages.size() == 1
        assert form.pages[0].name == 'Page1'
    }

    void testNumberOfQuestions() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()
        assert form.allQuestions.size() == 11
        assert extractQuestions(form).size() == 11
    }

    void testSingleSelectOption() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()

        def question = form.allQuestions.find { it.binding == 'what_is_sex' }
        assert question.class == SingleSelectQuestion

        def options = question.options
        assert options.size() == 2
        assert options.any { it.bind == 'male' && it.text == 'Male' }
        assert options.any { it.bind == 'female' && it.text == 'female' }
    }

    void testMultipleSelectOption() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()

        def question = form.allQuestions.find { it.binding == 'select_your_diseases' }
        assert question instanceof MultiSelectQuestion

        def options = question.options
        assert options.size() == 3
        assert options.any { it.bind == 'aids' && it.text == 'AIDS' }
        assert options.any { it.bind == 'tb' && it.text == 'TB' }
        assert options.any { it.bind == 'whooping_cough' && it.text == 'Whooping cough' }
    }

    void testDynamicOption() {
        def form = new XFormDeserializer(xml: Fixtures.expectedXForm).parse()

        assert form.dynamicOptions.size() == 2

        assert form.dynamicOptions.district.size() == 3
        assert form.dynamicOptions.district.find { it.bind == 'kampala' }.option == 'Kampala'
        assert form.dynamicOptions.district.find { it.bind == 'kampala' }.parentBinding == 'uganda'

        assert form.dynamicOptions.district.find { it.bind == 'nairobi' }.option == 'Nairobi'
        assert form.dynamicOptions.district.find { it.bind == 'nairobi' }.parentBinding == 'kenya'

        assert form.dynamicOptions.district.find { it.bind == 'kenya_kampala' }.option == 'Kampala'
        assert form.dynamicOptions.district.find { it.bind == 'kenya_kampala' }.parentBinding == 'kenya'

        assert form.dynamicOptions.school.size() == 4
        assert form.dynamicOptions.school.find { it.bind == 'macos' }.option == 'Macos'
        assert form.dynamicOptions.school.find { it.bind == 'macos' }.parentBinding == 'kampala'

        assert form.dynamicOptions.school.find { it.bind == 'machaccos' }.option == 'Machaccos'
        assert form.dynamicOptions.school.find { it.bind == 'machaccos' }.parentBinding == 'nairobi'

        assert form.dynamicOptions.school.find { it.bind == 'bugiroad' }.option == 'Bugiroad'
        assert form.dynamicOptions.school.find { it.bind == 'bugiroad' }.parentBinding == 'kampala'

        assert form.dynamicOptions.school.find { it.bind == 'kenya_kampala_bugiroad' }.option == 'Bugiroad'
        assert form.dynamicOptions.school.find { it.bind == 'kenya_kampala_bugiroad' }.parentBinding == 'kenya_kampala'

        println form.dynamicOptions

        DynamicQuestion districtQn = form.allQuestions.find { it.binding == 'district' }
        assert districtQn.class == DynamicQuestion
        assert districtQn.dynamicInstanceId == 'district'
        assert districtQn.parentQuestionId == 'country'

        DynamicQuestion schQn = form.allQuestions.find { it.binding == 'school' }
        assert schQn.class == DynamicQuestion
        assert schQn.dynamicInstanceId == 'school'
        assert schQn.parentQuestionId == 'district'
    }

    void testDynamicOptionAttributesInQuestions() {
        def form = new XFormDeserializer(xml: Fixtures.xmlFormWithDynamicInstanceIds).parse()

        assert form.dynamicOptions.size() == 1

        DynamicQuestion districtQn = form.allQuestions.find { it.binding == 'subregion' }
        assert districtQn.class == DynamicQuestion
        assert districtQn.dynamicInstanceId == 'subregion2'
        assert districtQn.parentQuestionId == 'region'

        DynamicQuestion schQn = form.allQuestions.find { it.binding == 'subregion_dupe' }
        assert schQn.class == DynamicQuestion
        assert schQn.dynamicInstanceId == 'subregion2'
        assert schQn.parentQuestionId == 'region'
    }

    void testTypeResolving() {
        def serializer = new XFormSerializer()
        def mkpForm = new MarkupDeserializer(oxdSampleForm).study().forms[0]

        def xForm = serializer.toXForm(mkpForm)
        def formFromXml = new XFormDeserializer(xml: xForm).parse()


        def validateType = { Form form ->
            def questions = form.allQuestions
            assert questions.find { it.binding == 'patient_id' }.type in ['string', 'longtext']
            assert questions.find { it.binding == 'patient_id' }.xformType == XformType.TEXT

            assert questions.find { it.binding == 'title' }.type == 'string'
            assert questions.find { it.binding == 'title' }.xformType == XformType.SELECT1

            assert questions.find { it.binding == 'sex' }.type == 'string'

            assert questions.find { it.binding == 'birthdate' }.type == 'date'
            assert questions.find { it.binding == 'birthdate' }.xformType == XformType.DATE

            assert questions.find { it.binding == 'weightkg' }.type == 'decimal'
            assert questions.find { it.binding == 'weightkg' }.xformType == XformType.DECIMAL


            assert questions.find { it.binding == 'is_patient_pregnant' }.type == 'boolean'
            assert questions.find { it.binding == 'is_patient_pregnant' }.xformType == XformType.BOOLEAN

            assert questions.find { it.binding == 'arvs' }.type == 'string'
            assert questions.find { it.binding == 'arvs' }.xformType == XformType.SELECT
            assert questions.find {
                it.binding == 'arvs'
            }.comment == 'Please select all anti-retrovirals that the patient is taking'

            assert questions.find { it.binding == 'picture' }.type == 'picture'
            assert questions.find { it.binding == 'picture' }.xformType == XformType.PICTURE

            assert questions.find { it.binding == 'sound' }.type == 'audio'
            assert questions.find { it.binding == 'sound' }.xformType == XformType.AUDIO

            assert questions.find { it.binding == 'record_video' }.type == 'video'
            assert questions.find { it.binding == 'record_video' }.xformType == XformType.VIDEO

            assert questions.find { it.binding == 'region' }.type == 'string'
            assert questions.find { it.binding == 'region' }.xformType == XformType.SELECT1

            assert questions.find { it.binding == 'sub_hyphen_region' }.type == 'string'
            assert questions.find { it.binding == 'sub_hyphen_region' }.xformType == XformType.SELECT1_DYNAMIC

            assert questions.find { it.binding == 'city' }.type == 'string'

            assert questions.find { it.binding == 'age' }.type == 'number'
            assert questions.find { it.binding == 'age' }.xformType == XformType.NUMBER

            assert questions.find { it.binding == 'child_sex' }.type == 'string'
            assert questions.find { it.binding == 'child_sex' }.xformType == XformType.SELECT1

            assert questions.find { it.binding == 'start_time' }.type == 'time'
            assert questions.find { it.binding == 'start_time' }.xformType == XformType.TIME

            assert questions.find { it.binding == 'endtime' }.type == 'time'

            assert questions.find { it.binding == 'details_of_children' }.xformType == XformType.REPEAT

            //options
            assert questions.find { it.binding == 'sex' }.options.size() == 2
            assert questions.find { it.binding == 'arvs' }.options.size() == 5



            ['azt', 'abicvar', 'efivarence', 'triomune', 'truvada'].each { binding ->
                assert questions.find { it.binding == 'arvs' }.options.any { option -> binding == option.bind }
            }
        }

        validateType(mkpForm)
        validateType(formFromXml)

    }

    void testGroupTypeIsDetected() {
        def groupedForm = ConversionHelper.oxd2Form(nestedGroups.xml)
        def grp = groupedForm['group_page_1']
        assert grp.xformType == XformType.GROUP
    }

    void testSkipLogic() {
        def form = new XFormDeserializer(xml: advancedMarkedUp.xform).parse()


        form.printAll(System.out)

        IQuestion question = form.getElement('armpain')
        assert question.validationLogic == '. = false() or $chestpain = true()'
        assert question.message == "You can't have angina without chestpain!"
        assert form.getElement('hypertension').skipLogic == '$gender = \'male\''
        assert form.getElement('hypertension').skipAction == 'enable'
        assert form.getElement('diastolic').validationLogic == '. >= 10 and . <= 150'
        assert form.getElement('birthdate').validationLogic == '(today() - .) > (365*18)'

        XFormSerializer ser = new XFormSerializer()

        String newForm = ser.toXForm(form)
        String oldForm = advancedMarkedUp.xform
        assertEquals oldForm, newForm

    }

    void testTopRoundTrips() {
        setFormDirectory()
        testRoundTrip(formWithAbsoluteId)
        testRoundTrip(multipleForms)
        testRoundTrip(formWithEndtime)
        testRoundTrip(oxdSampleForm)
        testRoundTrip(formUsingRelativeBinds)
        testRoundTrip(formWithCSVImport)
        testRoundTrip(formWithDynamicInstanceReferences)
        testRoundTrip(formWithValidationOnInnerRepeat)
        testRoundTrip(skipLogicInRepeat)
        testRoundTrip(skipLogicInRepeat)
        testRoundTrip(formRepeatWithAttributesOnRepeats)
        testRoundTrip(formWithMultiplePage)
        testRoundTrip(formWithSkipLogic)
        testRoundTrip(formWithActionAttributes)
        testRoundTrip(formWithValidationLogic)
        testRoundTrip(normalPurcform)
        testRoundTrip(formWithId)
        testRoundTrip(normalPurcform2)
        serializer.numberBindings = true
        testRoundTrip(wssbForm.markUp)
        testRoundTrip(gpsForm.markUp)
        testRoundTrip(formWithDollarInString)
        testRoundTrip(dynFormWithQuotes.markUp)
        testRoundTrip(formWithLayoutAndBindAttributes)
        testRoundTrip(formWithInvisible.form)
        testRoundTrip(formWithAttribs)


        Study.validateWithXML.set(true)
        testRoundTrip(formWithIncompatibleOXDId.form)
    }

    void testRoundTrip(String markup) {
        def form1 = new MarkupDeserializer(markup).study().forms[0]
        def oxd1 = ConversionHelper.markup2Oxd(markup)

        def form2 = ConversionHelper.oxd2Form(oxd1)
        def oxd2 = ConversionHelper.form2Oxd(form2)

        try {
            assertEquals oxd1, oxd2
        } catch (ComparisonFailure f) {
            System.err.println("Some form failed to pass round trip $form1.name")
            assertXMLEqual oxd1, oxd2
        }

        def odk1 = ConversionHelper.markup2Odk(markup, false, false, false)

        form2 = ConversionHelper.odk2Form(odk1)
        def odk2 = ConversionHelper.form2Odk(form2, false, false, false)

        try {
            assertEquals odk1, odk2
        } catch (ComparisonFailure f) {
            System.err.println("Some form failed to pass round trip $form1.name")
            assertXMLEqual odk1, odk2
        }

    }

    void testDeSerializingWithLayoutAttributesInComment() {
        def form = new XFormDeserializer(formWithLayoutAndBindAttributesToCommentsXML).parse()

        assertXMLEqual formWithLayoutAndBindAttributesXML, new XFormSerializer().toXForm(form)
    }

    void testDeSerializingWithLayoutAttributes() {
        def form = new XFormDeserializer(formWithLayoutAndBindAttributesXML).parse()

        assertXMLEqual formWithLayoutAndBindAttributesXML, new XFormSerializer().toXForm(form)
    }

    void testNestedGroups() {
        testRoundTrip(nestedGroups.markUp)
        testRoundTrip(formWithLayoutAndBindAttributes)
    }

    void testJRNameSpaceHandling() {
        def markup = '''

            ## f

            @id start
            @invisible
            @bind:jr:preload timestamp
            @bind:jr:preloadParams start
            @dateTime
            ...


            @id end
            @invisible
            @bind:jr:preload timestamp
            @bind:jr:preloadParams end
            @dateTime
            ...


            @id today
            @invisible
            @bind:jr:preload date
            @bind:jr:preloadParams today
            @date
            ...
            '''

        testRoundTrip(markup)
    }

}
