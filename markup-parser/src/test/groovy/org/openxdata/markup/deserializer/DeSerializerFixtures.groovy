package org.openxdata.markup.deserializer

/**
 * Created by kay on 6/16/14.
 */
class DeSerializerFixtures {

    static forms = [
            advancedMarkedUp: [
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
                    xform: '''<xforms>
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
    ]
}
