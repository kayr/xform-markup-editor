package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Study
import org.openxdata.markup.Util

/**
 * Created by kay on 7/13/14.
 */
class ODKSerializerTest extends GroovyTestCase {
    private Study study
    def serializer = new ODKSerializer();

    void setUp() {

        def parser = Util.createParser(Fixtures.normalPurcform)
        study = parser.study()

    }


    void testToXForm() {
        def form = study.forms[0]

        def xml = serializer.toXForm(form)

        println xml

//        assertEquals Fixtures.expectedXForm, xml
    }
}
