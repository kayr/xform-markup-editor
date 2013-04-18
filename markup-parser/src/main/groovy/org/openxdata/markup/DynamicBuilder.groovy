package org.openxdata.markup

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
class DynamicBuilder {

    def csvSrc = "";
    List<List<String>> parsedCsv

    List<IQuestion> questions = []
    Map<String, List<DynamicOption>> dynamicOptions = [:]
    String singleSelectQuestion
    def singleSelectOptions = []


    public void appendLine(String line) {
        line = line << "\n"
        csvSrc = csvSrc << line
    }


    public void addQuestionsToForm(HasQuestions form) {
        try {
            parse()
            if (isBuildDynamicModelOnly()) {
                questions.each {
                    form.addQuestion(it)
                }
            } else {
                def singleSelectQuestionInstance = Form.findQuestionWithBinding(singleSelectQuestion, form)
                if (singleSelectQuestionInstance == null)
                    throw new ValidationException("""Error while parsing CSV. SingleSelect question with id [$singleSelectQuestion]
could not be found in the form""")
                singleSelectQuestionInstance.options = singleSelectOptions
            }

            form.parentForm.dynamicOptions = dynamicOptions

        } catch (Exception e) {
            if (e instanceof ValidationException)
                throw e
            throw new RuntimeException("Error while creating dynamic question:\n " + e.toString(), e)
        }
    }

    private boolean isBuildDynamicModelOnly() {
        return getSingleSelectReferenceIfAvailable() == null
    }

    public void parse() {
        List<String[]> csv = parseCsv()

        def singleSelectCol = getValuesForColumn(csv, 0).unique {Util.getBindName(it)}
        def singleSelVar = getSingleSelectReferenceIfAvailable()
        if (isBuildDynamicModelOnly()) {
            def singleSelQuestion = makeSingleSelectFromList(singleSelectCol)
            questions << singleSelQuestion
        } else {
            this.singleSelectQuestion = singleSelVar
            singleSelectCol.remove(0)
            singleSelectOptions = singleSelectCol.collect {return new Option(it)}
        }

        def headers = csv[0]

        for (int headerIdx = 1; headerIdx < headers.length; headerIdx++) {
            def csvHeader = headers[headerIdx]

            DynamicQuestion qn = new DynamicQuestion(csvHeader)
            if (isBuildDynamicModelOnly()) {
                qn.dynamicInstanceId = qn.binding
                qn.parentQuestionId = questions[headerIdx - 1].binding  //set previous header column as the parent of the current one.
                questions << qn
            } else {
                validateVariable(qn.binding, qn.text)
            }

            buildDynamicModel(headerIdx, qn.binding)

        }

    }

    private void buildDynamicModel(Integer headerIdx, String dynamicBinding) {
        def visitedChildren = new HashSet()
        dynamicOptions[dynamicBinding] = []
        for (int csvRowIdx = 1; csvRowIdx < parsedCsv.size(); csvRowIdx++) {

            def csvRow = parsedCsv[csvRowIdx]

            def childName = csvRow[headerIdx]
            def childBind = Util.getBindName(childName)
            def parent = Util.getBindName(csvRow[headerIdx - 1])

            if (visitedChildren.contains(childBind)) continue

            def option = new DynamicOption(child: childName, parent: parent)
            dynamicOptions[dynamicBinding] << option
            visitedChildren.add(childBind)
        }
    }

    private String getSingleSelectReferenceIfAvailable() {
        def topColumn = parsedCsv[0][0]
        def question = topColumn.find(/[$][a-z][a-z0-9_]*/)
        validateVariable(question, topColumn)
        return question == null ? question : question - '$'
    }

    private void validateVariable(String variable, parentVariable) {
        if (variable != null && variable != parentVariable) {
            throw new ValidationException("""Invalid Variable in CSV [${parentVariable}]
 An Id should start with lower case characters follow by low case characters, numbers or underscores""")
        }
    }

    private List<String[]> parseCsv() {
        def csv = toStringArrayList(csvSrc)

        csv = fillUpSpace(csv)

        StringWriter str = new StringWriter()
        def csvWriter = new CSVWriter(str)
        csvWriter.writeAll(csv)
        csvSrc = str.toString()
        parsedCsv = csv
        return parsedCsv
    }

    List<String[]> fillUpSpace(List<String[]> strings) {

        strings.eachWithIndex {row, rowIdx ->
            row.eachWithIndex {cellValue, cellIdx ->
                if (cellValue.isEmpty()) {
                    strings[rowIdx][cellIdx] = strings[rowIdx - 1][cellIdx]
                } else {
                    strings[rowIdx][cellIdx] = strings[rowIdx][cellIdx].trim()
                }
            }
        }
        return strings
    }

    SingleSelectQuestion makeSingleSelectFromList(List<String> strings) {

        SingleSelectQuestion qn = new SingleSelectQuestion(strings.remove(0))
        strings.each {qn.addOption(new Option(it))}

        return qn
    }

    static List<String[]> toStringArrayList(def csv) {
        CSVReader rd = new CSVReader(new StringReader(csv.toString()))
        return rd.readAll();
    }



    static List getValuesForColumn(List csvList, int colIdx) {
        csvList.collect {
            it[colIdx]
        }
    }
}
