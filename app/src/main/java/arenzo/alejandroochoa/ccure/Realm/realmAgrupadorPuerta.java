package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmAgrupadorPuerta extends RealmObject {

    @PrimaryKey
    private int AGRId;

    private int PUEId;

    private String Fase;
    private int Tipo;
    private String FechaHora;
    private String MUsuarioId;

    public int getAGRId() {
        return AGRId;
    }

    public void setAGRId(int AGRId) {
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

    public int getTipo() {
        return Tipo;
    }

    public void setTipo(int tipo) {
        Tipo = tipo;
    }

    public String getFechaHora() {
        return FechaHora;
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
