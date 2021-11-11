/**
 * Trabalho final da disciplina Construção de Compiladores 2021/2
 * 
 * Eduardo Andrade - eduardo.a@edu.pucrs.br - 17111012-5 Julia Alberti -
 * julia.maia@edu.pucrs.br - 18106160-7 Marcelo Heredia -
 * marcelo.heredia@edu.pucrs.br - 16204047-1
 */

public enum ClasseID {
  TipoBase, VarGlobal, NomeFuncao, NomeParam, VarLocal, NomeStruct, CampoStruct;
  
  public static boolean isValidReturnType(ClasseID id) {
    if(id == ClasseID.TipoBase ||
       id == ClasseID.NomeStruct)
      return true;
      
    return false;
  }
}
