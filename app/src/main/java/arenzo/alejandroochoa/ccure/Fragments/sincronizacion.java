package arenzo.alejandroochoa.ccure.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmNotificacion;
import arenzo.alejandroochoa.ccure.Realm.realmPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import io.realm.Realm;
import io.realm.RealmResults;


public class sincronizacion extends Fragment {

    private final static String TAG = "sincronizacion";

    private RadioButton rdRed, rdArchivo, rdLeerArchivo;
    private Button btnSincronizar;
    ProgressDialog anillo = null;
    Realm realmPrincipal;
    String URL = "";
    boolean saberEstadoConsulta;

    private SharedPreferences PREF_SINCRONIZACION;

    public sincronizacion() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sincronizacion, container, false);
        rdRed = view.findViewById(R.id.rdRed);
        rdArchivo = view.findViewById(R.id.rdArchivo);
        rdLeerArchivo = view.findViewById(R.id.rdLeerArchivo);
        btnSincronizar = view.findViewById(R.id.btnSincronizar);
        PREF_SINCRONIZACION = getContext().getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        URL = PREF_SINCRONIZACION.getString("URL", "");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventosVista();
        realmPrincipal = Realm.getDefaultInstance();
    }

    private  void eventosVista(){
        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizar();
            }
        });
    }

    private void sincronizar(){
        int tipo = tipoSincronizacion();
        if (tipo != 0) {
            mostrarCargandoAnillo();
            if (tipo == 1) {
                //Archivo
                RealmController.with(getActivity());
                segundoPlanoArchivo sincronizar = new segundoPlanoArchivo();
                sincronizar.execute(new String[]{});
            } else if (tipo == 2){
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getContext())) {
                    if (conexion.isOnline()) {
                        //Red
                        sincronizarRed();
                    } else
                        avisoNoConexion();
                } else
                    avisoNoRed();
            }else{
                sincronizarArchivo();
            }
        }else{
            Toast.makeText(getContext(), "Seleccione un método de sincronización  ", Toast.LENGTH_SHORT).show();
        }
    }


    private int tipoSincronizacion(){
        if (rdArchivo.isChecked() || rdRed.isChecked() || rdLeerArchivo.isChecked()){
            if (rdArchivo.isChecked()){
                return 1;
            }else if (rdRed.isChecked()){
                return 2;
            }else
                return 3;
        }
        return 0;
    }

    private void sincronizarRed(){
        final helperRetrofit helperRetrofit = new helperRetrofit(URL);
        realmPrincipal.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<realmESPersonal> resultado = obtenerRegistrosRed();
                if (resultado.size() > 0) {
                    RealmController realmController = new RealmController();
                    for (int i = 0; i < resultado.size(); i++) {
                        realmESPersonal persona = resultado.get(i);
                        helperRetrofit.actualizarChecadas(persona.getNoEmpleado(), persona.getNoTarjeta(), persona.getPUEClave(), persona.getFechaHoraEntrada(), resultado.size() - 1, i, getContext(), anillo, persona.getFaseIngreso(), realmController);
                    }
                }else
                    resultadoDialog("Actualmente todo está sincronizado.", getContext());
            }
        });

        anillo.dismiss();
    }

    public RealmResults<realmESPersonal> obtenerRegistrosRed(){
        return realmPrincipal.where(realmESPersonal.class).equalTo("Fase","N").findAll();
    }

    public boolean borrarTablasSincronizacion(){
        saberEstadoConsulta = false;
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
                saberEstadoConsulta = true;
            }
        });

        return saberEstadoConsulta;
    }

    private void avisoNoRed(){
        ocultarCargandoAnillo();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("AVISO")
                .setMessage("Encienda el Wi-Fi o los datos móviles.")
                .setPositiveButton("Activar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void avisoNoConexion(){
        ocultarCargandoAnillo();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("AVISO")
                .setMessage("No cuentas con conexion a internet")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void mostrarCargandoAnillo(){
        this.anillo = ProgressDialog.show(getActivity(), "Sincronizando datos", "Cargando...", true, false);
    }

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }

    public void resultadoDialog(String mensaje, Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("ÉXITO")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    public void resultadoDialogNegativo(String mensaje){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("ATENCIÓN")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void sincronizarArchivo(){
        String archivo = leerArchivo();
        if(!archivo.equals("")) {
            if (!existenDatos()) {
                String[] archivoSeparado = archivo.split("~");
                if (guardarPuertas(archivoSeparado[0]))
                    if (guardarPersonalPuerta(archivoSeparado[1]))
                        if (guardarTarjetasPersonal(archivoSeparado[2]))
                            if (guardarPersonalInfo(archivoSeparado[3])) {
                                ocultarCargandoAnillo();
                                resultadoDialog("Terminó la sincronización por archivo, los datos se guardaron correctamente.", getContext());
                                return;
                            }
                ocultarCargandoAnillo();
                dialogErrorGuardadoDatosArchivo();
            } else {
                ocultarCargandoAnillo();
                resultadoDialogNegativo("No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
            }
        }
    }

    private boolean existenDatos(){
        RealmResults<realmESPersonal> aESPersonal = RealmController.getInstance().obtenerRegistros();
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
        }catch (IOException ex){
            ocultarCargandoAnillo();
            Toast.makeText(getContext(), "No se puede leer el archivo. Asegurese de que exista.", Toast.LENGTH_SHORT).show();
        }
        return archivo.toString();
    }
//TODO VERIFICAR BASES DE DATOS
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
        return RealmController.getInstance().insertarPuertasArchivo(aPuertas, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
        return RealmController.getInstance().insertarPersonalPuertaArchivo(aPersonalPuerta, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
            tarjetasPersonal.setFoto(elementosTarjetas[5]);
            aTarjetasPersonal.add(tarjetasPersonal);
        }
        return RealmController.getInstance().insertarTarjetasPersonalArchivo(aTarjetasPersonal, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
        return RealmController.getInstance().insertarInfoPersonalArchivo(aPersonalInfo, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
    }

    private void dialogErrorGuardadoDatosArchivo(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("ERROR")
                .setMessage("Ocurrió un error al sincronizar el archivo, ¿desea volver a intentarlo?")
                .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sincronizarArchivo();
                    }
                })
                .setNegativeButton("Cancelar", null)
        .show();
    }

    private class segundoPlanoArchivo extends AsyncTask<String, Void, String> {
        Realm realm;
        boolean saberEstadoConsulta = false;
        @Override
        protected String doInBackground(String... urls) {
            realm = Realm.getDefaultInstance();
            RealmResults<realmESPersonal> resultado = obtenerRegistros();
            if (resultado.size() > 0) {
                if (sincronizarArchivo())
                    if (actualizarChecadasEnviadas()) {
                        //Verifico que todos los datos se hayan actualizado a enviado
                        RealmResults<realmESPersonal> results = obtenerRegistros();
                        if (results.size() == 0) {
                            if (borrarTablasSincronizacion()) {
                                return "true";
                            }else
                                return "false";
                        }else
                            return "false";
                    }
                return "false";
            }else
                return "actualizado";
        }
        @Override
        protected void onPostExecute(String result) {
            ocultarCargandoAnillo();
            if (result.equals("true"))
                resultadoDialog("ÉXITO", "El archivo de sincronización se creó correctamente.");
            else if(result.equals("actualizado"))
                resultadoDialog("ATENCIÓN","Actualmente todo está sincronizado.");
            else
                resultadoDialog("ATENCIÓN","No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
        }

        private boolean actualizarChecadasEnviadas(){
            saberEstadoConsulta = false;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<realmESPersonal> results = obtenerRegistros();
                    for (int i = 0 ; i < results.size() ; i++){
                        realmESPersonal persona = results.get(i);
                        persona.setFase("E");
                        i--;
                    }
                    saberEstadoConsulta = true;
                }
            });
            return saberEstadoConsulta;
        }

        private RealmResults<realmESPersonal> obtenerRegistros(){
            return realm.where(realmESPersonal.class).equalTo("Fase","N").findAll();
        }

        private boolean borrarTablasSincronizacion(){
            saberEstadoConsulta = false;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.delete(realmPuerta.class);
                    realm.delete(realmPersonal.class);
                    realm.delete(realmPersonalInfo.class);
                    realm.delete(realmPersonalPuerta.class);
                    realm.delete(realmESPersonal.class);
                    realm.delete(realmNotificacion.class);
                    saberEstadoConsulta = true;
                }
            });
            return saberEstadoConsulta;
        }

        private boolean sincronizarArchivo(){
            RealmResults<realmESPersonal> results = obtenerRegistros();
            if (crearDirectorio()){
                String contenido = crearContenidoArchivo(results);
                try {
                    File file = new File(Environment.getExternalStorageDirectory()+"/CCURE", "respaldo.txt");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(contenido.getBytes());
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    resultadoDialog("ERROR", "Ocurrio un error al crear el archivo de respaldo: "+e.getMessage());return false;
                }
            }
            resultadoDialog("ERROR", "No se pudo crear el directorio, intentelo de nuevo.");
            return false;
        }

        private boolean crearDirectorio(){
            File directorio = new File(Environment.getExternalStorageDirectory(),"/CCURE");
            boolean creado = true;
            if (Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, "PUEDO ALMACENAR DATOS");
            }
            if (!directorio.exists()){
                creado = directorio.mkdirs();
            }
            if (creado) {
                Log.d(TAG, "Ya LO CREE");
                return true;
            }
            else
                Log.e(TAG, "NO LO PUDE CREAR " );
            return false;
        }

        private String crearContenidoArchivo(RealmResults<realmESPersonal> resultsESPersonal){
            ArrayList<realmESPersonal> aPersonal = new ArrayList<>();
            for ( int i = 0 ; i < resultsESPersonal.size() ; i++){
                realmESPersonal personal = new realmESPersonal();
                personal.setNoEmpleado(resultsESPersonal.get(i).getNoEmpleado());
                personal.setNoTarjeta(resultsESPersonal.get(i).getNoTarjeta());
                personal.setPUEClave(resultsESPersonal.get(i).getPUEClave());
                personal.setFechaHoraEntrada(resultsESPersonal.get(i).getFechaHoraEntrada());
                personal.setFaseIngreso(resultsESPersonal.get(i).getFaseIngreso());
                aPersonal.add(personal);
            }
            String archivo = "";
            int i = 0;
            for (realmESPersonal persona : aPersonal){
                if (i == aPersonal.size() - 1)
                    archivo += persona.getNoEmpleado()  + "-" + persona.getNoTarjeta() + "-" + persona.getPUEClave() + "-" + persona.getFechaHoraEntrada() + "-" +persona.getFaseIngreso();
                else
                    archivo += persona.getNoEmpleado()  + "-" + persona.getNoTarjeta() + "-" + persona.getPUEClave() + "-" + persona.getFechaHoraEntrada() + "-" +persona.getFaseIngreso() + "\n~";
                i++;
            }
            return archivo;
        }

        private void resultadoDialog(String titulo, String mensaje){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle(titulo)
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", null)
                    .show();
        }

    }

}

