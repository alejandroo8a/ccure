package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmESPersonal extends RealmObject {


    private String NoEmpleado;
    private String NoTarjeta;
    private String PUEId;
    private String FechaHoraEntrada;

    private String FaseIngreso;
    private String Fase;
    private String TipoEntrada;
    private String Observaciones;
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

    public String getPUEId() {
        return PUEId;
    }

    public void setPUEId(String PUEId) {
        this.PUEId = PUEId;
    }

    public String getFechaHoraEntrada() {
        return FechaHoraEntrada;
    }

    public void setFechaHoraEntrada(String fechaHoraEntrada) {
        FechaHoraEntrada = fechaHoraEntrada;
    }

    public String getFaseIngreso() {
        return FaseIngreso;
    }

    public void setFaseIngreso(String faseIngreso) {
        FaseIngreso = faseIngreso;
    }

    public String getFase() {
        return Fase;
    }

    public void setFase(String fase) {
        Fase = fase;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
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

    public String getTipoEntrada() {
        return TipoEntrada;
    }

    public void setTipoEntrada(String tipoEntrada) {
        TipoEntrada = tipoEntrada;
    }
}
