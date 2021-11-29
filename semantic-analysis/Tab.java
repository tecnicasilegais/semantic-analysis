/**
 * Trabalho final da disciplina Construção de Compiladores 2021/2
 * 
 * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5 
 * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7 
 * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
 * Sarah Lacerda - sarah.silva@edu.pucrs.br - 17104191-6
 */

import java.util.ArrayList;


public class Tab
{
    private ArrayList<Symbol> symbols;
    private String scopeName;

    public static final Symbol Tp_ERROR   = Symbol.createNormal("_error_", null,  IdentType.BaseType);
    public static final Symbol Tp_INT    = Symbol.createNormal("int", null, IdentType.BaseType);
    public static final Symbol Tp_DOUBLE = Symbol.createNormal("double", null,  IdentType.BaseType);
    public static final Symbol Tp_BOOL   = Symbol.createNormal("bool", null,  IdentType.BaseType);
    public static final Symbol Tp_STRING = Symbol.createNormal("string", null, IdentType.BaseType);
    public static final Symbol Tp_STRUCT = Symbol.createNormal("struct", null, IdentType.BaseType);
    public static final Symbol Tp_ARRAY  = Symbol.createNormal("array", null,  IdentType.BaseType);
    public static final Symbol Tp_VOID   = Symbol.createNormal("void", null,  IdentType.BaseType);
    
    public Tab() {
      this.scopeName = "GLOBALS";
        symbols = new ArrayList<Symbol>();
    }

    public Tab(String scopeName){
      this.scopeName = scopeName;
      symbols = new ArrayList<Symbol>();
    }
    
     public void insert(Symbol node) {
      symbols.add(node);
    }    

    public void showBaseTypes() {
      System.out.println(Tp_ERROR);
      System.out.println(Tp_INT);
      System.out.println(Tp_DOUBLE);
      System.out.println(Tp_BOOL);
      System.out.println(Tp_STRING);
      System.out.println(Tp_STRUCT);
      System.out.println(Tp_ARRAY);
      System.out.println(Tp_VOID);
    }
    
    public void show() {
      System.out.println("\n\nSymbol Table:\n");
      showBaseTypes();
      for (Symbol node : symbols) {
          System.out.println(node);
      }
    }

    public void showAllLocalScopes(){
      for(Symbol node : symbols) {
        if (node.hasLocalScope()){
          node.showLocalScope();
        }
      }
    }

    public void showLocalScope() {
      for (Symbol node : symbols) {
          System.out.println(node);
      }
    }
      
    public Symbol find(String id) {
      for (Symbol node : symbols) {
          if (node.getIdent().equals(id)) {
	          return node;
          }
      }
      return null;
    }

    public Symbol get(int i) {
      try {
        return symbols.get(i);
      }
      catch(Exception ex){
        return Tp_ERROR;
      }
    }

    public String getScopeName() {
      return this.scopeName;
    }

    public  ArrayList<Symbol> getLista() {return symbols;}
}



