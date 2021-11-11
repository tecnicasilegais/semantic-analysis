    
    
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
%token LITERAL, AND, VOID, IF
%token STRUCT, FUNCT
%token STRING

%right '='
%nonassoc '>'
%left '+'
%left AND
%left '['  

%type <sval> IDENT
%type <ival> NUM
%type <dval> D_NUM
%type <obj> type
%type <obj> exp

%%

prog: { currClass = ClasseID.VarGlobal; } dList listaFuncoes ;

dList: decl dList | ;

decl: type { currentType = (TS_entry)$1; } TArray declAux ';'
    | declStruct
      ;

declAux: id '=' exp { validaTipo(ATRIB, currentType, (TS_entry)$3);  } 
        | Lid
        ;

//todo fazer conteudo TS entry ident nomestruct
declStruct: STRUCT id '{' listaCampos '}' ';'
          ;

listaCampos: listaCampos campoStruct
            | campoStruct
            ;

campoStruct: type Lid ';'
            ;

Lid: Lid  ',' id 
    | id  
    ;

id: IDENT   { TS_entry nodo = ts.pesquisa($1);
                            if (nodo != null) 
                              yyerror("(sem) variavel >" + $1 + "< jah declarada");
                          else ts.insert(new TS_entry($1, currentType, currClass)); 
                       }
    
    ;

TArray: '[' NUM ']' TArray { currentType = new TS_entry("?",Tp_ARRAY, 
                                                   currClass, $2, currentType); }
       |
       ;

             //
              // faria mais sentido reconhecer todos os tipos como ident! 
              // 
type: INT    { $$ = Tp_INT; }
     | DOUBLE  { $$ = Tp_DOUBLE; }
     | BOOL   { $$ = Tp_BOOL; }
     | STRING { $$ = Tp_STRING; }   
     ;

listaFuncoes: listaFuncoes funcao
            |
            ;
//todo fazer tipoRetorno listaParametros 
funcao: FUNCT tipoRetorno IDENT '(' listaParametros ')' blocoFuncao
      ;

blocoFuncao: '{' conteudoFuncao '}' ;

conteudoFuncao: conteudoFuncao decl
              | conteudoFuncao cmd
              |
              ;

tipoRetorno: type
           | VOID
           | IDENT { TS_entry nodo = ts.pesquisa($1);
                      if(nodo == null || !ClasseID.isValidReturnType(nodo.getClasse())){
                        yyerror("semantic: invalid return type for function: " + $1);
                      }
                    }
           ;

listaParametros: ;

bloco: '{' listacmd '}';

listacmd: listacmd cmd
        |
         ;

cmd:  exp ';' 
      | IF '(' exp ')' bloco   {  if ( ((TS_entry)$3) != Tp_BOOL) 
                                     yyerror("(sem) expressão (if) deve ser lógica "+((TS_entry)$3).getTipoStr());
                             }     
       ;


exp: exp '+' exp { $$ = validaTipo('+', (TS_entry)$1, (TS_entry)$3); }
    | exp '>' exp { $$ = validaTipo('>', (TS_entry)$1, (TS_entry)$3); }
    | exp AND exp { $$ = validaTipo(AND, (TS_entry)$1, (TS_entry)$3); } 
    | NUM         { $$ = Tp_INT; }    
    | D_NUM       { $$ = Tp_DOUBLE; } 
    | LITERAL     { $$ = Tp_STRING; }        
    | '(' exp ')' { $$ = $2; }
    | IDENT       { TS_entry nodo = ts.pesquisa($1);
                    if (nodo == null) {
                       yyerror("(sem) var <" + $1 + "> nao declarada"); 
                       $$ = Tp_ERRO;    
                       }           
                    else
                        $$ = nodo.getTipo();
                  }         
     | exp '=' exp  {  $$ = validaTipo(ATRIB, (TS_entry)$1, (TS_entry)$3);  } 
     | exp '[' exp ']'  {  if ((TS_entry)$3 != Tp_INT) 
                              yyerror("(sem) indexador deve ser do tipo INT ");
                           else 
                               if (((TS_entry)$1).getTipo() != Tp_ARRAY)
                                  yyerror("(sem) elemento não indexado ");
                               else 
                                  $$ = ((TS_entry)$1).getTipoBase();
                         } 
    // | "chamada funct f1(3, b+c, v[4]);"
    // | "verificar acesso a elem struct, al1.escalaMatricula.dia"
    ;

%%

  private Yylex lexer;

  private TabSimb ts;

  public static TS_entry Tp_INT =  new TS_entry("int", null, ClasseID.TipoBase);
  public static TS_entry Tp_DOUBLE = new TS_entry("double", null,  ClasseID.TipoBase);
  public static TS_entry Tp_BOOL = new TS_entry("bool", null,  ClasseID.TipoBase);
  public static TS_entry Tp_STRING = new TS_entry("string", null, ClasseID.TipoBase);
  public static TS_entry Tp_STRUCT = new TS_entry("struct", null, ClasseID.TipoBase);
  public static TS_entry Tp_ARRAY = new TS_entry("array", null,  ClasseID.TipoBase);
  public static TS_entry Tp_VOID = new TS_entry("void", null,  ClasseID.TipoBase);

  public static TS_entry Tp_ERRO = new TS_entry("_erro_", null,  ClasseID.TipoBase);

  public static final int ARRAY = 1500;
  public static final int ATRIB = 1600;

  private String currEscopo;
  private ClasseID currClass;

  private TS_entry currentType;

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
    //System.err.println("Erro (linha: "+ lexer.getLine() + ")\tMensagem: "+error);
    System.err.printf("Error (line: %2d) \tMessage: %s\n", lexer.getLine(), error);
  }


  public Parser(Reader r) {
    lexer = new Yylex(r, this);

    ts = new TabSimb();

    //
    // não me parece que necessitem estar na TS
    // já que criei todas como public static...
    //
    ts.insert(Tp_ERRO);
    ts.insert(Tp_INT);
    ts.insert(Tp_DOUBLE);
    ts.insert(Tp_BOOL);
    ts.insert(Tp_STRING);
    ts.insert(Tp_ARRAY);
    ts.insert(Tp_STRUCT);
    

  }  

  public void setDebug(boolean debug) {
    yydebug = debug;
  }

  public void listarTS() { ts.listar();}

  public static void main(String args[]) throws IOException {
    System.out.println("\n\nVerificador semantico simples\n");

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

    yyparser.listarTS();

    System.out.print("\n\nFeito!\n");
    
  }


   TS_entry validaTipo(int operador, TS_entry A, TS_entry B) {
       
         switch ( operador ) {
              case ATRIB:
                    if ( (A == Tp_INT && B == Tp_INT)                         ||
                         (A == Tp_DOUBLE && (B == Tp_INT || B == Tp_DOUBLE )) ||
                         (A == Tp_STRING && B == Tp_STRING)                   ||
                         (A == B) 
                        )
                         return A;
                     else
                         yyerror("(sem) tipos incomp. para atribuicao: "+ A.getTipoStr() + " = "+B.getTipoStr());
                    break;

              case '+' :
                    if ( A == Tp_INT && B == Tp_INT)
                          return Tp_INT;
                    else if ( (A == Tp_DOUBLE && (B == Tp_INT || B == Tp_DOUBLE)) ||
                                            (B == Tp_DOUBLE && (A == Tp_INT || A == Tp_DOUBLE)) ) 
                         return Tp_DOUBLE;     
                    else
                        yyerror("(sem) tipos incomp. para soma: "+ A.getTipoStr() + " + "+B.getTipoStr());
                    break;

             case '>' :
                     if ((A == Tp_INT || A == Tp_DOUBLE) && (B == Tp_INT || B == Tp_DOUBLE))
                         return Tp_BOOL;
                      else
                        yyerror("(sem) tipos incomp. para op relacional: "+ A.getTipoStr() + " > "+B.getTipoStr());
                      break;

             case AND:
                     if (A == Tp_BOOL && B == Tp_BOOL)
                         return Tp_BOOL;
                      else
                        yyerror("(sem) tipos incomp. para op lógica: "+ A.getTipoStr() + " && "+B.getTipoStr());
                 break;
            }

            return Tp_ERRO;
           
     }

