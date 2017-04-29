package arenzo.alejandroochoa.ccure.Realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class realmNotificacion extends RealmObject {

    @PrimaryKey
    private int NOTId;

    private String Mensaje;
    private String Fase;
    private String Tipo;
    private String Valor;

    public int getNOTId() {
        return NOTId;
    }

    public void setNOTId(int NOTId) {
        this.NOTId = NOTId;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

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

    public String getValor() {
        return Valor;
    }

    public void setValor(String valor) {
        Valor = valor;
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
