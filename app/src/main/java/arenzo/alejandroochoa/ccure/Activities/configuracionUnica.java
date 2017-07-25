package arenzo.alejandroochoa.ccure.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmAgrupador;
import arenzo.alejandroochoa.ccure.Realm.realmAgrupadorPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmDispositivo;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmNotificacion;
import arenzo.alejandroochoa.ccure.Realm.realmPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmUsuario;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import arenzo.alejandroochoa.ccure.WebService.retrofit;
import io.realm.Realm;
import io.realm.RealmResults;

public class configuracionUnica extends AppCompatActivity {
    private final static String TAG = "configuracionUnica";

    private EditText edtNombreDispositivoUnico, edtWebServiceUnico, edtURLExportacionUnico;
    private Spinner spPuertasUnico;
    private Button btnGuardarConfiguracionUnico;
    ProgressDialog anillo = null;
    private SharedPreferences PREF_CONFIGURACION_UNICA;
    private String URL;
    RealmController realmController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_unica);
        edtNombreDispositivoUnico = (EditText)findViewById(R.id.edtNombreDispositivoUnico);
        edtWebServiceUnico = (EditText)findViewById(R.id.edtWebServiceUnico);
        edtURLExportacionUnico = (EditText)findViewById(R.id.edtURLExportacionUnico);
        spPuertasUnico = (Spinner)findViewById(R.id.spPuertasUnico);
        btnGuardarConfiguracionUnico = (Button)findViewById(R.id.btnGuardarConfiguracionUnico);
        eventosVista();
        borrarDatos();
        cargarDatosVista();
        centrarTituloActionBar();
        PREF_CONFIGURACION_UNICA = getSharedPreferences("CCURE", getApplicationContext().MODE_PRIVATE);
        mostrarDialogUrl();
    }


    private void eventosVista(){
        btnGuardarConfiguracionUnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
    }

    private void cargarDatosVista(){
        realmController = new RealmController(getApplication());
        realmDispositivo dispositivo = realmController.obtenerDispositivo();
        if (dispositivo != null){
            edtNombreDispositivoUnico.setText(dispositivo.getDescripcion().toString());
            edtWebServiceUnico.setText(dispositivo.getURLWebService().toString());
            edtURLExportacionUnico.setText(dispositivo.getURLExportacion());
        }
    }

    private void borrarDatos() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(realmPuerta.class);
                realm.delete(realmPersonal.class);
                realm.delete(realmPersonalInfo.class);
                realm.delete(realmPersonalPuerta.class);
                realm.delete(realmESPersonal.class);
                realm.delete(realmNotificacion.class);
                realm.delete(realmAgrupador.class);
                realm.delete(realmAgrupadorPuerta.class);
                realm.delete(realmDispositivo.class);
                realm.delete(realmUsuario.class);
            }
        });
    }

    private void guardarDatos() {
        if (edtNombreDispositivoUnico.length() > 0 && edtURLExportacionUnico.length() > 0 && edtWebServiceUnico.length() > 0) {
            realmDispositivo dispositivo = realmController.obtenerDispositivo();
            final int idAgrupador = realmController.obtenerIdAgrupador(spPuertasUnico.getSelectedItem().toString());
            RealmResults<realmAgrupadorPuerta> aAgrupadorPuertas = realmController.obtenerAgrupadoresPuertas(idAgrupador);
            boolean saberEstadoInsercion;
            if (dispositivo == null) {
                saberEstadoInsercion = realmController.insertarConfiguracion(edtNombreDispositivoUnico.getText().toString(), "A", idAgrupador, edtWebServiceUnico.getText().toString(), edtURLExportacionUnico.getText().toString(), "CONFIGURACION");
            } else {
                saberEstadoInsercion = realmController.actualizarConfiguracion(edtNombreDispositivoUnico.getText().toString(), "A", idAgrupador, edtWebServiceUnico.getText().toString(), edtURLExportacionUnico.getText().toString(), "CONFIGURACION");
            }
            if (saberEstadoInsercion) {
                if (aAgrupadorPuertas.size() > 0){
                    realmPuerta puerta1 = realmController.obtenerPuerta(aAgrupadorPuertas.get(0).getPUEId());
                    realmPuerta puerta2 = realmController.obtenerPuerta(aAgrupadorPuertas.get(1).getPUEId());
                    guardarPuerta(puerta1, puerta2, idAgrupador);
                    RealmResults<realmPuerta> aPuertas = realmController.obtenerPuertas(aAgrupadorPuertas.get(0).getPUEId());
                    iterarPuertas(aPuertas);
                }
                else {
                    crearDialogError("Error", "Sus datos no se guardaron, el agrupador que seleccionó no contiene puertas.");
                    return;
                }
                guardarYaExisteConfiguracionUrlNombrePuerta(edtWebServiceUnico.getText().toString(), spPuertasUnico.getSelectedItem().toString());
                obtenerTodosDatos();
            } else {
                crearDialogError("Error", "Sus datos no se guardaron, intentelo de nuevo.");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Complete todos los campos para guardar", Toast.LENGTH_SHORT).show();
        }
    }




    private void crearDialogError(String titulo,String mensaje){
        AlertDialog.Builder dialog =  new AlertDialog.Builder(this);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void centrarTituloActionBar() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private void guardarYaExisteConfiguracionUrlNombrePuerta(String url, String nombrePuerta){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putBoolean("CONFIGURADO", true);
        editor.putString("URL", url);
        editor.putString("NOMBREPUERTA", nombrePuerta);
        editor.commit();
    }

    private void guardarURL(String url){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putString("URL", url);
        editor.commit();
    }

    private void guardarPuerta(realmPuerta puerta1, realmPuerta puerta2, int idAgrupador){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putString("NOMBREPUERTAENTRADA", puerta1.getDescripcion());
        editor.putString("NOMBREPUERTASALIDA", puerta2.getDescripcion());
        editor.putInt("IDPUERTAENTRADA", puerta1.getPUEId());
        editor.putInt("IDPUERTASALIDA", puerta2.getPUEId());
        editor.putString("CLAVEPUERTAENTRADA", puerta1.getPUEClave());
        editor.putString("CLAVEPUERTASALIDA", puerta2.getPUEClave());
        editor.putString("GRUIDENTRADA", puerta1.getGRUID());
        editor.putString("GRUIDSALIDA", puerta2.getGRUID());
        editor.putInt("IDAGRUPADOR", idAgrupador);
        editor.commit();
    }

    private void iterarPuertas(RealmResults<realmPuerta> aPuertas){
        for (int i = 0 ; i < aPuertas.size() ; i++){
            guardarGRUId(i, aPuertas.get(i));
        }
    }

    private void guardarGRUId(int total, realmPuerta puerta){
        total++;
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putString("GRUIDACTUAL"+total, puerta.getGRUID());
        editor.putInt("TOTALGRUID", total);
        editor.commit();
    }

    private void obtenerTodosDatos(){
        mostrarCargandoAnillo("Obteniendo todos los datos...");
        helperRetrofit helper = new helperRetrofit(PREF_CONFIGURACION_UNICA.getString("URL",""));
        helper.obtenerPersonalInfo(getApplicationContext(), this.anillo, true);
    }

    private void mostrarCargandoAnillo(String mensaje){
        this.anillo = ProgressDialog.show(this, "Sincronizando", mensaje, true, false);
    }

    private void obtenerAgrupadores(){
        mostrarCargandoAnillo("Obteniendo puertas...");
        helperRetrofit helperRetrofit = new helperRetrofit(URL);
        helperRetrofit.actualizarAgrupadores(this, anillo, spPuertasUnico);
    }

    private void mostrarDialogUrl(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_url, null);
        final EditText edtUrlDialog = view.findViewById(R.id.edtUrlDialog);

        builder.setTitle("Agregar URL")
                .setView(view)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ocultarTeclado();
                        guardarURL(edtUrlDialog.getText().toString());
                        URL = PREF_CONFIGURACION_UNICA.getString("URL","");
                        edtWebServiceUnico.setText(edtUrlDialog.getText().toString());
                        obtenerAgrupadores();
                    }
                })
                .setCancelable(false)
        .show();
    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }


}
