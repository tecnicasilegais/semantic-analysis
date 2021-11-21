    
    
%{
  /**
   * Trabalho final da disciplina Construção de Compiladores 2021/2
   * 
   * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5
   * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7
   * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
   */
  import java.io.*;
%}


%token IDENT, INT, DOUBLE, BOOL, NUM, D_NUM
%token LITERAL, AND, VOID, IF, ELSE
%token STRUCT, FUNCT, RETURN
%token STRING

%right '='
%nonassoc '>'
%left '+'
%left AND
%left '['  

%type <sval> IDENT
%type <sval> LITERAL
%type <ival> NUM
%type <dval> D_NUM
%type <obj> type
%type <obj> exp

%%

program: globalDeclarationList functionList ;

//Variable declarations

globalDeclarationList: { currIdType = IdentType.Global; } globalDeclaration globalDeclarationList | ;

globalDeclaration: type { currentType = (Symbol)$1; } auxDecl ';'
                 | structDeclaration
                 ;

localDeclaration: type { currentType = (Symbol)$1; } auxDecl ';'
                ;

auxDecl: IDENT arrayDecl { createIdent($1); }
       | declAttribuition
       ;

arrayDecl: '[' NUM ']' arrayDecl { currentType = Symbol.createArray("?",Tab.Tp_ARRAY, 
                                                   currIdType, $2, currentType); }
         | '[' NUM ']'  { currentType = Symbol.createArray("?",Tab.Tp_ARRAY, 
                                                   currIdType, $2, currentType); }
         ;

declAttribuition: id '=' exp { checkType('=', currentType, (Symbol)$3);  } 
                | identList
                ;

identList: identList ',' id 
         | id  
         ;

id: IDENT { createIdent($1); }
  ;

//Struct declarations

structDeclaration: STRUCT { currIdType = IdentType.StructDef;
                            currentType = Tab.Tp_STRUCT; } id 
                      '{' { currIdType = IdentType.StructField; } structFieldList '}' ';'
                 ;

structFieldList: structFieldList structFieldDeclaration
               | structFieldDeclaration
               ;

structFieldDeclaration: type { currentType = (Symbol)$1; } auxStructDecl ';'
                      ;

auxStructDecl: IDENT arrayDecl { createIdent($1); }
             | identList
             ;

type: INT    { $$ = Tab.Tp_INT; }
     | DOUBLE  { $$ = Tab.Tp_DOUBLE; }
     | BOOL   { $$ = Tab.Tp_BOOL; }
     | STRING { $$ = Tab.Tp_STRING; }   
     | IDENT {Symbol node = ts.find($1, IdentType.StructDef); 
              if (node == null) { yyerror("Semantic: Type > " + $1 + " < doesn't exist."); 
                                  $$ = Tab.Tp_ERROR; } //variables can be struct type or base types
              else { $$ = node; } } 
     ;

//Functions

functionList: functionList function
            |
            ;

//todo fazer tipoRetorno listaParametros 
function: FUNCT { currIdType = IdentType.FunctionDef; } returnType id 
                    '(' { currIdType = IdentType.FunctionParam; } args ')' functionBlock
        ;

functionBlock: '{' { currIdType = IdentType.Local; }  functionContent '}' ;

functionContent: functionContent command
               | functionContent localDeclaration
               |
               ;

returnType: type { currentType = (Symbol)$1; }
          | VOID { currentType = Tab.Tp_VOID; }
          ;

args: functionParameterList
    |
    ;

/*
return: RETURN exp ';' 
      | RETURN ';'
      |
      ;
*/
functionParameterList: functionParameter ',' functionParameterList
                     | functionParameter
                     ;

functionParameter: type { currentType = (Symbol)$1; } id
                      ;

commandList: commandList command
           |
           ;

command: exp '=' exp ';' { checkType('=', (Symbol)$1, (Symbol)$3); }
       | IF '(' exp ')' block else  {  if ( ((Symbol)$3) != Tab.Tp_BOOL) 
                                     yyerror("Semantic: 'if' Expression must be of logical type." + ((Symbol)$3).getTypeString());
                                    }     
       ;

else: ELSE block
    |
    ;

block: '{' commandList '}';

exp: exp '+' exp     { $$ = checkType('+', (Symbol)$1, (Symbol)$3); }
   | exp '>' exp     { $$ = checkType('>', (Symbol)$1, (Symbol)$3); }
   | exp AND exp     { $$ = checkType(AND, (Symbol)$1, (Symbol)$3); } 
   | '(' exp ')'     { $$ = $2; }    
   | exp '[' exp ']' { if ((Symbol)$3 != Tab.Tp_INT) {
                          yyerror("Semantic: Index must be an Integer.");
                          $$ = Tab.Tp_ERROR;
                        }
                        else if (((Symbol)$1).getType() != Tab.Tp_ARRAY) {
                          yyerror("Semantic: Element is not an array, it can not be indexed.");
                        }
                        else {
                          $$ = ((Symbol)$1).getBaseType();
                        }
                      } 
   | NUM              { $$ = Tab.Tp_INT; }    
   | D_NUM            { $$ = Tab.Tp_DOUBLE; } 
   | LITERAL          { $$ = Tab.Tp_STRING; }
   | IDENT            { $$ = searchIdent($1); }     
   ;


%%

  private Yylex lexer;

  private Tab ts;

  public static final int ARRAY = 1500;

  private Tab currScope;
  private IdentType currIdType;
  private Symbol currentType;

  private int yylex () {
    int yyl_return = -1;
    try {
      yylval = new ParserVal(0);
      yyl_return = lexer.yylex();
    }
    catch (IOException e) {
      System.err.println("IO error :"+e);
    }
    return yyl_return;
  }


  public void yyerror (String error) {
    System.err.printf("Error (line: %2d) \tMessage: %s\n", lexer.getLine(), error);
  }

  public void yywarning (String warning) {
    System.err.printf("Warning (line: %2d) \tMessage: %s\n", lexer.getLine(), warning);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);

    ts = new Tab(); 
    currScope = ts;
  }  

  public void setDebug(boolean debug) {
    yydebug = debug;
  }

  public void showTable() { ts.show(); ts.showAllLocalScopes();}

  public static void main(String args[]) throws IOException {
    System.out.println("\n\nSimple Semantic Verifier\n");

    Parser yyparser;
    if ( args.length > 0 ) {
      // parse a file
      yyparser = new Parser(new FileReader(args[0]));
    }
    else {
      // interactive mode
      System.out.println("[Quit with CTRL-D]");
      System.out.print("Programa de entrada:\n");
      yyparser = new Parser(new InputStreamReader(System.in));
    }

    yyparser.yyparse();

    yyparser.showTable();

    System.out.print("\n\nDone!\n");
    
  }

  Symbol searchIdent(String ident) {
    Symbol node;
    if(currScope != ts) {
      node = currScope.find(ident);
      if (node != null) {
        return node.getType();
      }
    }
    node = ts.find(ident);
    if (node != null) {
      return node.getType();
    }
    yyerror("Semantic: Variable <" + ident + "> does not exist."); 
    return Tab.Tp_ERROR;
  }

  void createIdent(String ident) {
    if(currScope != ts) { //only local variable, function parameter or struct field here
      Symbol node = currScope.find(ident, currIdType);

      if(node != null) {
        switch (currIdType) {
          case StructField:
            yyerror("Semantic => struct: " + currScope.getScopeName() + " already have a field named: " + ident);
            return;
          case FunctionParam:
          case Local:
            yyerror("Semantic => function: " + currScope.getScopeName() + " already have a definition for: " + ident);
            break;
          default:
            yyerror("Semantic => Unexpeted IdentType: " + currIdType.name());
            return;
        }
      }
      else if (IdentType.isLocalType(currIdType)) {
        currScope.insert(Symbol.createNormal(ident, currentType, currIdType));
      }
    }
    //Globals, struct name and function name here...
    globalIdent(ident); //verifies global ident creation and verify if a local ident shadows a global one

  }

  void globalIdent(String ident){
    Symbol node = ts.find(ident, currIdType);
    switch (currIdType) {
      //verifying first the local scope ones for warnings
      case FunctionParam:
      case Local:
        if(node != null) { yywarning("Semantic => variable > " + ident + " < shadows a global definition."); }
        break;
      case Global:
        if(node != null) {
          yyerror("Semantic => variable > " + ident + " < already defined.");
        } else {
          ts.insert(Symbol.createNormal(ident, currentType, currIdType));
        }
        break;
      case FunctionDef:
        if(node != null) {
          yyerror("Semantic => function name > " + ident + " < is already defined. ");
        } else {
          createFunctionOrStruct(ident);
        }
        break;
      case StructDef:
        if(node != null) {
          yyerror("Semantic => struct name > " + ident + " < is already defined. ");
        } else {
          createFunctionOrStruct(ident);
        }
        break;
    }
  }

  void createFunctionOrStruct(String ident){
    Symbol functionOrStruct = Symbol.createStructOrFunction(ident, currentType, currIdType);

    currScope = functionOrStruct.getLocalScope();
    
    ts.insert(functionOrStruct);
  }

  Symbol checkType(int operator, Symbol s1, Symbol s2) {
       
    switch ( operator ) {
      case '=':
        if (checkAssignTypes(s1, s2))
             return s1;
         else
             yyerror("Semantic: Incompatible types for assignment: " + s1.getTypeString() + " = " + s2.getTypeString());
        break;

      case '+' :
        if ( s1 == Tab.Tp_INT && s2 == Tab.Tp_INT)
              return Tab.Tp_INT;
        else if ( (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE)) ||
                                (s2 == Tab.Tp_DOUBLE && (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE)) ) 
             return Tab.Tp_DOUBLE;     
        else
            yyerror("Semantic: Incompatible types for addition: " + s1.getTypeString() + " + " + s2.getTypeString());
        break;

      case '>' :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          yyerror("Semantic: Incompatible types for relational operation: " + s1.getTypeString() + " > " + s2.getTypeString());
          break;

      case AND:
        if (s1 == Tab.Tp_BOOL && s2 == Tab.Tp_BOOL)
          return Tab.Tp_BOOL;
        else
          yyerror("Semantic: Incompatible types for logical operation: " + s1.getTypeString() + " && " +s2.getTypeString());
          break;
    }

    return Tab.Tp_ERROR;
  }

  bool checkAssignTypes(Symbol s1, Symbol s2) {
    if ( (s1 == Tab.Tp_INT && s2 == Tab.Tp_INT) ||
             (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE )) ||
             (s1 == Tab.Tp_STRING && s2 == Tab.Tp_STRING) ||
             (s1 == s2) 
        )
        return true;
    return false;
  }

