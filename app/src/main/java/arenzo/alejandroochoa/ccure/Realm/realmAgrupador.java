package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class realmAgrupador extends RealmObject {

    @PrimaryKey
    private int AGRId;

    private String Descripcion;
    private String Fase;
    private String MFechaHora;
    private String MUsuarioId;

    public int getAGRId() {
        return AGRId;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFase() {
        return Fase;
    }

    public void setFase(String fase) {
        Fase = fase;
    }

    public String getMFechaHora() {
        return MFechaHora;
    }

    public void setMFechaHora(String MFechaHora) {
        this.MFechaHora = MFechaHora;
    }

    public String getMUsuarioId() {
        return MUsuarioId;
    }

    public void setMUsuarioId(String MUsuarioId) {
        this.MUsuarioId = MUsuarioId;
    }
}
