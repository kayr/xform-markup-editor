package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder
import org.openxdata.markup.IQuestion

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/5/13
 * Time: 9:26 PM
 * To change this template use File | Settings | File Templates.
 */
class RepeatLayout extends Layout {

    List<Layout> questions = []
    int left = 0

    void setQn(IQuestion qn) {
        for (it in qn.questions) {
            Layout layout = serializer.getLayoutHandler(it)
            layout.left = left
            left = layout.width + left
            questions << layout
        }
        super.setQn(qn)
    }

    @Override
    void setNumberBindings(boolean numberBindings) {
        questions.each {
            it.numberBindings = numberBindings
        }
        super.setNumberBindings(numberBindings)
    }

    @Override
    void setNumberText(boolean numberText) {
        questions.each {
            it.numberText = numberText
        }
        super.setNumberText(numberText)
    }

    void add(MarkupBuilder xml) {

        standardMappings.putAll([fontWeight: "bold", fontStyle: "italic"])
        int height = addLabel(xml)

        top += height


        questions.each {
            it.addRptLabel(xml)
        }

        top += 25

        xml.Item(WidgetType: "GroupBox", HelpText: qn.comment != null ? qn.comment : qn.getText(numberText),
                Binding: qn.getBinding(numberBindings), Left: "0px", Top: "${top}px", Width: "${width}px",
                Height: "100px", TabIndex: getNextWidgetId(), fontSize: "16px", fontFamily: FONT, Repeated: "1") {

            xml.Item(WidgetType: "Button", Text: "Add New", HelpText: "addnew", Binding: "addnew", Left: "10px",
                    Top: "55px", Width: "90px", Height: "30px", TabIndex: "0", fontSize: "12px", fontFamily: FONT)

            questions.each {
                it.addRepeat(xml)
            }
        }
    }

    void setTopPosition(int idx) {
        top = idx
        questions.each { it.setTopPosition(top + 25) }

    }

    int getWidth() {
        return qn.questions.size() * 200
    }

    int getTTHeight() {
        //return idx + 25 + 25 + 100 + 10
        return 25 + 25 + 100 + 10
    }
}
