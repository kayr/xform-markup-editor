package org.openxdata.markup.ui

import org.openxdata.markup.Util

/**
 * Created by kay on 11/10/2015.
 */
class FormGuidanceTest extends GroovyTestCase {


    void testAddingOnWithMultipleForms() {
        def studyMarkup = '''### Study
                   |## f1
                   |q1
                   |## f2
                   |q1'''.stripMargin()
        def study = Util.createParser(studyMarkup).study()

        UniqueIdProcessor fg = new UniqueIdProcessor(study: study, markup: studyMarkup)

        assertTrue !fg.hasUniqueIdentifier()

        def newMarkup = fg.addUniqueIdentifier()
        assertEquals newMarkup, '''### Study
                                |## f1
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id
                                |## f2
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id'''.stripMargin()

    }

    void testAddOnStudyWithOneOfTheFormsHavingAndId() {
        def studyMarkup = '''### Study
                   |## f1
                   |q1
                   |@absoluteid unique_id
                   |@calculate once(concat('uuid:',uuid()))
                   |@invisible
                   |Unique Id
                   |## f2
                   |q1'''.stripMargin()
        def study = Util.createParser(studyMarkup).study()

        UniqueIdProcessor fg = new UniqueIdProcessor(study: study, markup: studyMarkup)

        assertTrue !fg.hasUniqueIdentifier()

        def newMarkup = fg.addUniqueIdentifier()
        assertEquals newMarkup, '''### Study
                                |## f1
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id
                                |## f2
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id'''.stripMargin()

    }

    void testStudyWithOneForm() {
        def studyMarkup = '''### Study
                   |## f1
                   |q1'''.stripMargin()
        def study = Util.createParser(studyMarkup).study()

        UniqueIdProcessor fg = new UniqueIdProcessor(study: study, markup: studyMarkup)

        assertTrue !fg.hasUniqueIdentifier()

        def newMarkup = fg.addUniqueIdentifier()
        assertEquals newMarkup, '''### Study
                                |## f1
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id'''.stripMargin()

    }

    void testStudyWhenStartLineIsOnDBID() {
        def studyMarkup = '''### Study
                   |## f1
                   |q1
                   |## f2
                   |q1'''.stripMargin()
        def study = Util.createParser(studyMarkup).study()

        UniqueIdProcessor fg = new UniqueIdProcessor(study: study, markup: studyMarkup)

        assertTrue !fg.hasUniqueIdentifier()

        def newMarkup = fg.addUniqueIdentifier()
        assertEquals newMarkup, '''### Study
                                |## f1
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id
                                |## f2
                                |q1
                                |@absoluteid unique_id
                                |@calculate once(concat('uuid:',uuid()))
                                |@invisible
                                |Unique Id'''.stripMargin()

    }


}
