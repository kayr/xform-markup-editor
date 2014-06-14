package org.openxdata.markup

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import org.openxdata.markup.exception.ValidationException

import static org.openxdata.markup.Util.parseBind

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
class DynamicBuilder {

    def csvSrc = "";
    String csvFile = null;
    List<List<String>> parsedCsv
    int line = 0

    List<IQuestion> questions = []
    Map<String, List<DynamicOption>> dynamicOptions = [:]
    String singleSelectQuestion
    def singleSelectOptions = []

    int linesAppended = 0
    private final static int MAX_LINES = 3

    public boolean appendLine(String line) {
        if (Study.quickParse.get() && line && linesAppended++ >= MAX_LINES)
            return false

        line = line << "\n"
        csvSrc = csvSrc << line
        return true
    }


    public void addQuestionsToForm(HasQuestions form) {
        try {
            parse()
            if (areWeBuildingQuestionsDirectlyFromCSV()) {
                questions.each {
                    it.line = line
                    form.addQuestion(it)
                }
            } else {
                def singleSelectQuestionInstance = Form.findQuestionWithBinding(singleSelectQuestion, form)

                if (singleSelectQuestionInstance == null)
                    throw new ValidationException("""Error while parsing CSV. SingleSelect question with id [$singleSelectQuestion]
could not be found in the form""")

                if (!(singleSelectQuestionInstance instanceof SingleSelectQuestion))
                    throw new ValidationException("Error while parsing CSV.Question with id[$singleSelectQuestion] is not a SingleSelect Question", singleSelectQuestionInstance.line)
                singleSelectQuestionInstance.options = singleSelectOptions
            }

            def localDynamicOptionKeys = dynamicOptions.keySet()
            def formDynamicOptionKeys = form.parentForm.dynamicOptions.keySet()

            def newLocalKeys = localDynamicOptionKeys - formDynamicOptionKeys

            if (!newLocalKeys.containsAll(localDynamicOptionKeys))
                throw new ValidationException("You have duplicate columns in your csv files ${localDynamicOptionKeys - newLocalKeys}")

            form.parentForm.dynamicOptions.putAll(dynamicOptions)


        } catch (Exception e) {
            if (e instanceof ValidationException)
                throw e
            def newEx = new RuntimeException("Error while creating dynamic question:\n " + e.toString(), e)
            newEx.stackTrace = e.stackTrace
            throw newEx
        }
    }

    private boolean areWeBuildingQuestionsDirectlyFromCSV() {
        return getSingleSelectReferenceIfAvailable() == null
    }

    public void parse() {
        List<String[]> csv = parseCsv()

        def singleSelectCol = getValuesForColumn(csv, 0).unique { Util.getBindName(it) }
        def singleSelVar = getSingleSelectReferenceIfAvailable()
        def weAreBuildingQnsDirectlyFromCSV = areWeBuildingQuestionsDirectlyFromCSV()

        if (weAreBuildingQnsDirectlyFromCSV) {
            def singleSelQuestion = makeSingleSelectFromList(singleSelectCol)
            questions << singleSelQuestion
        } else {
            this.singleSelectQuestion = singleSelVar
            singleSelectCol.remove(0)
            singleSelectOptions = singleSelectCol.collect { return new Option(it) }
        }

        def headers = csv[0]

        for (int headerIdx = 1; headerIdx < headers.length; headerIdx++) {
            def csvHeader = headers[headerIdx]

            DynamicQuestion qn = new DynamicQuestion(csvHeader)
            if (weAreBuildingQnsDirectlyFromCSV) {
                qn.dynamicInstanceId = qn.binding
                qn.parentQuestionId = questions[headerIdx - 1].binding  //set previous header column as the parentBinding of the current one.
                questions << qn
            } else {
                validateVariable(qn.binding, qn.text)
            }

            buildDynamicModel(headerIdx, qn.binding)

        }

    }

    //todo optimise this for faster perfomance
    private void buildDynamicModel(Integer columnIdx, String instanceId) {

        def createdOptions = new HashSet()
        //map of visited children and parents
        def visitedChildren = new HashMap()
        dynamicOptions[instanceId] = []

        for (int rowIdx = 1; rowIdx < parsedCsv.size(); rowIdx++) {

            def currentRow = parsedCsv[rowIdx]

            def childName = currentRow[columnIdx]

            //optimisation to avoid parsing text many times
            //get the parent binding it was already parsed by the previous call to this method
            def parentBinding = columnIdx == 1 ? parseBind(currentRow[columnIdx - 1]).bind : currentRow[columnIdx - 1]


            def option = new DynamicOption(parentBinding, childName)

            //we do not allow options of same parent tna child  in a single dynamic instance
            if (createdOptions.contains(option)) {
                //set the new bind in the global csv so as to keep history
                //generally a visited column should have binding by the time we leave this method
                currentRow[columnIdx] = option.bind
                continue
            }


            //check if this child option_bind already exist and change the binding if parent is different
            def currentBinding = option.bind
            if (visitedChildren.containsKey(currentBinding)) {

                String parBind = option.parentBinding
                String childBind = option.bind

                //build a proper binding that with proper number if underscores
                def newBinding = parBind.endsWith('_') || childBind.startsWith('_') ? "${option.parentBinding}${option.bind}" : "${option.parentBinding}_${option.bind}"

                option.setBind(newBinding)

                //we do not allow options of same parent the child  in a single dynamic instance
                if (createdOptions.contains(option)) {
                    currentRow[columnIdx] = option.bind  //set the new bind in the csv so as to keep history
                    continue
                }
                println "WARNING: Duplicate DynamicOption[$currentBinding in $option.parentBinding] with [$currentBinding in ${visitedChildren[currentBinding]}] : created new binding [$newBinding]"
            }

            // add this option to the instance
            dynamicOptions[instanceId] << option

            currentRow[columnIdx] = option.bind  //set the new bind in the csv so as to keep history
            createdOptions.add(option)
            if (!visitedChildren[currentBinding])
                visitedChildren[currentBinding] = new HashSet()
            //parent bindings are mainly kept for logging purposes for now. Remove them later
            visitedChildren[currentBinding] << option.parentBinding
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
        if (csvFile != null) {
            readCSVFile()
        }
        def csv = toStringArrayList(csvSrc)

        csv = fillUpSpace(csv)

        StringWriter str = new StringWriter()
        def csvWriter = new CSVWriter(str)
        csvWriter.writeAll(csv)
        csvSrc = str.toString()
        parsedCsv = csv
        return parsedCsv
    }

    private void readCSVFile() {
        def file = new File(csvFile)
        if (!file.exists()) {
            def formDir = System.getProperty('form.dir')
            file = new File(formDir + "/$csvFile")
            if (!file.exists()) {
                println "formDir: $formDir path: $file.absolutePath"
                throw new FileNotFoundException(csvFile, "The Dynamic list could not be found")
            }
        }
        if (Study.quickParse.get()) {
            readMaxLines(file)
        } else {
            csvSrc = file.text
        }
    }

    private void readMaxLines(File file) {
        file.withReader { reader ->
            def line = reader.readLine()
            while (line) {

                def appended = appendLine(line)
                if (!appended) return

                line = reader.readLine()
            }
        }
    }

    //todo optimise this for faster perfomance
    List<String[]> fillUpSpace(List<String[]> strings) {

        strings.eachWithIndex { row, rowIdx ->
            row.eachWithIndex { cellValue, cellIdx ->
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
        strings.each { qn.addOption(new Option(it)) }

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
