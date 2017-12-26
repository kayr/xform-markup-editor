package org.openxdata.markup.util

import groovy.transform.CompileStatic
import groovy.xml.MarkupBuilder

@CompileStatic
class CustomMarkupBuilder extends MarkupBuilder {

    CustomIndentPrinter indentPrinter

    static CustomMarkupBuilder _(Writer writer) {
        return new CustomMarkupBuilder(new CustomIndentPrinter(writer))
    }

    private CustomMarkupBuilder(CustomIndentPrinter indentPrinter1) {
        super(indentPrinter1)
        indentPrinter = indentPrinter1
    }
}
