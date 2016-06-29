package org.openxdata.markup.deserializer

/**
 * Created by kay on 6/16/14.
 */
class DeSerializerFixtures {

    static def advancedMarkedUp = [
            markUp: '''### Study


## Form


@required
Gender
  >$male Male
  >$female Female


@required
@date
@validif (today() - .) > (365*18)
@message Age Less than 18 is too young
@id birthdate
Birth date


@required
@number
@validif . > 0 and . < 300
@message Weight must be between 0 and 300 kgs
@id weight
Weight


@required
@number
@validif . > 0 and . < 600
@message Height is too tall
@id heightcm
Height


@required
@readonly
@calculate $weight div (($heightcm div 100.0)*($heightcm div 100.0))
@id bmi
Bio Mass Index


@required
@number
@validif . >= 50 and . <= 300
@message Systolic BP must be between 50 and 300 mmHg
@id systolic
Systolic BP


@required
@number
@validif . >= 10 and . <= 150
@message Diastolic BP must be between 10 and 150 mmHg
@id diastolic
Diastolic BP


@boolean
@required
@id hearthistory
Heart health History


@boolean
@required
@enableif $gender = 'male'
@id hypertension
Has Suspension


@boolean
@required
@id chestpain
Has chest pain


@boolean
@required
@id angina
Has angina


@validif . = false() or $chestpain = true()
@message You can't have angina without chestpain!
@boolean
@id armpain
Has armpain
''',
            xform : '''<xforms>
  <model>
    <instance id="study_form_v1">
      <study_form_v1 id="0" name="Form" formKey="study_form_v1">
        <gender />
        <birthdate />
        <weight />
        <heightcm />
        <bmi />
        <systolic />
        <diastolic />
        <hearthistory />
        <hypertension />
        <chestpain />
        <angina />
        <armpain />
      </study_form_v1>
    </instance>
    <bind id="gender" nodeset="/study_form_v1/gender" type="xsd:string" required="true()" />
    <bind id="birthdate" nodeset="/study_form_v1/birthdate" type="xsd:date" required="true()" constraint="(today() - .) &gt; (365*18)" message="Age Less than 18 is too young" />
    <bind id="weight" nodeset="/study_form_v1/weight" type="xsd:int" required="true()" constraint=". &gt; 0 and . &lt; 300" message="Weight must be between 0 and 300 kgs" />
    <bind id="heightcm" nodeset="/study_form_v1/heightcm" type="xsd:int" required="true()" constraint=". &gt; 0 and . &lt; 600" message="Height is too tall" />
    <bind id="bmi" nodeset="/study_form_v1/bmi" type="xsd:string" required="true()" locked="true()" calculate="/study_form_v1/weight div ((/study_form_v1/heightcm div 100.0)*(/study_form_v1/heightcm div 100.0))" />
    <bind id="systolic" nodeset="/study_form_v1/systolic" type="xsd:int" required="true()" constraint=". &gt;= 50 and . &lt;= 300" message="Systolic BP must be between 50 and 300 mmHg" />
    <bind id="diastolic" nodeset="/study_form_v1/diastolic" type="xsd:int" required="true()" constraint=". &gt;= 10 and . &lt;= 150" message="Diastolic BP must be between 10 and 150 mmHg" />
    <bind id="hearthistory" nodeset="/study_form_v1/hearthistory" type="xsd:boolean" required="true()" />
    <bind id="hypertension" nodeset="/study_form_v1/hypertension" type="xsd:boolean" required="true()" relevant="/study_form_v1/gender = 'male'" action="enable" />
    <bind id="chestpain" nodeset="/study_form_v1/chestpain" type="xsd:boolean" required="true()" />
    <bind id="angina" nodeset="/study_form_v1/angina" type="xsd:boolean" required="true()" />
    <bind id="armpain" nodeset="/study_form_v1/armpain" type="xsd:boolean" constraint=". = false() or /study_form_v1/chestpain = true()" message="You can't have angina without chestpain!" />
  </model>
  <group id="1">
    <label>Page1</label>
    <select1 bind="gender">
      <label>Gender</label>
      <item id="male">
        <label>Male</label>
        <value>male</value>
      </item>
      <item id="female">
        <label>Female</label>
        <value>female</value>
      </item>
    </select1>
    <input bind="birthdate">
      <label>Birth date</label>
    </input>
    <input bind="weight">
      <label>Weight</label>
    </input>
    <input bind="heightcm">
      <label>Height</label>
    </input>
    <input bind="bmi">
      <label>Bio Mass Index</label>
    </input>
    <input bind="systolic">
      <label>Systolic BP</label>
    </input>
    <input bind="diastolic">
      <label>Diastolic BP</label>
    </input>
    <input bind="hearthistory">
      <label>Heart health History</label>
    </input>
    <input bind="hypertension">
      <label>Has Suspension</label>
    </input>
    <input bind="chestpain">
      <label>Has chest pain</label>
    </input>
    <input bind="angina">
      <label>Has angina</label>
    </input>
    <input bind="armpain">
      <label>Has armpain</label>
    </input>
  </group>
</xforms>''']
    static def wssbForm = [
            markUp: '''
### Tripple Study
## WSSB Members


@id wssbq5_1
*who do you contact when you have a techincal problem on your scheme
>$scheme scheme attendant
>$technician Technician
>$hpm HPM
>Don't Know

@id wssbq5_2
@showif $wssbq5_1 = 'scheme' or $wssbq5_1='hpm' or $wssbq5_1='hpm\' and $wssbq5_1='hpm\'or $wssbq5_1='technician\'
@comment When your water facility stops working, how many days does it on average take for a HPM to come and do an assessment, once you have reported the problem?
*How many days does it take for this person to make an assessment  on reported problems
>1
>2
>3
>4
>5
>6-10
>10 and above
>Dont know

'''
    ]

    static def gpsForm = [
            markUp: ''' ### F

## f

@gps
Gp1

repeat {    ffd
    @gps
    Gp2

    @Gps
    Gp3

}
''']
    static def dynFormWithQuotes = [
            markUp: ''' ### F

## f

@gps
Gp1

@parent gp1
Qn 1
$> cities

dynamic_instance{
parent,cities
uganda,kampala
kenya,"nairobi ""M"""
uganda,entebbe
}
''']

    static def nestedGroups = [
            markUp     : '''### s

## f
@id oldpageid
#> old page
q1.1

@id group_page_1
@layout:jdsj jsjd
@bind:someBindAttr bind_attr
group{ p1.2
    q1.1.1
    group{
        q1.1.2
        @id group_page_2
        group{ g.1.1.3
            q1.1.3.1
        }
    }
}
group{
   q1.3
}
q1.4
#> otherpage
q4
''',
            xml        : '''<xforms>
  <model>
    <instance id="s_f_v1">
      <s_f_v1 id="0" name="f" formKey="s_f_v1">
        <oldpageid>
          <q1_dot_1 />
          <group_page_1>
            <q1_dot_1_dot_1 />
            <q1_dot_1_dot_2 />
            <group_page_2>
              <q1_dot_1_dot_3_dot_1 />
            </group_page_2>
          </group_page_1>
          <q1_dot_3 />
          <q1_dot_4 />
        </oldpageid>
        <q4 />
      </s_f_v1>
    </instance>
    <bind id="oldpageid" nodeset="/s_f_v1" />
    <bind id="q1_dot_1" nodeset="/s_f_v1/q1_dot_1" type="xsd:string" />
    <bind id="group_page_1" nodeset="/s_f_v1" someBindAttr="bind_attr" />
    <bind id="q1_dot_1_dot_1" nodeset="/s_f_v1/q1_dot_1_dot_1" type="xsd:string" />
    <bind id="q1_dot_1_dot_2" nodeset="/s_f_v1/q1_dot_1_dot_2" type="xsd:string" />
    <bind id="group_page_2" nodeset="/s_f_v1" />
    <bind id="q1_dot_1_dot_3_dot_1" nodeset="/s_f_v1/q1_dot_1_dot_3_dot_1" type="xsd:string" />
    <bind id="q1_dot_3" nodeset="/s_f_v1/q1_dot_3" type="xsd:string" />
    <bind id="q1_dot_4" nodeset="/s_f_v1/q1_dot_4" type="xsd:string" />
    <bind id="q4" nodeset="/s_f_v1/q4" type="xsd:string" />
  </model>
  <group id="oldpageid" binding="oldpageid">
    <label>old page</label>
    <input bind="q1_dot_1">
      <label>q1.1</label>
    </input>
    <group id="group_page_1" jdsj="jsjd" binding="group_page_1">
      <label>p1.2</label>
      <input bind="q1_dot_1_dot_1">
        <label>q1.1.1</label>
      </input>
      <group id="0">
        <label></label>
        <input bind="q1_dot_1_dot_2">
          <label>q1.1.2</label>
        </input>
        <group id="group_page_2" binding="group_page_2">
          <label>g.1.1.3</label>
          <input bind="q1_dot_1_dot_3_dot_1">
            <label>q1.1.3.1</label>
          </input>
        </group>
      </group>
    </group>
    <group id="0">
      <label></label>
      <input bind="q1_dot_3">
        <label>q1.3</label>
      </input>
    </group>
    <input bind="q1_dot_4">
      <label>q1.4</label>
    </input>
  </group>
  <group id="2">
    <label>otherpage</label>
    <input bind="q4">
      <label>q4</label>
    </input>
  </group>
</xforms>''',
            xmlNumbered: '''<xforms>
  <model>
    <instance id="s_f_v1">
      <s_f_v1 id="0" name="f" formKey="s_f_v1">
        <_1oldpageid>
          <_1_1q1_dot_1 />
          <_1_2group_page_1>
            <_1_2_1q1_dot_1_dot_1 />
            <_1_2_2q1_dot_1_dot_2 />
            <_1_2_3group_page_2>
              <_1_2_3_1q1_dot_1_dot_3_dot_1 />
            </_1_2_3group_page_2>
          </_1_2group_page_1>
          <_1_3q1_dot_3 />
          <_1_4q1_dot_4 />
        </_1oldpageid>
        <_2q4 />
      </s_f_v1>
    </instance>
    <bind id="_1oldpageid" nodeset="/s_f_v1/_1oldpageid" />
    <bind id="_1_1q1_dot_1" nodeset="/s_f_v1/_1_1q1_dot_1" type="xsd:string" />
    <bind id="_1_2group_page_1" nodeset="/s_f_v1/_1_2group_page_1" someBindAttr="bind_attr" />
    <bind id="_1_2_1q1_dot_1_dot_1" nodeset="/s_f_v1/_1_2_1q1_dot_1_dot_1" type="xsd:string" />
    <bind id="_1_2_2q1_dot_1_dot_2" nodeset="/s_f_v1/_1_2_2q1_dot_1_dot_2" type="xsd:string" />
    <bind id="_1_2_3group_page_2" nodeset="/s_f_v1/_1_2_3group_page_2" />
    <bind id="_1_2_3_1q1_dot_1_dot_3_dot_1" nodeset="/s_f_v1/_1_2_3_1q1_dot_1_dot_3_dot_1" type="xsd:string" />
    <bind id="_1_3q1_dot_3" nodeset="/s_f_v1/_1_3q1_dot_3" type="xsd:string" />
    <bind id="_1_4q1_dot_4" nodeset="/s_f_v1/_1_4q1_dot_4" type="xsd:string" />
    <bind id="_2q4" nodeset="/s_f_v1/_2q4" type="xsd:string" />
  </model>
  <group id="oldpageid" binding="oldpageid">
    <label>1. old page</label>
    <input bind="_1_1q1_dot_1">
      <label>1.1. q1.1</label>
    </input>
    <group id="group_page_1" jdsj="jsjd" binding="group_page_1">
      <label>1.2. p1.2</label>
      <input bind="_1_2_1q1_dot_1_dot_1">
        <label>1.2.1. q1.1.1</label>
      </input>
      <group id="0">
        <label></label>
        <input bind="_1_2_2q1_dot_1_dot_2">
          <label>1.2.2. q1.1.2</label>
        </input>
        <group id="group_page_2" binding="group_page_2">
          <label>1.2.3. g.1.1.3</label>
          <input bind="_1_2_3_1q1_dot_1_dot_3_dot_1">
            <label>1.2.3.1. q1.1.3.1</label>
          </input>
        </group>
      </group>
    </group>
    <group id="0">
      <label></label>
      <input bind="_1_3q1_dot_3">
        <label>1.3. q1.3</label>
      </input>
    </group>
    <input bind="_1_4q1_dot_4">
      <label>1.4. q1.4</label>
    </input>
  </group>
  <group id="2">
    <label>otherpage</label>
    <input bind="_2q4">
      <label>2. q4</label>
    </input>
  </group>
</xforms>''',
            formatted :'''

### s


## f


@id oldpageid
#> old page


q1.1


@id group_page_1
@layout:jdsj jsjd
@bind:someBindAttr bind_attr
group{ p1.2

    q1.1.1


    group{

        q1.1.2


        @id group_page_2
        group{ g.1.1.3

            q1.1.3.1
        }
    }
}


group{

    q1.3
}


q1.4


#> otherpage


q4
'''

    ]


}
