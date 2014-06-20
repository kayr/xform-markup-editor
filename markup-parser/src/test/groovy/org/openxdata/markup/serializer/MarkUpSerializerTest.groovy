package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Form
import org.openxdata.markup.Util

/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializerTest extends GroovyTestCase {

    void testTopMarkUp() {
        Form form = Util.createParser(Fixtures.oxdSampleForm).study().forms[0]

        println MarkupAligner.align(MarkUpSerializer.toMarkUp(form))

    }
}
