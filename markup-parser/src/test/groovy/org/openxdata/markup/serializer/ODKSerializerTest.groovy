package org.openxdata.markup.serializer

import org.openxdata.markup.Form
import org.openxdata.markup.Util

import static org.openxdata.markup.serializer.ODKFixtures.*

/**
 * Created by kay on 7/13/14.
 */
class ODKSerializerTest extends GroovyTestCase {
    def serializer = new ODKSerializer();

    void testReadonlyAndInvisibleIsConvertedToReadonly() {
        assertEquals toODK(formWithInvisible.form), formWithInvisible.xml
    }

    void testReadonlyAndSkipLogicAreProcessedOk() {
        assertEquals toODK(formWithSkipLogicAndReadOnly.form), formWithSkipLogicAndReadOnly.xml
    }

    void testStartTimeAnd() {
        assertEquals toODK(timeStamp.form, true), timeStamp.xml
    }

    void testOxdSampleForm() {
        println toODK(oxdSampleForm.form, true)
//        assertEquals toODK(timeStamp.form,true), timeStamp.xml
    }


    String toODK(String markup, boolean oxd = false) {
        toODK(toForm(markup), oxd)
    }

    static Form toForm(String markup) {
        Util.createParser(markup).study().forms[0]
    }

    String toODK(Form form, boolean oxd = false) {
        serializer.oxdConversion = oxd
        serializer.toXForm(form)
    }
}
