package org.openxdata.markup.serializer

import org.openxdata.markup.Fixtures

/**
 * Created by kay on 7/15/14.
 */
class ODKFixtures {

    static def formWithInvisible = [
            form: '''
### Std
## Frm

@invisible
Invisible


@readonly
Disabled

''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Frm</h:title>
    <model>
      <instance>
        <std_frm_v1 id="0" name="Frm">
          <invisible />
          <disabled />
        </std_frm_v1>
      </instance>
      <bind id="invisible" nodeset="/std_frm_v1/invisible" type="string" readonly="true()" />
      <bind id="disabled" nodeset="/std_frm_v1/disabled" type="string" readonly="true()" />
    </model>
  </h:head>
  <h:body>
    <group>
      <label>Page1</label>
      <input ref="/std_frm_v1/invisible">
        <label>Invisible</label>
      </input>
      <input ref="/std_frm_v1/disabled">
        <label>Disabled</label>
      </input>
    </group>
  </h:body>
</h:html>'''
    ]
    static def formWithSkipLogicAndReadOnly = [
            form: '''
### Std
## Frm

Sex
>m
>f

@readonly
@enableif $sex = 'f'
Is pregnant

@readonly
@hideif $sex = 'f'
Are you a father HideIf

@readonly
@disableif $sex = 'f'
Are you a father DisableIF
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>Frm</h:title>
    <model>
      <instance>
        <std_frm_v1 id="0" name="Frm">
          <sex />
          <is_pregnant />
          <are_you_a_father_hideif />
          <are_you_a_father_disableif />
        </std_frm_v1>
      </instance>
      <bind id="sex" nodeset="/std_frm_v1/sex" type="string" />
      <bind id="is_pregnant" nodeset="/std_frm_v1/is_pregnant" type="string" relevant="/std_frm_v1/sex = 'f'" />
      <bind id="are_you_a_father_hideif" nodeset="/std_frm_v1/are_you_a_father_hideif" type="string" relevant="not(/std_frm_v1/sex = 'f')" />
      <bind id="are_you_a_father_disableif" nodeset="/std_frm_v1/are_you_a_father_disableif" type="string" relevant="not(/std_frm_v1/sex = 'f')" />
    </model>
  </h:head>
  <h:body>
    <group>
      <label>Page1</label>
      <select1 ref="/std_frm_v1/sex">
        <label>Sex</label>
        <item>
          <label>m</label>
          <value>m</value>
        </item>
        <item>
          <label>f</label>
          <value>f</value>
        </item>
      </select1>
      <select1 ref="/std_frm_v1/is_pregnant">
        <label>Is pregnant</label>
        <item>
          <label>Yes</label>
          <value>true</value>
        </item>
        <item>
          <label>No</label>
          <value>false</value>
        </item>
      </select1>
      <select1 ref="/std_frm_v1/are_you_a_father_hideif">
        <label>Are you a father HideIf</label>
        <item>
          <label>Yes</label>
          <value>true</value>
        </item>
        <item>
          <label>No</label>
          <value>false</value>
        </item>
      </select1>
      <select1 ref="/std_frm_v1/are_you_a_father_disableif">
        <label>Are you a father DisableIF</label>
        <item>
          <label>Yes</label>
          <value>true</value>
        </item>
        <item>
          <label>No</label>
          <value>false</value>
        </item>
      </select1>
    </group>
  </h:body>
</h:html>'''
    ]

    static def timeStamp = [
            form: '''### S
## F

@id endtime
@dateTime
Prefilled Date
''',
            xml: '''<h:html xmlns="http://www.w3.org/2002/xforms" xmlns:h="http://www.w3.org/1999/xhtml" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:jr="http://openrosa.org/javarosa">
  <h:head>
    <h:title>F</h:title>
    <model>
      <instance>
        <s_f_v1 id="0" name="F">
          <endtime />
        </s_f_v1>
      </instance>
      <bind id="endtime" nodeset="/s_f_v1/endtime" type="dateTime" jr:preload="timestamp" jr:preloadParams="end" />
    </model>
  </h:head>
  <h:body>
    <group>
      <label>Page1</label>
      <input ref="/s_f_v1/endtime">
        <label>Prefilled Date</label>
      </input>
    </group>
  </h:body>
</h:html>'''
    ]

    static def oxdSampleForm = [
            form: Fixtures.oxdSampleForm,
            xml: ''''''
    ]

}
