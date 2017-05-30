package arenzo.alejandroochoa.ccure.WebService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.nucleo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by AlejandroMissael on 09/05/2017.
 */

public class helperRetrofit {

    private final static String TAG = "helperRetrofit";

    private Retrofit adapterRetrofit;
    private retrofit helper;

    public helperRetrofit(String url) {
        configurarAdapterRetrofit(url);
    }

    private void configurarAdapterRetrofit(String url){
        adapterRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        helper = adapterRetrofit.create(retrofit.class);
    }


    public void ValidarEmpleado(String NoEmpleado, String NoTarjeta, String ClavePuerta, final Context context){
        Call<String> validarCall = helper.getValidarEmpleado(parametrosValidarEmpleado(NoEmpleado,NoTarjeta,ClavePuerta));
        validarCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()){
                    return;
                }
                String resultado = response.body();
                //TODO TRABAJAR LA RESPUESTA DEL WEB SERVICE, VER VALIDAREMPLEADO
                //TODO GUARDAR EL RESULTADO Y MOSTRAR EL NUCLEO
                if(resultado == "PERMITIDO"){
                    Intent intent = new Intent(context, nucleo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    //intent.putExtra()
                    context.startActivity(intent);
                }
                else
                    Toast.makeText(context, "No se encontró el usuario", Toast.LENGTH_SHORT).show();;
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    private Map<String, String> parametrosValidarEmpleado(String NoEmpleado, String NoTarjeta, String ClavePuerta){
        Map<String, String> parametros = new HashMap<>();
        parametros.put("NoEmpleado", NoEmpleado);
        parametros.put("NoTarjeta", NoTarjeta);
        parametros.put("ClavePuerta", ClavePuerta);
        return parametros;

    }

    public void ObtenerTarjetasPersonal(String tipo){
        Call<List<tarjetasPersonal>> tarjetasCall = helper.getTarjetasPersonal(parametrosTarjetasPersonal(tipo));
        tarjetasCall.enqueue(new Callback<List<tarjetasPersonal>>() {
            @Override
            public void onResponse(Call<List<tarjetasPersonal>> call, Response<List<tarjetasPersonal>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<tarjetasPersonal> aTarjetasPersonal = response.body();

                //TODO TERMINAR EL METODO, VER OBTENER TARJETA PERSONAL
            }

            @Override
            public void onFailure(Call<List<tarjetasPersonal>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });

    }

    private Map<String, String> parametrosTarjetasPersonal(String tipo){
        Map<String, String> parametros = new HashMap<>();
        parametros.put("Tipo", tipo);
        return parametros;
    }

    private void obtenerPersonalPuerta(){
        Call<List<realmPersonalPuerta>> personalCall = helper.getPersonalPuerta();
        personalCall.enqueue(new Callback<List<realmPersonalPuerta>>() {
            @Override
            public void onResponse(Call<List<realmPersonalPuerta>> call, Response<List<realmPersonalPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<realmPersonalPuerta> aPersonalPuerta = response.body();
                //TODO TERMINAR EL METODO, VER OBTENER PERSONAL PUERTA
            }

            @Override
            public void onFailure(Call<List<realmPersonalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    private void obtenerPersonalInfo(){
        Call<List<realmPersonalInfo>> personalCall = helper.getPersonalInfo();
        personalCall.enqueue(new Callback<List<realmPersonalInfo>>() {
            @Override
            public void onResponse(Call<List<realmPersonalInfo>> call, Response<List<realmPersonalInfo>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<realmPersonalInfo> aPersonalPuerta = response.body();
                //TODO TERMINAR EL METODO, VER OBTENER INFO PERSONAL
            }

            @Override
            public void onFailure(Call<List<realmPersonalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    private void actualizarPuertas(){
        Call<List<puertas>> puertasCall = helper.getActualizarPuertas();
        puertasCall.enqueue(new Callback<List<puertas>>() {
            @Override
            public void onResponse(Call<List<puertas>> call, Response<List<puertas>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<puertas> aPersonalPuerta = response.body();
                //TODO TERMINAR EL METODO, VER ACTUALIZAR PUERTAS
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    private void actualizarChecadas(List<oChecada> aChecadas){
        Call<List<String>> checadasCall = helper.getActualizarChecadas(aChecadas);
        checadasCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<String> aChecadas = response.body();
                //TODO TERMINAR EL METODO, VER ACTUALIZAR CHECADAS
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });

    }

}
