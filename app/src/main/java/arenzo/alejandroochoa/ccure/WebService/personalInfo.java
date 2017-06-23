package arenzo.alejandroochoa.ccure.WebService;

/**
 * Created by AlejandroMissael on 20/06/2017.
 */

public class personalInfo {

    private String Foto;
    private String NoEmpleado;
    private String Nombre;
    private String Puesto;


    public personalInfo() {
    }

    public String getFoto() {
        return Foto;
    }

    public void setFoto(String foto) {
        Foto = foto;
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
}
