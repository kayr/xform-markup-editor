package org.openxdata.markup

import org.openxdata.markup.deserializer.MarkupDeserializer
import org.openxdata.markup.deserializer.ODKDeSerializer
import org.openxdata.markup.deserializer.StudyDeSerializer
import org.openxdata.markup.deserializer.XFormDeserializer
import org.openxdata.markup.serializer.MarkUpSerializer
import org.openxdata.markup.serializer.ODKSerializer
import org.openxdata.markup.serializer.XFormSerializer

import java.util.concurrent.ConcurrentHashMap

/**
 * Convenience class for converting between the different formats
 * Created by kay on 1/6/2017.
 */
class Converter {


    private static Map<FORMAT, Closure<Form>> TO_FORM = [:]
    private static Map<FORMAT, Closure<String>> FROM_BASE = [:]

    static class ConverterBuilder<T> {
        FORMAT to
        FORMAT from
        private EnumSet<FLAGS> _flags = FLAGS.none()
        def cacheId

        ConverterBuilder<T> from(FORMAT from) {
            this.from = from
            return this
        }

        ConverterBuilder<T> to(FORMAT to) {
            this.to = to
            return this
        }

        ConverterBuilder<T> cacheId(cacheId) {
            this.cacheId = cacheId
            return this
        }

        def <T> ConverterBuilder<T> returnType(Class<T> classype) {
            return this
        }

        T convert(src, FLAGS flag, FLAGS... flags) {
            convert(src, FLAGS.of(flag, flags))
        }

        T convert(src, EnumSet<FLAGS> flags = _flags) {
            if (to && from) {
                //noinspection UnnecessaryQualifiedReference
                return (T) Converter.to(to, from, src, flags, cacheId)
            }

            if (to) {
                return (T) fromFormTo(this.to, src as Form, flags, cacheId)
            }

            if (from) {
                return (T) toFormFrom(from, src, flags, cacheId)
            }

            throw new RuntimeException("Cannot Convert neither (TO) nor (FROM) was set")
        }


        ConverterBuilder<T> flags(FLAGS... flags) {
            _flags.addAll(flags)
            return this
        }

        ConverterBuilder<T> flags(EnumSet<FLAGS> flags) {
            _flags.addAll(flags)
            return this
        }

        @SuppressWarnings("GroovyUnusedDeclaration")
        ConverterBuilder log() {
            println("Covert $from -> $to ")
            println("   1). $from -> $FORMAT.FORM ")
            println("   2). $FORMAT.FORM -> $to ")
            return this
        }

    }

    static <T> ConverterBuilder to(FORMAT to, Class<T> aClass = String) {
        return new ConverterBuilder<T>(to: to)
    }

    static <T> ConverterBuilder<T> from(FORMAT from, Class<T> aClass = String) {
        return new ConverterBuilder<T>(from: from)
    }

    static <T> T to(FORMAT toFormat, FORMAT from, src) {
        to(toFormat, from, src, FLAGS.none(), null)
    }

    static <T> T to(FORMAT toFormat, FORMAT from, src, cacheId) {
        to(toFormat, from, src, FLAGS.none(), cacheId)
    }

    static <T> T to(FORMAT toFormat, FORMAT from, src, EnumSet<FLAGS> flags) {
        to(toFormat, from, src, flags, null)
    }

    static  <T> T to(FORMAT to, FORMAT from,  src, EnumSet<FLAGS> flags,  cacheId) {

        def key = generateKey(notNull(cacheId, src), to, from)



        def flagsCopy = flags.clone()

        def result = fromCacheOrEval(key, flagsCopy) {
            def form = toFormFrom(from, src, flagsCopy, cacheId)

            if (to == FORMAT.FORM) return form
            flagsCopy.remove(FLAGS.VALIDATE_FORM)
            return fromFormTo(to, form, flagsCopy, cacheId)
        }


        return result as T
    }

    private static <T> T notNull(... objects) {
        return (T) objects.findResult(Closure.IDENTITY)
    }


    static Form toFormFrom(FORMAT from, src, EnumSet<FLAGS> flags = FLAGS.none(), cacheId = null) {
        def func = TO_FORM[from]
        def key = generateKey(notNull(cacheId, src), from)

        def form = fromCacheOrEval(key, flags) { func.call(src, flags) }

        if (FLAGS.VALIDATE_FORM in flags) { validateForm(flags, form) }

        return form
    }

    static fromFormTo(FORMAT to, Form src, EnumSet<FLAGS> flags = FLAGS.none(), cacheId = null) {
        if (FLAGS.VALIDATE_FORM in flags) { validateForm(flags, src) }

        def func = FROM_BASE[to]
        def key = generateKey(notNull(cacheId, src), to)
        return fromCacheOrEval(key, flags) { func.call(src, flags) }
    }


    private static void validateForm(EnumSet<FLAGS> flags, Form form) {
        def validateWithXML = Study.validateWithXML.get()
        try {
            if (FLAGS.USE_LENIENT_ID_VALIDATION in flags) { Study.validateWithXML.set(true) }
            form.validate()
        } finally {
            Study.validateWithXML.set(validateWithXML)
        }
    }

    private static Map cacheData = new ConcurrentHashMap()

    static List generateKey(... keys) {
        return Arrays.asList(keys)
    }

    static <T> T cache(key, T value) {
        cacheData[key] = value
        return value
    }

    static cacheHits = 0// simply fo testing purposes
    static cacheMiss = 0// simply fo testing purposes

    static <T> T fromCacheOrEval(key, EnumSet<FLAGS> flags, Closure<T> eval) {

        if (FLAGS.CLEAR_CACHE in flags) {
            cacheData.clear()
        }

        if (FLAGS.USE_CACHE in flags) {
            def val = cacheData[key]
            if (val != null) {
                cacheHits = ++cacheHits
                return (T) val
            }
            cacheMiss = ++cacheMiss
            val = eval.call()
            return cache(key, val)
        } else {
            return eval.call()
        }
    }

    static void clearCache() { cacheData.clear() }

    static void clearStats() { cacheHits = 0; cacheMiss = 0 }

    static int cacheSize() { cacheData.size() }

    static Set cacheKeys() { cacheData.keySet() }


    static
    {
        //MARKUP
        TO_FORM[FORMAT.MARKUP] = { String src, EnumSet<FLAGS> flags ->
            def quickParse = Study.quickParse.get()
            try {
                Study.quickParse.set(FLAGS.USE_QUICK_PARSE in flags)
                def deserializer = new MarkupDeserializer(src, FLAGS.VALIDATE_FORM in flags)
                return deserializer.study().forms.first()

            } finally {
                Study.quickParse.set(quickParse)
            }
        }
        FROM_BASE[FORMAT.MARKUP] = { Form form, EnumSet<FLAGS> flags ->
            MarkUpSerializer.toFormMarkUp(form)
        }

        //OXD
        TO_FORM[FORMAT.OXD] = { String src, EnumSet<FLAGS> flags ->
            def serializer = new XFormDeserializer(src)
            return serializer.parse()
        }
        FROM_BASE[FORMAT.OXD] = { Form form, EnumSet<FLAGS> flags ->
            XFormSerializer ser = createXformSerializer(flags)
            return ser.toXForm(form)
        }

        //STUDY_XML
        TO_FORM[FORMAT.STUDY_XML] = { String src, EnumSet<FLAGS> flags ->
            def serializer = new StudyDeSerializer()
            serializer.validating = FLAGS.VALIDATE_FORM in flags
            return serializer.toStudy(src).firstForm
        }
        FROM_BASE[FORMAT.STUDY_XML] = { Form form, EnumSet<FLAGS> flags ->
            def serializer = createXformSerializer(flags)
            def study = form.study
            if (!study) {
                study = new Study(false)
                study.name = form.name
                study.addForm(form)
            }
            return serializer.toStudyXml(study)

        }

        //ODK
        TO_FORM[FORMAT.ODK] = { String src, EnumSet<FLAGS> flags ->
            def serializer = new ODKDeSerializer(src)
            return serializer.parse()
        }
        FROM_BASE[FORMAT.ODK] = { Form form, EnumSet<FLAGS> flags ->
            def ser = new ODKSerializer()
            ser.numberBindings = FLAGS.NUMBER_IDS in flags
            ser.numberQuestions = FLAGS.NUMBER_LABELS in flags
            ser.oxdConversion = FLAGS.ODK_OXD_MODE in flags
            ser.addMetaInstanceId = FLAGS.ODK_ADD_META_INSTANCE in flags
            return ser.toXForm(form)
        }

        //OBJECT
        TO_FORM[FORMAT.FORM] = { Form src, EnumSet<FLAGS> flags ->
            return src
        }
        FROM_BASE[FORMAT.FORM] = { Form form, EnumSet<FLAGS> flags ->
            return form
        }


    }

    private static XFormSerializer createXformSerializer(EnumSet<FLAGS> flags) {
        def ser = new XFormSerializer()
        ser.numberBindings = FLAGS.NUMBER_IDS in flags
        ser.numberQuestions = FLAGS.NUMBER_LABELS in flags
        ser.generateView = FLAGS.OXD_GENERATE_VIEW in flags
        ser.putExtraAttributesInComments = FLAGS.OXD_EXTRA_ATTRIBUTES_IN_COMMENTS in flags
        return ser
    }

    // SOME CONVENIENCE METHODS
    static Form markup2Form(src) {
        markup2Form(src, FLAGS.of(FLAGS.VALIDATE_FORM), null)
    }

    static Form markup2Form(src, cacheId) {
        markup2Form(src, FLAGS.none(), cacheId)
    }

    static Form markup2Form(src, EnumSet<FLAGS> flags) {
        markup2Form(src, flags, null)

    }

    static Form odk2Form(String xml) {
        return toFormFrom(FORMAT.ODK, xml)
    }

    static Form oxd2Form(String xml) {
        return toFormFrom(FORMAT.OXD, xml)
    }

    static String oxd2Odk(String xml) {
        return from(FORMAT.OXD).to(FORMAT.ODK).convert(xml)
    }

    static String markup2Oxd(String markup) {
        from(FORMAT.MARKUP).to(FORMAT.OXD).convert(markup)
    }


    static Form markup2Form(src, EnumSet<FLAGS> flags, cacheId) {
        return toFormFrom(FORMAT.MARKUP, src, flags, cacheId)

    }

    static Form oxd2FormWithCache(String xform, cacheID = xform, boolean pClearCache = false) {

        def flags = FLAGS.of(FLAGS.USE_CACHE)

        if (pClearCache) flags << FLAGS.CLEAR_CACHE

        return toFormFrom(FORMAT.OXD, xform, flags, cacheID)

    }

}

enum FORMAT {
    MARKUP, FORM, OXD, ODK, STUDY_XML
}

enum FLAGS {

    VALIDATE_FORM,
    NUMBER_LABELS,
    NUMBER_IDS,
    CLEAR_CACHE,
    USE_LENIENT_ID_VALIDATION,
    USE_CACHE,
    USE_QUICK_PARSE,
    ODK_ADD_META_INSTANCE,
    ODK_OXD_MODE,
    OXD_EXTRA_ATTRIBUTES_IN_COMMENTS,
    OXD_GENERATE_VIEW

    static EnumSet<FLAGS> none() { EnumSet.noneOf(FLAGS) }


    static EnumSet<FLAGS> of(FLAGS flag, FLAGS... flags) {
        return EnumSet.of(flag, flags)
    }
}
