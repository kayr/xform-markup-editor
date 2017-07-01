package org.openxdata.markup

import org.openxdata.markup.exception.DuplicateElementException
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 1/29/13
 * Time: 11:21 PM
 * To change this template use File | Settings | File Templates.
 */
class Form implements HasQuestions {

    public static final String VARIABLE_REGEX
    static {
        VARIABLE_REGEX = '(?<!\\\\)[\$][:]?[^\\s]+'//(?<!\\)[$][:]?[^\s]+
    }

    String dbId
    Study study
    String version = 'v1'
    int line
    int dbIdLine
    int idLine

    Map<IFormElement, String> idxCache = [:]

    Map<String, List<DynamicOption>> dynamicOptions = [:]


    Form() { init() }

    Form(String name) {
        this.name = name
        init()
    }


    List<Page> getPages() {
        return elements.findAll { it instanceof Page } as List<Page>
    }

    Page getFirstPage() {
        return pages ? pages.get(0) : null
    }

    private def init() {
        this.xformType = XformType.FORM
    }

    void validate() {
        allElements.each {
            validateSkipLogic(it)
            validateValidationLogic(it)

            if (it instanceof IQuestion)
                validateCalculation(it)

            if (it instanceof DynamicQuestion)
                it.validate()

            if (it instanceof RepeatQuestion) {
                def jrCount = it.layoutAttributes['jrcount']
                if (jrCount)
                    validateXpath(jrCount, it, "jr:count")
            }

            it.validateIdExistsIfNecessary()
            validateNotDuplicate(it)

        }
    }

    void validateNotDuplicate(IFormElement qn) {
        if (!qn.binding) return
        def otherQns = getElements(qn.binding)
        if (otherQns.size() > 1)
            throw new DuplicateElementException(otherQns)
    }


    public static String getAbsoluteBindingXPath(String xpath, IFormElement question, Map config = [:]) {
        return _absoluteBindingXP(xpath, question, config + [indexed: false])
    }

    public static String getIndexedAbsoluteBindingXPath(String xpath, IFormElement question, Map config = [:]) {
        return _absoluteBindingXP(xpath, question, config + [indexed: true])
    }

    private static _absoluteBindingXP(String xpath, IFormElement question, Map config = [:]) {
        try {
            def xp = new XPathUtil(xpath)
            def result = xp.removeMarkupSyntax(question, config)
            return result
        } catch (Exception x) {
            System.err.println("Could not remove markup from [$xpath]: Reason: $x")
            return xpath
        }
    }

    @Deprecated
    static IQuestion findQuestionWithBinding(String binding, IFormElement hasQuestions) {
        return hasQuestions.parentForm.getElement(binding) as IQuestion
    }


    @Deprecated
    static List<IQuestion> extractQuestions(HasQuestions hasQuestions) {
        return hasQuestions.allQuestions
    }

    static void validateCalculation(IQuestion iQuestion) {
        if (!iQuestion.calculation)
            return

        validateXpath(iQuestion.calculation, iQuestion, 'Calculation')
    }

    static void validateValidationLogic(IFormElement question) {

        if (!question.validationLogic)
            return

        if (!question.message)
            throw new ValidationException("Validation message has not been set on question [$question.text]", question.line)

        validateXpath(question.validationLogic, question, 'Validation')

    }

    static void validateSkipLogic(IFormElement question) {

        if (!question.skipLogic)
            return

        if (!question.skipAction)
            question.skipAction = "enable"

        validateXpath(question.skipLogic, question, 'Skip')
    }

    static String validateXpath(String xpath, IFormElement question, String logicType) {
        try {
            XPathUtil.validateXpath(xpath, question, logicType)
        } catch (ValidationException e) {
            throw e
        } catch (Exception e) {
            def e1 = new ValidationException("Error parsing XPATH[$xpath] $logicType logic for \n [$question.text] \n $e.message", question.line, e)
            e1.stackTrace = e.stackTrace
            throw e1
        }
    }


    Form getParentForm() {
        return this
    }


    public String getBinding() {

        if (id == null) {
            if (study.name)
                id = Util.getBindName("${study.name}_${name}_${version}")
            else
                id = Util.getBindName("${name}_${version}")
        }
        return id
    }

    String toString() {
        name
    }

    def addDynamicOptions(String instanceId, List<DynamicOption> dynamicOptions) {
        if (!this.dynamicOptions[instanceId]) {
            this.dynamicOptions[instanceId] = []
        }
        this.dynamicOptions[instanceId].addAll(dynamicOptions)
    }

    String getIndex(IFormElement element) {
        return idxCache[element]
    }


    void buildIndex() {
        buildIndex(null, this, 0)
    }

    Integer buildIndex(String parent, HasQuestions p, int currentIdx) {

        int idx = currentIdx
        for (e in p.elements) {
            String newId = null
            if (e.id) {
                idx++
                newId = parent ? parent + '.' + idx : idx.toString()
                idxCache[e] = newId
            }

            if (e instanceof HasQuestions) {
                if (e.id) {
                    buildIndex(newId, e, 0)
                } else {
                    idx = buildIndex(parent, e, idx)
                }
            }
        }
        return idx
    }

    def printAll(PrintStream out) {

        allElements.each {
            out.println "___________________________"
            out.println "${it.xformType.value.padRight(15)}: ${it.getText(true)}"
            if (it.id)
                out.println "ID             : $it.id"
            if (it instanceof IQuestion && it.required)
                out.println "Required       : $it.required"
            if (it instanceof IQuestion && it.readOnly)
                out.println "Readonly       : $it.readOnly"
            if (!it.visible)
                out.println "Visible        : $it.visible"
            if (it.skipLogic)
                out.println "SkipLogic      : $it.skipAction if $it.skipLogic"
            if (it instanceof IQuestion && it.calculation)
                out.println "Calcn          : $it.calculation"
            if (it.validationLogic)
                out.println "Validation     : $it.validationLogic\n" +
                        "               $it.message "
        }
        out.println "___________________________"
    }

    int getStartLine() {
        [dbIdLine, idLine, line].findAll().min()
    }
}
