package arenzo.alejandroochoa.ccure.WebService;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Activities.main;
import arenzo.alejandroochoa.ccure.Fragments.checadas;
import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.Modelos.agrupadorPuerta;
import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.respuestaChecadas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import io.realm.Realm;
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

    public void ValidarEmpleado(final String NoEmpleado, String NoTarjeta, String ClavePuerta, final Context context, final ProgressDialog anillo, final ImageView imgFondoAcceso, final TextView txtResultadoChecada, final realmPersonalPuerta personal){
        Call<String> validarCall = helper.getValidarEmpleado(NoEmpleado, NoTarjeta, ClavePuerta);
        Log.d(TAG, "HICE LA PETISION ");
        validarCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (!response.isSuccessful()){
                    return;
                }
                String resultado = response.body();
                Log.d(TAG, "onResponse: "+resultado);
                anillo.dismiss();
                if(resultado.equals("PERMITIDO")){
                    realmPersonalInfo detallesPersonal = new realmPersonalInfo();
                    detallesPersonal.setNoEmpleado(NoEmpleado);
                    detallesPersonal.setNombre("Sin nombre");
                    detallesPersonal.setFoto("NO");
                    new checadas().mostrarAlertaEmpleado(context, txtResultadoChecada, imgFondoAcceso, detallesPersonal, personal);
                }
                else {
                    txtResultadoChecada.setText("Acceso Denegado");
                    imgFondoAcceso.setColorFilter(Color.parseColor("#ff669900"));
                    checadas.vibrarCelular(context);
                    new checadas().guardarResultadoChecadaNoEncontrado(NoEmpleado, context);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void ObtenerTarjetasPersonal(String tipo, final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<tarjetasPersonal>> tarjetasCall = helper.getTarjetasPersonal(tipo);
        tarjetasCall.enqueue(new Callback<List<tarjetasPersonal>>() {
            @Override
            public void onResponse(Call<List<tarjetasPersonal>> call, Response<List<tarjetasPersonal>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<tarjetasPersonal> aTarjetasPersonal = response.body();
                Log.d(TAG, "OBTUVE OBTENER TARJETAS PERSONAL "+aTarjetasPersonal.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarTarjetasPersonal(aTarjetasPersonal)){
                    obtenerPersonalPuerta(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<tarjetasPersonal>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });

    }

    private Map<String, String> parametrosTarjetasPersonal(String tipo){
        Map<String, String> parametros = new HashMap<>();
        parametros.put("Tipo", tipo);
        return parametros;
    }

    public void obtenerPersonalPuerta(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<personalPuerta>> personalCall = helper.getPersonalPuerta();
        personalCall.enqueue(new Callback<List<personalPuerta>>() {
            @Override
            public void onResponse(Call<List<personalPuerta>> call, Response<List<personalPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalPuerta> aPersonalPuerta = response.body();
                Log.d(TAG, "OBTUVE PERSONAL PUUERTA "+ aPersonalPuerta.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarPersonalPuerta(aPersonalPuerta)){
                    actualizarPuertas(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void obtenerPersonalInfo(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<personalInfo>> personalCall = helper.getPersonalInfo();
        personalCall.enqueue(new Callback<List<personalInfo>>() {
            @Override
            public void onResponse(Call<List<personalInfo>> call, Response<List<personalInfo>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalInfo> aPersonalInfo = response.body();
                Log.d(TAG, "OBTUVE EL PERSONAL INFO "+aPersonalInfo.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarInfoPersonal(aPersonalInfo)){
                    ObtenerTarjetasPersonal("Ggit s", context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarAgrupadores(final Context context, final ProgressDialog anillo, final Spinner spPuertasUnico){
        Call<List<agrupador>> puertasCall = helper.getAgrupadores();
        puertasCall.enqueue(new Callback<List<agrupador>>() {
            @Override
            public void onResponse(Call<List<agrupador>> call, Response<List<agrupador>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<agrupador> aAgrupadores = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR AGRUPADORES "+aAgrupadores.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarAgrupador(aAgrupadores)){
                    llenarSpinnerAgrupador(context, aAgrupadores, spPuertasUnico);
                    anillo.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<agrupador>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }


    public void actualizarPuertas(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<puertas>> puertasCall = helper.getActualizarPuertas();
        puertasCall.enqueue(new Callback<List<puertas>>() {
            @Override
            public void onResponse(Call<List<puertas>> call, Response<List<puertas>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<puertas> aPersonalPuerta = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR PUERTAS "+aPersonalPuerta.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarPuertas(aPersonalPuerta)){
                    actualizarAgrupadorPuerta(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarAgrupadorPuerta(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<agrupadorPuerta>> puertasCall = helper.getAgrupadorPuerta();
        puertasCall.enqueue(new Callback<List<agrupadorPuerta>>() {
            @Override
            public void onResponse(Call<List<agrupadorPuerta>> call, Response<List<agrupadorPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<agrupadorPuerta> aAgrupadorPuerta = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR AGRUPADOR PUERTA "+aAgrupadorPuerta.size());
                Realm.getInstance(context);
                if (RealmController.getInstance().insertarAgrupadorPuerta(aAgrupadorPuerta)){
                    anillo.dismiss();
                    if (mostrarPrimerPantalla){
                        Intent intent = new Intent(context, main.class);
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<agrupadorPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarChecadas(String NoEmpleado, String NoTarjeta, String PueClave) {
        Call<List<respuestaChecadas>> checadasCall = helper.getActualizarChecadas(NoEmpleado, NoTarjeta, PueClave, "12-03-2017");
        Log.d(TAG, "HICE LA PETICION ");
        checadasCall.enqueue(new Callback<List<respuestaChecadas>>() {
            @Override
            public void onResponse(Call<List<respuestaChecadas>> call, Response<List<respuestaChecadas>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<respuestaChecadas>aRespuestaChecadas = response.body();
                Log.d(TAG, "onResponse: " + aRespuestaChecadas.get(0).getMessage());
            }

            @Override
            public void onFailure(Call<List<respuestaChecadas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA FALLO: " + t.getMessage());
            }
        });
    }

    private String obtenerFecha(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }

    private void llenarSpinnerAgrupador(Context context, List<agrupador> aAgrupadores, final Spinner spPuertasUnico){
        ArrayList<String> aAgrupadoresDescripcion = new ArrayList<>();
        for (agrupador agrupador : aAgrupadores){
            aAgrupadoresDescripcion.add(agrupador.getDescripcion());
        }
        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_item, aAgrupadoresDescripcion);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPuertasUnico.setAdapter(adapter);
    }


}
