package arenzo.alejandroochoa.ccure.Modelos;

public class agrupador {

    private int AGRId;

    private String Descripcion;
    private String Fase;
    private String MFechaHora;
    private String MUsuarioId;

    public int getAGRId() {
        return AGRId;
    }

    public void setAGRId(int AGRId) {
        this.AGRId = AGRId;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
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
