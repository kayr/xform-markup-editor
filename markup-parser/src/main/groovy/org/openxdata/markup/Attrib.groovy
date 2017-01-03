package org.openxdata.markup

import groovy.transform.CompileStatic
import org.openxdata.markup.exception.InvalidAttributeException
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/9/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class Attrib {

    static List types = ['number', 'decimal', 'date', 'boolean', 'time', 'datetime', 'picture', 'video', 'audio',
                         'picture', 'gps', 'barcode', 'longtext']

    static
    List allowedAttributes = ['readonly', 'required', 'jrcount', 'id', 'absoluteid', 'invisible', 'comment', 'skiplogic', 'skipaction',
                              'hideif', 'appearance', 'style (on forms)', 'enableif', 'disableif', 'showif', 'validif', 'message', 'calculate', 'parent', 'hint', 'default']


    static void addAttribute(IQuestion question, String attribute, int line) {
        def params = extractAttribAndParam(attribute)
        String param = params['param']
        String lowCaseAttrib = params['attrib']
        boolean isBind = params['isBind']
        boolean isLayout = params['isLayout']

        if (isBind) {
            setBindAttribute(question, lowCaseAttrib, param, line)
            return
        }

        if (isLayout) {
            setLayoutAttribute(question, lowCaseAttrib, param, line)
            return
        }

        //type attributes are only allowed on TextQuestions
        def invalid = !(question instanceof TextQuestion) && types.contains(lowCaseAttrib) ||
                //parent attribute is only allowed on Dynamic Questions
                (!(question instanceof DynamicQuestion) && lowCaseAttrib == 'parent')

        if (invalid) {
            throw new InvalidAttributeException("Cannot set datatype [$attribute] on a ${question.class.simpleName}", line)
        }

        if (types.contains(lowCaseAttrib)) {

            if (lowCaseAttrib == 'datetime') {
                lowCaseAttrib = 'dateTime'
            }

            question.type = lowCaseAttrib
        } else if (allowedAttributes.contains(lowCaseAttrib)) {
            setElementAttribute(question, lowCaseAttrib, param, line)
            setQuestionAttribute(question, lowCaseAttrib, param, line)
        } else {
            throw new InvalidAttributeException("""Attibute [@$attribute] has no meaning.\n""" +
                    """Supported attributes include $types \n$allowedAttributes""", line)
        }

    }

    static void addAttributeToForm(Form form, String attribute, int line) {
        def params = extractAttribAndParam(attribute)

        String attrib = params['attrib']
        String param = params['param']
        boolean isBind = params['isBind']
        boolean isLayout = params['isLayout']

        if (isBind) {
            setBindAttribute(form, attrib, param, line)
            return
        }

        if (isLayout) {
            setLayoutAttribute(form, attrib, param, line)
            return
        }


        switch (attrib) {
            case 'id':
                Util.validateId(param, line)
                form.id = param
                form.idLine = line
                break
            case 'dbid':
                form.dbId = param
                form.dbIdLine = line
                break
            case 'version'://todo write test
                form.version = param
                break
            case 'style':
                form.layoutAttributes['style'] = param
                break
            default:
                throw new InvalidAttributeException("Attribute $attrib on form $form.name in not supported", line)
        }
    }

    static void addAttributeToPage(Page page, String attribute, int line) {
        def params = extractAttribAndParam(attribute)

        String attrib = params['attrib']
        String param = params['param']
        boolean isBind = params['isBind']
        boolean isLayout = params['isLayout']

        if (isBind) {
            setBindAttribute(page, attrib, param, line)
            return
        }

        if (isLayout) {
            setLayoutAttribute(page, attrib, param, line)
            return
        }

        setElementAttribute(page, attrib, param, line, true)
    }

    static Map extractAttribAndParam(String attribute) {
        attribute = attribute.trim().replaceAll(/\s+/, ' ')



        def attributeName = attribute.split(/\s+/)[0]


        def isBind = attribute.startsWith('bind:')
        def isLayout = attribute.startsWith('layout:')
        if (!(isBind || isLayout)) {
            attributeName = attributeName.toLowerCase()
        }
        def param = attributeName.length() == attribute.length() ? "" : (attribute[attributeName.length()..attribute.length() - 1]).trim()
        [attrib: attributeName, param: param, isBind: isBind, isLayout: isLayout]
    }

    static void setBindAttribute(IFormElement question, String attribute, String param, int line) {
        putAttribute(
                question.bindAttributes, 'bind:',
                attribute, param, 'Bind', line
        )
    }

    static void setLayoutAttribute(IFormElement question, String attribute, String param, int line) {
        putAttribute(
                question.layoutAttributes, 'layout:',
                attribute, param, 'Layout', line
        )
    }

    private static putAttribute(Map container,
                                String qualifier,
                                String attribute,
                                String param,
                                String type,
                                int line) {
        String newAttribute = Util.replaceFirst(attribute, qualifier, '')

        if (!newAttribute) {
            throw new ValidationException("Invalid $type Attribute", line)
        }

        Util.validateId(newAttribute, line, true)

        if (!param) {
            param = true
        }

        container.put(newAttribute, param)

    }


    static void setElementAttribute(IFormElement question, String attribute, String param, int line, boolean throwException = false) {

        switch (attribute) {
            case 'invisible':
                question.visible = false
                break
            case 'id':
                Util.validateId(param, line)
                question.setBinding param
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
            case 'parent':
                Util.validateId(param, line)
                (question as DynamicQuestion).parentQuestionId = param
                break
            case 'appearance':
                question.layoutAttributes['appearance'] = param
                break
            default:
                if (throwException)
                    throw new InvalidAttributeException("Attribute $attribute on Element $question.name in not supported", line)

        }
    }


    static void setQuestionAttribute(IQuestion question, String attribute, String param, int line, boolean throwException = false) {
        switch (attribute) {
            case 'readonly':
                (question as IQuestion).readOnly = true
                break
            case 'required':
                (question as IQuestion).required = true
                break
            case 'comment':
            case 'hint':
                (question as IQuestion).comment = param
                break
            case 'calculate':
                (question as IQuestion).calculation = param
                break
            case 'parent':
                Util.validateId(param, line)
                (question as DynamicQuestion).parentQuestionId = param
                break
            case 'default':
                (question as IQuestion).value = param
                break
            case 'jrcount':
                if (question instanceof RepeatQuestion)
                    question.layoutAttributes['jrcount'] = param
                else
                    throw new InvalidAttributeException('Arribute jrcount can only be set on Repeat Question', line)

            default:
                if (throwException)
                    throw new InvalidAttributeException("Attribute $attribute on Question $question.name in not supported", line)

        }
    }

}
