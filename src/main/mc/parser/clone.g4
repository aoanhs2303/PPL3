program: (declerations)*;

declerations: declerationsVariable | declerationsFunction;

declerationsVariable: primitivetype manyvariable SM;

primitivetype: INTTYPE | FLOATTYPE | STRINGTYPE | BOOLEANTYPE;

manyvariable: variable (CM variable)* SM;

variable: ID | arrray;

arrray: ID LSB INTLIT RSB;

declerationsFunction: typeFunction ID LB parameterlist RB blockstatement;

typeFunction: primitivetype | VOIDTYPE | primitivetype LSB RSB;

prameterlist: (parameter (CM parameter)*)?;

parameter: primitivetype ID (LSB RSB)?;

blockstatement: LP declerationPart statementPart RB;

declerationPart: declerationsVariable*;

statementPart: statement*;

statement: ifStatement
		 | dowhileStatement
		 | forStatement
		 | continueStatement
		 | returnStatement
		 | expressStatement
		 | blockstatement
		 | breakStatement
		 ;

ifStatement: IF LB expression RB statement (ELSE statement)? ;

dowhileStatement: DO statementPart+ WHILE express SM;

forStatement: FOR LB expression SM expression SM expression RB statement;

breakStatement: BREAK SM;

returnStatement: RETURN expression? SM;

expressStatement: expression SM;

expression: expression1 ASSIGN expression | expression1;

expression1: expression1 OR expression2 | expression2;

expression2: expression2 AND expression3 | expression3;

expression3: expression4 (EQUAL | NOTEQUAL) expression4 | expression4;

expression4: expression5 (LESS| LESSEQUAL | GREATER | GREATEREUQAL) expression5 | expression5;

expression5: expression5 (ADD|SUB) expression6 | expression6;

expression6: expression6 (DIV | MUL | MOD) expression7 | expression7;

expression7 (SUB | NOT) expression7 | expression8;

expression8: expression9 LSB expression RSB | expression9;

expression9: LB expression RB
		   | ID
		   | BOOLEANLIT
		   | STRINGT
		   | INTLIT
		   | FLOAT
		   | funcall
		   ;

funcall: ID LB expressionList RB;

expressionList: expression? | expression (CM expression)*;


BOOLEANLIT: TRUE | FALSE;
INTLIT: [0-9]+;
FLOAT: Digits '.' Digits ? ExponentPart?
	 | '.' Digits ExponentPart?
	 | Digits ExponentPart
	 ;

fragment Digits: [0-9]+;
fragment ExponentPart: [eE][-]?Digits;

STRING: '"' StringCharacter? '"'
{
	String str = getText();
	str = str.substring(1, str.length()-1);
	setText(str);
};

fragment Escape: '\\' [bntfr''""\\];

fragment SingleCharacter: ~[""\\r\n]|Escape;

frament StringCharacter+;

ID: [_a-zA-Z][_a-zA-Z0-9]*;

WS: [ \t\n\r]+ -> skip;

COMMENT_LINE: '//' ~[\r\n]* -> skip;

COMMENT_BLOCK: '/*' .*? '*/' -> skip

ILLEGAL_ESCAPE: '"' .*? '\\' ~[bntfr''""\\]
{
	String str = getText();
	str = str.substring(1);
	setText(str);
}

UNCLOSE_STRING: '"' StringCharacter*
{
	String str = getText();
	str = str.substring(1);
	setText(str);
}

ERROR_CHAR: .;