package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmDispositivo extends RealmObject {

    @PrimaryKey
    private int DISId;

    private String Descripcion;
    private String Fase;
    private int AGRId;
    private String URLWebService;
    private String URLExportacion;
    private String MFechaHora;
    private String MUsuarioId;

    public int getDISId() {
        return DISId;
    }

    public void setDISId(int DISId) {
        this.DISId = DISId;
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

    public int getAGRId() {
        return AGRId;
    }

    public void setAGRId(int AGRId) {
        this.AGRId = AGRId;
    }

    public String getURLWebService() {
        return URLWebService;
    }

    public void setURLWebService(String URLWebService) {
        this.URLWebService = URLWebService;
    }

    public String getURLExportacion() {
        return URLExportacion;
    }

    public void setURLExportacion(String URLExportacion) {
        this.URLExportacion = URLExportacion;
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
