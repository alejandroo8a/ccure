package arenzo.alejandroochoa.ccure.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.Preference;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
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

    public void guardarDatos() {
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
                    guardarYaConfigurado();
                    iterarPuertas(aPuertas);
                }
                else {
                    crearDialogError("Error", "Sus datos no se guardaron, el agrupador que seleccionó no contiene puertas.");
                    return;
                }
                guardarYaExisteConfiguracionUrlNombrePuerta(edtWebServiceUnico.getText().toString(), spPuertasUnico.getSelectedItem().toString(), edtNombreDispositivoUnico.getText().toString());
                obtenerTodosDatos();
            } else {
                crearDialogError("Error", "Sus datos no se guardaron, intentelo de nuevo.");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Complete todos los campos para guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarYaConfigurado(){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putBoolean("YACONFIGURADO", true);
        editor.commit();
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

    private void guardarYaExisteConfiguracionUrlNombrePuerta(String url, String nombrePuerta, String nombreEquipo){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putBoolean("CONFIGURADO", true);
        editor.putString("NOMBREEQUIPO", nombreEquipo);
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

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }

    private void obtenerAgrupadores(AlertDialog alert){
        mostrarCargandoAnillo("Obteniendo puertas...");
        helperRetrofit helperRetrofit = new helperRetrofit(URL);
        helperRetrofit.actualizarAgrupadores(this, anillo, spPuertasUnico, alert, PREF_CONFIGURACION_UNICA);
    }

    private void mostrarDialogUrl(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_url, null);
        final EditText edtUrlDialog = view.findViewById(R.id.edtUrlDialog);
        String nombreBotonPositivo = configurarYaSincronizado(edtUrlDialog);
        builder.setTitle("Agregar URL")
                .setView(view)
                .setPositiveButton(nombreBotonPositivo, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setNeutralButton("Sincronizar por archivo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setCancelable(false);
        final AlertDialog alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
                guardarURL(edtUrlDialog.getText().toString());
                URL = PREF_CONFIGURACION_UNICA.getString("URL","");
                edtWebServiceUnico.setText(edtUrlDialog.getText().toString());
                obtenerAgrupadores(alert);
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
                sincronizarArchivo(alert);
            }
        });

    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void sincronizarArchivo(AlertDialog alerta){
        String archivo = leerArchivo();
        if(!archivo.equals("")) {
            mostrarCargandoAnillo("Sincronizando archivo...");
            String[] archivoSeparado = archivo.split("~");
            if (guardarPuertas(archivoSeparado[0]))
                if (guardarPersonalPuerta(archivoSeparado[1]))
                    if (guardarTarjetasPersonal(archivoSeparado[2]))
                        if (guardarPersonalInfo(archivoSeparado[3])) {
                            ocultarCargandoAnillo();
                            alerta.dismiss();
                            resultadoDialog("Éxito", "Terminó la sincronización por archivo, los datos se guardaron correctamente.");
                            return;
                        }
            ocultarCargandoAnillo();
            dialogErrorGuardadoDatosArchivo(alerta);
        }
    }

    private String leerArchivo(){
        File file = new File(Environment.getExternalStorageDirectory()+"/CCURE/baseDatos.txt");
        StringBuilder archivo = new StringBuilder();
        try{
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String linea;
            while((linea = bufferedReader.readLine()) != null){
                archivo.append(linea);
                archivo.append('\n');
            }
        }catch (IOException ex){
            Toast.makeText(this, "No se puede leer el archivo. Asegurese de que exista.", Toast.LENGTH_SHORT).show();
        }
        return archivo.toString();
    }

    private boolean guardarPuertas(String oPuertas){
        String[] aCantidadPuertas = oPuertas.split("\n");
        List<puertas> aPuertas = new ArrayList<>();
        for(int i = 0; i < aCantidadPuertas.length ; i++){
            String[] aElementosPuerta = aCantidadPuertas[i].split("-");
            puertas puerta = new puertas();
            puerta.setPUEId(Integer.parseInt(aElementosPuerta[0]));
            puerta.setPUEClave(aElementosPuerta[1]);
            puerta.setDescripcion(aElementosPuerta[2]);
            puerta.setFase(aElementosPuerta[3]);
            puerta.setGRUID(aElementosPuerta[4]);
            aPuertas.add(puerta);
        }
        return RealmController.getInstance().insertarPuertasArchivo(aPuertas, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
    }
    private boolean guardarPersonalPuerta(String personal){
        String[] aCantidadPersonalPuerta = personal.split("\n");
        List<personalPuerta> aPersonalPuerta = new ArrayList<>();
        for(int i = 0 ; i < aCantidadPersonalPuerta.length ; i++){
            String[] elementosPersonalPuerta = aCantidadPersonalPuerta[i].split("-");
            personalPuerta personalPuerta = new personalPuerta();
            personalPuerta.setNoEmpleado(elementosPersonalPuerta[0]);
            personalPuerta.setNoTarjeta(elementosPersonalPuerta[1]);
            personalPuerta.setGRUId(elementosPersonalPuerta[2]);
            aPersonalPuerta.add(personalPuerta);
        }
        return RealmController.getInstance().insertarPersonalPuertaArchivo(aPersonalPuerta, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
    }

    private boolean guardarTarjetasPersonal(String tarjetas){
        String[] aCantidadTarjetas = tarjetas.split("\n");
        List<tarjetasPersonal> aTarjetasPersonal = new ArrayList<>();
        for(int i = 0 ; i < aCantidadTarjetas.length ; i++){
            String[] elementosTarjetas = aCantidadTarjetas[i].split("-");
            tarjetasPersonal tarjetasPersonal = new tarjetasPersonal();
            tarjetasPersonal.setNoEmpleado(elementosTarjetas[0]);
            tarjetasPersonal.setNoTarjeta(elementosTarjetas[1]);
            tarjetasPersonal.setNombre(elementosTarjetas[2]);
            tarjetasPersonal.setEmpresa(elementosTarjetas[3]);
            tarjetasPersonal.setTipo(elementosTarjetas[4]);
            aTarjetasPersonal.add(tarjetasPersonal);
        }
        return RealmController.getInstance().insertarTarjetasPersonalArchivo(aTarjetasPersonal, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
    }

    private boolean guardarPersonalInfo(String personal){
        String[] aCantidadPersonal = personal.split("\n");
        List<personalInfo> aPersonalInfo = new ArrayList<>();
        for(int i = 0 ; i < aCantidadPersonal.length ; i++){
            String[] elementosPersonal = aCantidadPersonal[i].split("-");
            personalInfo personalInfo = new personalInfo();
            personalInfo.setFoto(elementosPersonal[0]);
            personalInfo.setNoEmpleado(elementosPersonal[1]);
            personalInfo.setNombre(elementosPersonal[2]);
            personalInfo.setPuesto(elementosPersonal[3]);
            aPersonalInfo.add(personalInfo);
        }
        return RealmController.getInstance().insertarInfoPersonalArchivo(aPersonalInfo, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
    }

    private void dialogErrorGuardadoDatosArchivo(final AlertDialog alerta){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("ERROR")
                .setMessage("Ocurrió un error al sincronizar el archivo, ¿desea volver a intentarlo?")
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sincronizarArchivo(alerta);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void resultadoDialog(String titulo, String mensaje){
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saberYaSincronizado();
                    }
                })
                .show();
    }

    private void saberYaSincronizado(){
        if(PREF_CONFIGURACION_UNICA.getBoolean("YACONFIGURADO", false))
            finish();
        else{
            Intent intent = new Intent(this, main.class);
            startActivity(intent);
            finish();
        }
    }

    private String configurarYaSincronizado(EditText edtUrlDialog){
        if (PREF_CONFIGURACION_UNICA.getBoolean("YACONFIGURADO", false)) {
            edtUrlDialog.setVisibility(View.GONE);
            edtNombreDispositivoUnico.setText(PREF_CONFIGURACION_UNICA.getString("NOMBREEQUIPO","Configuracion"));
            edtWebServiceUnico.setVisibility(View.INVISIBLE);
            return "Sincronizar por red";
        }
        return "Agregar";
    }

}
