package org.openxdata.markup

import groovy.transform.builder.Builder
import groovy.transform.builder.SimpleStrategy
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

    @Builder(builderStrategy = SimpleStrategy, prefix = '')
    static class ConverterBuilder {
        FORMAT to
        FORMAT from
        private EnumSet<FLAGS> _flags = FLAGS.none()
        def cacheId

        def <T> T convert(def src, EnumSet<FLAGS> flags = _flags) {
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

        ConverterBuilder flags(FLAGS... flags) {
            _flags.addAll(flags)
            return this
        }

        ConverterBuilder flags(EnumSet<FLAGS> flags) {
            _flags.addAll(flags)
            return this
        }

        @SuppressWarnings("GroovyUnusedDeclaration")
        ConverterBuilder log() {
            println("Covert $from -> $to ")
            println("   $from -> $FORMAT.FORM ")
            println("   $FORMAT.FORM -> $to ")
            return this
        }

    }

    static def ConverterBuilder to(FORMAT to) {
        return new ConverterBuilder(to: to)
    }

    static def ConverterBuilder from(FORMAT to) {
        return new ConverterBuilder(from: to)
    }

    static def <T> T to(FORMAT toFormat, FORMAT from, def src) {
        to(toFormat, from, src, FLAGS.none(), null)
    }

    static def <T> T to(FORMAT toFormat, FORMAT from, def src, def cacheId) {
        to(toFormat, from, src, FLAGS.none(), cacheId)
    }

    static def <T> T to(FORMAT toFormat, FORMAT from, def src, EnumSet<FLAGS> flags) {
        to(toFormat, from, src, flags, null)
    }

    static def <T> T to(FORMAT to, FORMAT from, def src, EnumSet<FLAGS> flags, def cacheId) {

        def key = generateKey(cacheId ?: src, to, from)

        def flagsCopy = flags.clone()

        def result = fromCacheOrEval(key, flagsCopy) {
            def form = toFormFrom(from, src, flagsCopy)

            if (to == FORMAT.FORM) return form
            flagsCopy.remove(FLAGS.VALIDATE_FORM)
            return fromFormTo(to, form, flagsCopy)
        }


        return result as T
    }

    static Form markup2Form(def src) {
        markup2Form(src, FLAGS.of(FLAGS.VALIDATE_FORM), null)
    }

    static Form markup2Form(def src, def cacheId) {
        markup2Form(src, FLAGS.none(), cacheId)
    }

    static Form markup2Form(def src, EnumSet<FLAGS> flags) {
        markup2Form(src, flags, null)

    }

    static Form markup2Form(def src, EnumSet<FLAGS> flags, def cacheId) {
        return toFormFrom(FORMAT.MARKUP, src, flags, cacheId)

    }

    static Form toFormFrom(FORMAT from, def src, EnumSet<FLAGS> flags = FLAGS.none(), def cacheId = null) {
        def func = TO_FORM[from]
        def key = generateKey(cacheId ?: src, from)

        def form = fromCacheOrEval(key, flags) { func.call(src, flags) }

        if (FLAGS.VALIDATE_FORM in flags) { validateForm(flags, form) }

        return form
    }

    static def fromFormTo(FORMAT to, Form src, EnumSet<FLAGS> flags = FLAGS.none(), def cacheId = null) {
        if (FLAGS.VALIDATE_FORM in flags) { validateForm(flags, src) }

        def func = FROM_BASE[to]
        def key = generateKey(cacheId ?: src, to)
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

    static List generateKey(def ... keys) {
        return Arrays.asList(keys)
    }

    static def <T> T cache(def key, T value) {
        cacheData[key] = value
        return value
    }

    static cacheHits = 0;// simply fo testing purposes

    static def <T> T fromCacheOrEval(def key, EnumSet<FLAGS> flags, Closure<T> eval) {

        if (FLAGS.CLEAR_CACHE in flags) {
            cacheData.clear()
        }

        if (FLAGS.USE_CACHE in flags) {
            def val = cacheData[key]
            if (val != null) {
                cacheHits = ++cacheHits
                return (T) val
            }
            val = eval.call()
            return cache(key, val)
        } else {
            return eval.call()
        }
    }

    static void clearCache() { cacheData.clear() }

    static int cacheSize() { cacheData.size() }


    static
    {
        //MARKUP
        TO_FORM[FORMAT.MARKUP] = { String src, EnumSet<FLAGS> flags ->
            def deserializer = new MarkupDeserializer(src, FLAGS.VALIDATE_FORM in flags)
            return deserializer.study().forms.first()
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
