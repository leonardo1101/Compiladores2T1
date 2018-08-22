
import java.io.FileInputStream;
import java.io.IOException;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import sun.jvm.hotspot.debugger.win32.coff.COMDATSelectionTypes;

public class Compilador {

    public static void main(String[] args) throws IOException, RecognitionException {
        //Cria a saida
        SaidaParser out = new SaidaParser();

        //Para quando for gerar o jar, utilizar a linha abaixo em vez da outra, pois iremos recever o c�digo por argumento
        //ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(args[0]));

        ANTLRInputStream input = new ANTLRInputStream(Compilador.class.getResourceAsStream("entrada/la1.txt"));

        LALexer lexer = new LALexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(new ErrorListener(out));
        parser.programa();
        out.println("Fim da compilacao");

    }
}