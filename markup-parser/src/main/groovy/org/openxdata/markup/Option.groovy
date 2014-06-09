package org.openxdata.markup
/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/30/13
 * Time: 4:11 PM
 * To change this template use File | Settings | File Templates.
 */
class Option implements IOption {

    String option
    String bind
    ISelectionQuestion parent
    int line

    Option(String option) {
        setOption(option)
    }

    Option(String option, int line) {
        this.line = line
        setOption(option)
    }

    Option(String option, String bind) {
        this.option = option
        this.bind = bind
    }


    void setOption(String newOption) {
        def parsedOption = Util.parseBind(newOption, line)
        option = parsedOption.option
        bind = parsedOption.bind
        this.line = line
    }

    @Override
    String getText() {
        return option
    }

    @Override
    String getBind() {
        return bind
    }

    String toString() {
        return option
    }

}
