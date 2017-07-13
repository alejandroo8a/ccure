package arenzo.alejandroochoa.ccure.Modelos;

/**
 * Created by AlejandroMissael on 20/06/2017.
 */

public class personalPuerta {

    private String ClavePuerta;
    private String NoEmpleado;
    private String NoTarjeta;
    private String PUEId;

    public personalPuerta() {
    }

    public String getClavePuerta() {
        return ClavePuerta;
    }

    public void setClavePuerta(String clavePuerta) {
        ClavePuerta = clavePuerta;
    }

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
}
