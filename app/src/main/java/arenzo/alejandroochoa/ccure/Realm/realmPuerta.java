package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmPuerta extends RealmObject{

    @PrimaryKey
    private int PUEId;

    private String PUEClave;
    private String Descripcion;
    private String Fase;
    private String GRUID;
    private String MFechaHora;
    private String MUsuarioId;

    public int getPUEId() {
        return PUEId;
    }

    public void setPUEId(int PUEId) {
        this.PUEId = PUEId;
    }

    public String getPUEClave() {
        return PUEClave;
    }

    public void setPUEClave(String PUEClave) {
        this.PUEClave = PUEClave;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getFase() {
        return Fase;
    }

    public void setFase(String fase) {
        Fase = fase;
    }

    public String getGRUID() {
        return GRUID;
    }

    public void setGRUID(String GRUID) {
        this.GRUID = GRUID;
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
