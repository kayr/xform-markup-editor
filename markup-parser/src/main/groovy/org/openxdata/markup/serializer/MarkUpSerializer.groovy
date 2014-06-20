package org.openxdata.markup.serializer

import org.codehaus.groovy.runtime.StringGroovyMethods
import org.openxdata.markup.*

/**
 * Created by kay on 6/20/14.
 */
class MarkUpSerializer {


    static String toStudy(Study study) {
        use(StringBuildCategory) {
            StringBuilder builder = new StringBuilder()
            builder << "### $study.name"
            study.forms.each {
                builder << toMarkUp(it)
            }
            builder.toString()
        }
    }

    static String toMarkUp(Form form) {
        use(StringBuildCategory) {
            StringBuilder builder = new StringBuilder()

            if (form.id)
                builder << "@id $form.id"
            builder << "## $form.name"

            form.pages.each {
                renderPage(builder, it)
            }

            form.dynamicOptions.each {
//                renderDynamicOptions(builder, it.key, it.value)
            }

            return builder.toString()
        }
    }

    static def renderDynamicOptions(def builder, String id, List<DynamicOption> options) {
        builder << 'dynamic{'


        builder << '}'

    }

    static def renderPage(def builder, Page page) {
        builder << "#> $page.name"
        page.questions.each {
            start(builder, it)
        }
    }

    def static start(def builder, IQuestion qn) {
        if (isAssignedId(qn))
            builder << "@id $qn.binding"
        if (qn.readOnly) builder << "@readonly"
        if (!qn.visible) builder << "@invisible"

        mayBeAddSkipLogic(builder, qn)
        mayBeAddValidationLogic(builder, qn)

        if (qn.calculation) builder << "@calculate $qn.calculation"

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
        builder << "${qn.required ? '*' : ''}$qn.text"
    }

    def static serialize(def builder, MultiSelectQuestion qn) {
        renderQuestionText(builder, qn)
        qn.options.each {
            builder << ">>\$$it.bind $it.text"
        }
    }

    def static serialize(def builder, SingleSelectQuestion qn) {
        renderQuestionText builder, qn
        qn.options.each {
            builder << ">\$$it.bind $it.text"
        }
    }

    def static serialize(def builder, RepeatQuestion qn) {
        builder << "repeat{ ${qn.required ? '*' : ''}$qn.text"
        qn.questions.each {
            start(builder, it)
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
