package arenzo.alejandroochoa.ccure.Modelos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AlejandroMissael on 20/06/2017.
 */

public class personalPuerta {

    private String NoEmpleado;
    private String NoTarjeta;
    @SerializedName("Grupo")
    private String GRUId;

    public personalPuerta() {
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
