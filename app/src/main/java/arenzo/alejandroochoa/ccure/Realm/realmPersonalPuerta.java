package arenzo.alejandroochoa.ccure.Realm;

import com.google.gson.annotations.SerializedName;
import io.realm.RealmObject;

public class realmPersonalPuerta extends RealmObject {


    private String NoEmpleado;
    private String NoTarjeta;
    @SerializedName("Grupo")
    private String GRUId;

    private String Fase;
    private String MFechaHora;
    private String MUsuarioId;

    public String getNoEmpleado() {
        return NoEmpleado;
    }

    public void setNoEmpleado(String noEmpleado) {
        NoEmpleado = noEmpleado;
    }

    public String getNoTarjeta() {
        return NoTarjeta;
    }

    public void setNoTarjeta(String noTarjeta) {
        NoTarjeta = noTarjeta;
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

    public void setGRUId(String GRUId) {
        this.GRUId = GRUId;
    }
}
