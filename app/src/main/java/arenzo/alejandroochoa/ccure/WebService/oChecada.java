package arenzo.alejandroochoa.ccure.WebService;

/**
 * Created by AlejandroMissael on 09/05/2017.
 */

public class oChecada {

    private String NoEmpleado;
    private String NoTarjeta;
    private String PUEClave;
    private String FechaHoraEntrada;

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

    public String getPUEClave() {
        return PUEClave;
    }

    public void setPUEClave(String PUEClave) {
        this.PUEClave = PUEClave;
    }

    public String getFechaHoraEntrada() {
        return FechaHoraEntrada;
    }

    public void setFechaHoraEntrada(String fechaHoraEntrada) {
        FechaHoraEntrada = fechaHoraEntrada;
    }
}
