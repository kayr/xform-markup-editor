package org.openxdata.markup

import org.openxdata.markup.exception.InvalidAttributeException

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

        Attrib.addAttribute(qn, "number")

        assertEquals qn.type, 'number'

        Attrib.addAttribute(qn, 'invisible')

        assertFalse qn.isVisible()

        Attrib.addAttribute(qn, 'dateTime')

        assertEquals 'dateTime',qn.type

        try {
            Attrib.addAttribute(sn, 'number')
            fail("Expecting an exception")
        } catch (InvalidAttributeException ex) {

        }

        Attrib.addAttribute(sn,'required')
        assertTrue sn.required

        Attrib.addAttribute(qn,"comment This is a comment")

        assertEquals "This is a comment",qn.comment

        Attrib.addAttribute(qn,'id hello_Question')


        assertEquals 'hello_question',qn.binding

        qn.setText("Hahahahha")    //Make sure the binding does not change since the parser set the question later

        assertEquals 'hello_question',qn.binding

        try {
            Attrib.addAttribute(qn, 'id jsjk sdj sdj')
            fail('Expecting an exception for an invalid attribute')
        } catch (Exception ex) {
            assertTrue ex instanceof InvalidAttributeException
        }

        try{
            Attrib.addAttribute(sn,'parent blah_blah')
            Attrib.addAttribute(qn,'parent blah_blah')
            Attrib.addAttribute(dn,'parent blah blah')
            fail('Expecting an exception here')
        }   catch (InvalidAttributeException ex){

        }

        Attrib.addAttribute(dn,'parent blah_blah')
        assertEquals 'blah_blah',dn.parentQuestionId


    }

    void testSetAttribOnForm() {

        Form form = new Form("form")

        Attrib.addAttributeToForm(form, 'id someid')

        assertEquals 'someid', form.id

        try {
            Attrib.addAttributeToForm(form, 'someattrib someid')
            fail("Expecting ${InvalidAttributeException.class}")
        } catch (InvalidAttributeException e) {
            assertEquals "Attribute someattrib on form $form.name in not supported", e.message
        }

        try {
            Attrib.addAttributeToForm(form, 'id UPPERCASEID')
            fail("Expecting ${InvalidAttributeException.class}")
        } catch (InvalidAttributeException e) {
              assertTrue e.message.startsWith('You have an invalid variable')
        }

    }
}
