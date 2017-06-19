package org.openxdata.markup.transformers

import org.openxdata.markup.FLAGS
import org.openxdata.markup.IFormElement
import org.openxdata.markup.RepeatQuestion
import org.openxdata.markup.TextQuestion
import org.openxdata.markup.exception.InvalidAttributeException

/**
 * Created by user on 6/17/2017.
 */
class JRCountTransformer implements Transformer {

    @Override
    void transform(EnumSet<FLAGS> flags, IFormElement element, TransformAttribute transformAttribute) {

        if (!(element instanceof RepeatQuestion)) throw new InvalidAttributeException('Arribute jrcount can only be set on Repeat Question', element.line)



        def thisElemId = element.id

        def otherElemId = "__${thisElemId}_count"

        def q = new TextQuestion("JR Count For (${thisElemId})")
        q.setVisible(false)
        q.setId(otherElemId)
        q.calculation = transformAttribute.param
        q.setLine(element.line)


        element.insertBeforeMe(q)


        element.layoutAttributes['jrcount'] = '$' + otherElemId


    }

}
