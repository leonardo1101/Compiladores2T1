
import java.io.IOException;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;

public class Compilador {

    public static void main(String[] args) {
        SaidaParser out = new SaidaParser();

        ANTLRInputStream input = new ANTLRInputStream(Compilador.class.getResourceAsStream("exemplos/lua5.txt"));
        LuaLexer lexer = new LuaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LuaParser parser = new LuaParser(tokens);
        parser.addErrorListener(new ErrorListener(out));
        parser.programa();
        if (!out.isModificado()) {
            out.println("Fim da analise. Sem erros sintaticos.");
        } else {
            out.println("Fim da analise. Com erros sintaticos.");
        }

    }
}
