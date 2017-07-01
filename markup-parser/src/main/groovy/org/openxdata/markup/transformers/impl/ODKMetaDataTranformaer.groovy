package org.openxdata.markup.transformers.impl

import groovy.transform.CompileStatic
import org.openxdata.markup.*
import org.openxdata.markup.transformers.FormBuilder
import org.openxdata.markup.transformers.TransformAttribute
import org.openxdata.markup.transformers.Transformer
import org.openxdata.markup.util.Assert

/**
 * Created by user on 7/1/2017.
 */
@CompileStatic
class ODKMetaDataTranformaer implements Transformer {
    @Override
    void transform(EnumSet<FLAGS> flags, IFormElement element, TransformAttribute transformAttribute) {

        Assert.attribute(element instanceof Form, "ODKTimeStamp can only be applied on forms", transformAttribute.line)

        Form form = element as Form


        def startQn =
                FormBuilder.create()
                           .binding("__start")
                           .text("Start Time")
                           .visible(false)
                           .type(XformType.DATE_TIME)
                           .bindAttr("jr:preload", "timestamp")
                           .bindAttr("jr:preloadParams", "start")
                           .meta(transformAttribute)
                           .question()

        def endQn =
                FormBuilder.create()
                           .binding("__end")
                           .text("End Time")
                           .type(XformType.DATE_TIME)
                           .bindAttr("jr:preload", "timestamp")
                           .bindAttr("jr:preloadParams", "end")
                           .meta(transformAttribute)
                           .question()

        def dateQn =
                FormBuilder.create()
                           .binding("__today")
                           .text("Today")
                           .type(XformType.DATE)
                           .bindAttr("jr:preload", "date")
                           .bindAttr("jr:preloadParams", "today")
                           .meta(transformAttribute)
                           .question()

        def instanceID =
                FormBuilder.create()
                           .text("Instance ID")
                           .binding("instanceID")
                           .calculation("concat('uuid:',uuid())")
                           .question()

        def unique_id =
                FormBuilder.create()
                           .text("Unique ID")
                           .binding("unique_id")
                           .calculation("once(concat('uuid:',uuid()))")
                           .question()

        [startQn, endQn, dateQn, instanceID, unique_id].each { IQuestion q ->
            q.setHasAbsoluteId(true)
            q.setVisible(false)
            q.setLine(transformAttribute.line)
        }



        def metaGroup =
                FormBuilder.create()
                           .groupQn()
                           .binding("__meta")
                           .absolute(true)
                           .meta(transformAttribute)
                           .addElements(startQn, endQn, dateQn, instanceID, unique_id)
                           .group()



        form.addElement(metaGroup)

    }
}
