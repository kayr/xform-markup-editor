package org.openxdata.markup.deserializer

import org.openxdata.markup.TestUtils
import org.openxdata.markup.serializer.MarkUpSerializer

/**
 * Created by kay on 1/1/2017.
 */
class ODKDeSerializerTest extends GroovyTestCase {
    void testParsingTide() {
        def f = new ODKDeSerializer(TestUtils.loadResourceText('ODKDeserializer/TIDE_farmassessment_pilot_final_v1.xml')).parse()
        def markup = TestUtils.trimAllLines(MarkUpSerializer.toFormMarkUp(f))

        def expected = TestUtils.loadResourceText('ODKDeserializer/TIDE_farmassessment_pilot_final_v1.xfm')
        expected = TestUtils.trimAllLines(expected)

        assertEquals expected, markup
    }
}
