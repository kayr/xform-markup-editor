package org.openxdata.markup

import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.deserializer.XFormDeserializer
import org.openxdata.markup.serializer.ODKSerializer
import org.openxdata.markup.serializer.XFormSerializer

/**
 * Created by kay on 6/29/2016.
 */
class TestUtils {
    static Form toForm(String markup) {
        new MarkupDeserializer(markup).study().forms[0]
    }

    static String toODK(String markup, boolean number = false, boolean oxd = false, boolean addMetaInstance = false) {
        toODK(toForm(markup), number, oxd, addMetaInstance)
    }

    static String toODK(Form form, boolean number = false, boolean oxd = false, boolean addMetaInstance = true) {
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


    static String toOXD(String markup, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {
        toOXD(toForm(markup), numberQuestions, putExtraAttributesInComments)
    }


    static String toOXD(Form form, boolean numberQuestions = false, boolean putExtraAttributesInComments = false) {
        def serializer = new XFormSerializer()
        serializer.numberBindings = numberQuestions
        serializer.numberBindings = numberQuestions
        serializer.putExtraAttributesInComments = putExtraAttributesInComments
        serializer.toXForm(form)
    }
}
