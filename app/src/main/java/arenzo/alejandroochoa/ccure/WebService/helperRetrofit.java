package arenzo.alejandroochoa.ccure.WebService;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import arenzo.alejandroochoa.ccure.Activities.configuracionUnica;
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
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import io.realm.Realm;
import io.realm.RealmResults;
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
    private RealmController realmController;

    public helperRetrofit(String url) {
        Log.d(TAG, "ME CONFIGURÉ CON LA IP "+ url);
        configurarAdapterRetrofit(url);
        configurarRealm();
    }

    private void configurarAdapterRetrofit(String url){
        adapterRetrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        helper = adapterRetrofit.create(retrofit.class);
    }

    private void configurarRealm(){
        realmController = new RealmController();
    }

    public void ValidarEmpleadoManual(final String NoEmpleado, final String NoTarjeta, final String puertaClave, final Context context, final ProgressDialog anillo, final ImageView imgFondoAcceso, final TextView txtResultadoChecada, final String numeroEmpleado, final String tipoChecada, final TextView txtNombre, final TextView txtPuestoEmpresa, final ImageView imgFotoPerfil, final View view, final checadas checadas) {
        Call<validarEmpleado> validarCall = helper.getValidarEmpleado(NoEmpleado, NoTarjeta, puertaClave);
        validarCall.enqueue(new Callback<validarEmpleado>() {
            @Override
            public void onResponse(Call<validarEmpleado> call, Response<validarEmpleado> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                validarEmpleado personal = response.body();
                anillo.dismiss();
                if (personal.getRespuesta().equals("PERMITIDO")) {
                    checadas.mostrarAlertaEmpleadoValidadoManual(context, txtResultadoChecada, imgFondoAcceso, personal, puertaClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, view);
                } else {
                    imgFondoAcceso.setColorFilter(Color.parseColor("#ffcc0000"));
                    txtResultadoChecada.setText("Acceso Denegado");
                    checadas.noEmpleado = "";
                    checadas.vibrarCelular(context);
                    if(personal.getEmpleado() == null|| personal.getEmpleado().getNombre().equals(""))
                        checadas.buscarEnValidacionesYaValidadoManual(NoEmpleado, puertaClave, numeroEmpleado, tipoChecada, view);
                    else
                        checadas.guardarResultadoChecadaValidadaManual(personal, "D", puertaClave, numeroEmpleado, tipoChecada,txtNombre, txtPuestoEmpresa, imgFotoPerfil, view);

                }
            }

            @Override
            public void onFailure(Call<validarEmpleado> call, Throwable t) {
                checadas.buscarEnValidacionesYaValidadoManual(NoEmpleado, puertaClave, numeroEmpleado, tipoChecada, view);
                Toast.makeText(context,"No se pudo conectar con el servidor: ValidarEmpleadoManual", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void ValidarEmpleadoRfid(final String NoEmpleado, final String NoTarjeta, final String puertaClave, final Context context, final ProgressDialog anillo, final ImageView imgFondoAcceso, final TextView txtResultadoChecada, final String idCaseta, final String numeroEmpleado, final String tipoChecada, final TextView txtNombre, final TextView txtPuestoEmpresa, final ImageView imgFotoPerfil, final checadas checadas) {
        Call<validarEmpleado> validarCall = helper.getValidarEmpleado(NoEmpleado, NoTarjeta, puertaClave);
        validarCall.enqueue(new Callback<validarEmpleado>() {
            @Override
            public void onResponse(Call<validarEmpleado> call, Response<validarEmpleado> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                validarEmpleado resultado = response.body();
                Log.d(TAG, "onResponse: " + resultado);
                anillo.dismiss();
                if (resultado.getRespuesta().equals("PERMITIDO")) {
                    checadas.guardarResultadoChecadaValidadaRfid(resultado, "P", puertaClave, numeroEmpleado, tipoChecada,txtNombre, txtPuestoEmpresa, imgFotoPerfil, NoTarjeta);
                } else {
                    if(resultado.getEmpleado() == null || resultado.getEmpleado().getNombre().equals(""))
                        checadas.buscarEnValidacionesYaValidadoRfid(NoTarjeta,puertaClave, numeroEmpleado, tipoChecada);
                    else {
                        imgFondoAcceso.setColorFilter(Color.parseColor("#ffcc0000"));
                        txtResultadoChecada.setText("Acceso Denegado");
                        checadas.vibrarCelular(context);
                        checadas.guardarResultadoChecadaValidadaRfid(resultado, "D", puertaClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, NoTarjeta);
                    }
                }
            }

            @Override
            public void onFailure(Call<validarEmpleado> call, Throwable t) {
                Toast.makeText(context,"No se pudo conectar con el servidor: ValidarEmpleadoRfid", Toast.LENGTH_SHORT).show();
                checadas.buscarEnValidacionesYaValidadoRfid(NoTarjeta,puertaClave, numeroEmpleado, tipoChecada);
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
                if (realmController.insertarTarjetasPersonal(aTarjetasPersonal)){
                    obtenerPersonalPuerta(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA ObtenerTarjetasPersonal FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: ObtenerTarjetasPersonal", Toast.LENGTH_SHORT).show();
                ObtenerTarjetasPersonal(context, anillo, mostrarPrimerPantalla);
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
                if (realmController.insertarPersonalPuerta(aPersonalPuerta)){
                    obtenerUsuarios(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalPuerta FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: obtenerPersonalPuerta", Toast.LENGTH_SHORT).show();
                obtenerPersonalPuerta(context, anillo, mostrarPrimerPantalla);
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
                if (realmController.insertarInfoPersonal(aPersonalInfo)){
                    ObtenerTarjetasPersonal(context, anillo, mostrarPrimerPantalla);
                }
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalInfo FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: obtenerPersonalInfo", Toast.LENGTH_SHORT).show();
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
                if (realmController.insertarUsuarios(aUsuarios)){
                    anillo.dismiss();
                    if (mostrarPrimerPantalla){
                        Intent intent = new Intent(context, main.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerUsuarios FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: obtenerUsuarios", Toast.LENGTH_SHORT).show();
                obtenerUsuarios(context, anillo, mostrarPrimerPantalla);
            }
        });
    }

    public void actualizarAgrupadores(final configuracionUnica activity, final ProgressDialog anillo, final Spinner spPuertasUnico, final AlertDialog alerta, final SharedPreferences PREF_CONFIGURACION_UNICA){
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
                if (realmController.insertarAgrupador(aAgrupadores)){
                    actualizarPuertas(activity, anillo, spPuertasUnico, aAgrupadores, alerta, PREF_CONFIGURACION_UNICA);
                }
            }

            @Override
            public void onFailure(Call<List<agrupador>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarAgrupadores FALLO: "+t.getMessage());
                anillo.dismiss();
                Toast.makeText(activity, "Error en conexión con el servidor. Intentelo de nuevo y asegurese que cuente con conexión a internet", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void actualizarPuertas(final configuracionUnica activity, final ProgressDialog anillo, final Spinner spPuertasUnico, final List<agrupador> aAgrupadores, final AlertDialog alerta, final SharedPreferences PREF_CONFIGURACION_UNICA){
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
                if (realmController.insertarPuertas(aPersonalPuerta)){
                    alerta.dismiss();
                    actualizarAgrupadorPuertaInicio(activity, anillo, spPuertasUnico, aAgrupadores, PREF_CONFIGURACION_UNICA);
                }
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarPuertas FALLO: "+t.getMessage());
                anillo.dismiss();
                Toast.makeText(activity, "Error en conexión con el servidor. Intentelo de nuevo y asegurese que cuente con conexión a internet", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void actualizarAgrupadorPuertaInicio(final configuracionUnica activity, final ProgressDialog anillo, final Spinner spPuertasUnico, final List<agrupador> aAgrupadores, final SharedPreferences PREF_CONFIGURACION_UNICA){
        Call<List<agrupadorPuerta>> puertasCall = helper.getAgrupadorPuerta();
        puertasCall.enqueue(new Callback<List<agrupadorPuerta>>() {
            @Override
            public void onResponse(Call<List<agrupadorPuerta>> call, Response<List<agrupadorPuerta>> response) {
                if (!response.isSuccessful()){
                    return;
                }
                List<agrupadorPuerta> aAgrupadorPuerta = response.body();
                Log.d(TAG, "OBTUVE ACTUALIZAR AGRUPADOR PUERTA INICIO "+aAgrupadorPuerta.size());
                if (realmController.insertarAgrupadorPuerta(aAgrupadorPuerta)){
                    llenarSpinnerAgrupador(activity.getApplicationContext(), aAgrupadores, spPuertasUnico);
                    anillo.dismiss();
                    if (PREF_CONFIGURACION_UNICA.getBoolean("YACONFIGURADO", false))
                        activity.guardarDatos();
                }
            }

            @Override
            public void onFailure(Call<List<agrupadorPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarAgrupadorPuertaInicio FALLO: "+t.getMessage());
                Toast.makeText(activity.getApplicationContext(),"No se pudo conectar con el servidor: actualizarAgrupadorPuertaInicio", Toast.LENGTH_SHORT).show();
                actualizarAgrupadorPuertaInicio(activity, anillo, spPuertasUnico, aAgrupadores, PREF_CONFIGURACION_UNICA);
            }
        });
    }

    public void actualizarChecadas(final String NoEmpleado, final String NoTarjeta, final String PueClave, final String fecha, final int totalPeticiones, final int numeroPeticion, final Context context, final ProgressDialog anillo, final String faseIngreso, final RealmController realmController) {
        Call<List<respuestaChecadas>> checadasCall = helper.getActualizarChecadas(NoEmpleado, NoTarjeta, PueClave, fecha, faseIngreso);
        checadasCall.enqueue(new Callback<List<respuestaChecadas>>() {
            @Override
            public void onResponse(Call<List<respuestaChecadas>> call, Response<List<respuestaChecadas>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "El servidor no tiene los parametros necesarios para sincronizar", Toast.LENGTH_SHORT).show();
                    return;
                }
                realmController.actualizarChecadaEnviada(fecha);
                if (totalPeticiones == numeroPeticion){
                    RealmResults<realmESPersonal> aPersonal = realmController.obtenerRegistros();
                    if (aPersonal.size() > 0) {
                        anillo.dismiss();
                        new sincronizacion().resultadoDialog("No se sincronizaron todos los datos, intentelo de nuevo.", context);
                    }else {
                        realmController.borrarTablasSincronizacionRed();
                        actualizarPuertasSincronizacion(context, anillo);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<respuestaChecadas>> call, Throwable t) {
                Toast.makeText(context,"No se pudo conectar con el servidor: actualizarChecadas", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void actualizarChecadasReposo(final String NoEmpleado, final String NoTarjeta, final String PueClave, String fecha, final Context context, final String faseIngreso, final RealmController realmController) {
        Call<List<respuestaChecadas>> checadasCall = helper.getActualizarChecadas(NoEmpleado, NoTarjeta, PueClave, fecha, faseIngreso);
        Log.d(TAG, "HICE LA PETICION ");
        checadasCall.enqueue(new Callback<List<respuestaChecadas>>() {
            @Override
            public void onResponse(Call<List<respuestaChecadas>> call, Response<List<respuestaChecadas>> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(context, "El servidor no tiene los parametros necesarios para sincronizar", Toast.LENGTH_SHORT).show();
                    return;
                }
                new checadas().eliminarChecadaPersonal(NoEmpleado, NoTarjeta, PueClave, realmController);
            }

            @Override
            public void onFailure(Call<List<respuestaChecadas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarChecadas FALLO: " + t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: actualizarChecadasReposo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void llenarSpinnerAgrupador(Context context, List<agrupador> aAgrupadores, final Spinner spPuertasUnico){
        ArrayList<String> aAgrupadoresDescripcion = new ArrayList<>();
        for (agrupador agrupador : aAgrupadores){
            aAgrupadoresDescripcion.add(agrupador.getDescripcion());
        }
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.item_spinner, aAgrupadoresDescripcion);
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
                Log.d(TAG, "OBTUVE PUERTAS "+ aPuertas.size());
                if (realmController.insertarPuertas(aPuertas)){
                    obtenerPersonalPuertaSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<puertas>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA actualizarPuertasSincronizacion FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: actualizarPuertasSincronizacion", Toast.LENGTH_SHORT).show();
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
                if (realmController.insertarPersonalPuerta(aPersonalPuerta)){
                    ObtenerTarjetasPersonalSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<personalPuerta>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalPuertaSincronizacion FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: obtenerPersonalPuertaSincronizacion", Toast.LENGTH_SHORT).show();
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
                if (realmController.insertarTarjetasPersonal(aTarjetasPersonal)){
                    obtenerPersonalInfoSincronizacion(context, anillo);
                }
            }

            @Override
            public void onFailure(Call<List<usuario>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA ObtenerTarjetasPersonalSincronizacion FALLO: "+t.getMessage());
                Toast.makeText(context,"No se pudo conectar con el servidor: ObtenerTarjetasPersonalSincronizacion", Toast.LENGTH_SHORT).show();
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
                if (realmController.insertarInfoPersonal(aPersonalInfo)){
                    anillo.dismiss();
                    new sincronizacion().resultadoDialog("El proceso ha finalizado correctamente. El dispositivo quedó actualizado con la información.", context);
                }
            }

            @Override
            public void onFailure(Call<List<personalInfo>> call, Throwable t) {
                Log.e(TAG, "LA CONSULTA obtenerPersonalInfoSincronizacion FALLO: "+t.getCause().toString());
                Toast.makeText(context,"No se pudo conectar con el servidor: obtenerPersonalInfoSincronizacion", Toast.LENGTH_SHORT).show();
                obtenerPersonalInfoSincronizacion(context, anillo);
            }
        });
    }

}
