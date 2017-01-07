package org.openxdata.markup

/**
 * Created by kay on 1/7/2017.
 */
class ConverterTest extends GroovyTestCase {


    void testToFrom() {

        def ALL_FORMATS = EnumSet.allOf(FORMAT)
        def BASE_UNIT = FORMAT.ODK
        def BASE_TEXT = Converter.to(BASE_UNIT).from(FORMAT.MARKUP).convert(Fixtures.oxdSampleForm)

        def flags = FLAGS.of(FLAGS.VALIDATE_FORM)

        for (toFormat in ALL_FORMATS) {
            def toText = Converter.to(toFormat).from(BASE_UNIT).flags(flags).convert(BASE_TEXT)
            def reverseText = Converter.from(toFormat).to(BASE_UNIT).convert(toText)
            assertEquals BASE_TEXT, reverseText
        }


    }
}
