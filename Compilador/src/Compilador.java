
import java.io.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
//import sun.jvm.hotspot.debugger.win32.coff.COMDATSelectionTypes;

public class Compilador {

    private final static String CAMINHO_CASOS_TESTE = "/Users/Macbook/IdeaProjects/Compiladores2-T1/casosDeTesteT1/1.arquivos_com_erros_sintaticos";

    public static void main(String[] args) throws IOException, RecognitionException {


        // Cria a saida
        Saida out = new Saida();

        // Para quando for gerar o jar, utilizar a linha abaixo em vez da outra, pois iremos recever o c�digo por argumento
        // ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(casoTeste));

        CharStream input = CharStreams.fromFileName(args[0]);

        LALexer lexer = new LALexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);

        // Adiciona o ErrorListener
        parser.addErrorListener(new ErrorListener(out));

        // Executa a análise sintatica
        LAParser.ProgramaContext arvore = parser.programa();


        if(!out.isModificado()){ // Caso tenha sido modificado

            // Cria o analisador semantico
            Visitor v = new Visitor();

            // Torna o tokenstream acessivel ao Analisador Semantico
            v.setTokenStream(tokens);

            // Executa a analise semantica
            v.visitPrograma(arvore);

            if(!out.isModificado()) { // Caso a analise tenha sido bem sucedida

                // Executa gerador de código
                GeradorDeCodigo gc = new GeradorDeCodigo();
                ParseTreeWalker.DEFAULT.walk(gc, arvore);
                out.println(gc.getString());
                try(PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {
                    pw.print(out);
                }
            }
            else{
                try(PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {
                    pw.print(out);
                    pw.println("Fim da compilacao");
                }
            }
        }
        else {
            try (PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {

                pw.print(out);
                pw.println("Fim da compilacao");
            }
        }
    }
}