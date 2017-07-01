package org.openxdata.markup.transformers.impl

import groovy.transform.CompileStatic
import org.openxdata.markup.FLAGS
import org.openxdata.markup.IFormElement
import org.openxdata.markup.RepeatQuestion
import org.openxdata.markup.transformers.FormBuilder
import org.openxdata.markup.transformers.TransformAttribute
import org.openxdata.markup.transformers.Transformer
import org.openxdata.markup.util.Assert

/**
 * Created by user on 6/17/2017.
 */
@CompileStatic
class JRCountTransformer implements Transformer {

    @Override
    void transform(EnumSet<FLAGS> flags, IFormElement element, TransformAttribute transformAttribute) {

        Assert.attribute element instanceof RepeatQuestion, 'Attribute jrcount can only be set on Repeat Question', element.line



        def thisElemId = element.id

        def otherElemId = "__${thisElemId}_count"

        def q = FormBuilder.create().textQn()
                           .text("JR Count For (${thisElemId})")
                           .visible(false)
                           .binding(otherElemId)
                           .calculation(transformAttribute.param)
                           .meta(transformAttribute)
                           .question()



        element.insertBeforeMe(q)


        element.layoutAttributes['jrcount'] = '$' + otherElemId


    }

}
