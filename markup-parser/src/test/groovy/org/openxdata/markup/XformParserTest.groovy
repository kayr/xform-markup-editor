package org.openxdata.markup

import org.antlr.runtime.CharStream
import org.antlr.runtime.ANTLRStringStream
import org.antlr.runtime.CommonTokenStream

import org.openxdata.markup.exception.DuplicateQuestionException
import org.openxdata.markup.exception.ValidationException

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

>>>>Repeat Question header
rpt question 1
>sdsd
rpt question 2
>>dsksd
>>>>Hello
sdhosj
>>>>
>>>>

>>>
Country,District,School
Uganda,Kampala,Macos
Kenya,Nairobi,Machaccos
Uganda,Kampala,Bugiroad
Kenya,Kampala,Bugiroad
>>>

##form2
jeelopo
"""
    XformParser parser;

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
            assertFalse it.questions.isEmpty()
        }
    }

    public void testSingleSelectExits() {
        def study = parser.study()

        SingleSelectQuestion qn = study.forms[0].questions.find { it instanceof SingleSelectQuestion }

        assertNotNull qn

        assertEquals 'Expecting 2 options', 2, qn.options.size()
    }

    public void testSingleSelectDoesNotNewLines() {
        def study = parser.study()

        SingleSelectQuestion qn = study.forms[0].questions.find { it instanceof SingleSelectQuestion }

        assertTrue qn.options.every {!it.text.contains('\n')}


    }

    public void testTwoTxtQuestionsExist() {
        def study = parser.study()

        def txtQuestions = study.forms[0].questions.findAll {it instanceof TextQuestion}

        assertEquals 'Expecting 2 text questions', 2, txtQuestions.size()
    }

    public void testMultiQuestionExists() {

        def study = parser.study()

        MultiSelectQuestion qn = study.forms[0].questions.find {it instanceof MultiSelectQuestion}

        assertNotNull 'Multi Question is expected', qn

        assertEquals 'Expects 3 option in the multisel question', 3, qn.options.size()

    }

    public void testRepeatQuestion() {
        def study = parser.study()

        RepeatQuestion qn = study.forms[0].questions.find {it instanceof RepeatQuestion}

        assertNotNull qn

        assertEquals 3, qn.questions.size()

        assertNotNull study.forms[0].questions.find {it instanceof SingleSelectQuestion}

        assertNotNull study.forms[0].questions.find {it instanceof MultiSelectQuestion}

        assertNotNull study.forms[0].questions.find {it instanceof RepeatQuestion}
    }

    public void testDynamicQuestion() {
        def study = parser.study()

        def count = study.forms[0].questions.count {it instanceof DynamicQuestion}

        assertEquals 'Expecting two dyn qns', 2, count
    }

    public void testAllFormsHaveRootStudy() {
        def study = parser.study()

        assertTrue study.forms.every { it.study != null}
    }

    public void testStudyHasName() {
        def study = parser.study()

        assertTrue study.name != null
        assertEquals "Snv Study", study.name
    }

    public void testTypeParsing() {
        def parser = createParser(Fixtures.formWithAttribs)
        def study = parser.study()
        def questions = study.forms[0].questions

        assertEquals "Expecting 1 invisile quesion", 1, questions.count {!it.visible}

        assertEquals 'Expecting 1 number questions', 1, questions.count {it.type == 'number'}

        assertEquals 'Decimal questions', 2, questions.count {it.type == 'decimal'}

        assertEquals 'Expecting 1 video question', 1, questions.count {it.type == 'video'}
    }

    void testFormWithDuplicatesThrowsException() {

        def parser = createParser(Fixtures.formWithDuplicates)

        try {
            def study = parser.study()
            fail('An exception is expected here')
        } catch (DuplicateQuestionException ex) {

        }
    }

    void testRepeatWithAttributes() {
        def parser = createParser(Fixtures.formRepeatWithAttributesOnRepeats)

        def study = parser.study()

        def qn = study.forms[0].questions[0]
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
            fail("Expecting unkown variable exception")
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

        assertEquals  '. > 5',qn.validationLogic

        assertEquals  'valid when greater than 5',qn.message
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

   void testFormWithPages(){
        def parser = createParser(Fixtures.formWithMultiplePage)

       def study = parser.study();

       def form = study.forms[0]

       assertEquals 2,form.pages.size()

       assertEquals 3 , form.pages[0].questions.size()

       assertEquals 4 , form.pages[1].questions.size()

    }

    void testFormWithDuplicatePages(){
        def parser = createParser(Fixtures.formWithDupePages)

        try{
             parser.study()
            fail("Expecting duplicate page exception")
        }   catch(ValidationException ex){
            assertTrue ex.message.startsWith("Duplicate pages")
        }
    }

    void testDupeQuestionInPagedForm(){
        def parser = createParser(Fixtures.formMultiplePageDupeQuestion)

        try{
            parser.study()
            fail("Expecting duplicate question exception")
        }catch (DuplicateQuestionException ex){
            //this is ok
        }
    }

    private XformParser createParser(String testString) throws IOException {
        CharStream stream = new ANTLRStringStream(testString);
        XformLexer lexer = new XformLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        XformParser parser = new XformParser(tokens);

        return parser;
    }




}
