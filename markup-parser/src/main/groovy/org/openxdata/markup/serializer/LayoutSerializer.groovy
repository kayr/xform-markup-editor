package org.openxdata.markup.serializer

import groovy.xml.MarkupBuilder
import org.openxdata.markup.*
import org.openxdata.markup.serializer.layout.*

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/4/13
 * Time: 11:36 PM
 * To change this template use File | Settings | File Templates.
 */
class LayoutSerializer {


    def wigdets = [gps: 'TextBox', string: 'TextBox']

    boolean numberBindings
    boolean numberText

    String generateLayout(Form form) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer);

        List<PageLayOut> pageLayouts = []

        form.pages.each { eachPage ->
            PageLayOut pageLayOut = new PageLayOut(page: eachPage)
            pageLayouts << pageLayOut
            eachPage.questions.each {
                def layout = getLayout(it)
                layout.numberBindings = numberBindings
                layout.numberText  = numberText
                layout.serializer = this
                pageLayOut.addComponent(layout)
            }
        }

        xml.doubleQuotes = true
        xml.Form() {
            pageLayouts.eachWithIndex { pageLayOut, idx ->
                if (idx == 0)
                    pageLayOut.addButtons = true
                pageLayOut.add(xml)
            }
        }

        return writer.toString()

    }

    Layout getLayout(IQuestion qn) {
        if (qn instanceof RepeatQuestion) {
            return new RepeatLayout(serializer: this, qn: qn)
        }

        if (qn instanceof SingleSelectQuestion ||
                qn instanceof DynamicQuestion) {
            return new SingleSelectLayout(qn: qn)
        }

        if (qn instanceof MultiSelectQuestion) {
            return new MultiLayout(qn: qn)
        }

        switch (qn.type) {

            case 'boolean':
                return new SingleSelectLayout(qn: qn)
            case 'video':
            case 'audio':
                return new VideoLayout(qn: qn)
            case 'picture':
                return new PicLayout(qn: qn)
            case 'longtext':
                return new LongTextLayout(qn: qn)
            case 'string':
            case 'number':
            case 'gps':
            case 'barcode':
            case 'decimal':
                return new TextLayout(qn: qn)
            default:
                return new TextLayout(qn: qn)

        }

    }

    int nextId

    def getNextId() {
        return nextId++
    }


}
