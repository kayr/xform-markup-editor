package org.openxdata.markup

import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.exception.DuplicateElementException
import org.openxdata.markup.exception.InvalidAttributeException
import org.openxdata.markup.exception.ValidationException
import org.openxdata.markup.serializer.XFormSerializer

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/31/13
 * Time: 3:33 AM
 * To change this template use File | Settings | File Templates.
 */
class XformParserTest extends GroovyTestCase {

    static def xform = """###Snv Study
##Snv Form

How are you
what is name

What is sex
>Male
>female

Select your diseases
>>AIDS
>>TB
>>Whooping cough

repeat { Repeat Question header
rpt question 1
>sdsd
rpt question 2
>>dsksd
repeat { Hello
sdhosj
}
}

dynamic {
Country,District,School
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
}

##form2
jeelopo
"""
    MarkupDeserializer parser;

    public void setUp() {
        parser = createParser(xform)
    }

    public void testStudyHas2Forms() {

        def study = parser.study()

        assertNotNull study
    }

    public void testTwoFormPresent() {

        def study = parser.study()

        assertEquals 2, study.forms.size()

    }

    public void testQuestionsArePresentInForm() {

        def study = parser.study()

        study.forms.each {
            assertFalse it.allQuestions.isEmpty()
        }
    }

    public void testSingleSelectExits() {
        def study = parser.study()

        SingleSelectQuestion qn = study.forms[0].allQuestions.find { it instanceof SingleSelectQuestion }

        assertNotNull qn

        assertEquals 'Expecting 2 options', 2, qn.options.size()
    }

    public void testSingleSelectDoesNotNewLines() {
        def study = parser.study()

        SingleSelectQuestion qn = study.forms[0].allQuestions.find { it instanceof SingleSelectQuestion }

        assertTrue qn.options.every { !it.text.contains('\n') }


    }

    public void testTwoTxtQuestionsExist() {
        def study = parser.study()

        def txtQuestions = study.forms[0].questions.findAll { it instanceof TextQuestion }

        assertEquals 'Expecting 2 text questions', 2, txtQuestions.size()
    }

    public void testMultiQuestionExists() {

        def study = parser.study()

        MultiSelectQuestion qn = study.forms[0].allQuestions.find { it instanceof MultiSelectQuestion }

        assertNotNull 'Multi Question is expected', qn

        assertEquals 'Expects 3 option in the multisel question', 3, qn.options.size()

    }

    public void testRepeatQuestion() {
        def study = parser.study()

        RepeatQuestion qn = study.forms[0].allElements.find { it instanceof RepeatQuestion }

        assertNotNull qn

        assertEquals 3, qn.questions.size()

        assertNotNull study.forms[0].allElements.find { it instanceof SingleSelectQuestion }

        assertNotNull study.forms[0].allElements.find { it instanceof MultiSelectQuestion }

        assertNotNull study.forms[0].allElements.find { it instanceof RepeatQuestion }

        def allQuestions = study.forms[0].allQuestions
        assertEquals 12, allQuestions.size()
    }

    public void testDynamicQuestion() {
        def study = parser.study()

        def count = study.forms[0].allElements.count { it instanceof DynamicQuestion }

        assertEquals 'Expecting two dyn qns', 2, count
    }

    public void testAllFormsHaveRootStudy() {
        def study = parser.study()

        assertTrue study.forms.every { it.study != null }
    }

    public void testStudyHasName() {
        def study = parser.study()

        assertTrue study.name != null
        assertEquals "Snv Study", study.name
    }

    public void testTypeParsing() {
        def parser = createParser(Fixtures.formWithAttribs)
        def study = parser.study()
        def questions = study.forms[0].allQuestions

        assertEquals "Expecting 1 invisile quesion", 1, questions.count { !it.visible }

        assertEquals 'Expecting 1 number questions', 1, questions.count { it.type == 'number' }

        assertEquals 'Decimal questions', 2, questions.count { it.type == 'decimal' }

        assertEquals 'Expecting 1 video question', 1, questions.count { it.type == 'video' }
    }

    void testFormWithDuplicatesThrowsException() {

        def parser = createParser(Fixtures.formWithDuplicates)

        try {
            def study = parser.study()
            fail('An exception is expected here')
        } catch (DuplicateElementException ex) {

        }
    }

    void testRepeatWithAttributes() {
        def parser = createParser(Fixtures.formRepeatWithAttributesOnRepeats)

        def study = parser.study()

        def qn = study.forms[0].allQuestions[0]
        assertEquals 'child_repeat', qn.binding
        assertEquals 'Details', qn.comment
    }


    void testFormWithSkipLogic() {
        def parser = createParser(Fixtures.formWithSkipLogic)

        def study = parser.study()

        def qn = Form.findQuestionWithBinding('pregnant', study.forms[0])

        assertNotNull qn
        assertEquals 'enable', qn.skipAction
        assertEquals qn.skipLogic, "\$sex = 'female'"

        qn = Form.findQuestionWithBinding('male_question', study.forms[0])

        assertNotNull qn

        assertEquals 'show', qn.skipAction

        assertEquals "\$sex = 'male'", qn.skipLogic

    }

    void testFormWithActionAttributesSkipLogic() {
        def parser = createParser(Fixtures.formWithActionAttributes)

        def study = parser.study()

        def qn = Form.findQuestionWithBinding('pregnant', study.forms[0])

        assertNotNull qn
        assertEquals 'enable', qn.skipAction
        assertEquals qn.skipLogic, "\$sex = 'female'"

        qn = Form.findQuestionWithBinding('male_question', study.forms[0])

        assertNotNull qn

        assertEquals 'show', qn.skipAction

        assertEquals "\$sex = 'male'", qn.skipLogic

    }

    void testFromWithErraticVariableSkipLogic() {
        def parser = createParser(Fixtures.formWithErraticVariableSkipLogic)

        try {
            def study = parser.study()
            fail("Expecting unknown variable exception")
        } catch (ValidationException e) {
            if (!(e.message.contains('unknown variable')))
                throw e
        }

    }

    void testFormWithErraticXPath() {
        def parser = createParser(Fixtures.formWithErraticXPathSkipLogic)

        try {
            parser.study()
            fail('Expecting a recognition exception')
        } catch (ValidationException e) {
            if (!(e.message.contains("Error parsing XPATH")))
                throw e
        }
    }

    void testFormWithValidValidationLogic() {
        def parser = createParser(Fixtures.formWithValidationLogic)

        def study = parser.study()

        def qn = Form.findQuestionWithBinding('age', study.forms[0])

        assertEquals '. > 5', qn.validationLogic

        assertEquals 'valid when greater than 5', qn.message
    }

    void testFromWithValidationLogicNoMessage() {
        def parser = createParser(Fixtures.formWithValidationLogicNoMessage)

        try {
            def study = parser.study()
            fail("Expecting a validation Exception")
        } catch (ValidationException e) {
            if (!(e.message.contains("Validation message has not been set")))
                throw e
        }
    }

    void testFormWithPages() {
        def parser = createParser(Fixtures.formWithMultiplePage)

        def study = parser.study();

        def form = study.forms[0]

        assertEquals 2, form.pages.size()


        assertEquals 3, form.pages[0].questions.size()

        assertEquals 4, form.pages[1].questions.size()

        assertEquals '_1gender', form.getElement('gender').indexedBinding
        assertEquals '_3_1child_name', form.getElement('child_name').indexedBinding
        assertEquals '_3lol', form.getElement('lol').indexedBinding
        assertEquals '_6district', form.getElement('district').indexedBinding

    }


    void testFormWithDuplicatePages() {
        def parser = createParser(Fixtures.formWithDupePages)

        shouldFail(DuplicateElementException) {
            parser.study()
            fail("Expecting duplicate page exception")
        }
    }

    void testDupeQuestionInPagedForm() {
        def parser = createParser(Fixtures.formMultiplePageDupeQuestion)

        try {
            parser.study()
            fail("Expecting duplicate question exception")
        } catch (DuplicateElementException ex) {
            //this is ok
        }
    }

    void testBadSkipLogicInRepeat() {

        def parser = createParser(Fixtures.badSkipLogicInRepeat)

        try {
            def study = parser.study()
            fail("Expectiong a validation Exception")
        } catch (ValidationException ex) {

        }

    }

    void testSkipLogicInRepeat() {

        def parser = createParser(Fixtures.skipLogicInRepeat)
        def study = parser.study()

        def rptQn = study.forms[0].allQuestions.find { it instanceof RepeatQuestion }

        assertNotNull rptQn.questions[0].skipLogic
    }

    void testRequiredWithStarsMatchesAnnotatedRequired() {
        def parser = createParser(Fixtures.requiredTwo)

        def parser2 = createParser(Fixtures.requiredQns)

        def study1 = parser.study()

        def study2 = parser2.study()

        XFormSerializer ser = new XFormSerializer()

        def xml1 = ser.toStudyXml(study1)

        def xml2 = ser.toStudyXml(study2)

        assertEquals xml1, xml2
    }

    void testDuplicateRepeatWithChild() {
        def parser = createParser(Fixtures.formRepeatChildDuplicates)

        try {
            def study = parser.study()
            fail('Expecting duplicate question Exception')
        } catch (DuplicateElementException e) {
            assertEquals e.question1.binding, e.question2.binding
        }
    }

    void testDynamicWithInstanceVariables() {
        def form1 = new MarkupDeserializer(Fixtures.normalPurcform).study().forms[0]

        def form2 = new MarkupDeserializer(Fixtures.normalPurcform2).study().forms[0]

        XFormSerializer ser = new XFormSerializer()

        def xForm1 = ser.toXForm(form1)
        def xForm2 = ser.toXForm(form2)

        assertEquals xForm1, xForm2


    }

    void testFormWithValidationAChildOfARepeat() {
        def form = new MarkupDeserializer(Fixtures.formWithValidationOnInnerRepeat).study().forms[0]
        assertEquals form.allQuestions.size(), 5

    }

    void testForDynamicInstanceValidation() {
        def form = createParser(Fixtures.formWithDynamicInstanceReferences).study().forms[0]

        DynamicQuestion dynamicQuestion = Form.findQuestionWithBinding("subregion", form)

        assertNotNull dynamicQuestion

        assertEquals 'subregion', dynamicQuestion.binding

        assertEquals 'region', dynamicQuestion.parentQuestionId

        assertEquals 'subregion2', dynamicQuestion.dynamicInstanceId

        def oldId = dynamicQuestion.dynamicInstanceId
        dynamicQuestion.dynamicInstanceId = 'blah'
        try {
            form.validate()
            fail("Expecting a Validation Exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("Instance ID[blah] does not exit in the form")
        }
        dynamicQuestion.dynamicInstanceId = oldId



        oldId = dynamicQuestion.parentQuestionId
        dynamicQuestion.parentQuestionId = null;
        try {
            form.validate()
            fail("Expectiong a Validation Exception")
        } catch (ValidationException ex) {
            assertTrue ex.message.contains("Please set the parent using the [@parent] attribute")
        }
        dynamicQuestion.parentQuestionId = oldId


        oldId = dynamicQuestion.parentQuestionId
        dynamicQuestion.parentQuestionId = 'blahDynamicInstance';
        try {
            form.validate()
            fail("Expectiong a Validation Exception")
        } catch (InvalidAttributeException ex) {
            assertTrue ex.message.contains("has an invalid parent question id")
        }
        dynamicQuestion.parentQuestionId = oldId


    }

    void testCSVImport() {

        Fixtures.setFormDirectory()

        def form = createParser(Fixtures.formWithCSVImport).study().forms[0]

        assertEquals 1, form.allQuestions.size()
        assertEquals 1, form.dynamicOptions.size()
    }

    void testMultilineParsing() {

        def form = createParser(Fixtures.form_With_Multiline).study().forms.first()

        assertEquals form.allQuestions.size(), 7
    }

    void testNoStudyInMarkup() {
        def markup = '''## form
Q1
'''

        def form = ConversionHelper.markup2Form(markup)

        assert form.binding == 'form_v1'
        assert form.allQuestions.size() == 1
    }


    private MarkupDeserializer createParser(String testString) throws IOException {
        return new MarkupDeserializer(testString)
    }


}
