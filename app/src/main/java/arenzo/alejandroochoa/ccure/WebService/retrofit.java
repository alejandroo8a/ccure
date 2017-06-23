package arenzo.alejandroochoa.ccure.WebService;

import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by AlejandroMissael on 08/05/2017.
 */

public interface retrofit{

    public static final String URL="http://192.168.1.10/CCUREMOVIL/ServiceMethods.svc/";

    @GET("ValidarEmpleadoJSON/{noEmpleado}/{noTarjeta}/{clavePuerta}")
    Call<String> getValidarEmpleado(@Path("noEmpleado") String noEmpleado,
                                    @Path("noTarjeta") String noTarjeta,
                                    @Path("clavePuerta") String clavePuerta );

    @GET("ObtenerTarjetasPersonal")
    Call<List<tarjetasPersonal>> getTarjetasPersonal(@QueryMap Map<String, String> parametros);

    @GET("ObtenerPersonalPuertaJSON")
    Call<List<personalPuerta>> getPersonalPuerta();

    @GET("ObtenerInfoPersonalJSON")
    @Headers("Content-Type: application/json")
    Call<List<personalInfo>> getPersonalInfo();

    @GET("ActualizarPuertas")
    Call<List<puertas>> getActualizarPuertas();

    @FormUrlEncoded
    @POST("ActualizarChecadasArrayJSON/{checadas}")
    Call<List<String>> getActualizarChecadas(@Field("checadas") List<oChecada> aChecadas);


/*con parametros
    @FormUrlEncoded
    @GET("/gomovil/movil.php")
    Call<List<realmPersonalPuerta>> getPersonalPuerta(
            @Field("tipo")String tipo,
            @Field("tipoNegocioN") String neg
    );*/

}
