package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class realmNotificacion extends RealmObject {

    @PrimaryKey
    private int NOTId;

    private String Mensaje;
    private String Fase;
    private String Tipo;
    private String Valor;

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

    private String MFechaHora;
    private String MUsuarioId;


}
