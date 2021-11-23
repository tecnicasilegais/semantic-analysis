/**
 * Trabalho final da disciplina Construção de Compiladores 2021/2
 * 
 * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5 
 * Julia Alberti - julia.maia@edu.pucrs.br - 18106160-7 
 * Marcelo Heredia - marcelo.heredia@edu.pucrs.br - 16204047-1
 */

public enum IdentType {
  BaseType, Global, Local, FunctionDef, FunctionParam, StructDef, StructField;

  public static boolean isValidReturnType(IdentType id) {
    if (id == IdentType.BaseType || id == IdentType.StructDef)
      return true;

    return false;
  }

  public static boolean isVariableType(IdentType id) {
    if (id == IdentType.Global || id == IdentType.Local || id == IdentType.FunctionParam) {
      return true;
    }
    return false;
  }

  public static boolean isLocalType(IdentType id){
    return id == IdentType.Local || id == IdentType.FunctionParam || id == IdentType.StructField;
  }

}
