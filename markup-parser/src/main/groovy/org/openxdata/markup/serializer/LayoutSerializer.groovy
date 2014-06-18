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

    boolean numberBindings
    boolean numberText
    int nextWidgetId

    String generateLayout(Form form) {

        List<PageLayout> pageLayouts = initPageLayoutHandlers(form)

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
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

    def List<PageLayout> initPageLayoutHandlers(Form form) {

        List<PageLayout> pageLayouts = []

        for (eachPage in form.pages) {
            PageLayout pageLayOut = new PageLayout(page: eachPage)

            eachPage.questions.each { IQuestion qn ->
                Layout layout = initQuestionLayout(qn)
                pageLayOut.addComponent(layout)
            }

            pageLayouts << pageLayOut
        }
        pageLayouts
    }

    def Layout initQuestionLayout(IQuestion qn) {
        def layout = getLayoutHandler(qn)
        layout.numberBindings = numberBindings
        layout.numberText = numberText
        layout.serializer = this
        return layout
    }

    Layout getLayoutHandler(IQuestion qn) {
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
            default:
                return new TextLayout(qn: qn)

        }
    }


    def synchronized getNextWidgetId() {
        return nextWidgetId++
    }


}
