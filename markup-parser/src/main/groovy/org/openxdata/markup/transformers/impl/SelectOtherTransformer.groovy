package org.openxdata.markup.transformers.impl

import groovy.transform.CompileStatic
import org.openxdata.markup.*
import org.openxdata.markup.transformers.FormBuilder
import org.openxdata.markup.transformers.TransformAttribute
import org.openxdata.markup.transformers.Transformer
import org.openxdata.markup.util.Assert


@CompileStatic
class SelectOtherTransformer implements Transformer {
    @Override
    void transform(EnumSet<FLAGS> flags, IFormElement element, TransformAttribute transformAttribute) {

        Assert.attribute(
                element.xformType in [XformType.SELECT1, XformType.SELECT],
                "Attribute [@${transformAttribute.annotation}] Can Only Be Used on Select Questions",
                transformAttribute.line)

        def selectQn = element as ISelectionQuestion

        if (selectQn.options.every { it.bind != 'other' }) {
            selectQn.options << new Option('Other', 'other')
        }

        def thisElementId = "__${element.binding}_other"


        def question = FormBuilder.create().textQn()
                                  .text("$element.text (Other)")
                                  .binding(thisElementId)
                                  .showIf("$element.markupVariable = 'other'")
                                  .meta(transformAttribute)
                                  .question()

        selectQn.insertAfterMe(question)


    }
}
