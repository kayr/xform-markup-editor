package org.openxdata.markup.serializer

import org.codehaus.groovy.runtime.StringGroovyMethods
import org.openxdata.markup.*

/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializer {


    static String toStudyMarkup(Study study) {
        use(StringBuildCategory) {
            StringBuilder builder = new StringBuilder()
            builder << "### $study.name"
            study.forms.each {
                builder << toFormMarkUp(it)
            }
            builder.toString()
        }
    }

    static String toFormMarkUp(Form form) {
        use(StringBuildCategory) {
            StringBuilder builder = new StringBuilder()

            if (form.id)
                builder << "@id $form.id"
            if (form.dbId && form.dbId != '0')
                builder << "@dbid $form.dbId"
            builder << "## $form.name"

            form.pages.each {
                renderPage(builder, it)
            }

            form.dynamicOptions.each {
                renderDynamicOptions(builder, it.key, it.value)
            }

            return builder.toString()
        }
    }

    static def renderDynamicOptions(def builder, String id, List<DynamicOption> options) {
        builder << 'dynamic_instance{'
        builder << "root,$id"
        for (o in options) {
            builder << "$o.parentBinding, $o.markUpText"
        }
        builder << '}'

    }

    static def renderPage(def builder, Page page) {
        builder << "#> $page.name"
        page.questions.each {
            renderQuestion(builder, it)
        }
    }

    def static renderQuestion(def builder, IQuestion qn) {
        if (isAssignedId(qn))
            builder << "@id $qn.binding"
        if (qn.readOnly) builder << "@readonly"
        if (!qn.visible) builder << "@invisible"

        mayBeAddSkipLogic(builder, qn)
        mayBeAddValidationLogic(builder, qn)

        if (qn.calculation) builder << "@calculate $qn.calculation"

        if (qn.comment) builder << "@comment $qn.comment"

        if (qn.value) builder << "@default $qn.value"

        if (qn instanceof TextQuestion) {
            renderQuestionText builder, qn
        } else {
            serialize(builder, qn)
        }

    }


    private static String mayBeAddSkipLogic(def builder, IQuestion qn) {
        if (!qn.skipLogic) return;
        def action = qn.skipAction?.trim() ?: 'enable'
        if (['enable', 'hide', 'show', 'disable'].contains(action)) {
            builder << "@${action}if $qn.skipLogic"
        } else {
            builder << "@skipaction $action"
            builder << "@skiplogic $qn.skipLogic"
        }
    }

    private static def isAssignedId(IQuestion qn) {
        Util.getBindName(qn.text) != qn.binding
    }

    private static String mayBeAddValidationLogic(def builder, IQuestion qn) {
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

    def static serialize(def builder, MultiSelectQuestion qn) {
        renderQuestionText(builder, qn)
        qn.options.each {
            builder << ">>$it.markUpText"
        }
    }

    def static serialize(def builder, SingleSelectQuestion qn) {
        renderQuestionText builder, qn
        qn.options.each {
            builder << ">$it.markUpText"
        }
    }

    def static serialize(def builder, RepeatQuestion qn) {
        builder << "repeat{ ${qn.required ? '*' : ''}$qn.text"
        qn.questions.each {
            renderQuestion(builder, it)
        }
        builder << "}"

    }

    def static serialize(def builder, DynamicQuestion qn) {
        builder << "@parent $qn.parentQuestionId"
        renderQuestionText builder, qn
        builder << "\$> $qn.dynamicInstanceId"
    }
}


@Category(StringBuilder)
class StringBuildCategory {
    def leftShift(def value) {
        StringGroovyMethods.leftShift(this, "\n$value")
    }
}
