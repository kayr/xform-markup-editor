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
class OdkMetaDataTransformer implements Transformer {

    final static String NAME = "odkmetadata"

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
                           .asQuestion()

        def endQn =
                FormBuilder.create()
                           .binding("__end")
                           .text("End Time")
                           .type(XformType.DATE_TIME)
                           .bindAttr("jr:preload", "timestamp")
                           .bindAttr("jr:preloadParams", "end")
                           .meta(transformAttribute)
                           .asQuestion()

        def dateQn =
                FormBuilder.create()
                           .binding("__today")
                           .text("Today")
                           .type(XformType.DATE)
                           .bindAttr("jr:preload", "date")
                           .bindAttr("jr:preloadParams", "today")
                           .meta(transformAttribute)
                           .asQuestion()

        def instanceID =
                FormBuilder.create()
                           .text("Instance ID")
                           .binding("instanceID")
                           .calculation("concat('uuid:',uuid())")
                           .asQuestion()

        [startQn, endQn, dateQn, instanceID].each { IQuestion q ->
            q.setHasAbsoluteId(true)
            q.setVisible(false)
            q.setLine(transformAttribute.line)
        }



        def metaGroup = form['meta']

        if (metaGroup != null)
            Assert.attribute metaGroup instanceof Page, "The Meta Question Should Be A Group Type", transformAttribute.line
        else {
            metaGroup =
                    FormBuilder.create()
                               .groupQn()
                               .binding("meta")
                               .absolute(true)
                               .meta(transformAttribute)
                               .addElements(startQn, endQn, dateQn, instanceID)
                               .group()
        }




        form.addElement(metaGroup)

    }
}
