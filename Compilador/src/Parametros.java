import java.util.ArrayList;

public class Parametros {

    private ArrayList<String> Lista;
    private String identificador;

    public Parametros(String identificador){
        this.identificador = identificador;
        Lista = new ArrayList<String>();
    }

    public String getIdentificador(){
        return identificador;
    }

    public void setLista(ArrayList<String> NLista){
        this.Lista = NLista;
    }

    public ArrayList<String> getLista(){
        return this.Lista;
    }
}
