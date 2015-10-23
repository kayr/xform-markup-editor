grammar HintParser;

options {
	output = AST;
}

tokens{
     ATTRIBUTE;
     PHRASE;
}

@header {
	package  org.openxdata.markup;
}

@lexer::header {
	package org.openxdata.markup;
}

@members {
	public void recoverFromMismatchedToken(IntStream input,
										   RecognitionException e,
										   int ttype,
										   BitSet follow)
		throws RecognitionException {
		throw e;
	}
}

@lexer::members {
	public void reportError(RecognitionException e) {
		throw new RuntimeException("lexical error", e);
	}
}

@rulecatch {
	catch (RecognitionException e) {
		throw e;
	}
}

hint
	: assignMent+
	;	

assignMent 	:  ID^ '=' STRING^
	;




ID  :	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')*
    ;

STRING
    :  '"' (STR_ENTRY | ESC_SEQ )* '"'
    ;

fragment
STR_ENTRY
	:	~('\\'|'"')
	;



fragment
HEX_DIGIT : ('0'..'9'|'a'..'f'|'A'..'F') ;

fragment
ESC_SEQ
    :   '\\' ('\"'|'\\')
    |   UNICODE_ESC
    |   OCTAL_ESC
    ;

fragment
OCTAL_ESC
    :   '\\' ('0'..'3') ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7') ('0'..'7')
    |   '\\' ('0'..'7')
    ;

fragment
UNICODE_ESC
    :   '\\' 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT
    ;
