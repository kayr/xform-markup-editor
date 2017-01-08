package org.openxdata.markup

/**
 * Created by kay on 1/7/2017.
 */
class ConverterTest extends GroovyTestCase {

    def ALL_FORMATS = EnumSet.allOf(FORMAT)
    def BASE_UNIT = FORMAT.ODK
    def BASE_OUTPUT = Converter.to(BASE_UNIT).from(FORMAT.MARKUP).convert(Fixtures.oxdSampleForm)


    void testAllPathsAndFormatsReturnTheRightData() {

        def flags = FLAGS.of(FLAGS.VALIDATE_FORM)

        for (toFormat in ALL_FORMATS) {
            def toText = Converter.to(toFormat).from(BASE_UNIT).flags(flags).convert(BASE_OUTPUT)
            def reverseText = Converter.from(toFormat).to(BASE_UNIT).convert(toText)
            assertEquals BASE_OUTPUT, reverseText
        }
    }

    void testCacheIsUsed() {
        def convert = { Converter.to(FORMAT.MARKUP).from(BASE_UNIT).convert(BASE_OUTPUT, FLAGS.USE_CACHE) }

        10.times { convert() }

        assert Converter.cacheHits == 9
        assert Converter.cacheSize() == 3
        assert Converter.cacheMiss == 3

    }

    void testCacheIsUsedWithCustomKey() {
        def convert = { Converter.to(FORMAT.MARKUP).from(BASE_UNIT).cacheId(0).convert(BASE_OUTPUT, FLAGS.USE_CACHE) }

        10.times { convert() }

        assert Converter.cacheHits == 9
        assert Converter.cacheSize() == 3
        assert Converter.cacheMiss == 3

        assert [[0, FORMAT.MARKUP],
                [0, FORMAT.ODK],
                [0, FORMAT.MARKUP, FORMAT.ODK]].containsAll(Converter.cacheKeys())
    }

    void testCacheIsCleared() {
        def convert = { Converter.to(FORMAT.MARKUP).from(BASE_UNIT).cacheId(0).convert(BASE_OUTPUT, FLAGS.USE_CACHE) }

        10.times { convert() }

        assert Converter.cacheSize() == 3

        Converter.to(FORMAT.MARKUP)
                 .from(BASE_UNIT)
                 .cacheId(0)
                 .convert(BASE_OUTPUT, FLAGS.CLEAR_CACHE)

        assert Converter.cacheSize() == 0


    }

    void testCacheSecondaryCacheIsUsed() {
        Converter.to(FORMAT.MARKUP).from(BASE_UNIT).cacheId(0).convert(BASE_OUTPUT, FLAGS.USE_CACHE)



        assert Converter.cacheSize() == 3
        assert Converter.cacheHits == 0
        assert Converter.cacheMiss == 3


        def form = Converter.toFormFrom(BASE_UNIT, BASE_OUTPUT, FLAGS.of(FLAGS.USE_CACHE), 0)

        assert Converter.cacheHits == 1
        assert Converter.cacheMiss == 3

        Converter.fromFormTo(FORMAT.MARKUP, form, FLAGS.of(FLAGS.USE_CACHE), 0)

        assert Converter.cacheHits == 2
        assert Converter.cacheMiss == 3

    }
}
