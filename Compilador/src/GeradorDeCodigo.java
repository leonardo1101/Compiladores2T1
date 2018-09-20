

public class GeradorDeCodigo extends LABaseListener {
    private String saida; // Contem a saída em C


    private PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas(); // Utilizada para verificacao de tipo em operacoes de leitura e escrita
    //private TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos("funcoes"); // Utilizada para verificacao de tipo de retorno de funcoes


    // Construtor, inicializa a saida com vazio
    public GeradorDeCodigo(){
        saida = "";
    }

    public void concatena(String sentenca){
        saida += sentenca;
    }

    public void concatenaequebralinha(String sentenca){
        saida += sentenca + "\n";
    }


    // E O TIPO LOGICO?
    // Retorna a TAG correspondente ao tipo em C
    public String getTagC(String tipo){
        switch (tipo){
            case "inteiro":
                return "%d";
            case "real":
                return "%f";
            case "literal":
                return "%s";
            default:
                return "null";
        }
    }

    // TODO: E O LOGICO?
    // Retorna o tipo correspondente em C
    public String getTipoC(String tipo){
        switch (tipo){
            case "inteiro":
                return "int";
            case "real":
                return "float";
            case "literal":
                return "char";
            default:
                return "null";
        }
    }

    // Converte os operadores relacionais para C
    public String converteOpRelacional(String expr){
        if(expr.contains("=") && !((expr.contains(">=")) || (expr.contains("<=")))){
            expr.replace("=","==");
        }
        if(expr.contains("<>")){
            expr.replace("<>", "!=");
        }
        return expr;
    }

    // Converte os operadores logicos para C
    public String converteOpLogico(String expr){
        if(expr.contains(" e ")){
            expr.replace(" e ", " && ");
        }
        if(expr.contains(" ou ")){
            expr.replace(" ou ", " || ");
        }
        if(expr.contains(" nao ")){
            expr.replace(" nao ", " ! ");
        }
        return expr;
    }

    // Coloca cabecalho do codigo

    @Override
    public void enterPrograma(LAParser.ProgramaContext context){
        concatenaequebralinha("#include <stdio.h>\n#include <stdlib.h>\n");

        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("main"));
    }

    @Override
    public void enterCorpo(LAParser.CorpoContext context){
        concatenaequebralinha("int main(){");

    }

    // Finaliza o programa
    @Override
    public void exitCorpo(LAParser.CorpoContext context){
        concatenaequebralinha("return 0;");
        concatena("}");
    }


    // Converte declaracoes de variaveis para C
    @Override
    public void enterVariavel(LAParser.VariavelContext context){
        String nome = context.nome5.getText();
        String tipo = context.tipo().getText();

        pilhaDeTabelas.topo().adicionarSimbolo(nome,tipo); // adiciono na tabela de simbolos

        concatena(getTipoC(tipo) + " " + nome);
        if (tipo.equals("literal")){
            concatena("[80]"); // NUMERO ARBITRARIO TODO: VER SE EM TODOS OS TESTES EH ASSIM
        }
        concatenaequebralinha(";"); // TODO: TRATAR O CASO DE lista de variaveis
    }


    // Converte comandos como leia e escreva
    @Override
    public void enterCmd(LAParser.CmdContext context){

        // Pega o primeiro token para ver qual comando eh
        String token = context.getStart().getText();

        // Se for leia
        if (token.equals("leia")){
            // Pega qual variavel vai ser lida e seu tipo
            String nome = context.cmdLeia().leiaIDENT.getText();
            String tipo = pilhaDeTabelas.topo().getTipo(nome);

            if (tipo.equals("literal")){
                concatenaequebralinha("gets(" + nome + ");");
            }
            else {
                concatena("scanf(");

                // pega a TAG correta
                concatena("\"" + getTagC(tipo) + "\"");

                // Coloca o operador & e a variavel a ser lida
                concatenaequebralinha(",&" + nome + ");");
            }
        }
        else if (token.equals("escreva")){
            concatena("printf(\"");
            String nome = context.cmdEscreva().escrevaExpr.getText();
            //TODO: Tratar varios casos, como vetor, funcao e registro

            String tipo = pilhaDeTabelas.topo().getTipo(nome);

            //TODO: ver se é funcao

            // Converte para o especificador corretor em C
            if ((tipo.equals("inteiro")) || (tipo.equals("real")) ||(tipo.equals("literal"))){
                concatena(getTagC(tipo) + "\"," + nome);
                concatenaequebralinha(");");
            }
            //TODO: fazer o ELSE = default dele
            //TODO: muuuitas verificacoes nao feitas
        }
    }

    public String getString(){
        return this.saida;
    }
}
