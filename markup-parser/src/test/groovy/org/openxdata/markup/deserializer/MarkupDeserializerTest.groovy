package org.openxdata.markup.deserializer
/**
 * Created by kay on 5/26/2016.
 */
class MarkupDeserializerTest extends GroovyTestCase {


    void testParse() {

        def erraticForm = """### fkfkf

## fjfjf
uu
   '''sdsdsd
ONew

sdsdsd''"""
        def ser = new MarkupDeserializer(erraticForm)


        shouldFail(RuntimeException) {
            ser.parse()
        }

    }
}
