package org.openxdata.markup

import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.deserializer.XFormDeserializer
import org.openxdata.markup.serializer.ODKSerializer
import org.openxdata.markup.serializer.XFormSerializer

import java.util.concurrent.ConcurrentHashMap

/**
 * Created by kay on 6/29/2016.
 */
class ConversionHelper {
    static Form markup2Form(String markup) {
        new MarkupDeserializer(markup).study().forms[0]
    }

    static String markup2Odk(String markup, boolean number = false, boolean oxd = false, boolean addMetaInstance = false) {
        form2Odk(markup2Form(markup), number, oxd, addMetaInstance)
    }

    static String form2Odk(Form form, boolean number = false, boolean oxd = false, boolean addMetaInstance = true) {
        ODKSerializer serializer = new ODKSerializer()
        serializer.oxdConversion = oxd
        serializer.addMetaInstanceId = addMetaInstance
        serializer.numberQuestions = number
        serializer.numberBindings = number
        serializer.toXForm(form)
    }

    static Form oxd2Form(String xml) {
        new XFormDeserializer(xml).parse()
    }

    static String oxd2Odk(String xml) {
        ODKSerializer.oxd2Odk(xml)
    }


    static String markup2Oxd(String markup, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {
        form2Oxd(markup2Form(markup), numberQuestions, putExtraAttributesInComments)
    }


    static String form2Oxd(Form form, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {
        def serializer = new XFormSerializer()
        serializer.numberBindings = numberQuestions
        serializer.numberBindings = numberQuestions
        serializer.putExtraAttributesInComments = putExtraAttributesInComments
        serializer.toXForm(form)
    }

    private static def cache = new ConcurrentHashMap<String, Form>()

    static Form oxd2FormWithCache(String xform, boolean pClearCache = true) {

        if (pClearCache) {
            clearCache()
        }

        def cachedForm = cache[xform]
        if (!cachedForm) {
            cachedForm = new XFormDeserializer(xform).parse()
            cache[xform] = cachedForm
        }

        return cachedForm
    }

    static void clearCache() { cache.clear() }
}
