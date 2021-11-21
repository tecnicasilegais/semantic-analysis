
public class Symbol {
   // basic contents
   private String ident;
   private IdentType identType;
   private Symbol type;

   // for Arrays
   private Symbol baseType;
   private int nElements;

   // for struct and function
   private Tab locals;

   public static Symbol createNormal(String ident, Symbol type, IdentType identType) {
      return new Symbol(ident, type, identType, 0, null, null);
   }

   public static Symbol createArray(String ident, Symbol type, IdentType identType, int elems, Symbol baseType) {
      return new Symbol(ident, type, identType, elems, baseType, null);
   }

   public static Symbol createStructOrFunction(String ident, Symbol type, IdentType identType) {
      return new Symbol(ident, type, identType, 0, null, new Tab(ident));
   }

   private Symbol(String ident, Symbol type, IdentType identType, int elems, Symbol baseType, Tab locals) {
      this.ident = ident;
      this.type = type;
      this.identType = identType;

      this.nElements = elems;
      this.baseType = baseType;

      this.locals = locals;
   }

   public String getIdent() {
      return this.ident;
   }

   public Symbol getType() {
      return this.type;
   }

   public IdentType getIdentType() {
      return this.identType;
   }

   public Symbol getBaseType() {
      return this.baseType;
   }

   public Tab getLocalScope() {
      return this.locals;
   }

   public boolean hasLocalScope() {
      return this.locals != null;
   }

   public int getElements() {
      return this.nElements;
   }

   public void showLocalScope() {
      System.out.println("\n\n Local Scope for: " + this.ident + ", Scope type: " + this.type.ident);
      this.locals.showLocalScope();
   }

   public String toString() {
      StringBuilder aux = new StringBuilder("");

      aux.append("Id: ");
      aux.append(String.format("%-15s", this.ident));

      aux.append(String.format("%-25s", "IdentType: " + this.identType));

      aux.append(String.format("%-15s", "Type: " + type2String(this.type)));

      return aux.toString();
   }

   public String getTypeString() {
      return type2String(this);
   }

   public String type2String(Symbol type) {
      if (type == null)
         return "-";
      else if (type == Tab.Tp_INT)
         return "int";
      else if (type == Tab.Tp_BOOL)
         return "boolean";
      else if (type == Tab.Tp_DOUBLE)
         return "double";
      else if (type == Tab.Tp_STRING)
         return "string";
      else if (type == Tab.Tp_STRUCT)
         return "struct";
      else if (type == Tab.Tp_VOID)
         return "void";
      else if (type.type == Tab.Tp_STRUCT)
         return type.ident;
      else if (type.getType() != null)
         return String.format("array(%d,%s)", type.nElements, type2String(type.baseType));
      else if (type == Tab.Tp_ERROR)
         return "_erro_";
      else
         return "erro/tp";
   }

}
