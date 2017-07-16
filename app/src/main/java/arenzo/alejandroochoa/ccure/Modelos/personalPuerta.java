package arenzo.alejandroochoa.ccure.Modelos;

/**
 * Created by AlejandroMissael on 20/06/2017.
 */

public class personalPuerta {

    private String NoEmpleado;
    private String NoTarjeta;
    private String PUEId;
    private String GRUId;

    public personalPuerta() {
    }

    public String getPUEId() {
        return PUEId;
    }

    public void setPUEId(String PUEId) {
        this.PUEId = PUEId;
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

    public String getGRUId() {
        return GRUId;
    }

    public void setGRUId(String GRUId) {
        this.GRUId = GRUId;
    }
}
