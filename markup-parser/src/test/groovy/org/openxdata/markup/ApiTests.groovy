package org.openxdata.markup

/**
 * Created by kay on 2/10/2017.
 */
class ApiTests extends GroovyTestCase {

    def markup = '''
                ## Form
                
                q1
                
                q2 
                
                @id g1
                group{
                    
                    q33
                    
                    @id g22
                    group{
                        
                        q444
                        
                        @id r1
                        repeat{ Repeats
                            
                            r5555
                            
                                @id rg3
                                group{
                                    rq66                
                                }
                                
                                @id r22
                                repeat{ dsd
                                     r2266
                                }
                            
                            r66666
                        }
                        
                        @id gxxx
                        group{
                            @id r2
                            repeat{ xxx
                                rq2
                            }
                        }
                    }
                    
                }
                
                unique id
                
                @id g3
                group{
                    q66                
                }
                
                q7
                '''

    void test_getAllFirstLevelQuestionsNotInRepeat_ShouldReturn_() {

        def form = Converter.markup2Form(markup)

        def qns = form.allFirstLevelQuestionsNotInRepeat


        assert qns.size() == 9
        assert ['q1', 'q2', 'q33', 'q444', 'r1', 'unique_id', 'q66', 'q7', 'r2'].every { t -> qns.any { q -> q.id == t } }

        def r1Repeats = (form['r1'] as HasQuestions).allFirstLevelQuestionsNotInRepeat
        assert r1Repeats.size() == 4
        assert ['r5555', 'rq66', 'r22', 'r66666'].every { t -> r1Repeats.any { q -> q.id == t } }


        def r2Repeats = (form['r22'] as HasQuestions).allFirstLevelQuestionsNotInRepeat
        assert r2Repeats.size() == 1
        assert ['r2266'].every { t -> r2Repeats.any { q -> q.id == t } }

        assert form['rq66'].firstRepeatParentOrForm.binding == 'r1'
        assert form['r1'].firstRepeatParentOrForm == form

        assert form.firstChildRepeats.size() == 2
        assert form.firstChildRepeats*.id == ['r1', 'r2']

    }

    void test_AddAfterElement() {

        def markup = '''
                ## Form
                
                q1
                
                q2 
                
                @id g1
                group{
                    
                    q33
                    
                    
                    group{
                        
                        q444
                        
            
                        @id gxxx
                        group{
                            @id r2
                            repeat{ xxx
                                rq2
                            }
                        }
                    }
                    
                }
                '''

        def form = Converter.markup2Form(markup)

        def q = new TextQuestion("Added-QQ1")
        def q1 = new TextQuestion("Added-QQ2")
        def q2 = new TextQuestion("Added-QQ3")
        def q3 = new TextQuestion("Added-QQ4")
        def q4 = new TextQuestion("Added-QQ5")
        def q5 = new TextQuestion("Added-QQ6")

        form['rq2'].insertAfterMe(q)

        form['rq2'].insertBeforeMe(q1)

        form['r2'].insertBeforeMe(q2)
        form['q444'].insertBeforeMe(q3)
        form['q2'].insertBeforeMe(q3)
        form['r2'].insertAfterMe(q5)



        def text = Converter.to(FORMAT.MARKUP,String).from(FORMAT.FORM).convert(form)


        def newMarkup = '''@id form_v1
## Form


q1


Added-QQ4


q2


@id g1
group { 

    q33


    group { 

        Added-QQ4


        q444


        @id gxxx
        group { 

            Added-QQ3


            @id r2
            repeat{ xxx

                Added-QQ2


                rq2


                Added-QQ1


            }


            Added-QQ6


        }


    }


}
'''

        assertEquals TestUtils.trimAllLines(newMarkup), TestUtils.trimAllLines(text)

    }

}
