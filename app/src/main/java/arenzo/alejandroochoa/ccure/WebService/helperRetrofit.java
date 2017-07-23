package arenzo.alejandroochoa.ccure.WebService;

import android.app.Activity;
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
import arenzo.alejandroochoa.ccure.Fragments.sincronizacion;
import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.Modelos.agrupadorPuerta;
import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.respuestaChecadas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
import arenzo.alejandroochoa.ccure.Modelos.usuario;
import arenzo.alejandroochoa.ccure.Modelos.validarEmpleado;
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
        Log.d(TAG, "ME CONFIGURÉ CON LA IP "+ url);
        configurarAdapterRetrofit(url);
    }

    private void configurarAdapterRetrofit(String url){
        adapterRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        helper = adapterRetrofit.create(retrofit.class);
    }

    public void ValidarEmpleado(final String NoEmpleado, String NoTarjeta, String ClavePuerta, final Context context, final ProgressDialog anillo, final ImageView imgFondoAcceso, final TextView txtResultadoChecada, final String idCaseta, final String numeroEmpleado, final String tipoChecada){
        Call<validarEmpleado> validarCall = helper.getValidarEmpleado(NoEmpleado, NoTarjeta, ClavePuerta);
        validarCall.enqueue(new Callback<validarEmpleado>() {
            @Override
            public void onResponse(Call<validarEmpleado> call, Response<validarEmpleado> response) {
                if (!response.isSuccessful()){
                    return;
                }
                validarEmpleado resultado = response.body();
                Log.d(TAG, "onResponse: "+resultado);
                anillo.dismiss();
                if(resultado.getRespuesta().equals("PERMITIDO")){
                    new checadas().mostrarAlertaEmpleadoValidado(context, txtResultadoChecada, imgFondoAcceso, resultado, idCaseta, numeroEmpleado, tipoChecada);
                }
                else {
                    imgFondoAcceso.setColorFilter(Color.parseColor("#ffcc0000"));
                    txtResultadoChecada.setText("Acceso Denegado");
                    checadas.vibrarCelular(context);
                    new checadas().guardarResultadoChecadaNoEncontrado(NoEmpleado, context, idCaseta, numeroEmpleado, tipoChecada);
                }
            }

            @Override
            public void onFailure(Call<validarEmpleado> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA ValidarEmpleado FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void ObtenerTarjetasPersonal(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<usuario>> tarjetasCall = helper.getTarjetasPersonal("O");
        tarjetasCall.enqueue(new Callback<List<usuario>>() {
            @Override
            public void onResponse(Call<List<usuario>> call, Response<List<usuario>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<usuario> aTarjetasPersonal = response.body();
                Log.d(TAG, "OBTUVE TARJETAS PERSONAL "+aTarjetasPersonal.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarTarjetasPersonal(aTarjetasPersonal)){
                    obtenerPersonalPuerta(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA ObtenerTarjetasPersonal FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });

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
                Log.d(TAG, "OBTUVE PERSONAL PUERTA "+ aPersonalPuerta.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarPersonalPuerta(aPersonalPuerta)){
                    obtenerUsuarios(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalPuerta FALLO: "+t.getMessage());
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
                if (RealmController.getInstance().insertarInfoPersonal(aPersonalInfo)){
                    ObtenerTarjetasPersonal(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalInfo FALLO: "+t.getMessage());
                obtenerPersonalInfo(context,anillo, mostrarPrimerPantalla);
            }
        });
    }

    public void obtenerUsuarios(final Context context, final ProgressDialog anillo, final boolean mostrarPrimerPantalla){
        Call<List<usuario>> usuariosCall = helper.getUsuario("G");
        usuariosCall.enqueue(new Callback<List<usuario>>() {
            @Override
            public void onResponse(Call<List<usuario>> call, Response<List<usuario>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<usuario> aUsuarios = response.body();
                Log.d(TAG, "OBTUVE USUARIOS " + aUsuarios.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarUsuarios(aUsuarios)){
                    actualizarAgrupadorPuerta(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerUsuarios FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarAgrupadores(final Activity activity, final ProgressDialog anillo, final Spinner spPuertasUnico){
        Call<List<agrupador>> puertasCall = helper.getAgrupadores();
        puertasCall.enqueue(new Callback<List<agrupador>>() {
            @Override
            public void onResponse(Call<List<agrupador>> call, Response<List<agrupador>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<agrupador> aAgrupadores = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR AGRUPADORES "+aAgrupadores.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarAgrupador(aAgrupadores)){
                    actualizarPuertas(activity, anillo, spPuertasUnico, aAgrupadores);
                }
            }

            @Override
            public void onFailure(Call<List<agrupador>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarAgrupadores FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }


    public void actualizarPuertas(final Activity activity, final ProgressDialog anillo, final Spinner spPuertasUnico, final List<agrupador> aAgrupadores){
        Call<List<puertas>> puertasCall = helper.getActualizarPuertas();
        puertasCall.enqueue(new Callback<List<puertas>>() {
            @Override
            public void onResponse(Call<List<puertas>> call, Response<List<puertas>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<puertas> aPersonalPuerta = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR PUERTAS "+aPersonalPuerta.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarPuertas(aPersonalPuerta)){
                    actualizarAgrupadorPuertaInicio(activity, anillo, spPuertasUnico, aAgrupadores);
                    anillo.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarPuertas FALLO: "+t.getMessage());
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
                Realm.getDefaultInstance();
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
                Log.e(TAG, "LA CONSULTA actualizarAgrupadorPuerta FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarAgrupadorPuertaInicio(final Activity activity, final ProgressDialog anillo, final Spinner spPuertasUnico, final List<agrupador> aAgrupadores){
        Call<List<agrupadorPuerta>> puertasCall = helper.getAgrupadorPuerta();
        puertasCall.enqueue(new Callback<List<agrupadorPuerta>>() {
            @Override
            public void onResponse(Call<List<agrupadorPuerta>> call, Response<List<agrupadorPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<agrupadorPuerta> aAgrupadorPuerta = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR AGRUPADOR PUERTA INICIO "+aAgrupadorPuerta.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarAgrupadorPuerta(aAgrupadorPuerta)){
                    llenarSpinnerAgrupador(activity.getApplicationContext(), aAgrupadores, spPuertasUnico);
                    anillo.dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<agrupadorPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarAgrupadorPuertaInicio FALLO: "+t.getMessage());
                anillo.dismiss();
            }
        });
    }

    public void actualizarChecadas(String NoEmpleado, String NoTarjeta, String PueClave, String fecha, final int totalPeticiones, final int numeroPeticion, final Context context, final ProgressDialog anillo, final String faseIngreso) {
        Call<List<respuestaChecadas>> checadasCall = helper.getActualizarChecadas(NoEmpleado, NoTarjeta, PueClave, fecha, faseIngreso);
        Log.d(TAG, "HICE LA PETICION ");
        checadasCall.enqueue(new Callback<List<respuestaChecadas>>() {
            @Override
            public void onResponse(Call<List<respuestaChecadas>> call, Response<List<respuestaChecadas>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                //List<respuestaChecadas>aRespuestaChecadas = response.body();
                if (totalPeticiones == numeroPeticion){
                    if (new sincronizacion().borrarTablasSincronizacion()){
                        actualizarPuertasSincronizacion(context, anillo);
                    }else
                        new sincronizacion().resultadoDialog("Ocurrió un error al sincronizar los datos, intentelo de nuevo.", context);
                }
            }

            @Override
            public void onFailure(Call<List<respuestaChecadas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarChecadas FALLO: " + t.getMessage());
            }
        });
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

    public void actualizarPuertasSincronizacion(final Context context, final ProgressDialog anillo){
        Call<List<puertas>> puertasCall = helper.getActualizarPuertas();
        puertasCall.enqueue(new Callback<List<puertas>>() {
            @Override
            public void onResponse(Call<List<puertas>> call, Response<List<puertas>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<puertas> aPuertas = response.body();
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarPuertas(aPuertas)){
                    obtenerPersonalPuertaSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarPuertasSincronizacion FALLO: "+t.getMessage());
                actualizarPuertasSincronizacion(context, anillo);
            }
        });
    }

    public void obtenerPersonalPuertaSincronizacion(final Context context, final ProgressDialog anillo){
        Call<List<personalPuerta>> personalCall = helper.getPersonalPuerta();
        personalCall.enqueue(new Callback<List<personalPuerta>>() {
            @Override
            public void onResponse(Call<List<personalPuerta>> call, Response<List<personalPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalPuerta> aPersonalPuerta = response.body();
                Log.d(TAG, "OBTUVE PERSONAL PUUERTA "+ aPersonalPuerta.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarPersonalPuerta(aPersonalPuerta)){
                    ObtenerTarjetasPersonalSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalPuertaSincronizacion FALLO: "+t.getMessage());
                obtenerPersonalPuertaSincronizacion(context, anillo);
            }
        });
    }

    public void ObtenerTarjetasPersonalSincronizacion(final Context context, final ProgressDialog anillo){
        Call<List<usuario>> tarjetasCall = helper.getTarjetasPersonal("O");
        tarjetasCall.enqueue(new Callback<List<usuario>>() {
            @Override
            public void onResponse(Call<List<usuario>> call, Response<List<usuario>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<usuario> aTarjetasPersonal = response.body();
                Log.d(TAG, "OBTUVE OBTENER TARJETAS PERSONAL "+aTarjetasPersonal.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarTarjetasPersonal(aTarjetasPersonal)){
                    obtenerPersonalInfoSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA ObtenerTarjetasPersonalSincronizacion FALLO: "+t.getMessage());
                ObtenerTarjetasPersonalSincronizacion(context, anillo);
            }
        });

    }

    public void obtenerPersonalInfoSincronizacion(final Context context, final ProgressDialog anillo){
        Call<List<personalInfo>> personalCall = helper.getPersonalInfo();
        personalCall.enqueue(new Callback<List<personalInfo>>() {
            @Override
            public void onResponse(Call<List<personalInfo>> call, Response<List<personalInfo>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<personalInfo> aPersonalInfo = response.body();
                Log.d(TAG, "OBTUVE EL PERSONAL INFO "+aPersonalInfo.size());
                Realm.getDefaultInstance();
                if (RealmController.getInstance().insertarInfoPersonal(aPersonalInfo)){
                    anillo.dismiss();
                    new sincronizacion().resultadoDialog("El proceso ha finalizado correctamente. El dispositivo quedó actualizado con la información.", context);
                }
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalInfoSincronizacion FALLO: "+t.getMessage());
                obtenerPersonalInfoSincronizacion(context, anillo);
            }
        });
    }

}
