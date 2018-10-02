

public class GeradorDeCodigo extends LABaseListener {
    private String saida; // Contem a saída em C


    private boolean switch_case = false; // comunicacao para avisar que está ocorrendo switch case

    private boolean switch_default = false; // comunicacao para avisar que no swich case tem default

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
            expr = expr.replace("=","==");
        }
        if(expr.contains("<>")){
            expr = expr.replace("<>", "!=");
        }
        return expr;
    }

    // Converte os operadores logicos para C
    public String converteOpLogico(String expr){
        if(expr.contains("e")){
            expr = expr.replace("e", " && ");
        }
        if(expr.contains("ou")){
            expr =  expr.replace("ou", " || ");
        }
        if(expr.contains("nao")){
            expr = expr.replace("nao", "!");
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

        if (context.tipo().registro() == null) {
            pilhaDeTabelas.topo().adicionarSimbolo(nome, tipo); // adiciono na tabela de simbolos

            concatena(getTipoC(tipo) + " " + nome);
            if (tipo.equals("literal")) {
                concatena("[80]"); // NUMERO ARBITRARIO TODO: VER SE EM TODOS OS TESTES EH ASSIM
            }
            //concatenaequebralinha(";"); // TODO: TRATAR O CASO DE lista de variaveis
        }
        else {
            pilhaDeTabelas.topo().adicionarSimbolo(nome, "registro");
        }

        for(LAParser.Identificador_varContext mais_var : context.lista_mais_var){
            pilhaDeTabelas.topo().adicionarSimbolo(mais_var.getText(),context.tipo().getText());
        }
    }

    @Override
    public void exitVariavel(LAParser.VariavelContext context) {
        for (LAParser.Identificador_varContext mais_var : context.lista_mais_var) {
            concatena("," + mais_var.getText());
        }
        concatenaequebralinha(";");
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
            String complemento = "";
            String tipo_complemento = "";
            concatena("printf(\"");
            String nome = context.cmdEscreva().escrevaExpr.getText();

            boolean mais = !context.cmdEscreva().complementoExpr().isEmpty();

            if (mais){
                complemento = context.cmdEscreva().complementoExpr(0).expressao().getText();
                System.out.println("complemento: " + complemento);
                tipo_complemento = pilhaDeTabelas.topo().getTipo(complemento);
                System.out.println("tipo_complemento: " + tipo_complemento);
            }



            //TODO: Tratar varios casos, como vetor, funcao e registro

            String tipo = pilhaDeTabelas.topo().getTipo(nome);

            //TODO: ver se é funcao

            // Converte para o especificador corretor em C
            if ((tipo.equals("inteiro")) || (tipo.equals("real")) ||(tipo.equals("literal"))){
                concatena(getTagC(tipo) + "\"," + nome);
                concatenaequebralinha(");");
            }
            else{
                if (nome.contains("\"")){
                    nome = nome.replace("\"", ""); // tira as aspas
                    concatena(nome);
                    if(complemento.equals("")){
                        concatena("\");");
                    }
                    else {
                        concatenaequebralinha(getTagC(tipo_complemento) + "\"," + complemento + ");");
                    }
                }
//                else if(nome.contains("+") || nome.contains("-")){
//                   //TODO:tratar aqui o caso de expressao - algoritmo 5
//                }
            }


            if(complemento.contains("\\n")){
                concatenaequebralinha("printf(\"\\n\");");
            }

            //TODO: fazer o ELSE = default dele
            //TODO: muuuitas verificacoes nao feitas
        }
        else if (token.equals("se")){
            String expressao = converteOpLogico(context.cmdSe().exprSe.getText());
            expressao = converteOpRelacional(expressao);
            concatenaequebralinha("if (" + expressao + "){");
        }
        else if (token.equals("caso")){
            concatenaequebralinha("switch(" + context.cmdCaso().exp_aritmetica().getText() + "){");

            if (context.cmdCaso().senao_opcional().cmd() != null){ // importante para diferenciar do else do comando if
                this.switch_default = true;
            }
        }
        else if(token.equals("para")){
            concatena("for(" + context.cmdPara().IDENT().getText() + "=");
            concatena(context.cmdPara().exp_aritmetica(0).getText() + ";");
            concatena(context.cmdPara().IDENT().getText() + "<=" + context.cmdPara().exp_aritmetica(1).getText() + ";");
            concatenaequebralinha(context.cmdPara().IDENT().getText() + "++){");
        }
        else if(token.equals("enquanto")){
            String expressao = converteOpLogico(context.cmdEnquanto().expressao().getText());
            expressao = converteOpRelacional(expressao);
            concatenaequebralinha("while (" + expressao + ") {");
        }
        else if(token.equals("faca")){
            concatenaequebralinha("do {");
        }
    }

    @Override
    public void exitCmd(LAParser.CmdContext context){
        String token = context.getStart().getText();
        if (token.equals("se")){
            System.out.println("terminei aqui o se");
            concatenaequebralinha("}");
        }
        //TODO: colocar aqui o default
        else if (token.equals("caso")){
            if(context.cmdCaso().senao_opcional() == null){
                concatenaequebralinha("}");
            }
        }
        else if(token.equals("para")){
            concatenaequebralinha("}");
        }
        else if(token.equals("enquanto")){
            concatenaequebralinha("}");
        }
        else if(token.equals("faca")){
            String expressao = converteOpLogico(context.cmdFaca().expressao().getText() );
            expressao = converteOpRelacional(expressao);
            concatenaequebralinha("} while (" + expressao + ");");
        }
        if (switch_case){
            concatenaequebralinha("break;");
            switch_case = false;
        }
    }

    @Override
    public void enterItem_selecao(LAParser.Item_selecaoContext context){
        String selecao = context.constantes().getText();
        if (selecao.contains("..")) {
            selecao = selecao.replace(".."," ");
            String[] selecoes = selecao.split(" ");
            int inicio = Integer.parseInt(selecoes[0].trim());
            int fim = Integer.parseInt(selecoes[1].trim());
            for (int i = inicio; i <= fim; i++) {
                concatenaequebralinha("case " + String.valueOf(i) + ":");
            }
        }
        else{
            concatenaequebralinha("case "+ selecao + ":");
        }
        switch_case = true;
    }


    @Override
    public void enterSenao_opcional(LAParser.Senao_opcionalContext context){
        if (switch_default) {
            System.out.println("entrei aqui no switch case");
            concatenaequebralinha("default:");
            switch_case = false;
        }
        else{
            System.out.println("nao e sw");
            concatenaequebralinha("}\nelse {");
        }
    }

    @Override
    public void exitSenao_opcional(LAParser.Senao_opcionalContext context){
        if (switch_default) {
            System.out.println("final aqui no switch case");
            concatenaequebralinha("}");
        }
    }


    @Override
    public void enterDeclaracao_local_constante(LAParser.Declaracao_local_constanteContext context) {
        concatenaequebralinha("#define " + context.nome.getText() + " " + context.valor_constante().getText());
    }

    @Override
    public void enterCmdAtribuicao(LAParser.CmdAtribuicaoContext context) {
        concatenaequebralinha(context.identificador().getText() + " = " + context.expressao().getText() + ";");
    }

    public String getString(){
        return this.saida;
    }
}
