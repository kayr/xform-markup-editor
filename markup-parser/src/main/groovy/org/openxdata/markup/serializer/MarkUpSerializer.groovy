package org.openxdata.markup.serializer

import au.com.bytecode.opencsv.CSVWriter
import org.openxdata.markup.*

/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializer {


    static String toStudyMarkup(Study study) {
        use(StringBuildCategory) {
            StringWriter w = new StringWriter()
            def builder = new IndentPrinter(w, "  ", true, true)
            builder << "### $study.name"
            study.forms.each {
                builder << toFormMarkUp(it)
            }
            w.toString()
        }
    }

    static String toFormMarkUp(Form form) {
        use(StringBuildCategory) {

            StringWriter w = new StringWriter()
            def builder = new IndentPrinter(w, "  ", true, true)
            builder.toString()


            if (form.id)
                builder << "@id $form.id"
            if (form.dbId && form.dbId != '0')
                builder << "@dbid $form.dbId"
            if (form.version && form.version != 'v1')
                builder << "@version $form.version"

            for (la in form.layoutAttributes) {

                if (la.key == 'style') {
                    builder << "@style $la.value"
                } else {
                    builder << "@layout:$la.key $la.value"
                }

            }

            builder << "## $form.name"

            seprator(builder)

            for (e in form.elements) {
                serialize(builder, e)
            }

            for (Map.Entry<String, List<DynamicOption>> o in form.dynamicOptions) {
                renderDynamicOptions(builder, o.key, o.value)
            }

            return w.toString()
        }
    }

    static def renderDynamicOptions(def builder, String id, List<DynamicOption> options) {

        def stringWriter = new StringWriter()
        CSVWriter writer = new CSVWriter(stringWriter)
        writer.writeNext(['root', id] as String[])
        for (o in options) {
            writer.writeNext([o.parentBinding, o.markUpText] as String[])
        }
        builder << 'dynamic_instance{'
        builder << stringWriter.toString()
        builder << '}'

    }

    static def renderPage(def builder, HasQuestions container) {
        builder << "#> $container.name"
        container.elements.each {
            renderBehaviourInfo(builder, it)
        }
    }

    def static renderBehaviourInfo(def builder, IFormElement elem) {
        if (elem.id && isAssignedId(elem))
            builder << "@${elem.id == 'unique_id' ? 'absoluteid' : 'id'} $elem.binding"

        if (elem instanceof IQuestion) {
            if (elem.readOnly) builder << "@readonly"
        }

        if (!elem.visible) builder << "@invisible"

        mayBeAddSkipLogic(builder, elem)
        mayBeAddValidationLogic(builder, elem)

        if (elem instanceof IQuestion) {
            if (elem.calculation) builder << "@calculate $elem.calculation"

            if (elem.comment) builder << "@comment $elem.comment"

            if (elem.value) builder << "@default $elem.value"
        }

        for (kv in elem.bindAttributes) {
            if (Attrib.allowedAttributes.contains(kv.key)) {
                builder << "@$kv.key $kv.value"
            } else {
                builder << "@bind:$kv.key $kv.value"
            }
        }

        for (kv in elem.layoutAttributes) {
            if (kv.key == 'appearance') {
                builder << "@$kv.key $kv.value"
            } else {
                builder << "@layout:$kv.key $kv.value"
            }
        }

    }


    private static def isAssignedId(IFormElement qn) {
        Util.getBindName(qn.text) != qn.binding
    }


    def static serialize(def builder, Page qn) {

        renderBehaviourInfo(builder, qn)

        def instanceParent = qn.firstInstanceParent
        def isFormWithPages = instanceParent instanceof Form && instanceParent.isHoldingPagesOnly()
        if (isFormWithPages) {
            builder << "#> ${qn.text ?: "Page $qn.questionIdx"}"
        } else {
            builder << "group { ${qn.text ?: ''}"
        }

        builder.println()

        isFormWithPages ?: ++builder
        for (e in qn.elements) {
            serialize(builder, e)
        }
        isFormWithPages ?: --builder
        if (!isFormWithPages) builder << '}'
        seprator(builder)
    }

    def static serialize(def builder, MultiSelectQuestion qn) {
        renderBehaviourInfo(builder, qn)
        renderQuestionText(builder, qn)
        ++builder
        qn.options.each {
            builder << ">>$it.markUpText"
        }
        --builder
        seprator(builder)
    }

    def static serialize(def builder, SingleSelectQuestion qn) {
        renderBehaviourInfo(builder, qn)
        renderQuestionText(builder, qn)
        ++builder
        qn.options.each {
            builder << ">$it.markUpText"
        }
        --builder
        seprator(builder)

    }

    def static serialize(def builder, RepeatQuestion qn) {
        renderBehaviourInfo(builder, qn)
        builder << "repeat{ ${qn.required ? '*' : ''}$qn.text"

        builder.println()

        ++builder
        qn.elements.each {
            serialize(builder, it)
        }
        --builder
        builder << "}"
        seprator(builder)

    }


    def static serialize(def builder, DynamicQuestion qn) {
        renderBehaviourInfo(builder, qn)
        builder << "@parent $qn.parentQuestionId"
        renderQuestionText builder, qn
        builder << "\$> $qn.dynamicInstanceId"
        seprator(builder)

    }

    def static serialize(def builder, TextQuestion qn) {
        renderBehaviourInfo(builder, qn)
        renderQuestionText(builder, qn)
        seprator(builder)

    }

    def static serialize(def builder, IQuestion qn) {
        throw new UnsupportedOperationException("Cannot Render [${qn}] Markup")
    }

    static seprator(def builder) {
        builder.println()
        builder.println()

    }

    private static def mayBeAddSkipLogic(def builder, IFormElement qn) {
        if (!qn.skipLogic) return;
        def action = qn.skipAction?.trim() ?: 'enable'
        if (['enable', 'hide', 'show', 'disable'].contains(action)) {
            builder << "@${action}if $qn.skipLogic"
        } else {
            builder << "@skipaction $action"
            builder << "@skiplogic $qn.skipLogic"
        }
    }

    private static def mayBeAddValidationLogic(def builder, IFormElement qn) {
        if (!qn.validationLogic) return;
        def logic = qn.validationLogic?.trim() ?: 'enable'
        builder << "@validif $logic"
        builder << "@message $qn.message"
    }

    private static void renderQuestionText(builder, IQuestion qn) {
        if (qn.type && qn.type != 'string') {
            builder << "@$qn.type"
        }
        builder << "${qn.required ? '*' : ''}$qn.text"
    }
}


@Category(IndentPrinter)
class StringBuildCategory {
    def leftShift(def value) {
        this.println(value)
    }

    def next() {
        this.incrementIndent()
        return this
    }

    def previous() {
        this.decrementIndent()
        return this
    }
}
