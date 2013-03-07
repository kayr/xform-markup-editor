package org.openxdata.markup

import org.openxdata.markup.exception.InvalidAttributeException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/9/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
class Attrib {

    static def types = ['number', 'decimal', 'date', 'boolean', 'time', 'dateTime', 'picture', 'video', 'audio',
            'picture', 'gps', 'barcode']

    static def allowedAttributes = ['readonly', 'required', 'id', 'invisible', 'comment', 'skiplogic', 'skipaction',
            'hideif', 'enableif', 'disableif', 'showif', 'validif', 'message', 'calculate']


    static void addAttribute(IQuestion question, String attribute) {
        attribute = attribute.trim().replaceAll(/\s+/, ' ')
        def lowCaseAttrib = attribute.toLowerCase().split(/\s+/)[0]

        def param = lowCaseAttrib.length() == attribute.length() ? "" : (attribute[lowCaseAttrib.length()..attribute.length() - 1]).trim()

        if (!(question instanceof TextQuestion) && types.contains(lowCaseAttrib)) {
            throw new InvalidAttributeException("Cannot set datatype $attribute on a ${question.class.simpleName}")
        }

        if (types.contains(lowCaseAttrib)) {
            question.type = attribute
        } else if (allowedAttributes.contains(lowCaseAttrib)) {
            setQuestionAttribute(question, lowCaseAttrib, param)
        } else {
            throw new InvalidAttributeException("""Attibute [@$attribute] has no meaning.
Supported attributes include $types \n$allowedAttributes""")
        }

    }

    static void setQuestionAttribute(IQuestion question, String attribute, String param) {
        switch (attribute) {
            case 'readonly':
                question.readOnly = true
                break
            case 'required':
                question.required = true
                break
            case 'invisible':
                question.visible = false
                break
            case 'comment':
                question.comment = param
                break
            case 'id':
                param = param.toLowerCase()
                if (param ==~ /[a-z][a-z0-9_]*/)
                    question.binding = param
                else
                    throw new InvalidAttributeException("""You have an invalid variable $attribute .
Attributes should start with a small letter followed by small letters and underscores""")
                break
            case 'hideif':
            case 'showif':
            case 'disableif':
            case 'enableif':
                question.skipAction = attribute - 'if'
                question.skipLogic = param
                break
            case 'skiplogic':
                question.skipLogic = param
                break
            case 'skipaction':
                question.skipAction = param
                break
            case 'validif':
                question.validationLogic = param
                break
            case 'message':
                question.message = param
                break

            case 'calculate':
                question.calculation = param
                break
        }
    }

}
