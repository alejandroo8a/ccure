package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmAgrupadorPuerta extends RealmObject {

    @PrimaryKey
    private Integer AGRId;
    @PrimaryKey
    private Integer PUEId;

    private String Fase;
    private Integer Tipo;

    public Integer getAGRId() {
        return AGRId;
    }

    public void setAGRId(Integer AGRId) {
        this.AGRId = AGRId;
    }

    public Integer getPUEId() {
        return PUEId;
    }

    public void setPUEId(Integer PUEId) {
        this.PUEId = PUEId;
    }

    public String getFase() {
        return Fase;
    }

    public void setFase(String fase) {
        Fase = fase;
    }

    public Integer getTipo() {
        return Tipo;
    }

    public void setTipo(Integer tipo) {
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

    private String FechaHora;
    private String MUsuarioId;
}
