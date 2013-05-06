package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder
import org.openxdata.markup.Page

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 5/5/13
 * Time: 9:22 PM
 * To change this template use File | Settings | File Templates.
 */

class PageLayOut extends Layout {
    Page page
    int top = 20

    private List<Layout> components = []
    boolean addButtons

    void add(MarkupBuilder xml) {
        xml.Page(Text: page.name, fontWeight: 'normal', fontSize: '16px', fontFamily: FONT, Binding: page.binding, Width: '900px', Height: "${top + 200}px", backgroundColor: '') {

            //build each particular layout component
            components.each {
                it.add(xml)
            }

            //add the submit and cancel buttons at the bottom if allowed
            if (addButtons) {
                xml.Item(WidgetType: "Button", Text: "Submit", HelpText: "submit", Binding: "submit", Left: "20px", Top: top + 'px', Width: "90px", Height: "30px", TabIndex: "0", fontSize: "16px", fontFamily: FONT)
                xml.Item(WidgetType: "Button", Text: "Cancel", HelpText: "cancel", Binding: "cancel", Left: "220px", Top: top + 'px', Width: "90px", Height: "30px", TabIndex: "0", fontSize: "16px", fontFamily: FONT)
            }
        }
    }

    @Override
    int getTTHeight() {
        return top + 200
    }



    void addComponent(Layout layout) {
        layout.setTopPosition(top)
        //add a gap between each widget
        top = top + layout.TTHeight + 20
        layout.parentLayout = this
        components << layout
    }


}
