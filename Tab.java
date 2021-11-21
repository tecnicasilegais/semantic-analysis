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

    public Symbol find(String id, IdentType currIdentType){
      for (Symbol node : symbols) {
          if (node.getIdent().equals(id)) {
            if(IdentType.relates(node.getIdentType(), currIdentType)){
              return node;
            }
          }
      }
      return null;
    }

    public String getScopeName() {
      return this.scopeName;
    }

    public  ArrayList<Symbol> getLista() {return symbols;}
}



