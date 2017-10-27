package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;

public class realmValidaciones extends RealmObject{

    private String NoEmpleado;
    private String NoTarjeta;
    private String PUEClave;
    private String FechaHoraEntrada;
    private String Nombre;
    private String Puesto;
    private String Foto;
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

    public void setPUEClave(String PUEClave) {
        this.PUEClave = PUEClave;
    }

    public void setFechaHoraEntrada(String fechaHoraEntrada) {
        FechaHoraEntrada = fechaHoraEntrada;
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

    public void setTipoEntrada(String tipoEntrada) {
        TipoEntrada = tipoEntrada;
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

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getPuesto() {
        return Puesto;
    }

    public void setPuesto(String puesto) {
        Puesto = puesto;
    }
}
