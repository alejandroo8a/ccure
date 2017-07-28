package arenzo.alejandroochoa.ccure.Modelos;

/**
 * Created by AlejandroMissael on 29/06/2017.
 */

public class respuestaValidarEmpleado {

    private String Foto;
    private String NoEmpleado;
    private String Nombre;
    private String Puesto;
    private String PUEClave;

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        this.Foto = foto;
    }

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

    public String getPuesto() {
        return Puesto;
    }

    public void setPuesto(String puesto) {
        Puesto = puesto;
    }

    public String getPUEClave() {
        return PUEClave;
    }

    public void setPUEClave(String PUEClave) {
        this.PUEClave = PUEClave;
    }
}
