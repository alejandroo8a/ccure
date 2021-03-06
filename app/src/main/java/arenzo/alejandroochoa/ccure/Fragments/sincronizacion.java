package arenzo.alejandroochoa.ccure.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Helpers.archivo;
import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Helpers.mac;
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


public class sincronizacion extends Fragment {

    private final static String TAG = "sincronizacion";

    private ListView lvTipoSincronizacion;
    private ArrayAdapter<String> adapter;
    private Button btnSincronizar;
    private boolean haySeleccionado = true;
    private int tipoSincronizacion = 0;
    private View ultimaVista;
    ProgressDialog anillo = null;
    RealmController realmPrincipal;
    String URL = "";

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
        lvTipoSincronizacion = (ListView) view.findViewById(R.id.lvTipoSincronizacion);
        btnSincronizar = (Button) view.findViewById(R.id.btnSincronizar);
        PREF_SINCRONIZACION = getContext().getSharedPreferences("CCURE", Context.MODE_PRIVATE);
        URL = PREF_SINCRONIZACION.getString("URL", "");
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setearListView();
        eventosVista();
        realmPrincipal = new RealmController(getActivity().getApplication());
    }

    private void setearListView(){
        adapter = new ArrayAdapter<>(getContext(), R.layout.item_sincronizacion, R.id.txtOpcionSincronizacion, obtenerOpcionesSincronizado());
        lvTipoSincronizacion.setAdapter(adapter);
    }

    private String[] obtenerOpcionesSincronizado(){
        return new String[]{"RED", "ARCHIVO", "LEER ARCHIVO"};
    }

    private  void eventosVista(){
        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizar();
            }
        });
        lvTipoSincronizacion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                cambiarColorListView(position, view);
            }
        });
    }

    private void cambiarColorListView(int posicionActual,  View view){
        if(ultimaVista != view) {
            TextView txtOpcionSincronizacion = (TextView) view.findViewById(R.id.txtOpcionSincronizacion);
            if (haySeleccionado) {
                txtOpcionSincronizacion.setTextColor(Color.WHITE);
                txtOpcionSincronizacion.setBackgroundColor(getResources().getColor(R.color.seleccionado));
                haySeleccionado = false;
            } else {
                TextView txtOpcionSincronizacionUltimo = (TextView) ultimaVista.findViewById(R.id.txtOpcionSincronizacion);
                txtOpcionSincronizacion.setTextColor(Color.WHITE);
                txtOpcionSincronizacion.setBackgroundColor(getResources().getColor(R.color.seleccionado));
                txtOpcionSincronizacionUltimo.setTextColor(getResources().getColor(R.color.fechaHora));
                txtOpcionSincronizacionUltimo.setBackgroundColor(getResources().getColor(R.color.noSeleccionado));
            }
            ultimaVista = view;
            setTipoSincronziacion(posicionActual);
        }
    }

    private void sincronizar(){
        if (tipoSincronizacion != 0) {
            mostrarCargandoAnillo("Sincronizando...");
            if (tipoSincronizacion == 1) {
                //Archivo
                segundoPlanoArchivo sincronizar = new segundoPlanoArchivo();
                sincronizar.execute();
            } else if (tipoSincronizacion == 2){
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getContext())) {
                    if (conexion.isOnline(anillo)) {
                        //Red
                        mostrarCargandoAnillo("Verificando MAC...");
                        verificarMac();
                    } else
                        avisoNoConexion();
                } else
                    avisoNoRed();
            }else{
                segundoPlanoArchivoLectura sincronizar = new segundoPlanoArchivoLectura();
                sincronizar.execute();
            }
        }else{
            Toast.makeText(getContext(), "Seleccione un método de sincronización  ", Toast.LENGTH_SHORT).show();
        }
    }

    private void setTipoSincronziacion(int posicion){
        if( posicion == 0)
            tipoSincronizacion = 2;
        else if (posicion == 1)
            tipoSincronizacion = 1;
        else
            tipoSincronizacion = 3;
    }

    private void verificarMac(){
        final helperRetrofit helperRetrofit = new helperRetrofit(URL);
        helperRetrofit.validarMac(null, this, null, anillo, null, null, PREF_SINCRONIZACION, mac.mac, "sincronizacion");
    }
    public void sincronizarRed(){
        final helperRetrofit helperRetrofit = new helperRetrofit(URL);
        RealmResults<realmESPersonal> resultado = realmPrincipal.obtenerRegistros();
        if (resultado.size() > 0) {
            anillo.setMessage("Enviando checadas...");
            for (int i = 0; i < resultado.size(); i++) {
                realmESPersonal persona = resultado.get(i);
                helperRetrofit.actualizarChecadas(persona.getNoEmpleado(), persona.getNoTarjeta(), persona.getPUEClave(), persona.getFechaHoraEntrada(), resultado.size() - 1, i, getContext(), anillo, persona.getFaseIngreso(), realmPrincipal);
            }
            RealmResults<realmESPersonal> resultadoBackUp = realmPrincipal.obtenerTodosRegistros();
            archivo.crearBackUp(getContext(), resultadoBackUp);
        }else {
            anillo.incrementProgressBy(70);
            resultadoDialogNoHayDatos("ATENCIÓN", "Actualmente no hay datos para enviar. ¿Desea hacer una sincronización con nuevos datos?", getContext());
            anillo.dismiss();
        }

    }

    private void sincronizarRedDirecto(){
        realmPrincipal.borrarTablasSincronizacionRed();
        mostrarCargandoAnillo("Actualizando agrupadores...");
        anillo.incrementProgressBy(30);
        final helperRetrofit helperRetrofit = new helperRetrofit(URL);
        helperRetrofit.actualizarAgrupadoresSincronizacion(getContext(), anillo);
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

    private void mostrarCargandoAnillo(String mensaje){
        this.anillo = new ProgressDialog(getContext());
        this.anillo.setMax(100);
        this.anillo.setTitle("Sincronizando");
        this.anillo.setMessage(mensaje);
        this.anillo.setCancelable(false);
        this.anillo.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        this.anillo.show();
    }

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }

    public void resultadoDialog(String titulo, String mensaje, Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    public void resultadoDialogNoHayDatos(String titulo, String mensaje, Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Sincronizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sincronizarRedDirecto();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void resultadoDialogNegativo(String mensaje){
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("ATENCIÓN")
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
    private class segundoPlanoArchivoLectura extends AsyncTask<String, Void, String> {
        Realm realm;
        String mensaje;
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
                    resultadoDialogNegativo("El archivo de sincronización no contiene datos");
                    break;
                case "false":
                    dialogErrorGuardadoDatosArchivo();
                    break;
                case "hayDatos":
                    resultadoDialogNegativo("No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
                    break;
                default:
                    resultadoDialog("ÉXITO", "Terminó la sincronización por archivo, los datos se guardaron correctamente.", getContext());
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
                return RealmController.getInstance().insertarAgrupadorArchivo(realm, aAgrupadores, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
                return RealmController.getInstance().insertarAgrupadorPuertaArchivo(realm, aAgrupadoresPuerta, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
                return RealmController.getInstance().insertarPuertasArchivo(realm, aPuertas, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
                return RealmController.getInstance().insertarPersonalPuertaArchivo(realm, aPersonalPuerta, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
                return RealmController.getInstance().insertarTarjetasPersonalArchivo(realm, aTarjetasPersonal, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
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
                return RealmController.getInstance().insertarInfoPersonalArchivo(realm, aPersonalInfo, PREF_SINCRONIZACION.getString("NUMERO_EMPLEADO", "CONFIGURACION"));
            } catch (ArrayIndexOutOfBoundsException ex) {
                mensaje = "La tabla de PersonalInfo no está bien creada. Error en el registro " + contador + ", no tiene el formato correcto.";
            }
            return false;
        }

        private void dialogErrorGuardadoDatosArchivo(){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("ERROR")
                    .setMessage(mensaje)
                    .setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            segundoPlanoArchivoLectura sincronizar = new segundoPlanoArchivoLectura();
                            sincronizar.execute();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
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
                            RealmResults<realmESPersonal> resultadoBackUp = obtenerTodosRegistros();
                            archivo.crearBackUp(getContext(), resultadoBackUp);
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
            switch (result) {
                case "true":
                    resultadoDialog("ÉXITO", "El archivo de sincronización se creó correctamente.");
                    break;
                case "actualizado":
                    resultadoDialog("ATENCIÓN", "Actualmente todo está sincronizado.");
                    break;
                default:
                    resultadoDialog("ATENCIÓN", "No es posible actualizar la base de datos. Es necesario exportar todo antes de actualizar.");
            }
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

        private RealmResults<realmESPersonal> obtenerTodosRegistros(){
            return realm.where(realmESPersonal.class).findAll();
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
                    realm.delete(realmAgrupador.class);
                    realm.delete(realmAgrupadorPuerta.class);
                    realm.delete(realmUsuario.class);
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
            }else
                Toast.makeText(getContext(), "El equipo no es compatible para almacenar los datos de las checadas. Utilice un equipo compatible.", Toast.LENGTH_LONG).show();
            if (!directorio.exists())
                creado = directorio.mkdirs();
            return creado;
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

