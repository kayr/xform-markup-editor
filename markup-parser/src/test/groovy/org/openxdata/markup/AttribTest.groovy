package org.openxdata.markup

import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.exception.InvalidAttributeException

import static ConversionHelper.markup2Form
import static org.openxdata.markup.ConversionHelper.markup2Oxd

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/9/13
 * Time: 1:49 PM
 * To change this template use File | Settings | File Templates.
 */
class AttribTest extends GroovyTestCase {

    TextQuestion qn = new TextQuestion("Hello")
    SingleSelectQuestion sn = new SingleSelectQuestion("Other")
    DynamicQuestion dn = new DynamicQuestion("Dynamic")

    void testAddAttribute() {

        Attrib.addAttribute(qn, "number", 1)

        assertEquals qn.type, 'number'

        //Test attributes are converted to lower case
        Attrib.addAttribute(qn, "Number", 1)

        assertEquals 'attributes are supposed to be converted to lower case', qn.type, 'number'

        Attrib.addAttribute(qn, 'invisible', 1)

        assertFalse qn.isVisible()

        Attrib.addAttribute(qn, 'dateTime', 1)

        assertEquals 'dateTime', qn.type

        try {
            Attrib.addAttribute(sn, 'number', 1)
            fail("Expecting an exception")
        } catch (InvalidAttributeException ex) {

        }

        Attrib.addAttribute(sn, 'required', 1)
        assertTrue sn.required

        Attrib.addAttribute(qn, "comment This is a comment", 1)

        assertEquals "This is a comment", qn.comment

        Attrib.addAttribute(qn, 'id hello_question', 1)
        assertEquals 'hello_question', qn.binding

        qn.setText("Hahahahha")    //Make sure the binding does not change since the parser set the question later

        assertEquals 'hello_question', qn.binding

        try {
            Attrib.addAttribute(qn, 'id jsjk sdj sdj', 1)
            fail('Expecting an exception for an invalid attribute')
        } catch (Exception ex) {
            assertTrue ex instanceof InvalidAttributeException
        }

        try {
            Attrib.addAttribute(qn, 'id hello_Question', 1)
            fail('Expecting an exception for an invalid attribute')
        } catch (Exception ex) {
            assertTrue ex instanceof InvalidAttributeException
        }

        try {
            Attrib.addAttribute(sn, 'parent blah_blah', 1)
            fail('Expecting an exception here')
        } catch (InvalidAttributeException ex) {

        }

        try {
            Attrib.addAttribute(qn, 'parent blah_blah', 1)
            fail('Expecting an exception here')
        } catch (InvalidAttributeException ex) {

        }

        try {
            Attrib.addAttribute(dn, 'parent blah blah', 1)
            fail('Expecting an exception here')
        } catch (InvalidAttributeException ex) {

        }

        Attrib.addAttribute(dn, 'parent blah_blah', 1)
        assertEquals 'blah_blah', dn.parentQuestionId


    }

    void testAddLayoutAndBindAttributes() {
        Attrib.addAttribute(qn, 'bind:bind', 1)
        assert qn.bindAttributes['bind'] == 'true'

        Attrib.addAttribute(qn, 'layout:layout', 1)
        assert qn.layoutAttributes['layout'] == 'true'

        Attrib.addAttribute(qn, 'layout:layout2 value two', 1)
        assert qn.layoutAttributes['layout2'] == 'value two'

        Attrib.addAttribute(qn, 'bind:bind2 anyval', 1)
        assert qn.bindAttributes['bind2'] == 'anyval'


        try {
            Attrib.addAttribute(qn, 'bind: bind3 anyval', 1)
            fail('Expecting invalid bind attribute')
        } catch (Exception x) {
            assert x.message.contains('Invalid Bind Attribute')
        }

        try {
            Attrib.addAttribute(qn, 'layout: layout anyval', 1)
            fail('Expecting invalid layout attribute')
        } catch (Exception x) {
            assert x.message.contains('Invalid Layout Attribute')
        }


    }

    void testSetAttribOnForm() {

        Form form = new Form("form")

        Attrib.addAttributeToForm(form, 'id someid', 1)

        assertEquals 'someid', form.id

        try {
            Attrib.addAttributeToForm(form, 'someattrib someid', 1)
            fail("Expecting ${InvalidAttributeException.class}")
        } catch (InvalidAttributeException e) {
            assertEquals "[Line:1:] Attribute someattrib on form $form.name in not supported", e.message
        }

        try {
            Attrib.addAttributeToForm(form, 'id UPPERCASEID', 1)
            fail("Expecting ${InvalidAttributeException.class}")
        } catch (InvalidAttributeException e) {
            assertTrue e.message.startsWith('[Line:1:] You have an invalid variable')
        }

    }

    void testLineNumbers() {

        ParserUtils.printTree(MarkupDeserializer.createAST(Fixtures.oxdSampleForm))



        def parser = new MarkupDeserializer(Fixtures.oxdSampleForm)
        def form = parser.study().forms[0]

        def questions = form.allQuestions

//        assert form.firstPage.line == 3
        assert form.line == 3
        assert questions.find { it.binding == 'patient_id' }.line == 6
        assert questions.find { it.binding == 'title' }.line == 8
        assert questions.find { it.binding == 'first_name' }.line == 13
        assert questions.find { it.binding == 'last_name' }.line == 16
        assert questions.find { it.binding == 'sex' }.line == 18
        assert questions.find { it.binding == 'birthdate' }.line == 26
        assert questions.find { it.binding == 'weightkg' }.line == 31
        assert questions.find { it.binding == 'height' }.line == 36
        assert questions.find { it.binding == 'is_patient_pregnant' }.line == 39
        assert questions.find { it.binding == 'arvs' }.line == 42
        assert questions.find { it.binding == 'picture' }.line == 50
        assert questions.find { it.binding == 'sound' }.line == 53
        assert questions.find { it.binding == 'record_video' }.line == 56
        assert questions.find { it.binding == 'region' }.line == 58
        assert questions.find { it.binding == 'sub_hyphen_region' }.line == 58
        assert questions.find { it.binding == 'city' }.line == 58
        assert questions.find { it.binding == 'children_number' }.line == 80
        assert questions.find { it.binding == 'details_of_children' }.line == 86
        assert questions.find { it.binding == 'name' }.line == 87
        assert questions.find { it.binding == 'age' }.line == 90
        assert questions.find { it.binding == 'child_sex' }.line == 93
        assert questions.find { it.binding == 'start_time' }.line == 101
        assert questions.find { it.binding == 'endtime' }.line == 105

//        form.allQuestions.each {
//            println "form.questions.find {it.binding == '$it.binding' }.line == $it.line"
//        }
    }

    void testJRCountCannotBeSetOnOtherQuestion() {
        def f = '''### s\n## f\n @jrcount\nq1'''
        try {
            markup2Form(f)
            fail('Expecting validation exception')
        } catch (InvalidAttributeException e) {
            assert e.message.contains('be set on Repeat Question')
        }
    }

    void testVersionIsSetOnForm() {
        def f = '''
                @version v3
                ## f
                one'''

        def xml = '<xforms>\n  <model>\n    <instance id="f_v3">\n      <f_v3 id="0" name="f" formKey="f_v3">\n        <one />\n      </f_v3>\n    </instance>\n    <bind id="one" nodeset="/f_v3/one" type="xsd:string" />\n  </model>\n  <group id="1">\n    <label>Page1</label>\n    <input bind="one">\n      <label>one</label>\n    </input>\n  </group>\n</xforms>'

        assertEquals xml, markup2Oxd(f)
    }

    void testFormAttribsAreSet() {
        def f = '''
                @dbid 3
                @id f2
                ## f
                one'''

        def form = markup2Form(f)

        assert form.dbIdLine == 2
        assert form.idLine == 3
        assert form.line == 4
    }


}
