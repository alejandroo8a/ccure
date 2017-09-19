package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

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
