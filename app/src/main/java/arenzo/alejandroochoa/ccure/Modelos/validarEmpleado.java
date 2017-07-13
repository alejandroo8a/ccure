package arenzo.alejandroochoa.ccure.Modelos;

import com.google.gson.annotations.SerializedName;

/**
 * Created by AlejandroMissael on 29/06/2017.
 */

public class validarEmpleado {
    @SerializedName("Informacion")
    private respuestaValidarEmpleado empleado;
    @SerializedName("Respuesta")
    private String respuesta;

    public respuestaValidarEmpleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(respuestaValidarEmpleado empleado) {
        this.empleado = empleado;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }
}
