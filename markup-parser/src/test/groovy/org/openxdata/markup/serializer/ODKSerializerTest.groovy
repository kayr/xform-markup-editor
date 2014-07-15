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
        println toODK(formWithInvisible.form)
        assertEquals toODK(formWithInvisible.form), formWithInvisible.xml
    }

    void testReadonlyAndSkipLogicAreProcessedOk() {
        println toODK(formWithSkipLogicAndReadOnly.form)
        assertEquals toODK(formWithSkipLogicAndReadOnly.form), formWithSkipLogicAndReadOnly.xml
    }


    String toODK(String markup) {
        toODK(toForm(markup))
    }

    static Form toForm(String markup) {
        Util.createParser(markup).study().forms[0]
    }

    String toODK(Form form) {
        serializer.toXForm(form)
    }
}
