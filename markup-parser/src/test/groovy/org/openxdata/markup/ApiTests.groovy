package org.openxdata.markup

/**
 * Created by kay on 2/10/2017.
 */
class ApiTests extends GroovyTestCase {


    void test_getAllFirstLevelQuestionsNotInRepeat_ShouldReturn_() {

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
                    }
                    
                }
                
                unique id
                
                @id g3
                group{
                    q66                
                }
                
                q7
                '''

        def form = Converter.markup2Form(markup)

        def qns = form.allFirstLevelQuestionsNotInRepeat


        assert qns.size() == 8
        assert ['q1', 'q2', 'q33', 'q444', 'r1', 'unique_id', 'q66', 'q7'].every { t -> qns.any { q -> q.id == t } }

        def r1Repeats = (form['r1'] as HasQuestions).allFirstLevelQuestionsNotInRepeat
        assert r1Repeats.size() == 4
        assert ['r5555', 'rq66', 'r22','r66666'].every { t -> r1Repeats.any { q -> q.id == t } }


        def r2Repeats = (form['r22'] as HasQuestions).allFirstLevelQuestionsNotInRepeat
        assert r2Repeats.size() == 1
        assert ['r2266'].every { t -> r2Repeats.any { q -> q.id == t } }


    }

}
