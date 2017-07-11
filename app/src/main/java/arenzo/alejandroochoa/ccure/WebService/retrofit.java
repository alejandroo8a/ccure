package arenzo.alejandroochoa.ccure.WebService;

import java.util.List;

import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.Modelos.agrupadorPuerta;
import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.respuestaChecadas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
import arenzo.alejandroochoa.ccure.Modelos.usuario;
import arenzo.alejandroochoa.ccure.Modelos.validarEmpleado;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 * Created by AlejandroMissael on 08/05/2017.
 */

public interface retrofit{

    public static String URL = "http://192.168.1.10/CCUREMOVIL/ServiceMethods.svc/";

    @GET("ValidarEmpleadoJSON/{noEmpleado}/{noTarjeta}/{clavePuerta}")
    Call<validarEmpleado> getValidarEmpleado(@Path("noEmpleado") String noEmpleado,
                                             @Path("noTarjeta") String noTarjeta,
                                             @Path("clavePuerta") String clavePuerta );

    @GET("ObtenerTarjetasPersonalJSON/{Tipo}")
    Call<List<usuario>> getTarjetasPersonal(@Path("Tipo") String tipo);

    @GET("ObtenerTarjetasPersonalJSON/{Tipo}")
    Call<List<usuario>> getUsuario(@Path("Tipo") String tipo);

    @GET("ObtenerPersonalPuertaJSON")
    Call<List<personalPuerta>> getPersonalPuerta();

    @GET("ObtenerInfoPersonalJSON")
    @Headers("Content-Type: application/json")
    Call<List<personalInfo>> getPersonalInfo();

    @GET("ActualizarAgrupadoresJSON")
    Call<List<agrupador>> getAgrupadores();

    @GET("ActualizarPuertasJSON")
    Call<List<puertas>> getActualizarPuertas();

    @GET("ActualizarAgrupadorPuertasJSON")
    Call<List<agrupadorPuerta>> getAgrupadorPuerta();

    @GET("ActualizarChecadasJSON/{noEmpleado}/{noTarjeta}/{pueClave}/{fechaHoraEntrada}/{faseIngreso}")
    Call<List<respuestaChecadas>> getActualizarChecadas(@Path("noEmpleado") String noEmpleado,
                                                        @Path("noTarjeta") String noTarjeta,
                                                        @Path("pueClave") String pueClave,
                                                        @Path("fechaHoraEntrada") String fechaHoraEntrada,
                                                        @Path("faseIngreso") String faseIngreso);

/*con parametros
    @FormUrlEncoded
    @GET("/gomovil/movil.php")
    Call<List<realmPersonalPuerta>> getPersonalPuerta(
            @Field("tipo")String tipo,
            @Field("tipoNegocioN") String neg
    );*/

}
