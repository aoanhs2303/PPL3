/**
 * Student name: Tran Nhu Luc
 * Student ID: 1511918
 */
grammar MC;

@lexer::header{
    package mc.parser;
}

@lexer::members{
@Override
public Token emit() {
    switch (getType()) {
    case UNCLOSE_STRING:       
        Token result = super.emit();
        // you'll need to define this method
        throw new UncloseString(result.getText());
        
    case ILLEGAL_ESCAPE:
        result = super.emit();
        throw new IllegalEscape(result.getText());

    case ERROR_CHAR:
        result = super.emit();
        throw new ErrorToken(result.getText()); 

    default:
        return super.emit();
    }
}
}

@parser::header{
    package mc.parser;
}

options{
    language=Java;
}


/*program  : mctype 'main' LB RB LP body? RP EOF ;

mctype: INTTYPE | VOIDTYPE ;

body: funcall SEMI;

exp: funcall | INTLIT ;

funcall: ID LB exp? RB ;*/



program: (declerations)*;

declerations: declerationsVariable | declerationsFunction;

declerationsVariable: primitivetype manyvariable SM;

primitivetype:  INTTYPE | FLOATTYPE | STRINGTYPE | BOOLEANTYPE;

manyvariable: variable (CM variable)*;
 
variable: ID | array;

array: ID LSB INTLIT RSB;

declerationsFunction: typeFunction ID LB parameterlist RB blockstatement;

typeFunction: primitivetype | VOIDTYPE | primitivetype LSB RSB ;

parameterlist: (parameter(CM parameter)*)?;

parameter: primitivetype ID (LSB RSB)?;  

blockstatement: LP declarationPart statementPart RP;

declarationPart: declerationsVariable*;

statementPart: statement*;

statement    : ifStatement 
             | dowhileStatement 
             | forStatement 
             | breakStatement 
             | continueStatement 
             | returnStatement 
             | expressionStatement
             | blockstatement 
             ;    

ifStatement: IF LB expression RB statement (ELSE statement)?;

dowhileStatement: DO statement+ WHILE expression SM ;

forStatement: FOR LB expression SM expression SM expression RB statement;

breakStatement: BREAK SM;

continueStatement: CONTINUE SM;

returnStatement: RETURN expression? SM;

expressionStatement: expression SM;

expression : expression1 ASSIGN expression | expression1;

expression1 : expression1 OR expression2 | expression2;

expression2 : expression2 AND expression3 | expression3;

expression3 : expression4 (EQUAL|NOTEQUAL) expression4 | expression4;

expression4 : expression5 (LESS | LESSEQUAL | GREATER | GREATEREQUAL) expression5 | expression5;

expression5 : expression5 (ADD | SUB) expression6 | expression6;

expression6 : expression6 (DIV | MUL | MOD) expression7 | expression7;

expression7 : (SUB | NOT) expression7 | expression8;

expression8 : expression9 LSB expression RSB | expression9;

expression9 : LB expression RB
            | ID
            | BOOLEANLIT
            | STRING
            | INTLIT
            | FLOAT
            | funcall
            ;
           

funcall: ID LB expressionList RB ;

expressionList : expression? | expression (CM expression)*;



BOOLEANLIT: TRUE | FALSE;
INTLIT: [0-9]+;
FLOAT: Digits '.' Digits ? ExponentPart?
     | '.' Digits ExponentPart? 
     | Digits ExponentPart
     ; 

fragment Digits: [0-9]+;
fragment ExponentPart: [eE][-]?Digits;

STRING: '"' StringCharacters? '"'
{
    String str = getText();
    str = str.substring(1,str.length() -1);
    setText(str);
};
fragment 
Escape: '\\' [bntfr''""\\];

fragment 
SingleCharacter: ~[""\\\r\n]|Escape; 

fragment
StringCharacters: SingleCharacter+;


//TYPE

INTTYPE:        'int' ;
VOIDTYPE:       'void' ;
BOOLEANTYPE:    'boolean';
FLOATTYPE:      'float';
STRINGTYPE:     'string';

//keyword:
BREAK:          'break';
CONTINUE:       'continue';
ELSE:           'else';
IF:             'if';
RETURN:         'return';
WHILE:              'while';
DO:                 'do';
FOR:                'for';
TRUE: 'true';
FALSE: 'false';



//Operator:
SUB: '-';
ADD: '+';
MUL: '*';
NOT: '!';
OR: '||';
NOTEQUAL: '!=';
LESS: '<';
LESSEQUAL: '<=';
ASSIGN: '=';

DIV: '/';
MOD: '%';
AND: '&&';
EQUAL: '==';
GREATER: '>';
GREATEREQUAL: '>=';


//3.4 Separators:
LSB: '[';
RSB: ']';

LP: '{';
RP: '}';

LB: '(';
RB: ')';

SM: ';';
CM: ',';


ID:     [_a-zA-Z][_a-zA-Z0-9]*;

WS : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines

COMMENT_LINE : '//'~[\r\n]* -> skip ;

COMMENT_BLOCK : '/*' .*? '*/' -> skip;





ILLEGAL_ESCAPE: '"' .*? '\\' ~[bntfr''""\\]? // them dau cham hoi
{
    String str = getText();
    str = str.substring(1);
    setText(str);   
};

UNCLOSE_STRING: '"' SingleCharacter*
{
    String str = getText();
    str = str.substring(1);
    setText(str);   
};

ERROR_CHAR: .;