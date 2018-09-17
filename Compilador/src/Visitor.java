import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.List;

public class Visitor extends LABaseVisitor {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    CommonTokenStream cts;

    public void setTokenStream(CommonTokenStream c){
        cts = c;
    }


    @Override
    public Object visitPrograma(LAParser.ProgramaContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));

        super.visitPrograma(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }


    @Override
    public Object visitDeclaracao_local_constante(LAParser.Declaracao_local_constanteContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        String tipo = (String) visitTipo_basico(ctx.tipo_basico());

        if(!escopoAtual.existeSimbolo(ctx.nome.getText())) {
                escopoAtual.adicionarSimbolo(ctx.nome.getText(), tipo);
        } else{
               throw new RuntimeException("Erro semantico: " + ctx.nome.getText() + "declarada duas vezes num mesmo escopo");
        }

        super.visitDeclaracao_local_constante(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }

    @Override
    public Object visitDeclaracao_local_tipo(LAParser.Declaracao_local_tipoContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        String tipo = (String) visitTipo(ctx.tipo());

        if(!escopoAtual.existeSimbolo(ctx.nome1.getText())) {
               escopoAtual.adicionarSimbolo(ctx.nome1.getText(), tipo);
        } else{
            throw new RuntimeException("Erro semantico: " + ctx.nome1.getText() + "declarada duas vezes num mesmo escopo");
        }

        super.visitDeclaracao_local_tipo(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }

    @Override
    public Object visitDeclaracao_global_procedimento(LAParser.Declaracao_global_procedimentoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        super.visitDeclaracao_global_procedimento(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitDeclaracao_global_funcao(LAParser.Declaracao_global_funcaoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        super.visitDeclaracao_global_funcao(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitIdentificador(LAParser.IdentificadorContext ctx) {

        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        String tipo = ""; //ctx.nome2.getType()

        if(!escopoAtual.existeSimbolo(ctx.nome2.getText())) {
            escopoAtual.adicionarSimbolo(ctx.nome2.getText(), tipo);
        } else{
            throw new RuntimeException("Erro semantico: " + ctx.nome2.getText() + "declarada duas vezes num mesmo escopo");
        }

        super.visitIdentificador(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }

    @Override
    public Double visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
        double valor = (double) visitTermo(ctx.termo1);
        for(int i=0; i<ctx.outrosTermos.size(); i++){
            LAParser.Op1Context op1 = ctx.op1(i);
            LAParser.TermoContext ot = ctx.outrosTermos.get(i);
            String strOp1 = op1.getText();
            if(strOp1.equals("+")){
                valor +=  (double) visitTermo(ot);
            } else {
                valor -=  (double) visitTermo(ot);
            }
        }

        return valor;
    }

    @Override
    public Double visitTermo(LAParser.TermoContext ctx) {
        double valor = (double) visitFator(ctx.fator1);
        for(int i=0; i<ctx.outrosFatores.size(); i++){
            LAParser.Op2Context op2 = ctx.op2(i);
            LAParser.FatorContext of = ctx.outrosFatores.get(i);
            String strOp2 = op2.getText();
            if(strOp2.equals("*")){
                valor *=  (double) visitFator(of);
            } else {
                valor /=  (double) visitFator(of);
            }
        }

        return valor;
    }


    @Override
    public Double visitFator(LAParser.FatorContext ctx) {
        double valor = (double) visitParcela(ctx.parcela1);
        for(int i=0; i<ctx.outrasParcelas.size(); i++) {
            LAParser.Op3Context op3 = ctx.op3(0);
            LAParser.ParcelaContext op = ctx.outrasParcelas.get(i);
            String strOp3 = op3.getText();
            if (strOp3.equals("%")) {
                valor %= (double) visitParcela(op);
            }
        }

        return valor;
    }


    @Override
    public Object visitParcela_unario_ident(LAParser.Parcela_unario_identContext ctx) {

        List<TabelaDeSimbolos> todosEscopos = pilhaDeTabelas.getTodasTabelas();

        for(TabelaDeSimbolos ts: todosEscopos){
            boolean etds = ts.existeSimbolo(ctx.IDENT().getText());
            if(etds){
                return ctx.IDENT().getText();
            }else {
                throw new RuntimeException("Erro semantico: " + ctx.IDENT().getText() + "Nao foi declara anteriormente");
            }
        }

        return null;
    }

    @Override
    public Object visitParcela_unario_int(LAParser.Parcela_unario_intContext ctx) {
        List<TabelaDeSimbolos> todosEscopos = pilhaDeTabelas.getTodasTabelas();

        for(TabelaDeSimbolos ts: todosEscopos){
            boolean etds = ts.existeSimbolo(ctx.NUM_INT().getText());
            if(etds){
                return ctx.NUM_INT().getText();
            }else {
                throw new RuntimeException("Erro semantico: " + ctx.NUM_INT().getText() + "Nao foi declara anteriormente");
            }
        }

        return null;
    }

    @Override
    public Object visitParcela_unario_real(LAParser.Parcela_unario_realContext ctx) {
        List<TabelaDeSimbolos> todosEscopos = pilhaDeTabelas.getTodasTabelas();

        for(TabelaDeSimbolos ts: todosEscopos){
            boolean etds = ts.existeSimbolo(ctx.NUM_REAL().getText());
            if(etds){
                return ctx.NUM_REAL().getText();
            }else {
                throw new RuntimeException("Erro semantico: " + ctx.NUM_REAL().getText() + "Nao foi declara anteriormente");
            }
        }

        return null;

    }

    @Override
    public Object visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {

        if(ctx.CADEIA() != null){
            return ctx.CADEIA().getText();
        }else {
            return super.visitIdentificador(ctx.identificador());
        }
    }

//NAO SEI COMO FAZER POR SER OPCIONAL (?)

//    @Override
//    public Object visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
//        double valor = (double) visitTermo(ctx.termo1);
//        for(int i=0; i<ctx.outrosTermos.size(); i++){
//            LAParser.Op1Context op1 = ctx.op1(i);
//            LAParser.TermoContext ot = ctx.outrosTermos.get(i);
//            String strOp1 = op1.getText();
//            if(strOp1.equals("+")){
//                valor +=  (double) visitTermo(ot);
//            } else {
//                valor -=  (double) visitTermo(ot);
//            }
//        }
//
//        return valor;
//
//
//        return super.visitExp_relacional(ctx);
//    }



    @Override
    public Boolean visitExpressao(LAParser.ExpressaoContext ctx) {
        boolean valor = (boolean) visitTermo_logico(ctx.termo_logico1);
        for(int i=0; i<ctx.outrosTermos_Logicos.size(); i++){
            LAParser.Op_logico_1Context opl1 = ctx.op_logico_1(i);
            LAParser.Termo_logicoContext otl = ctx.outrosTermos_Logicos.get(i);
            String strOp1 = opl1.getText();
            if(strOp1.equals("ou")){
                valor |=  (boolean) visitTermo_logico(otl);
            }
        }

        return valor;
    }



//NECESSARIO VERIFICAR SE O TIPO EXISTE?

    @Override
    public Object visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {

        if(ctx.IDENT() != null){
            return ctx.IDENT().getText();
        }else {
            return super.visitTipo_basico(ctx.tipo_basico());
        }

    }

    @Override
    public Object visitValor_constante_cadeia(LAParser.Valor_constante_cadeiaContext ctx) {
        return ctx.CADEIA().getText();
    }

    @Override
    public Object visitValor_constante_int(LAParser.Valor_constante_intContext ctx) {
        return ctx.NUM_INT().getText();
    }

    @Override
    public Object visitValor_constante_real(LAParser.Valor_constante_realContext ctx) {
        return ctx.NUM_REAL().getText();
    }

    @Override
    public Object visitCmdSe(LAParser.CmdSeContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.expressao().getText())); //????

        super.visitCmdSe(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdCaso(LAParser.CmdCasoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.exp_aritmetica().getText())); //????

        super.visitCmdCaso(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdPara(LAParser.CmdParaContext ctx) {

        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        super.visitCmdPara(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdEnquanto(LAParser.CmdEnquantoContext ctx) {

        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.expressao().getText())); //????

        super.visitCmdEnquanto(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdFaca(LAParser.CmdFacaContext ctx) {

        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.expressao().getText())); //????

        super.visitCmdFaca(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdChamada(LAParser.CmdChamadaContext ctx) {

        List<TabelaDeSimbolos> todosEscopos = pilhaDeTabelas.getTodasTabelas();

        for(TabelaDeSimbolos ts: todosEscopos){
            boolean etds = ts.existeSimbolo(ctx.IDENT().getText());
            if(etds){
                ctx.IDENT().getText();
            }else {
                throw new RuntimeException("Erro semantico: " + ctx.IDENT().getText() + "Funçao inexistente");
            }
        }

        return null;
    }


}
