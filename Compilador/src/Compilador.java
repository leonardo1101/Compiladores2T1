
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import sun.jvm.hotspot.debugger.win32.coff.COMDATSelectionTypes;

public class Compilador {

    private final static String CAMINHO_CASOS_TESTE = "/Users/Macbook/IdeaProjects/Compiladores2-T1/casosDeTesteT1/1.arquivos_com_erros_sintaticos";

    public static void main(String[] args) throws IOException, RecognitionException {

        File diretorioCasosTeste = new File(CAMINHO_CASOS_TESTE + "/entrada");
        File [] casosTeste = diretorioCasosTeste.listFiles();
        int totalCasosTeste = casosTeste.length;
        int casosTesteErrados = 0;
        for(File casoTeste : casosTeste){
            //Cria a saida
            SaidaParser out = new SaidaParser();

            //Para quando for gerar o jar, utilizar a linha abaixo em vez da outra, pois iremos recever o c�digo por argumento
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(casoTeste));

            //ANTLRInputStream input = new ANTLRInputStream(Compilador.class.getResourceAsStream("entrada/la1.txt"));

            LALexer lexer = new LALexer(input);

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LAParser parser = new LAParser(tokens);
            parser.addErrorListener(new ErrorListener(out));
            parser.programa();
            if(!out.isModificado()){
                out.println("Fim sem erros");
            }
            System.out.print(out);
            System.out.println("Fim da compilacao");
        }

    }
}