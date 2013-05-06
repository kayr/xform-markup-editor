package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder
import org.openxdata.markup.IOption
import org.openxdata.markup.IQuestion
import org.openxdata.markup.serializer.LayoutSerializer

abstract class Layout {

    public static final String FONT = "Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif"
    int top
    int left
    IQuestion qn
    def parentLayout
    LayoutSerializer serializer
    boolean numberBindings
    boolean numberText

    def standardMappings = [fontSize: "12px", fontFamily: FONT, Left: "40px"]

    void addRepeat(MarkupBuilder xml) {
        addRptInput(inputType, xml)
    }

    int getWidgetHeight(String widgetName) {
        return 25;
    }

    void add(MarkupBuilder xml) {
        def height = addLabel(xml)
        top = top + height - 8//add space
        addInput(getInputType(), xml)
    }

    void setTopPosition(int idx) {
        top = idx
    }

    int getWidth() {
        return 200
    }

    int getLabelWidth() {
        //simply approximation
        return qn.getText(numberText).length() * 8
    }

    abstract int getTTHeight()

    String getInputType() {
        throw new UnsupportedOperationException("qetUserInput() should overridden!!")
    }

    def String getText() {
        return qn.getText(numberText)
    }

    def String getBinding() {
        return qn.getBinding(numberBindings)
    }

    def String getComment() {
        return qn.comment == null ? qn.getText(numberText) : qn.comment
    }

    int getNextWidgetId() {
        return serializer.nextWidgetId
    }


    int addLabel(MarkupBuilder xml) {
        def height = 25
        def props = new HashMap(standardMappings)
        props.putAll(WidgetType: "Label", Text: qn.getText(numberText), HelpText: qn.comment,
                Binding: qn.getBinding(numberBindings), Top: top + 'px', Left: '20px',
                Width: "${labelWidth}px", Height: "${height}px", TabIndex: "0", fontSize: "12px", fontFamily: FONT)
        xml.Item(props)
        return height
    }

    int addInput(String widgetType, MarkupBuilder xml) {
        def height = getWidgetHeight(widgetType)
        def params = [WidgetType: widgetType, Text: '', HelpText: comment, Binding: binding,
                Top: "${top}px", Width: "${width}px", Height: "${height}px", TabIndex: nextWidgetId]
        params.putAll(standardMappings)
        xml.Item(params)
        return height
    }

    int addCheckBox(IOption option, MarkupBuilder xml) {
        def height = getWidgetHeight('CheckBox')
        def map = [WidgetType: 'CheckBox', Text: option.text, HelpText: option.text, Binding: option.bind,
                ParentBinding: option.parent.getBinding(numberBindings), Top: "${top}px", Width: "200px",
                Height: "${height}px", TabIndex: nextWidgetId]
        map.putAll(standardMappings)
        xml.Item(map)
        return height
    }

    int addGroupBox(String binding, MarkupBuilder xml, Closure closure) {
        def height = getWidgetHeight('GroupBox')
        def grpBinding = binding != null ? binding : "LEFT20pxTOP${top}px"
        xml.Item(WidgetType: "GroupBox", HelpText: "Video", Binding: grpBinding, Left: "40px", Top: top + "px",
                Width: "${width}px", Height: "${height}px", TabIndex: nextWidgetId, fontSize: "12px",
                fontFamily: FONT, borderStyle: "dashed") {
            closure.call()

        }
        return height
    }

    int addRptLabel(MarkupBuilder xml) {
        def width = getWidth()
        xml.Item(WidgetType: "Label", Text: text, HelpText: comment, Binding: binding,
                Left: "${left}px", Top: top + 'px', Width: "200px", Height: "25px",
                TabIndex: "0", fontSize: "12px", fontFamily: FONT)
        return width
    }

    private int addRptInput(String widgetType, MarkupBuilder xml) {
        xml.Item(WidgetType: widgetType, Text: '', HelpText: comment, Binding: binding,
                Left: "${left}px", Top: '10px', Width: "${width}px", Height: "25px",
                TabIndex: qn.contextIdx, fontSize: "12px", fontFamily: FONT)
        return width
    }

    int addRptCheckBox(IOption option, MarkupBuilder xml) {
        def width = getWidth()
        xml.Item(WidgetType: 'CheckBox', Text: option.text, HelpText: option.text, Binding: option.bind,
                ParentBinding: option.parent.getBinding(numberBindings), Left: "40px", Top: "10px", Width: "200px",
                Height: "25px", TabIndex: qn.contextIdx, fontSize: "12px", fontFamily: FONT)
        return width

    }
}

