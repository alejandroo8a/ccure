package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class realmAgrupadorPuerta extends RealmObject {


    private int AGRId;

    private int PUEId;

    private String Fase;
    private String Tipo;
    private String FechaHora;
    private String MUsuarioId;

    void setAGRId(int AGRId) {
        this.AGRId = AGRId;
    }

    public int getPUEId() {
        return PUEId;
    }

    public void setPUEId(int PUEId) {
        this.PUEId = PUEId;
    }

    public String getFase() {
        return Fase;
    }

    public void setFase(String fase) {
        Fase = fase;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public void setFechaHora(String fechaHora) {
        FechaHora = fechaHora;
    }

    public String getMUsuarioId() {
        return MUsuarioId;
    }

    public void setMUsuarioId(String MUsuarioId) {
        this.MUsuarioId = MUsuarioId;
    }


}
