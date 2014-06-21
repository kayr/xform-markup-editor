package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures
import org.openxdata.markup.Form
import org.openxdata.markup.Util

/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializerTest extends GroovyTestCase {

    void testTopMarkUp() {
        def study = Util.createParser(Fixtures.formWithValidationOnInnerRepeat).study()

        println MarkupAligner.align(MarkUpSerializer.toStudy(study))

    }
}
