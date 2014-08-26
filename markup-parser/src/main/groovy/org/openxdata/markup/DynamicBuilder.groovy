package org.openxdata.markup

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import groovy.transform.CompileStatic
import org.openxdata.markup.exception.ValidationException

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
@CompileStatic
class DynamicBuilder {

    boolean instanceOnly = false
    StringBuilder csvSrc = new StringBuilder();
    String csvFilePath = null;
    List<String[]> parsedCsv
    int line = 0

    List<IQuestion> questions = []
    Map<String, List<DynamicOption>> dynamicOptions = [:]
    String referencedSingleSelectBinding
    List parsedSingleSelectOptions = []

    int linesAppended = 0
    private final static int MAX_LINES = 3

    DynamicBuilder() {}

    DynamicBuilder(boolean instanceOnly) {
        this.instanceOnly = instanceOnly;
    }

    public void addQuestionsToForm(HasQuestions form) {
        try {
            parse()
            if (!isInstanceOnly()) {
                if (isCsvDirectBuild()) {
                    addBuiltQnsDirectly(form)
                } else {
                    bindOptionsToSingleSelectInstance(form)
                }
            }
            addDynamicOptions(form)
        } catch (Exception e) {
            if (e instanceof ValidationException) throw e

            def newEx = new RuntimeException("Error while creating dynamic question:\n " + e.toString(), e)
            newEx.setStackTrace(e.getStackTrace())
            throw newEx
        }
    }

    private void addDynamicOptions(HasQuestions form) {
        def localDynamicOptionKeys = dynamicOptions.keySet()
        def formDynamicOptionKeys = form.parentForm.dynamicOptions.keySet()

        //get the local keys and remove the already existing keys
        def newLocalKeys = localDynamicOptionKeys - formDynamicOptionKeys

        //all local keys should be new otherwise it indicates you have duplicate dynamic instances
        if (!newLocalKeys.containsAll(localDynamicOptionKeys))
            throw new ValidationException("You have duplicate columns in your csv files ${localDynamicOptionKeys - newLocalKeys}")

        form.parentForm.dynamicOptions.putAll(dynamicOptions)
    }

    private void bindOptionsToSingleSelectInstance(HasQuestions form) {
        IQuestion singleSelectQuestionInstance = Form.findQuestionWithBinding(referencedSingleSelectBinding, form)

        if (singleSelectQuestionInstance == null)
            throw new ValidationException("Error while parsing CSV. SingleSelect question with id [$referencedSingleSelectBinding]\ncould not be found in the form")

        if (!(singleSelectQuestionInstance instanceof SingleSelectQuestion))
            throw new ValidationException("Error while parsing CSV.Question with id[$referencedSingleSelectBinding] is not a SingleSelect Question", singleSelectQuestionInstance.line)

        (singleSelectQuestionInstance as SingleSelectQuestion).options = parsedSingleSelectOptions
    }

    private def addBuiltQnsDirectly(HasQuestions form) {
        for (qn in questions) {
            qn.line = line
            form.addQuestion(qn)
        }
    }

    private boolean isCsvDirectBuild() {
        return getSingleSelectReferenceIfAvailable() == null && !isInstanceOnly()
    }

    public void parse() {
        List<String[]> csv = parseCsv()

        def weAreBuildingQnsDirectlyFromCSV = isCsvDirectBuild()
        if (!isInstanceOnly()) {
            def options = getUniqueValuesForFirstColumn(csv)
            if (weAreBuildingQnsDirectlyFromCSV) {
                makeSingleSelectFromList(options)
            } else {
                generateSingleSelectOptions(options)
            }
        }

        buildModelAndQuestions(weAreBuildingQnsDirectlyFromCSV)
    }

    private void generateSingleSelectOptions(List<String> options) {
        def singleSelVar = getSingleSelectReferenceIfAvailable()
        this.referencedSingleSelectBinding = singleSelVar
        options.remove(0)
        parsedSingleSelectOptions = options.collect { return new Option(it as String) }
    }

    private static List<String> getUniqueValuesForFirstColumn(List<String[]> csv) {
        getValuesForColumn(csv, 0).unique { Util.getBindName(it as String) } as List<String>
    }

    private void buildModelAndQuestions(boolean weAreBuildingQnsDirectlyFromCSV) {
        String[] headers = parsedCsv[0]
        for (int headerIdx = 1; headerIdx < headers.length; headerIdx++) {

            String csvHeader = headers[headerIdx]

            DynamicQuestion qn = new DynamicQuestion(csvHeader)
            if (weAreBuildingQnsDirectlyFromCSV) {
                qn.dynamicInstanceId = qn.binding
                qn.parentQuestionId = questions[headerIdx - 1].binding
                //set previous header column as the parentBinding of the current one.
                questions << qn
            } else {
                validateVariable(qn.binding, qn.text)
            }

            def instanceId = qn.binding as String
            buildDynamicModel(headerIdx, instanceId)
        }
    }

    //todo optimise this for faster perfomance
    private void buildDynamicModel(Integer columnIdx, String instanceId) {
        HashSet createdOptions = new HashSet()
        //map of visited children and parents we use a HashMap mainly for logging purposes to
        //to show the parents a specific child has
        Map<String, HashSet<String>> visitedChildren = new HashMap()
        dynamicOptions[instanceId] = []

        for (int rowIdx = 1; rowIdx < parsedCsv.size(); rowIdx++) {

            def currentRow = parsedCsv[rowIdx]

            String childName = currentRow[columnIdx]

            //optimisation to avoid parsing text many times
            //get the parent binding it was already parsed by the previous call to this method
            String parentBinding = columnIdx == 1 ? Util.parseBind(currentRow[columnIdx - 1],rowIdx)['bind'] : currentRow[columnIdx - 1]


            def option = new DynamicOption(parentBinding, childName)

            //we do not allow options of same parent and child  in a single dynamic instance
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

                //build a proper binding with proper number of underscores
                def newBinding = parBind.endsWith('_') || childBind.startsWith('_') ? "${option.parentBinding}${option.bind}" : "${option.parentBinding}_${option.bind}"

                option.setBind(newBinding as String)

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
            //parent bindings are mainly kept for logging purposes
            (visitedChildren[currentBinding] as HashSet) << option.parentBinding
        }
    }

    private String getSingleSelectReferenceIfAvailable() {
        def topColumn = parsedCsv[0][0]
        def question
        if (isInstanceOnly()) {
            question = topColumn
        } else {
            question = topColumn.find(/[$][a-z][a-z0-9_]*/)
        }
        validateVariable(question, topColumn)
        return question == null ? question : question - '$'
    }

    private static void validateVariable(String variable, parentVariable) {
        if (variable != null && variable != parentVariable) {
            throw new ValidationException("Invalid Variable in CSV [${parentVariable}]\n An Id should start with lower case characters follow by low case characters, numbers or underscores")
        }
    }

    public boolean appendLine(String line) {
        if (Study.quickParse.get() && line && linesAppended++ >= MAX_LINES)
            return false
        csvSrc << line << "\n"
        return true
    }

    private List<String[]> parseCsv() {
        if (csvFilePath != null) {
            readCSVFile()
        }

        def csv = toStringArrayList(csvSrc.toString())
        parsedCsv = fillUpSpace(csv)
//        convertCsvToString(csv)
        return parsedCsv
    }

    private String convertCsvToString(List<String[]> csv) {
        StringWriter str = new StringWriter()
        def csvWriter = new CSVWriter(str)
        csvWriter.writeAll(csv)
        str.toString()
    }

    private void readCSVFile() throws Exception {
        def file = new File(csvFilePath)
        if (!file.exists()) {
            def formDir = System.getProperty('form.dir')
            file = new File(formDir + "/$csvFilePath")
        }

        if (!file.exists()) {
            println "formDir: $file.parent path: $file.absolutePath"
            throw new FileNotFoundException("The Dynamic list could not be found: [$file.absolutePath]")
        }

        if (Study.quickParse.get()) {
            readMaxLines(file)
        } else {
            csvSrc = new StringBuilder(file.text)
        }
    }

    private void readMaxLines(File file) {
        file.withReader { Reader reader ->
            def line = reader.readLine()
            while (line) {

                def appended = appendLine(line)
                if (!appended) return

                line = reader.readLine()
            }
        }
    }

    //todo optimise this for faster perfomance
    static List<String[]> fillUpSpace(List<String[]> strings) {

        strings.eachWithIndex { String[] row, int rowIdx ->
            row.eachWithIndex { String cellValue, int cellIdx ->
                if (cellValue.isEmpty()) {
                    strings[rowIdx][cellIdx] = strings[rowIdx - 1][cellIdx]
                } else {
                    strings[rowIdx][cellIdx] = strings[rowIdx][cellIdx].trim()
                }
            }
        }
        return strings
    }

    def makeSingleSelectFromList(List<String> strings) {
        SingleSelectQuestion qn = new SingleSelectQuestion(strings.remove(0))
        strings.each { qn.addOption(new Option(it as String)) }
        questions << qn
    }

    static List<String[]> toStringArrayList(def csv) {
        CSVReader rd = new CSVReader(new StringReader(csv.toString()))
        return rd.readAll();
    }

    static List getValuesForColumn(List csvList, int colIdx) {
        csvList.collect { String[] row ->
            row[colIdx]
        }
    }
}
