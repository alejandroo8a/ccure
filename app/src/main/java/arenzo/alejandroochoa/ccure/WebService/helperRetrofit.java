package arenzo.alejandroochoa.ccure.WebService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Activities.nucleo;
import arenzo.alejandroochoa.ccure.Fragments.checadas;
import arenzo.alejandroochoa.ccure.R;
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


    public void ValidarEmpleado(String NoEmpleado, String NoTarjeta, String ClavePuerta, final Context context, final ProgressDialog anillo, final ImageView imgFondoAcceso, final TextView txtResultadoChecada){
        Call<String> validarCall = helper.getValidarEmpleado(NoEmpleado, NoTarjeta, ClavePuerta);
        Log.d(TAG, "HICE LA PETISION ");
        validarCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()){
                    return;
                }
                String resultado = response.body();
                //TODO TRABAJAR LA RESPUESTA DEL WEB SERVICE, VER VALIDAREMPLEADO
                //TODO GUARDAR EL RESULTADO Y MOSTRAR EL NUCLEO
                Log.d(TAG, "onResponse: "+resultado);
                anillo.dismiss();
                if(resultado.equals("PERMITIDO")){
                    new checadas().mostrarAlertaEmpleado(context, txtResultadoChecada, imgFondoAcceso);
                    /*Intent intent = new Intent(context, nucleo.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    //intent.putExtra()
                    context.startActivity(intent);*/
                }
                else {
                    txtResultadoChecada.setText("Acceso Denegado");
                    imgFondoAcceso.setColorFilter(Color.parseColor("#ff669900"));
                    checadas.vibrarCelular(context);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    private Map<String, String> parametrosValidarEmpleado(String NoEmpleado, String NoTarjeta, String ClavePuerta){
        Map<String, String> parametros = new HashMap<>();
        parametros.put("noEmpleado", NoEmpleado);
        parametros.put("noTarjeta", NoTarjeta);
        parametros.put("clavePuerta", ClavePuerta);
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

    public void obtenerPersonalPuerta(){
        Call<List<personalPuerta>> personalCall = helper.getPersonalPuerta();
        personalCall.enqueue(new Callback<List<personalPuerta>>() {
            @Override
            public void onResponse(Call<List<personalPuerta>> call, Response<List<personalPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalPuerta> aPersonalPuerta = response.body();
                Log.d(TAG, "EL tamaño personal puerta es: "+ aPersonalPuerta.size());
                //TODO TERMINAR EL METODO, VER OBTENER PERSONAL PUERTA
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    public void obtenerPersonalInfo(){
        Call<List<personalInfo>> personalCall = helper.getPersonalInfo();
        personalCall.enqueue(new Callback<List<personalInfo>>() {
            @Override
            public void onResponse(Call<List<personalInfo>> call, Response<List<personalInfo>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalInfo> aPersonalInfo = response.body();
                Log.d(TAG, "EL tamaño es: "+ aPersonalInfo.size());
                //TODO TERMINAR EL METODO, VER OBTENER INFO PERSONAL
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });
    }

    public void actualizarPuertas(){
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

    public void actualizarChecadas(List<oChecada> aChecadas){
        Call<List<String>> checadasCall = helper.getActualizarChecadas(aChecadas);
        Log.d(TAG, "HICE LA PETICION ");
        checadasCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                Log.d(TAG, "onResponse: "+response.body().toString());
                //TODO TERMINAR EL METODO, VER ACTUALIZAR CHECADAS
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
            }
        });

    }

}
