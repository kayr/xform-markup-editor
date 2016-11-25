package org.openxdata.markup.ui

/**
 * Created by kay on 11/25/2016.
 */
class FileSavingTest extends GroovyTestCase {

    void testCurrentFileNull_TextNull() {
        assert !IOHelper.needsSaving(null, null)
    }

    void testCurrentFileNull_TextNotNull() {
        assert IOHelper.needsSaving(null, 'Text')
    }

    void testCurrentFileNotNull_TextNull() {
        assert IOHelper.needsSaving(file('SomeText'), null)
    }

    void testCurrentFileNotNull_TextNotNull_SameText() {
        assert !IOHelper.needsSaving(file('SomeText'), 'SomeText')
    }

    void testCurrentFileNotNull_TextNotNull_NotExist() {
        assert IOHelper.needsSaving(notExists(file('SomeText')), 'SomeText')
    }

    void testCurrentFileNot_TextNotNull_DiffText() {
        assert IOHelper.needsSaving(file('SomeText'), 'OtherText')
    }

    def file(String text) {
        def file = new File('')
        file.metaClass.getText = { String enconding -> text }
        file.metaClass.exists = { true }
        return file
    }

    def notExists(File file){
        file.metaClass.exists = { false }
        return file
    }


}
