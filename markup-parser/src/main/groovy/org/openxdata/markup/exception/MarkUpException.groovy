package org.openxdata.markup.exception

import groovy.transform.CompileStatic

@CompileStatic
class MarkUpException extends RuntimeException {
    MarkUpException() {
    }

    MarkUpException(String var1) {
        super(var1)
    }

    MarkUpException(String var1, Throwable var2) {
        super(var1, var2)
    }

    MarkUpException(Throwable var1) {
        super(var1)
    }

}
