import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Visitor extends LABaseVisitor {

    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();

    CommonTokenStream cts;
    List<Parametros> listaP = new ArrayList<>();
    LinkedList<List<String>> pilhaTipo = new LinkedList<>() ;

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
        String tipo = (String) super.visitTipo_basico(ctx.tipo_basico());

        if(!escopoAtual.existeSimbolo(ctx.nome.getText())) {
                escopoAtual.adicionarSimbolo(ctx.nome.getText(), tipo);
        } else{
            Saida.println("Linha "+ctx.nome.getLine()+": identificador " +ctx.nome.getText()+ " ja declarado anteriormente");

        }

        super.visitDeclaracao_local_constante(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }

    @Override
    public Object visitDeclaracao_local_tipo(LAParser.Declaracao_local_tipoContext ctx) {
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        String tipo = (String) super.visitTipo(ctx.tipo());

        if(!escopoAtual.existeSimbolo(ctx.nome1.getText())) {
               escopoAtual.adicionarSimbolo(ctx.nome1.getText(), tipo);
        } else{
            Saida.println("Linha "+ctx.nome1.getLine()+": identificador " +ctx.nome1.getText()+ " ja declarado anteriormente");

        }

        super.visitDeclaracao_local_tipo(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }

    @Override
    public Object visitDeclaracao_global_procedimento(LAParser.Declaracao_global_procedimentoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        Parametros p =new Parametros(ctx.IDENT().getText());
        ArrayList<String> parametros = new ArrayList<>();
        for ( LAParser.ParametroContext pc :   ctx.parametros().parametro()){
            parametros.add(pc.tipo_estendido().getText());

        }
        p.setLista(parametros);
        listaP.add(p);

        super.visitDeclaracao_global_procedimento(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitDeclaracao_global_funcao(LAParser.Declaracao_global_funcaoContext ctx) {
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos(ctx.IDENT().getText()));

        Parametros p =new Parametros(ctx.IDENT().getText());
        ArrayList<String> parametros = new ArrayList<>();
        for ( LAParser.ParametroContext pc :   ctx.parametros().parametro()){
            parametros.add(pc.tipo_estendido().getText());

        }
        p.setLista(parametros);
        listaP.add(p);

        super.visitDeclaracao_global_funcao(ctx);

        pilhaDeTabelas.desempilhar();

        return null;
    }

    @Override
    public Object visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx)  {

        String tipoVar = pilhaDeTabelas.getTipoSimbolo(ctx.identificador().nome3.getText());
        String tipoAtrb = visitExpressao(ctx.expressao());
        System.out.println("Variavel : " + ctx.identificador().nome3.getText());
        System.out.println("Tipo atribuido: " + tipoAtrb);
        if(tipoVar == "real" && tipoAtrb=="inteiro")
            tipoAtrb="real";
       if(!tipoVar.equals(tipoAtrb) ){
           Saida.println("Linha " + ctx.identificador().nome3.getLine() + ": atribuicao nao compativel para " + ctx.identificador().nome3.getText() );
       }


        return null;
    }
    @Override
    public Object visitVariavel(LAParser.VariavelContext ctx){
        TabelaDeSimbolos escopoAtual = pilhaDeTabelas.topo();
        String tipo = ctx.tipo().getText();

        for(LAParser.Identificador_varContext ctx_identificador: ctx.identificador_var()) {
            String nomeVar = (String) visitIdentificador_var(ctx_identificador);
            if (!escopoAtual.existeSimbolo(nomeVar) ){
                escopoAtual.adicionarSimbolo(nomeVar, tipo);
            } else {
                Saida.println("Linha " + ctx_identificador.nome2.getLine() + ": identificador " + nomeVar + " ja declarado anteriormente");
            }
        }
        super.visitVariavel(ctx);
        return "";
    }


    @Override
    public String visitIdentificador_var(LAParser.Identificador_varContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitIdentificador(LAParser.IdentificadorContext ctx) {
        if(!pilhaDeTabelas.existeSimbolo(ctx.nome3.getText())) {
            Saida.println("Linha "+ctx.nome3.getLine()+": identificador " + ctx.nome3.getText() + " nao declarado");
        }else{
            String tipo = pilhaDeTabelas.getTipoSimbolo(ctx.nome3.getText());
            if(!pilhaTipo.isEmpty()) {
                List<String> l = pilhaTipo.pop();
                l.add(tipo);
                pilhaTipo.add(l);
            }


        }

      //  super.visitIdentificador(ctx); // talvez precise deixar isso aqui pra ele visitar os filhos, precisa confirmar

        return null;
    }



//    @Override
//    public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx) {
//        String tipoT = null;
//        List<LAParser.TermoContext> lista = new ArrayList<>();
//        if(ctx != null){
//            lista = ctx.outrosTermos;
//            lista.add(ctx.termo1);
//            for (LAParser.TermoContext termoContext : lista) {
//                if (super.visitTermo(termoContext) != null)
//                    tipoT = (String) super.visitTermo(termoContext);
//            }
//            for (LAParser.TermoContext termoContext : lista) {
//                String t = (String) visitTermo(termoContext);
//
//                if (t != null && tipoT != null) {
//                    if (!tipoT.equals(t)) {
//
//                        return "TD";
//                    }
//                }
//            }
//        }
//        return tipoT;
//    }

//    @Override
//    public String visitTermo(LAParser.TermoContext ctx) {
//        String tipoT = null;
//        List<LAParser.FatorContext> lista = ctx.outrosFatores;
//        lista.add(ctx.fator1);
//        for(LAParser.FatorContext fator : lista){
//
//            if(super.visitFator(fator) != null)
//                tipoT  = (String) super.visitFator(fator);
//        }
//        for(LAParser.FatorContext fator : lista){
//            String t = (String) super.visitFator(fator);
//
//            System.out.println("Antes: " + tipoT + " Depois: " + t);
//            if(t!= null) {
//                if (!tipoT.equals(t)) {
//                    return "TD";
//                }
//            }
//        }
//        return tipoT;
//
//    }

//
//    @Override
//    public String visitFator(LAParser.FatorContext ctx) {
//        String tipoT = null;
//        List<LAParser.ParcelaContext> lista = ctx.outrasParcelas;
//        lista.add(ctx.parcela1);
//        for(LAParser.ParcelaContext parcela : lista){
//            if(super.visitParcela(parcela) != null)
//                tipoT  = (String) super.visitParcela(parcela);
//        }
//        for(LAParser.ParcelaContext parcela : lista){
//            String t = (String) super.visitParcela(parcela);
//
//            if(t!= null) {
//                if (!tipoT.equals(t)) {
//                    return "TD";
//                }
//            }
//        }
//        return tipoT;
//    }
//    @Override
//    public String visitFator_logico(LAParser.Fator_logicoContext ctx) {
//        return (String) super.visitParcela_logica(ctx.parcela_logica());
//    }

//        if(visitParcela_unario_ident(ctx.parcela_unario_ident()) != null)
//            return (String) visitParcela_unario_ident(ctx.parcela_unario_ident());
//
//        if(visitParcela_unario_int(ctx.parcela_unario_int())!= null)
//            return (String) visitParcela_unario_int(ctx.parcela_unario_int());
//
//        if(visitParcela_unario_real(ctx.parcela_unario_real())!= null)
//            return (String) visitParcela_unario_real(ctx.parcela_unario_real());
//
//        if(visitParcela_unario_exp(ctx.parcela_unario_exp()) != null)
//            return (String) visitParcela_unario_exp(ctx.parcela_unario_exp()) ;
//
//        if(visitParcela_unario_identificador(ctx.parcela_unario_identificador())!= null)
//            return (String) visitParcela_unario_identificador(ctx.parcela_unario_identificador());
//        return null;
//
//    }



//    @Override public String visitParcela(LAParser.ParcelaContext ctx) {
//        if(visitParcela_nao_unario(ctx.parcela_nao_unario()) != null)
//            return (String) visitParcela_nao_unario(ctx.parcela_nao_unario());
//
//        if(visitParcela_unario(ctx.parcela_unario())!= null)
//            return (String) visitParcela_unario(ctx.parcela_unario());
//
//        return null;
//    }

    @Override public String visitParcela_unario_identificador(LAParser.Parcela_unario_identificadorContext ctx) {
        if(!pilhaTipo.isEmpty()) {
            List<String> l = pilhaTipo.pop();
            System.out.println("Entrou aqui " + ctx.identificador().nome3.getText());
            System.out.println("Tipo : " + pilhaDeTabelas.getTipoSimbolo(ctx.identificador().nome3.getText()));
            l.add(pilhaDeTabelas.getTipoSimbolo(ctx.identificador().nome3.getText()));
            pilhaTipo.add(l);
        }
        super.visitParcela_unario_identificador(ctx);
        return "";
    }
//    @Override public String visitParcela_unario_exp(LAParser.Parcela_unario_expContext ctx) {
//        return (String) super.visitExpressao(ctx.expressao());
//    }



    @Override
    public String visitParcela_unario_ident(LAParser.Parcela_unario_identContext ctx) {
        String tipo = null;
        if(ctx != null) {
            if (pilhaDeTabelas.existeSimbolo(ctx.IDENT().getText())) {
                tipo = pilhaDeTabelas.getTipoSimbolo(ctx.IDENT().getText());
                List<String> l = pilhaTipo.pop();
                l.add(tipo);
                pilhaTipo.add(l);

            } else {
                Saida.println("Linha " + ctx.getStart().getLine() + ": identificador " + ctx.IDENT().getText() + " nao declarado");
            }
        }
        super.visitParcela_unario_ident(ctx);
        return "";
    }

    @Override
    public String visitParcela_unario_int(LAParser.Parcela_unario_intContext ctx) {

        if(!pilhaTipo.isEmpty()) {
            List<String> l = pilhaTipo.pop();
            l.add("inteiro");

            System.out.println("Inttt");
            pilhaTipo.add(l);
        }
        super.visitParcela_unario_int(ctx);
        return "";
    }

    @Override
    public String visitParcela_unario_real(LAParser.Parcela_unario_realContext ctx) {

        List<String> l = pilhaTipo.pop();
        System.out.println("Ola o real :" + ctx.getText());
        l.add("real");
        pilhaTipo.add(l);
        super.visitParcela_unario_real(ctx);
        return "";

    }

    @Override
    public String visitParcela_nao_unario(LAParser.Parcela_nao_unarioContext ctx) {

        if(ctx == null){
            return null;
        }else {
            if(ctx.identificador() == null && ctx.CADEIA() != null) {
                if(!pilhaTipo.isEmpty()) {
                    List<String> l = pilhaTipo.pop();
                    System.out.println("Foiiiiiii " + ctx.oi.getLine());
                    l.add("literal");
                    pilhaTipo.add(l);
                }
            }else{
                if(!pilhaTipo.isEmpty()) {
                    List<String> l = pilhaTipo.pop();
                    l.add(pilhaDeTabelas.getTipoSimbolo(ctx.identificador().nome3.getText()));
                    pilhaTipo.add(l);
                }
            }
        }
        super.visitParcela_nao_unario(ctx);
        return "";
    }

    @Override public String visitParcela_logica(LAParser.Parcela_logicaContext ctx) {
        if(!pilhaTipo.isEmpty() && ctx.exp_relacional() == null) {
            List<String> l = pilhaTipo.pop();
            l.add("logico");
            pilhaTipo.add(l);
        }
        super.visitParcela_logica(ctx);
        return "";
    }
    @Override
    public String visitExp_relacional(LAParser.Exp_relacionalContext ctx) {
        
        super.visitExp_relacional(ctx);
    }

    @Override
    public String visitExpressao(LAParser.ExpressaoContext ctx) {
        List<String> listaTipos =  new ArrayList<>();
        pilhaTipo.push(listaTipos);
        super.visitExpressao(ctx);
        listaTipos = pilhaTipo.pop();
        String tipo = listaTipos.get(0);

        for(int i=1;i < listaTipos.size();i++){
            System.out.println("0 "+ tipo + "\n"+ i +" "+ listaTipos.get(i));
            if(!tipo.equals(listaTipos.get(i)) && listaTipos.get(i) != null){
                if(!((tipo.equals("inteiro") && listaTipos.get(i).equals("real")) || (tipo.equals("real") && listaTipos.get(i).equals("inteiro"))))
                    return "Tipo Incopativeis";
            }
        }
        return tipo;


    }


//NECESSARIO VERIFICAR SE O TIPO EXISTE?

    @Override
    public String visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx) {
        String tipo_basico = "";
        if(ctx.tipo_basico() !=null)
            if(super.visitTipo_basico(ctx.tipo_basico()) != null)
                tipo_basico = ctx.tipo_basico().getText();
        if(ctx.nome4 != null)
            tipo_basico = ctx.nome4.getText();
        System.out.println("Tipo: " + tipo_basico);

        if(tipo_basico != ""){
            if(tipo_basico.equals("real") || tipo_basico.equals("inteiro")  || tipo_basico.equals("literal")  || tipo_basico.equals("logico")  ){
                return tipo_basico;
            } else{
                Saida.println("Linha "+ctx.nome4.getLine()+": tipo " + ctx.nome4.getText() + " nao declarado");
            }
        }else {
            return tipo_basico;
        }
        super.visitTipo_basico_ident(ctx);
        return null;
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
        int i = 0;
        boolean encontrou = false;
        Parametros p = new Parametros("a");
        while (i < listaP.size() && ! encontrou){
            p = listaP.get(i);
            if(p.getIdentificador().equals(ctx.IDENT().getText())){
                encontrou = true;
            }
        }
        if(!encontrou){
            Saida.println("Erro semantico: " + ctx.IDENT().getText() + "Funçao inexistente");
        }else{
            for(i=0;i<p.getLista().size();i++){
                if(p.getLista().get(i).equals(ctx.expressao(i)));
            }

        }


        return null;
    }



}
