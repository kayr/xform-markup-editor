grammar XPath;

/*
A simplified XPath 1.0 grammar.

Originally written by Jan-Willem van den Broek
Modified by Brent Atkinson for use with OpenXData
*/

options {
	output = AST;
}

tokens {
  PATHSEP  =  '/';
  LPAR  =  '(';
  RPAR  =  ')';
  LBRAC  =  '[';
  RBRAC  =  ']';
  MINUS  =  '-';
  PLUS  =  '+';
  DOT  =  '.';
  MUL  =  '*';
  COMMA  =  ',';
  EQ = '=';
  NEQ = '!=';
  LESS  =  '<';
  MORE  =  '>';
  LE  =  '<=';
  GE  =  '>=';
  COLON  =  ':';
  AND = 'and';
  OR = 'or';
  DIV = 'div';
  MOD = 'mod';
  TRUE = 'true';
  FALSE = 'false';
  ABSPATH;
  RELPATH;
  LITERAL;
  NUMBER;
  FUNCTION;
  QNAME;
  UMINUS;
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

eval  :  expr EOF -> expr
  ;

locationPath
  :  relativeLocationPath -> ^(RELPATH relativeLocationPath)
  |  absoluteLocationPathNoroot -> ^(ABSPATH absoluteLocationPathNoroot)
  ;

absoluteLocationPathNoroot
  :  PATHSEP relativeLocationPath
  ;

relativeLocationPath
  :  step (PATHSEP step)*
  ;

step  :  nodeTest
  |  abbreviatedStep
  ;

nodeTest:  nameTest
  ;

abbreviatedStep
  :  DOT
  ;

expr  :  orExpr  ;

primaryExpr
//include brackets we need them
  :  LPAR expr RPAR
  |  Literal -> ^(LITERAL Literal)
  |  Number  -> ^(NUMBER Number)
  |  functionCall
  ;


functionCall
//include brackets we need them
  :  functionName LPAR functionArgs? RPAR -> ^(FUNCTION functionName LPAR functionArgs? RPAR)
  |  booleanCompat LPAR RPAR -> ^(FUNCTION booleanCompat LPAR RPAR)
  |  booleanCompat -> ^(FUNCTION booleanCompat)
  ;

functionArgs
  :  expr ( COMMA! expr )*
  ;

booleanCompat : TRUE | FALSE;

pathExprNoRoot
  :  locationPath
  |  primaryExpr (PATHSEP relativeLocationPath)?
  ;

orExpr  :  andExpr (OR^ andExpr)*
  ;

andExpr  :  equalityExpr (AND^ equalityExpr)*
  ;

equalityExpr
  :  relationalExpr ((EQ|NEQ)^ relationalExpr)*
  ;

relationalExpr
  :  additiveExpr ((LESS|MORE|LE|GE)^ additiveExpr)*
  ;

additiveExpr
  :  multiplicativeExpr ((PLUS|MINUS)^ multiplicativeExpr)*
  ;

multiplicativeExpr
  :  unaryExprNoRoot ((MUL|DIV|MOD)^ multiplicativeExpr)?
  |  PATHSEP ((DIV|MOD)^ multiplicativeExpr)?
  ;

unaryExprNoRoot
  :  MINUS unaryExprNoRoot -> ^(UMINUS unaryExprNoRoot)
  | pathExprNoRoot
  ;

qName  :  pre=nCName -> ^(QNAME $pre)
  |       pre=nCName COLON lcl=nCName -> ^(QNAME $pre $lcl)
  ;

functionName
  :  qName  // Does not match nodeType, as per spec.
  ;

nameTest: qName
  ;

nCName:  NCName
  ;

Number  :  Digits (DOT Digits?)?
  |  DOT Digits
  ;

fragment
Digits  :  ('0'..'9')+
  ;

Literal  :  '"' ~'"'* '"'
  |  '\'' ~'\''* '\''
  ;

Whitespace
  :  (' '|'\t'|'\n'|'\r')+ {$channel = HIDDEN;}
  ;

NCName  :  NCNameStartChar NCNameChar*
  ;

fragment
NCNameStartChar
  :  'A'..'Z'
  |   '_'
  |  'a'..'z'
  |  '\u00C0'..'\u00D6'
  |  '\u00D8'..'\u00F6'
  |  '\u00F8'..'\u02FF'
  |  '\u0370'..'\u037D'
  |  '\u037F'..'\u1FFF'
  |  '\u200C'..'\u200D'
  |  '\u2070'..'\u218F'
  |  '\u2C00'..'\u2FEF'
  |  '\u3001'..'\uD7FF'
  |  '\uF900'..'\uFDCF'
  |  '\uFDF0'..'\uFFFD'
// Unfortunately, java escapes can't handle this conveniently,
// as they're limited to 4 hex digits. TODO.
//  |  '\U010000'..'\U0EFFFF'
  ;

fragment
NCNameChar
  :  NCNameStartChar | '-' | '.' | '0'..'9'
  |  '\u00B7' | '\u0300'..'\u036F'
  |  '\u203F'..'\u2040'
  ;

