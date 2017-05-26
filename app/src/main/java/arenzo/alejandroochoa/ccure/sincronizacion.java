package arenzo.alejandroochoa.ccure;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmNotificacion;
import arenzo.alejandroochoa.ccure.Realm.realmPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.WebService.conexion;
import io.realm.Realm;
import io.realm.RealmResults;


public class sincronizacion extends Fragment {

    //TODO TERMINAR SINCRONIZACION DE RED Y ARCHIVO
    private final static String TAG = "sincronizacion";

    private RadioButton rdRed, rdArchivo;
    private Button btnSincronizar;
    ProgressDialog anillo = null;

    public sincronizacion() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sincronizacion, container, false);
        rdRed = (RadioButton)view.findViewById(R.id.rdRed);
        rdArchivo = (RadioButton)view.findViewById(R.id.rdArchivo);
        btnSincronizar = (Button)view.findViewById(R.id.btnSincronizar);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventosVista();
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
        if (tipo != 0){
            conexion conexion = new conexion();
            //Verificar conexion
            mostrarCargandoAnillo();
            if (conexion.isAvaliable(getContext())){
                if (conexion.isOnline()){
                    //Archivo
                    if (tipo == 1){
                        RealmController.with(getActivity());
                        segundoPlanoArchivo sincronizar =  new segundoPlanoArchivo();
                        sincronizar.execute(new String[]{});
                    }else{
                        //Red
                        sincronizarRed();
                    }
                }else
                    avisoNoConexion();
            }else
                avisoNoRed();
        }else{
            Toast.makeText(getContext(), "Seleccione un método de sincronización  ", Toast.LENGTH_SHORT).show();
        }
    }


    private int tipoSincronizacion(){
        if (rdArchivo.isChecked() || rdRed.isChecked()){
            if (rdArchivo.isChecked()){
                return 1;
            }else{
                return 2;
            }
        }
        return 0;
    }

    private boolean sincronizarRed(){
        return false;
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
        this.anillo = ProgressDialog.show(getContext(), "Sincronizando datos", "Cargando...", true, false);
    }

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }



    private class segundoPlanoArchivo extends AsyncTask<String, Void, String> {

        Realm realm;
        boolean saberEstadoConsulta = false;
        @Override
        protected String doInBackground(String... urls) {
            realm = Realm.getDefaultInstance();
            if (sincronizarArchivo())
                if (actualizarChecadasEnviadas()){
                    //Verifico que todos los datos se hayan actualizado a enviado
                    RealmResults<realmESPersonal> results = obtenerRegistros();
                    if (results.size() == 0)
                        if (borrarTablasSincronizacion())
                            return "true"; //TODO FALTA HACER CONSULTAS PARA LLENAR LA BD
                }
            return "false";
        }
        @Override
        protected void onPostExecute(String result) {
            ocultarCargandoAnillo();
            if (result.equals("true"))
                resultadoDialog("El proceso ha finalizado correctamente. El dispositivo quedó actualizado con la información.");
            else
                resultadoDialog("No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
        }

        private boolean actualizarChecadasEnviadas(){
            saberEstadoConsulta = false;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<realmESPersonal> results = obtenerRegistros();
                    for (realmESPersonal persona : results){
                        persona.setFase("E");
                    }
                    saberEstadoConsulta = true;
                }
            });
            return saberEstadoConsulta;
        }

        private RealmResults<realmESPersonal> obtenerRegistros(){
            return realm.where(realmESPersonal.class).equalTo("Fase","N").findAll();
        }

        public boolean borrarTablasSincronizacion(){
            saberEstadoConsulta = false;
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.clear(realmPuerta.class);
                    realm.clear(realmPersonal.class);
                    realm.clear(realmPersonalInfo.class);
                    realm.clear(realmPersonalPuerta.class);
                    realm.clear(realmESPersonal.class);
                    realm.clear(realmNotificacion.class);
                    saberEstadoConsulta = true;
                }
            });
            return saberEstadoConsulta;
        }

        private boolean sincronizarArchivo(){
            // TODO SE DEBE DE PENSAR COMO HACER LA INSTANCIA DE REALM
            RealmResults<realmESPersonal> results = obtenerRegistros();
            if (crearDirectorio()){
                String contenido = crearContenidoArchivo(results);
                Log.i(TAG, "CONTENIDO"+ results);
                try {
                    File file = new File(Environment.getExternalStorageDirectory()+"/CCURE", "respaldo.txt");
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(contenido.getBytes());
                    outputStream.close();
                    return true;
                } catch (IOException e) {
                    resultadoDialog("Ocurrio un error al crear el archivo de respaldo: "+e.getMessage());return false;
                }
            }
            resultadoDialog("No se pudo crear el directorio, intentelo de nuevo.");
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
                return  true;
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
            ArrayList<realmESPersonal> aPer = new ArrayList<>();
            for ( int i = 0 ; i < 5000 ; i++){
                realmESPersonal personal = new realmESPersonal();
                personal.setNoEmpleado(String.valueOf(i));
                personal.setNoTarjeta("123BD");
                personal.setPUEId(1);
                personal.setFechaHoraEntrada("2017/05/16 23:12:23");
                aPer.add(personal);
            }
            String archivo = "";
            for (realmESPersonal persona : aPer){
                archivo += "NoEmpleado | " + persona.getNoEmpleado()  + " | NoTarjeta | " + persona.getNoTarjeta() + " | PUEClave | " + persona.getPUEId() + " | FechaHoraEntrada |" + persona.getFechaHoraEntrada() +" | ";

            }
            return archivo;
        }

        private void resultadoDialog(String mensaje){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setTitle("ÉXITO")
                    .setMessage(mensaje)
                    .setPositiveButton("Aceptar", null)
                    .show();
        }

    }

}

