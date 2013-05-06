package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder
import org.openxdata.markup.MultiSelectQuestion

class MultiLayout extends Layout {

    void add(MarkupBuilder xml) {
        def height = addLabel(xml)
        top = top + height - 5
        MultiSelectQuestion multiQn = qn
        multiQn.options.each {
            height = addCheckBox(it, xml)
            top = top + height - 5
        }
    }

    void setTopPosition(int idx) {
        top = idx
    }

    int getTTHeight() {
        return (qn.options.size() * 25) + 20
    }

    @Override
    String getInputType() {
        return 'TextBox'
    }  //will not be called

}
