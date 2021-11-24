    
    
%{
  /**
   * Trabalho final da disciplina Construção de Compiladores 2021/2
   * 
   * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5
   * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7
   * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
   */
  import java.io.*;
  import java.util.function.Consumer;
%}


%token IDENT, INT, DOUBLE, BOOL, NUM, D_NUM, TRUE, FALSE, STRING
%token LITERAL, VOID, IF, ELSE
%token STRUCT, FUNCT, RETURN
%token AND, OR, GTEQ, LTEQ, EQ, NEQ, INCR, DECR

// https://en.wikipedia.org/wiki/Operators_in_C_and_C%2B%2B#Operator_precedence

%right '='                      // 16
%left OR                        // 15
%left AND                       // 14
%nonassoc EQ, NEQ               // 10
%nonassoc '>', '<', GTEQ, LTEQ  // 9
%left '+', '-'                  // 6
%left "*"                       // 5
%right '!'                      // 3
%left '[', INCR, DECR           // 2

%type <sval> IDENT
%type <sval> LITERAL
%type <ival> NUM
%type <dval> D_NUM
%type <obj> type
%type <obj> exp
%type <obj> incrType

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

type: INT      { $$ = Tab.Tp_INT; }
     | DOUBLE  { $$ = Tab.Tp_DOUBLE; }
     | BOOL    { $$ = Tab.Tp_BOOL; }
     | STRING  { $$ = Tab.Tp_STRING; }   
     | IDENT   {Symbol node = ts.find($1); 
                if (node == null) { yyerror("Semantic: Type > " + $1 + " < doesn't exist."); 
                                    $$ = Tab.Tp_ERROR; } //variables can be struct type or base types
                else if (node.getIdentType() != IdentType.StructDef) { yyerror("Ident > " + $1 + " < is not a type."); 
                                                                  $$ = Tab.Tp_ERROR; }
                else { $$ = node; } } 
     ;

//Functions

functionList: functionList function
            |
            ;


function: FUNCT { currIdType = IdentType.FunctionDef; } returnType id 
                    '(' { currIdType = IdentType.FunctionParam; } args ')' functionBlock
        ;

functionBlock: '{' { currIdType = IdentType.Local; }  functionContent return '}' ;

functionContent: functionContent command
               | functionContent localDeclaration
               |
               ;

returnType: type { currentType = (Symbol)$1; }
          | VOID { currentType = Tab.Tp_VOID; }
          ;

return: RETURN exp ';' { checkReturnType((Symbol)$2); }
      | RETURN ';'     { checkReturnType(Tab.Tp_VOID); }
      |                { checkReturnType(Tab.Tp_VOID);  }
      ;

args: functionParameterList
    |
    ;

functionParameterList: functionParameter { currentFType.incrParameters(); } ',' functionParameterList
                     | functionParameter { currentFType.incrParameters(); }
                     ;

functionParameter: type { currentType = (Symbol)$1; } id
                 ;

commandList: commandList command
           |
           ;

command: incrType INCR ';'                 { checkType(INCR, (Symbol)$1, null); }
       | incrType DECR ';'                 { checkType(DECR, (Symbol)$1, null); }
       | exp '=' exp ';'                   { checkType('=', (Symbol)$1, (Symbol)$3); }
       | IF '(' exp ')' ifBlock else       {  if ( ((Symbol)$3) != Tab.Tp_BOOL) 
                                                 yyerror("Semantic: 'if' Expression must be of logical type, received type: " + ((Symbol)$3).getTypeString());
                                           }     
       | functionCall ';'      
       ;

else: ELSE ifBlock
    |
    ;

conditionalReturn: RETURN exp ';' { checkReturnType((Symbol)$2);  }
                 | RETURN ';'     { checkReturnType(Tab.Tp_VOID);  }
                 |
                 ;


ifBlock: '{' commandList conditionalReturn '}';

exp: '(' exp ')'     { $$ = $2; }
   | '!' exp         { $$ = checkType('!', (Symbol)$2, null); }
   | incrType INCR   { $$ = checkType(INCR, (Symbol)$1, null); }
   | incrType DECR   { $$ = checkType(DECR, (Symbol)$1, null); }
   | exp '*' exp     { $$ = checkType('*', (Symbol)$1, (Symbol)$3); }
   | exp '+' exp     { $$ = checkType('+', (Symbol)$1, (Symbol)$3); }
   | exp '-' exp     { $$ = checkType('-', (Symbol)$1, (Symbol)$3); }
   | exp '>' exp     { $$ = checkType('>', (Symbol)$1, (Symbol)$3); }
   | exp '<' exp     { $$ = checkType('<', (Symbol)$1, (Symbol)$3); }
   | exp GTEQ exp    { $$ = checkType(GTEQ, (Symbol)$1, (Symbol)$3); }
   | exp LTEQ exp    { $$ = checkType(LTEQ, (Symbol)$1, (Symbol)$3); }
   | exp EQ exp      { $$ = checkType(EQ, (Symbol)$1, (Symbol)$3); }
   | exp NEQ exp     { $$ = checkType(NEQ, (Symbol)$1, (Symbol)$3); }
   | exp AND exp     { $$ = checkType(AND, (Symbol)$1, (Symbol)$3); } 
   | exp OR exp      { $$ = checkType(OR, (Symbol)$1, (Symbol)$3); } 
   | accessField     { $$ = currentSType; }
   | functionCall    { $$ = currentFType; }
   | FALSE           { $$ = Tab.Tp_BOOL; }
   | TRUE            { $$ = Tab.Tp_BOOL; }
   | NUM             { $$ = Tab.Tp_INT; }    
   | D_NUM           { $$ = Tab.Tp_DOUBLE; } 
   | LITERAL         { $$ = Tab.Tp_STRING; }
   | incrType        { $$ = $1; }
   ;


incrType: IDENT           { $$ = searchIdent($1); }
        | IDENT '[' exp ']' { $$ = checkArrayAccess(searchIdent($1), (Symbol)$3); }      
        ;

accessField: accessField '.' IDENT { currentSType = checkStructAccess(currentSType, $3); }
           | IDENT '.' IDENT       { currentSType = checkStructAccess(searchIdent($1), $3); }
           ;

functionCall: IDENT { currentFType = checkIsFunction($1); } '(' fCallArgs ')' { currentFType = checkParamCount(); }
            ;

fCallArgs: fCallParameters
         |
         ;

fCallParameters: exp { checkFunctionCallParameter((Symbol)$1); } ',' fCallParameters
               | exp { checkFunctionCallParameter((Symbol)$1); }
               ;


%%

  private Yylex lexer;

  private Tab ts;

  public static final int ARRAY = 1500;

  private Tab currScope;
  private IdentType currIdType;
  private Symbol currentType;

  private Symbol currentSType;
  private Symbol currentFType;
  private int currParameter = 0;

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
      Symbol node = currScope.find(ident);

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
    Symbol node = ts.find(ident);
    Consumer<String> defaultError = (id) -> { yyerror("Semantic => There is already a definition for > " + id + " < in the current scope."); };
    switch (currIdType) {
      //verifying first the local scope ones for warnings
      case FunctionParam:
      case Local:
        if(node != null) { yywarning("Semantic => variable > " + ident + " < shadows a global definition."); }
        break;
      case Global:
        if(node != null) {
          defaultError.accept(ident);
        } else {
          ts.insert(Symbol.createNormal(ident, currentType, currIdType));
        }
        break;
      case FunctionDef:
      case StructDef:
        if(node != null) {
          defaultError.accept(ident);
        } else {
          createFunctionOrStruct(ident);
        }
        break;
    }
  }

  void createFunctionOrStruct(String ident){
    Symbol functionOrStruct = Symbol.createStructOrFunction(ident, currentType, currIdType);

    currScope = functionOrStruct.getLocalScope();
    currentFType = functionOrStruct;

    ts.insert(functionOrStruct);
  }

  @FunctionalInterface
  public interface DError<T, U> {
      public void apply(T t, U u);
  }

  Symbol checkType(int operator, Symbol s1, Symbol s2) {
    
    DError<String, String> defaultError = (opDescr, op) -> {
       yyerror("Semantic: Incompatible types for " + opDescr + ": " + s1.getTypeString() + op + s2.getTypeString()); 
       };
      
    switch ( operator ) {
      case '=':
        if (checkAssignTypes(s1, s2))
          return s1;
         else
          defaultError.apply("assignment", " = ");
        break;

      case '+' :
        if ( s1 == Tab.Tp_INT && s2 == Tab.Tp_INT)
              return Tab.Tp_INT;
        else if ( (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE)) ||
                                (s2 == Tab.Tp_DOUBLE && (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE)) ) 
             return Tab.Tp_DOUBLE;     
        else
            defaultError.apply("addition", " + ");
        break;

      case '*' :
        if ( s1 == Tab.Tp_INT && s2 == Tab.Tp_INT)
              return Tab.Tp_INT;
        else if ( (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE)) ||
                                (s2 == Tab.Tp_DOUBLE && (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE)) ) 
             return Tab.Tp_DOUBLE;     
        else
            defaultError.apply("multiplication", " * ");
        break;

      case '-' :
        if ( s1 == Tab.Tp_INT && s2 == Tab.Tp_INT)
              return Tab.Tp_INT;
        else if ( (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE)) ||
                                (s2 == Tab.Tp_DOUBLE && (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE)) ) 
             return Tab.Tp_DOUBLE;     
        else
            defaultError.apply("subtraction", " - ");
        break;

      case '>' :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " > ");
        break;

      case '<' :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " < ");
        break;

      case GTEQ :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " >= ");
        break;

      case LTEQ :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " <= ");
        break;

      case EQ :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " == ");
        break;

      case NEQ :
        if ((s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE))
          return Tab.Tp_BOOL;
        else
          defaultError.apply("relational operation", " != ");
        break;

      case AND:
        if (s1 == Tab.Tp_BOOL && s2 == Tab.Tp_BOOL)
          return Tab.Tp_BOOL;
        else
          defaultError.apply("logical operation", " && ");
        break;

      case OR:
        if (s1 == Tab.Tp_BOOL && s2 == Tab.Tp_BOOL)
          return Tab.Tp_BOOL;
        else
          defaultError.apply("logical operation", " || ");
        break;

      case INCR:
        if (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) {
          return s1;
        }
        else
          yyerror("Semantic: Incompatible type for increment operation: " + s1.getTypeString() + " ++");
        break;

      case DECR:
        if (s1 == Tab.Tp_INT || s1 == Tab.Tp_DOUBLE) {
          return s1;
        }
        else
          yyerror("Semantic: Incompatible type for decrement operation: " + s1.getTypeString() + " ++");
        break;

      case '!':
        if (s1 == Tab.Tp_BOOL) {
          return Tab.Tp_BOOL;
        }
        else
          yyerror("Semantic: Incompatible type for negate operation: ! " + s1.getTypeString());
        break;
    }

    return Tab.Tp_ERROR;
  }

  boolean checkAssignTypes(Symbol s1, Symbol s2) {
    if ( (s1 == Tab.Tp_INT && s2 == Tab.Tp_INT) ||
             (s1 == Tab.Tp_DOUBLE && (s2 == Tab.Tp_INT || s2 == Tab.Tp_DOUBLE )) ||
             (s1 == Tab.Tp_STRING && s2 == Tab.Tp_STRING) ||
             (s1 == s2) 
        )
        return true;
    return false;
  }

  void checkReturnType(Symbol s1) {
    Symbol s2 = ts.find(currScope.getScopeName());

    boolean isOk = checkAssignTypes(s2.getType(), s1);
    if(!isOk){
      yyerror("Semantic: Invalid return type for function > " + currScope.getScopeName() + " < Expected: " + s2.getType().getTypeString() + ", Received: " + s1.getTypeString());
    }
  }

  Symbol checkArrayAccess(Symbol s1, Symbol s2) {
    if(s2 != Tab.Tp_INT) {
      yyerror("Semantic: Index must be an Integer.");
      return Tab.Tp_ERROR;
    }
    else if (s1.getType() != Tab.Tp_ARRAY) {
      yyerror("Semantic: Element > " + s1.getIdent() + " < is not an array, it can not be indexed.");
      return Tab.Tp_ERROR;
    }
    return s1.getBaseType();
  }


  Symbol checkStructAccess(Symbol s1, String sFieldName) {
    if(s1.getType() != Tab.Tp_STRUCT){
      yyerror("Semantic: Element > " + s1.getIdent() + " < is not a struct, it can not have fields.");
      return Tab.Tp_ERROR;
    }
    Symbol sField = s1.getLocalScope().find(sFieldName);

    if(sField == null) {
      yyerror("Semantic: Struct field > " + sFieldName + " < does not exist for struct: " + s1.getIdent());
      return Tab.Tp_ERROR;
    }

    return sField.getType();
  }

  Symbol checkIsFunction(String fName) {
    Symbol f1 = ts.find(fName);
    if(f1 != null  && f1.getIdentType() == IdentType.FunctionDef){
      return f1;
    }
    yyerror("Semantic: Function > " + fName + " < does not exist.");
    return Tab.Tp_ERROR;
  }

  void checkFunctionCallParameter(Symbol usedParam) {
    Symbol parameter = currentFType.getLocalScope().get(currParameter);

    currParameter++;       

    if (parameter == Tab.Tp_ERROR || parameter.getIdentType() != IdentType.FunctionParam) {
      yyerror("Semantic: Unexpected parameter > " + usedParam.getIdent() + " <");
      return;
    }
    else if (usedParam == Tab.Tp_ERROR) {
      yyerror("Semantic: Invalid parameter type, Expected: " + parameter.getType().getIdent() + ", Received: " + usedParam.getIdent());
      return;
    }

    parameter = parameter.getType();

    if(!checkAssignTypes(parameter, usedParam)) {
      yyerror("Semantic: Invalid parameter type, Expected: " + parameter.getIdent() + ", Received: " + usedParam.getIdent());
      return;
    }
  }

  Symbol checkParamCount() {
    if (currentFType.getParameterCount() == currParameter) {
      currParameter = 0;
      return currentFType.getType();
    }
    yyerror("Semantic: Incompatible number of parameters for function > " + currentFType.getIdent() + " < Expected: " + currentFType.getParameterCount() + ", Received " + currParameter);
    currParameter = 0;
    return Tab.Tp_ERROR;
  }