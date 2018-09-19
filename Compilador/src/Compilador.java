
import java.io.*;

import org.antlr.v4.runtime.*;
//import sun.jvm.hotspot.debugger.win32.coff.COMDATSelectionTypes;

public class Compilador {

    private final static String CAMINHO_CASOS_TESTE = "/Users/Macbook/IdeaProjects/Compiladores2-T1/casosDeTesteT1/1.arquivos_com_erros_sintaticos";

    public static void main(String[] args) throws IOException, RecognitionException {

        //File diretorioCasosTeste = new File(CAMINHO_CASOS_TESTE + "/entrada");
        //File [] casosTeste = diretorioCasosTeste.listFiles();
        //int totalCasosTeste = casosTeste.length;
        //int casosTesteErrados = 0;
        //for(File casoTeste : casosTeste){
            //Cria a saida
            Saida out = new Saida();

            //Para quando for gerar o jar, utilizar a linha abaixo em vez da outra, pois iremos recever o c�digo por argumento
            //ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(casoTeste));

         CharStream input = CharStreams.fromFileName(args[0]);

            LALexer lexer = new LALexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);
          //  parser.removeErrorListeners();
            parser.addErrorListener(new ErrorListener(out));
            LAParser.ProgramaContext arvore = parser.programa();


            if(!out.isModificado()){
                Visitor v = new Visitor();   //leo colocou isso agora mas n funcionou parece
                v.setTokenStream(tokens);
                v.visitPrograma(arvore);
                if(!out.isModificado())
                    out.println("Fim sem erros");
            }

            try(PrintWriter pw = new PrintWriter(new FileWriter(args[1]))) {

                pw.print(out);
                pw.println("Fim da compilacao");
            }
        //}

    }
}