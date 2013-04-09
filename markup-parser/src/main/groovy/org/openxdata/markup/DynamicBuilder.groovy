package org.openxdata.markup

import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 2/1/13
 * Time: 11:52 PM
 * To change this template use File | Settings | File Templates.
 */
class DynamicBuilder {

    def csvSrc = "";

    List<IQuestion> questions = []


    public void appendLine(String line) {
        line = line << "\n"
        csvSrc = csvSrc << line
    }


    public void addQuestionsToForm(HasQuestions form) {
        try {
            parse()
            questions.each {
                form.addQuestion(it)
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while creating dynamic question: " + e.toString(), e)
        }
    }

    public void parse() {
        def csv = toStringArrayList(csvSrc)

        csv = fillUpSpace(csv)

        StringWriter str = new StringWriter()
        def csvWriter = new CSVWriter(str)
        csvWriter.writeAll(csv)
        csvSrc = str.toString()

        def singleSelectCol = getValuesForColumn(csv, 0).unique {Util.getBindName(it)}

        def singleSelQuestion = makeSingleSelectFromList(singleSelectCol)

        questions << singleSelQuestion

        def headers = csv[0]

        headers.eachWithIndex {csvHeader, headerIdx ->

            if (headerIdx == 0)
                return

            DynamicQuestion qn = new DynamicQuestion(csvHeader)
            qn.parentQuestionId = questions[headerIdx - 1].binding  //set previous header column as the parent of the current one.

            def visitedChildren = new HashSet()

            csv.eachWithIndex { csvRow, csvRowIdx ->
                if (csvRowIdx == 0) return

                def childName = csvRow[headerIdx]
                def childBind = Util.getBindName(childName)

                def parent = Util.getBindName(csvRow[headerIdx - 1])
                def option = new DynamicOption(child: childName, parent: parent)
                if (visitedChildren.contains(childBind)) return
                qn.options.add(option)
                visitedChildren.add(childBind)

            }

            questions << qn
        }

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
