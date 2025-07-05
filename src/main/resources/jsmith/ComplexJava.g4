grammar ComplexJava;

compilationUnit
    : classDeclaration
    ;

classDeclaration
    : 'public class' Identifier '{'
        fieldDeclaration*
        methodDeclaration+
      '}'
    ;

fieldDeclaration
    : type Identifier ';'
    ;

methodDeclaration
    : 'public' type Identifier '(' parameterList? ')' block
    ;

parameterList
    : parameter (',' parameter)*
    ;

parameter
    : type Identifier
    ;

block
    : '{' statement* '}'
    ;

statement
    : variableDeclaration
    | assignment
    | ifStatement
    | forStatement
    | whileStatement
    | returnStatement
    | methodCall ';'
    ;

variableDeclaration
    : type Identifier ('=' expression)? ';'
    ;

assignment
    : Identifier '=' expression ';'
    ;

ifStatement
    : 'if' '(' expression ')' block ('else' block)?
    ;

forStatement
    : 'for' '(' variableDeclaration expression ';' assignment ')' block
    ;

whileStatement
    : 'while' '(' expression ')' block
    ;

returnStatement
    : 'return' expression? ';'
    ;

methodCall
    : Identifier '(' (expression (',' expression)*)? ')'
    ;

expression
    : expression binaryOp expression
    | '(' expression ')'
    | Identifier
    | literal
    ;

binaryOp
    : '+' | '-' | '*' | '/' | '%' | '>' | '<' | '==' | '!=' | '>=' | '<=' | '&&' | '||'
    ;

type
    : 'int' | 'double' | 'boolean' | 'String'
    ;

literal
    : INT
    | DOUBLE
    | BOOL
    | STRING
    ;

Identifier: [a-zA-Z_][a-zA-Z0-9_]*;
INT: [0-9]+;
DOUBLE: [0-9]+'.'[0-9]+;
BOOL: 'true' | 'false';
STRING: '"' .*? '"';
WS: [ \t\r\n]+ -> skip; 