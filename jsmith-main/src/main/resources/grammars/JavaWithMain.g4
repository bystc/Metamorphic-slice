/*
 * Java grammar for generating classes with main methods.
 * This grammar is designed to work with GrammarFileBasedGenerator.
 */

grammar JavaWithMain;

// Parser Rules

compilationUnit
    : packageDeclaration? importDeclaration* classDeclaration EOF
    ;

packageDeclaration
    : 'package' SPACE packageName ';' NL
    ;

packageName
    : /* $jsmith-unique */ Identifier
    | packageName '.' /* $jsmith-unique */ Identifier
    ;

importDeclaration
    : singleTypeImportDeclaration
    | typeImportOnDemandDeclaration
    ;

singleTypeImportDeclaration
    : 'import' SPACE 'java.util.*' ';' NL
    | 'import' SPACE 'java.io.*' ';' NL
    | 'import' SPACE 'java.util.ArrayList' ';' NL
    | 'import' SPACE 'java.util.HashMap' ';' NL
    ;

typeImportOnDemandDeclaration
    : 'import' SPACE 'java.util.*' ';' NL
    ;

classDeclaration
    : normalClassDeclaration
    ;

normalClassDeclaration
    : 'public' SPACE 'class' SPACE /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier classBody
    ;

classBody /* $jsmith-scope */
    : '{' mainMethodDeclaration '}' NL
    ;

mainMethodDeclaration
    : 'public' SPACE 'static' SPACE 'void' SPACE 'main' '(' 'String' SPACE '[' ']' SPACE 'args' ')' methodBody
    ;

methodBody
    : '{' statementList '}'
    ;

statementList
    : statement*
    ;

statement
    : printStatement
    | variableDeclarationStatement
    | forLoopStatement
    | whileLoopStatement
    | ifStatement
    | switchStatement
    | expressionStatement
    ;

printStatement
    : 'System.out.println' '(' printArgument ')' ';'
    ;

printArgument
    : StringLiteral
    | /* $jsmith-var-use */ Identifier
    | expression
    ;

variableDeclarationStatement
    : type /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier '=' expression ';'
    | type /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier ';'
    ;

type
    : 'int'
    | 'String'
    | 'boolean'
    | 'long'
    | 'double'
    | 'float'
    ;

forLoopStatement
    : 'for' '(' forInit ';' forCondition ';' forUpdate ')' forBody
    ;

forInit
    : 'int' /* $jsmith-unique */ /* $jsmith-var-decl */ Identifier '=' /* $jsmith-predicate(int) */
    ;

forCondition
    : /* $jsmith-var-use */ Identifier '<' /* $jsmith-predicate(int) */
    ;

forUpdate
    : /* $jsmith-var-use */ Identifier '++'
    ;

forBody
    : '{' statementList '}'
    | statement
    ;

whileLoopStatement
    : 'while' '(' whileCondition ')' whileBody
    ;

whileCondition
    : 'System.currentTimeMillis' '(' ')' '%' /* $jsmith-predicate(int) */ '<' /* $jsmith-predicate(int) */
    | /* $jsmith-var-use */ Identifier '>' /* $jsmith-predicate(int) */
    ;

whileBody
    : '{' statementList '}'
    | statement
    ;

ifStatement
    : 'if' '(' ifCondition ')' ifBody
    ;

ifCondition
    : 'System.currentTimeMillis' '(' ')' '%' /* $jsmith-predicate(int) */ '>' /* $jsmith-predicate(int) */
    | /* $jsmith-var-use */ Identifier '==' /* $jsmith-predicate(int) */
    | /* $jsmith-var-use */ Identifier '!=' /* $jsmith-predicate(int) */
    ;

ifBody
    : '{' statementList '}'
    | statement
    ;

switchStatement
    : 'switch' '(' switchExpression ')' switchBody
    ;

switchExpression
    : /* $jsmith-predicate(int) */
    | /* $jsmith-var-use */ Identifier
    ;

switchBody
    : '{' switchCase* defaultCase? '}'
    ;

switchCase
    : 'case' /* $jsmith-predicate(int) */ ':' statementList 'break' ';'
    ;

defaultCase
    : 'default' ':' statementList 'break' ';'
    ;

expressionStatement
    : expression ';'
    ;

expression
    : /* $jsmith-var-use */ Identifier
    | /* $jsmith-predicate(int) */
    | /* $jsmith-predicate(String) */
    | /* $jsmith-predicate(boolean) */
    | /* $jsmith-var-use */ Identifier '+' /* $jsmith-var-use */ Identifier
    | /* $jsmith-var-use */ Identifier '-' /* $jsmith-var-use */ Identifier
    | /* $jsmith-var-use */ Identifier '*' /* $jsmith-var-use */ Identifier
    | /* $jsmith-var-use */ Identifier '/' /* $jsmith-var-use */ Identifier
    ;

// Lexer Rules

Identifier
    : [a-zA-Z_$] [a-zA-Z0-9_$]*
    ;

StringLiteral
    : '"' (~["\\\r\n] | '\\' .)* '"'
    ;

SPACE
    : ' '
    ;

NL
    : '\r'? '\n'
    ;

WS
    : [ \t\r\n]+ -> skip
    ;

COMMENT
    : '/*' .*? '*/' -> skip
    ;

LINE_COMMENT
    : '//' ~[\r\n]* -> skip 