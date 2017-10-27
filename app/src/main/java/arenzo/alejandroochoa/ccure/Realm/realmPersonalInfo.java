package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;

public class realmPersonalInfo extends RealmObject {


    private String NoEmpleado;

    private String Nombre;
    private String Foto;
    private String Puesto;
    private String Fase;
    private String MFechaHora;
    private String MUsuarioId;

    public String getNoEmpleado() {
        return NoEmpleado;
    }

    public void setNoEmpleado(String noEmpleado) {
        NoEmpleado = noEmpleado;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
    }

    public String getPuesto() {
        return Puesto;
    }

    public void setPuesto(String puesto) {
        Puesto = puesto;
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
