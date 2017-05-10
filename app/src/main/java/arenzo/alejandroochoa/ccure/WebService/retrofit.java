package arenzo.alejandroochoa.ccure.WebService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by AlejandroMissael on 08/05/2017.
 */

public interface retrofit{

    public static final String URLVALIDAREMPLEADO="http://localhost:3000";
    public static final String URLTARJETASPERSONAL="http://localhost:3000";
    public static final String URLPERSONALPUERTA="http://localhost:3000";
    public static final String URLINFOPERSONAL="http://localhost:3000";
    public static final String URLACTUALIZARPUERTAS="http://localhost:3000";
    public static final String URLACTUALIZARCHECADAS="http://localhost:3000";

    @FormUrlEncoded
    @GET("ValidarEmpleado")
    Call<String> getValidarEmpleado(@QueryMap Map<String, String> parametros);

    @GET("ObtenerTarjetasPersonal")
    Call<List<tarjetasPersonal>> getTarjetasPersonal(@QueryMap Map<String, String> parametros);

    @GET("ObtenerPersonalPuerta")
    Call<List<realmPersonalPuerta>> getPersonalPuerta();

    @GET("ObtenerInfoPersonal")
    Call<List<realmPersonalInfo>> getPersonalInfo();

    @GET("ActualizarPuertas")
    Call<List<puertas>> getActualizarPuertas();

    @POST("ActualizarChecadas")
    Call<List<String>> getActualizarChecadas(@Body List<oChecada> aChecadas);


/*con parametros
    @FormUrlEncoded
    @GET("/gomovil/movil.php")
    Call<List<realmPersonalPuerta>> getPersonalPuerta(
            @Field("tipo")String tipo,
            @Field("tipoNegocioN") String neg
    );*/

}
