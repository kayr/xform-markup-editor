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

    static def types = ['number', 'decimal', 'date', 'boolean', 'time', 'datetime', 'picture', 'video', 'audio',
            'picture', 'gps', 'barcode', 'longtext']

    static
    def allowedAttributes = ['readonly', 'required', 'id', 'absoluteid', 'invisible', 'comment', 'skiplogic', 'skipaction',
            'hideif', 'enableif', 'disableif', 'showif', 'validif', 'message', 'calculate', 'parent', 'hint','default']


    static void addAttribute(IQuestion question, String attribute, int line) {
        def params = extractAttribAndParam(attribute)
        def param = params.param
        def lowCaseAttrib = params.attrib

        //type attributes are only allowed on TextQuestions
        def invalid = !(question instanceof TextQuestion) && types.contains(lowCaseAttrib) ||
                //parent attribute is only allowed on Dynamic Questions
                (!(question instanceof DynamicQuestion) && lowCaseAttrib == 'parent')

        if (invalid) {
            throw new InvalidAttributeException("Cannot set datatype [$attribute] on a ${question.class.simpleName}", line)
        }



        if (types.contains(lowCaseAttrib)) {
            if (attribute == 'datetime')
                attribute = 'dateTime'
            question.type = attribute
        } else if (allowedAttributes.contains(lowCaseAttrib)) {
            setQuestionAttribute(question, lowCaseAttrib, param, line)
        } else {
            throw new InvalidAttributeException("""Attibute [@$attribute] has no meaning.
Supported attributes include $types \n$allowedAttributes""", line)
        }

    }

    static void addAttributeToForm(Form form, String attribute, int line) {
        def params = extractAttribAndParam(attribute)

        def attrib = params.attrib
        def param = params.param

        switch (attrib) {
            case 'id':
                Util.validateId(param, line)
                form.id = param
                break
            case 'dbid':
                form.dbId = param
                break
            default:
                throw new InvalidAttributeException("Attribute $attrib on form $form.name in not supported", line)
        }
    }

    static Map extractAttribAndParam(String attribute) {
        attribute = attribute.trim().replaceAll(/\s+/, ' ')
        def lowCaseAttrib = attribute.toLowerCase().split(/\s+/)[0]

        def param = lowCaseAttrib.length() == attribute.length() ? "" : (attribute[lowCaseAttrib.length()..attribute.length() - 1]).trim()
        [attrib: lowCaseAttrib, param: param]
    }

    static void setQuestionAttribute(IQuestion question, String attribute, String param, int line) {
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
            case 'hint':
                question.comment = param
                break
            case 'id':
                Util.validateId(param, line)
                question.binding = param
                break
            case 'absoluteid':
                Util.validateId(param, line)
                question.binding = param
                question.hasAbsoluteId = true
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
            case 'parent':
                Util.validateId(param, line)
                question.parentQuestionId = param
                break
            case 'default':
                question.value = param
                break
        }
    }

}
