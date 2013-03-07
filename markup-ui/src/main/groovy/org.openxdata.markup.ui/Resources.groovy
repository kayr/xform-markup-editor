package org.openxdata.markup.ui

/**
 * Created with IntelliJ IDEA.
 * User: kay
 * Date: 3/2/13
 * Time: 1:36 PM
 * To change this template use File | Settings | File Templates.
 */
class Resources {

    static def sampleStudy = '''//This is a product of http://omnitech.co.ug and http://www.openxdata.org
### Sample Markup Study

## Simple Form


Name


Sex
>male
>female


@boolean
Is pregnant


@number
What is your Age


Course units
>>Computer Science
>>Math
>>History


@number
Score math


@number
Score Computer Science


@number
Score History

Total score

@number
Number of children

repeat{ Child details

    Child Name

    Child Sex
    >male
    >female

    @number
    Child Age

}


dynamic{
Region,\tSub-Region,\t    City
Washington,\tKing,\t    Seattle
Washington,\tKing,\t    Redmond
Washington,\tPierce,\t    Tacoma
Washington,\tPierce,\t    Puyallup
Texas,\t    King-Texas,\tDumont
Texas,\t    King-Texas,\tFinney
Texas,\t    Cameron,\tbrownsville
Texas,\t    Cameron,\tharlingen
Africa,\t    Uganda,\t    Kampala
Africa,\t    Uganda,\t    Masaka
Africa,\t    Uganda,\t    Mbale
Africa,\t    Uganda,\t    Mbarara
Africa,\t    Kenya,\t    Nairobi
Africa,\t    Kenya,\t    Kisumu
Africa,\t    Kenya,\t    Eldoret
Europe,\t    Netherlands,Netherlandis
Europe,\t    Netherlands,Another Netherlands
}
'''

    static def simpleSkip= '''### Markup Example Study


## Simple Skip Logic form


@required
Name



@id sex
Sex
>male
>$female female


@boolean
@enableif $sex = 'female'
Is pregnant



@number
@validif . < 40 and . > 20
@message You should be betwen 20 and 40 years
What is your Age



@required
@id course
Course units
>>$comp_sci_opt Computer Science
>>$math_opt Math
>>$history_opt History



@number
@enableif $course = 'math_opt'
@validif . > 0 and . < 100
@message You the score has to be between 0 and 100
@id math
Score math



@number
@enableif $course = 'comp_sci_opt'
@validif . > 0 and . < 100
@message You the score has to be between 0 and 100
@id comp_sci
Score Computer Science



@number
@enableif $course = 'history_opt'
@validif . > 0 and . < 100
@message You the score has to be between 0 and 100
@id history
Score History



@readonly
@calculate $math + $history + $comp_sci
Total score
'''

    static def advanceSkip='''//This form was used to demostrate the new Evaluator developed by Brent
//NOTE: THIS FORM CAN CURRENTLY RUN ON THE MFORMS CLIENTS 2.0 UPWARDS
### Advanced Skiplogic Study

## Advanced Skiplogic form

@required
Gender
>$male Male
>$female Female


@required
@date
@validif  (today() - .) > (365*18)
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



@required
@boolean
@enableif $bmi > 30.0 or ( $chestpain = 'male' and $hypertension = true() ) or ($systolic > 180 or $diastolic > 110) or (today() - $birthdate > 365 * 35 and $hearthistory = true() ) or ($chestpain = true() and $angina != true() ) or  $armpain = true()
Seen Cardioligist
'''

    static def oxdSampleForm='''### Example study2

## Example form2

Patient ID

Title
>Mr
>Mrs

First name

@required
Last name

Sex
>$male Male
>Female


@date
@validif . < today()
@message Birthdate Cannot be after today
Birthdate

@decimal
@validif . > 0  and . <= 200
@message Should be between 0 and 200 (inclusive)
Weight(Kg)

@decimal
@validif . >= 1 and . <= 9
@message Height should be between 1 and 9
Height

@enableif $sex = 'male'
Is patient pregnant

@comment Please select all anti-retrovirals that the patient is taking
ARVS
>>AZT
>>ABICVAR
>>EFIVARENCE
>>TRIOMUNE
>>TRUVADA

@picture
Picture

@audio
Sound

@video
Record video

dynamic{
Region,\tSub-Region,\t    City
Washington,\tKing,\t    Seattle
Washington,\tKing,\t    Redmond
Washington,\tPierce,\t    Tacoma
Washington,\tPierce,\t    Puyallup
Texas,\t    King-Texas,\tDumont
Texas,\t    King-Texas,\tFinney
Texas,\t    Cameron,\tbrownsville
Texas,\t    Cameron,\tharlingen
Africa,\t    Uganda,\t    Kampala
Africa,\t    Uganda,\t    Masaka
Africa,\t    Uganda,\t    Mbale
Africa,\t    Uganda,\t    Mbarara
Africa,\t    Kenya,\t    Nairobi
Africa,\t    Kenya,\t    Kisumu
Africa,\t    Kenya,\t    Eldoret
Europe,\t    Netherlands,Netherlandis
Europe,\t    Netherlands,Another Netherlands
}

@id children_number
Number of children



@validif length(.) =  $children_number
@message Enter details of all children
repeat{ Details of Children
    Name

    @number
    Age

    @id child_sex
    Sex
    >Male
    >Female
}



@time
Start time

@time
@id endtime
End time
'''
}
