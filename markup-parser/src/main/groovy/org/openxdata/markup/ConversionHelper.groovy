package org.openxdata.markup
/**
 * Deprecated use {@link Converter}
 */
@Deprecated
class ConversionHelper {
    static Form markup2Form(String markup) {
        return Converter.markup2Form(markup)
    }

    static String markup2Odk(String markup, boolean number = false, boolean oxd = false, boolean addMetaInstance = false) {

        form2Odk(markup2Form(markup), number, oxd, addMetaInstance)
    }

    static String form2Odk(Form form, boolean number = false, boolean oxd = false, boolean addMetaInstance = true) {

        def flags = FLAGS.none()

        if (number) flags << FLAGS.NUMBER_IDS << FLAGS.NUMBER_LABELS
        if (oxd) flags << FLAGS.ODK_OXD_MODE
        if (addMetaInstance) flags << FLAGS.ODK_ADD_META_INSTANCE

        return Converter.fromFormTo(FORMAT.ODK, form, flags)
    }

    static Form odk2Form(String xml) {
        return Converter.toFormFrom(FORMAT.ODK, xml)
    }

    static Form oxd2Form(String xml) {
        return Converter.toFormFrom(FORMAT.OXD, xml)
    }

    static String oxd2Odk(String xml) {
        return Converter.to(FORMAT.OXD, FORMAT.ODK, xml)
    }


    static String markup2Oxd(String markup, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {
        form2Oxd(markup2Form(markup), numberQuestions, putExtraAttributesInComments)
    }


    static String form2Oxd(Form form, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {

        def flags = FLAGS.none()

        if (numberQuestions) flags << FLAGS.NUMBER_IDS << FLAGS.NUMBER_LABELS
        if (putExtraAttributesInComments) flags << FLAGS.OXD_EXTRA_ATTRIBUTES_IN_COMMENTS

        return Converter.fromFormTo(FORMAT.OXD, form, flags)
    }

    static Form oxd2FormWithCache(String xform, def cacheID = xform, boolean pClearCache = false) {

        def flags = FLAGS.of(FLAGS.USE_CACHE)

        if (pClearCache) flags << FLAGS.CLEAR_CACHE

        return Converter.toFormFrom(FORMAT.OXD, xform, flags, cacheID)

    }


}

