package org.openxdata.markup.deserializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study

/**
 * Created by kay on 5/26/2016.
 */
class MarkupDeserializerTest extends GroovyTestCase {


    void testParse() {

        println("""""")
        def ser = new MarkupDeserializer(Fixtures.formWithDynamicInstanceReferences)


        def parse = ser.parse()

    }
}
