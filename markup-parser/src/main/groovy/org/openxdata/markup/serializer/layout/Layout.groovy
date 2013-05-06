package org.openxdata.markup.serializer.layout

import groovy.xml.MarkupBuilder
import org.openxdata.markup.IOption
import org.openxdata.markup.IQuestion
import org.openxdata.markup.serializer.LayoutSerializer

abstract class Layout {

    public static final String FONT = "Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, sans-serif"
    int top
    int left
    IQuestion qn
    def parentLayout
    LayoutSerializer serializer
    boolean numberBindings
    boolean numberText


    int addLabel(MarkupBuilder xml, Map extraProps = [:]) {
        def height = 25
        def props = [WidgetType: "Label", Text: qn.getText(numberText), HelpText: qn.comment,
                Binding: qn.getBinding(numberBindings), Left: "20px", Top: top + 'px',
                Width: "${labelWidth}px", Height: "${height}px", TabIndex: "0", fontSize: "12px", fontFamily: FONT]
        props.putAll(extraProps)
        xml.Item(props)
        return height
    }

    private int addInput(String widgetType, MarkupBuilder xml) {
        def height = getWidgetHeight(widgetType)
        xml.Item(WidgetType: widgetType, Text: (qn instanceof Map) ? qn.getText(numberText) : '',
                HelpText: qn.comment == null ? qn.getText(numberText) : qn.comment, Binding: qn.getBinding(numberBindings),
                Left: "40px", Top: top + 'px', Width: "${width}px", Height: "${height}px",
                TabIndex: getNextId(), fontSize: "12px", fontFamily: FONT)
        return height
    }

    int getNextId() {
        return serializer.nextId
    }

    int addCheckBox(IOption option, MarkupBuilder xml) {
        def height = getWidgetHeight('CheckBox')
        xml.Item(WidgetType: 'CheckBox', Text: option.text, HelpText: option.text, Binding: option.bind,
                ParentBinding: option.parent.getBinding(numberBindings), Left: "40px", Top: "${top}px", Width: "200px",
                Height: "${height}px", TabIndex: getNextId(), fontSize: "12px", fontFamily: FONT)
        return height

    }

    int addGroupBox(String binding, MarkupBuilder xml, Closure closure) {
        def height = getWidgetHeight('GroupBox')
        def grpBinding = binding != null ? binding : "LEFT20pxTOP${top}px"
        xml.Item(WidgetType: "GroupBox", HelpText: "Video", Binding: grpBinding, Left: "40px", Top: top + "px",
                Width: "200px", Height: height + "px", TabIndex: getNextId(), fontSize: "12px",
                fontFamily: "Verdana, 'Lucida Grande', 'Trebuchet MS', Arial, Sans-Serif", borderStyle: "dashed") {
            closure.call()

        }
        return height
    }

    int addRptLabel(MarkupBuilder xml) {
        def width = getWidth()
        xml.Item(WidgetType: "Label", Text: qn.getText(numberText), HelpText: qn.comment,
                Binding: qn.getBinding(numberBindings), Left: "${left}px", Top: top + 'px',
                Width: "200px", Height: "25px", TabIndex: "0", fontSize: "12px", fontFamily: FONT)
        return width
    }

    private int addRptInput(String widgetType, MarkupBuilder xml) {
        def width = getWidth()
        xml.Item(WidgetType: widgetType, Text: (qn instanceof Map) ? qn.getText(numberText) : '',
                HelpText: qn.comment == null ? qn.getText(numberText) : qn.comment, Binding: qn.getBinding(numberBindings),
                Left: "${left}px", Top: '10px', Width: "200px", Height: "25px",
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

    String getInputType() {
        switch (qn.type) {
            case 'date':
                return 'DatePicker'
            case 'dateTime':
                return 'DateTimeWidget'
            case 'time':
                return 'TimeWidget';
            case 'longtext':
                return 'TextArea'
            default:
                return 'TextBox'
        }
    }

    void setBeginIdx(int idx) {
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
}

