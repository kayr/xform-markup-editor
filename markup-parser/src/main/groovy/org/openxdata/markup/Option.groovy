package org.openxdata.markup

import org.openxdata.markup.exception.ValidationException

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

    Option(String option) {
        setOption(option)
    }



    void setOption(String newOption) {
        def parsedOption = Util.parseBind(newOption)
        option = parsedOption.option
        bind = parsedOption.bind
    }

    @Override
    String getText() {
        return option
    }

    @Override
    String getBind() {
        return bind
    }
}
