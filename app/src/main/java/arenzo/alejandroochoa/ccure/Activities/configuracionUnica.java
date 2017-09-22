package arenzo.alejandroochoa.ccure.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
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

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Helpers.imei;
import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.Modelos.agrupadorPuerta;
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
import io.realm.Realm;
import io.realm.RealmResults;

public class configuracionUnica extends AppCompatActivity {
    private final static String TAG = "configuracionUnica";

    private EditText edtNombreDispositivoUnico, edtWebServiceUnico, edtURLExportacionUnico;
    private Spinner spPuertasUnico;
    private Button btnGuardarConfiguracionUnico;
    ProgressDialog anillo = null;
    private SharedPreferences PREF_CONFIGURACION_UNICA;
    public String URL, mensaje;
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
        PREF_CONFIGURACION_UNICA = getSharedPreferences("CCURE", MODE_PRIVATE);
        eventosVista();
        borrarDatos();
        cargarDatosVista();
        centrarTituloActionBar();
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
            edtNombreDispositivoUnico.setText(dispositivo.getDescripcion());
            edtWebServiceUnico.setText(dispositivo.getURLWebService());
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
                edtNombreDispositivoUnico.setText(dispositivo.getDescripcion());
                edtWebServiceUnico.setText(dispositivo.getURLWebService());
                edtURLExportacionUnico.setText(dispositivo.getURLExportacion());
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
                mostrarCargandoAnillo("Comprobando conexión a internet");
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getApplicationContext())) {
                    if (conexion.isOnline(anillo)) {
                        obtenerTodosDatos();
                    } else
                        Toast.makeText(configuracionUnica.this, "No cuenta con conexión con el servidor", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(configuracionUnica.this, "Activa el WI-FI o los datos móviles para comenzar", Toast.LENGTH_SHORT).show();
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
        editor.apply();
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
        editor.apply();
    }

    private void guardarURL(String url){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putString("URL", url);
        editor.apply();
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
        editor.apply();
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
        editor.apply();
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

    private void obtenerValidacionImei(AlertDialog alert){
        mostrarCargandoAnillo("Obteniendo puertas...");
        helperRetrofit helperRetrofit = new helperRetrofit(URL);
        imei.setImei(getApplicationContext());
        helperRetrofit.validarImei(this, null, anillo, spPuertasUnico, alert, PREF_CONFIGURACION_UNICA, imei.imei, "configuracionUnica");
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
                if(!PREF_CONFIGURACION_UNICA.getBoolean("YACONFIGURADO", false))
                    guardarURL(edtUrlDialog.getText().toString());
                URL = PREF_CONFIGURACION_UNICA.getString("URL","");
                edtWebServiceUnico.setText(edtUrlDialog.getText().toString());
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getApplicationContext())) {
                    if (conexion.isOnline(anillo)) {
                        obtenerValidacionImei(alert);
                    } else
                        Toast.makeText(configuracionUnica.this, "No cuenta con conexión con el servidor", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(configuracionUnica.this, "Conectate a una red para comenzar", Toast.LENGTH_SHORT).show();
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ocultarTeclado();
                mostrarCargandoAnillo("Sincronizando archivo...");
                segundoPlanoArchivoLectura sincronizar = new segundoPlanoArchivoLectura();
                sincronizar.execute();
            }
        });

    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public void resultadoDialogNegativo(String mensaje){
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle("ATENCIÓN")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private class segundoPlanoArchivoLectura extends AsyncTask<String, Void, String> {
        Realm realm;
        @Override
        protected String doInBackground(String... urls) {
            realm = Realm.getDefaultInstance();
            String archivo = leerArchivo();
            if (!archivo.equals("")) {
                if (!existenDatos(realm)) {
                    String[] archivoSeparado = archivo.split("~");
                    if (RealmController.getInstance().borrarTablasSincronizacionArchivo(realm))
                        if (guardarAgrupadores(realm, archivoSeparado[0]))
                            if (guardarAgrupadorPuerta(realm, archivoSeparado[1]))
                                if (guardarPuertas(realm, archivoSeparado[2]))
                                    if (guardarPersonalPuerta(realm, archivoSeparado[3]))
                                        if (guardarTarjetasPersonal(realm, archivoSeparado[4]))
                                            if (guardarPersonalInfo(realm, archivoSeparado[5]))
                                                return "true";
                    return "false";
                } else {
                    return "hayDatos";
                }
            } else
                return "vacio";
        }
        @Override
        protected void onPostExecute(String result) {
            ocultarCargandoAnillo();
            switch (result) {
                case "vacio":
                    resultadoDialogNegativo("No se puede leer el archivo. Asegurese de que exista.");
                    break;
                case "false":
                    dialogErrorGuardadoDatosArchivo();
                    break;
                case "hayDatos":
                    resultadoDialogNegativo("No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
                    break;
                default:
                    resultadoDialog("ÉXITO", "Terminó la sincronización por archivo, los datos se guardaron correctamente.");
                    break;
            }
        }


        private boolean existenDatos(Realm realm){
            RealmResults<realmESPersonal> aESPersonal = RealmController.getInstance().obtenerRegistrosArchivo(realm);
            return aESPersonal.size() > 0;
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
            }catch (IOException ignored){
            }
            return archivo.toString();
        }

        private boolean guardarAgrupadores(Realm realm, String oAgrupadores){
            String[] aCantidadAgrupadores = oAgrupadores.split("\n");
            int contador = 0;
            List<agrupador> aAgrupadores = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadAgrupadores.length; i++) {
                    String[] aElementosAgrupador = aCantidadAgrupadores[i].split("-");
                    contador = i;
                    agrupador agrupador = new agrupador();
                    agrupador.setAGRId(Integer.parseInt(aElementosAgrupador[0]));
                    agrupador.setDescripcion(aElementosAgrupador[1]);
                    agrupador.setFase(aElementosAgrupador[2]);
                    aAgrupadores.add(agrupador);
                }
                return RealmController.getInstance().insertarAgrupadorArchivo(realm, aAgrupadores, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de Agrupadores no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }

        private boolean guardarAgrupadorPuerta(Realm realm, String oAgrupadoresPuerta){
            String[] aCantidadAgrupadoresPuerta = oAgrupadoresPuerta.split("\n");
            int contador = 0;
            List<agrupadorPuerta> aAgrupadoresPuerta = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadAgrupadoresPuerta.length; i++) {
                    contador = i;
                    String[] aElementosAgrupadorPuerta = aCantidadAgrupadoresPuerta[i].split("-");
                    agrupadorPuerta agrupadorPuerta = new agrupadorPuerta();
                    agrupadorPuerta.setAGRId(Integer.parseInt(aElementosAgrupadorPuerta[0]));
                    agrupadorPuerta.setPUEId(Integer.parseInt(aElementosAgrupadorPuerta[1]));
                    agrupadorPuerta.setFase(aElementosAgrupadorPuerta[2]);
                    agrupadorPuerta.setTipo(aElementosAgrupadorPuerta[3]);
                    aAgrupadoresPuerta.add(agrupadorPuerta);
                }
                return RealmController.getInstance().insertarAgrupadorPuertaArchivo(realm, aAgrupadoresPuerta, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de AgrupadoresPuerta no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }

        private boolean guardarPuertas(Realm realm, String oPuertas){
            String[] aCantidadPuertas = oPuertas.split("\n");
            int contador = 0;
            List<puertas> aPuertas = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadPuertas.length; i++) {
                    contador = i;
                    String[] aElementosPuerta = aCantidadPuertas[i].split("-");
                    puertas puerta = new puertas();
                    puerta.setPUEId(Integer.parseInt(aElementosPuerta[0]));
                    puerta.setPUEClave(aElementosPuerta[1]);
                    puerta.setDescripcion(aElementosPuerta[2]);
                    puerta.setFase(aElementosPuerta[3]);
                    puerta.setGRUID(aElementosPuerta[4]);
                    aPuertas.add(puerta);
                }
                return RealmController.getInstance().insertarPuertasArchivo(realm, aPuertas, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de Puertas no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }
        private boolean guardarPersonalPuerta(Realm realm, String personal){
            String[] aCantidadPersonalPuerta = personal.split("\n");
            int contador = 0;
            List<personalPuerta> aPersonalPuerta = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadPersonalPuerta.length; i++) {
                    contador = i;
                    String[] elementosPersonalPuerta = aCantidadPersonalPuerta[i].split("-");
                    personalPuerta personalPuerta = new personalPuerta();
                    personalPuerta.setNoEmpleado(elementosPersonalPuerta[0]);
                    personalPuerta.setNoTarjeta(elementosPersonalPuerta[1]);
                    personalPuerta.setGRUId(elementosPersonalPuerta[2]);
                    aPersonalPuerta.add(personalPuerta);
                }
                return RealmController.getInstance().insertarPersonalPuertaArchivo(realm, aPersonalPuerta, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de PersonalPuerta no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }

        private boolean guardarTarjetasPersonal(Realm realm, String tarjetas){
            String[] aCantidadTarjetas = tarjetas.split("\n");
            int contador = 0;
            List<tarjetasPersonal> aTarjetasPersonal = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadTarjetas.length; i++) {
                    contador = i;
                    String[] elementosTarjetas = aCantidadTarjetas[i].split("-");
                    tarjetasPersonal tarjetasPersonal = new tarjetasPersonal();
                    tarjetasPersonal.setNoEmpleado(elementosTarjetas[0]);
                    tarjetasPersonal.setNoTarjeta(elementosTarjetas[1]);
                    tarjetasPersonal.setNombre(elementosTarjetas[2]);
                    tarjetasPersonal.setEmpresa(elementosTarjetas[3]);
                    tarjetasPersonal.setTipo(elementosTarjetas[4]);
                    tarjetasPersonal.setFoto(elementosTarjetas[5]);
                    aTarjetasPersonal.add(tarjetasPersonal);
                }
                return RealmController.getInstance().insertarTarjetasPersonalArchivo(realm, aTarjetasPersonal, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            }catch(ArrayIndexOutOfBoundsException ex){
                mensaje = "La tabla de TarjetasPersonal no está bien creada. Error en el registro "+ contador + ", no tiene el formato correcto.";
            }
            return false;
        }

        private boolean guardarPersonalInfo(Realm realm, String personal){
            String[] aCantidadPersonal = personal.split("\n");
            int contador = 0;
            List<personalInfo> aPersonalInfo = new ArrayList<>();
            try {
                for (int i = 0; i < aCantidadPersonal.length; i++) {
                    contador = i;
                    String[] elementosPersonal = aCantidadPersonal[i].split("-");
                    personalInfo personalInfo = new personalInfo();
                    personalInfo.setFoto(elementosPersonal[0]);
                    personalInfo.setNoEmpleado(elementosPersonal[1]);
                    personalInfo.setNombre(elementosPersonal[2]);
                    personalInfo.setPuesto(elementosPersonal[3]);
                    aPersonalInfo.add(personalInfo);
                }
                return RealmController.getInstance().insertarInfoPersonalArchivo(realm, aPersonalInfo, PREF_CONFIGURACION_UNICA.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de PersonalInfo no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }


    }

    private void dialogErrorGuardadoDatosArchivo(){
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("ERROR")
                .setMessage(mensaje)
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mostrarCargandoAnillo("Sincronizando archivo...");
                        segundoPlanoArchivoLectura sincronizar = new segundoPlanoArchivoLectura();
                        sincronizar.execute();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void resultadoDialog(String titulo, String mensaje){
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setCancelable(false)
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
