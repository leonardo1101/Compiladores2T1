import java.util.ArrayList;

public class Visitor extends LABaseVisitor {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();


    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        super.visitPrograma(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

//    @Override
//    public Object visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
//
//        for(LAParser.Decl_local_globalContext d: ctx.decl_local_global()) {
//            visitDecl_local_global(d);
//        }
//        return null;
//    }

    @Override
    public Object visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        int[] vetor = new int[30];

        for(int indice = 0;indice < vetor.length; indice ++) {
            int x = vetor[indice];
        }

        for(String x : vetor) {

        }


        if(ctx.declaracao_global() != null){
            visitDeclaracao_global(ctx.declaracao_global());
        }
        if (ctx.declaracao_local() != null){
            visitDeclaracao_local(ctx.declaracao_local());
        }

        return null;
    }

    @Override
    public Object visitDeclaracao_local(LAParser.Declaracao_localContext ctx){
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();

        if(ctx.variavel() != null){ //variavel
            visitVariavel(ctx.variavel());
        } else if (ctx.tipo_basico() != null){ //tipo basico
            String tipo = new String();

            if(!escopoAtual.existeSimbolo(ctx.nome1.getText())) {
                escopoAtual.adicionarSimbolo(ctx.nome1.getText(), tipo);
            } else{
                throw new RuntimeException("Erro semantico: " + ctx.nome1.getText() + "declarada duas vezes num mesmo escopo");
            }

        } else { //tipo

            String tipo = new String();

            if(ctx.tipo().tipo_estendido() != null){
                tipo = (String) visitTipo_estendido(ctx.tipo().tipo_estendido());
            } else if(ctx.tipo().registro() != null){
                tipo = (String) visitRegistro(ctx.tipo().registro());
            }

            if(!escopoAtual.existeSimbolo(ctx.nome1.getText())) {
                escopoAtual.adicionarSimbolo(ctx.nome1.getText(), tipo);
            } else{
                throw new RuntimeException("Erro semantico: " + ctx.nome1.getText() + "declarada duas vezes num mesmo escopo");
            }
        }

        return null;
    }

    @Override
    public Object visitDeclaracao_global_funcao(LAParser.Declaracao_global_funcaoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        super.visitDeclaracao_global_funcao(ctx);

        pilhaDeTabelas.desempilhar();
    }

/*@Override
    public Object visitDeclaracao_global(LAParser.Declaracao_globalContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        if(escopoAtual.existeSimbolo(ctx.nome.getText())) {
           throw new RuntimeException("Erro semantico: " + ctx.nome.getText() + "declarada duas vezes num mesmo escopo");
        } else {
            //escopoAtual.adicionarSimbolo(ctx.nome.getText(), );
        }
        pilhaDeTabelas.empilhar(escopoAtual);

        pilhaDeTabelas.desempilhar();
        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Object visitCorpo(LAParser.CorpoContext ctx) {
        return super.visitCorpo(ctx);
    }*/



}
