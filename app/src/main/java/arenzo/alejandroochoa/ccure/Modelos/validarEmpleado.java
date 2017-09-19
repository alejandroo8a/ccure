package arenzo.alejandroochoa.ccure.Modelos;

import com.google.gson.annotations.SerializedName;

public class validarEmpleado {
    @SerializedName("Informacion")
    private respuestaValidarEmpleado empleado;
    @SerializedName("Respuesta")
    private String respuesta;

    public respuestaValidarEmpleado getEmpleado() {
        return empleado;
    }

    public String getRespuesta() {
        return respuesta;
    }

}
