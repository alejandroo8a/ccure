package arenzo.alejandroochoa.ccure.Modelos;

public class agrupadorPuerta {

    private int AGRId;
    private int PUEId;
    private String Fase;
    private String Tipo;
    private String FechaHora;
    private String MUsuarioId;

    public int getAGRId() {
        return AGRId;
    }

    public void setAGRId(int AGRId) {
        this.AGRId = AGRId;
    }

    public int getPUEId() {
        return PUEId;
    }

    public void setPUEId(int PUEId) {
        this.PUEId = PUEId;
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

    public String getMUsuarioId() {
        return MUsuarioId;
    }

    public void setMUsuarioId(String MUsuarioId) {
        this.MUsuarioId = MUsuarioId;
    }
}
