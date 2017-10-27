package arenzo.alejandroochoa.ccure.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import io.realm.RealmResults;

public final class archivo {

    private static String TAG = "archivo";
    private static SharedPreferences PREF_ARCHIVO;

    public static boolean crearBackUp(Context context, RealmResults<realmESPersonal> resultsESPersonal){
        if(crearDirectorio(context)) {
            String nombreArchivo = crearNombreArchivo();
            String contenidoFinal = crearContenidoArchivo(resultsESPersonal);
            if (crearArchivo(contenidoFinal, context, nombreArchivo)) {
                eliminarArchivoDuplicado(context);
                guardarNombreArchivo(nombreArchivo, context);
            }
        }else {
            Toast.makeText(context, "No tiene permiso para almacenar datos, active el permiso en la configuración de la App", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private static boolean crearDirectorio(Context context){
        File directorio = new File(Environment.getExternalStorageDirectory(),"/CCURE/BACKUP");
        boolean creado = true;
        if (Environment.getExternalStorageState().startsWith(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "PUEDO ALMACENAR DATOS");
        }else
            Toast.makeText(context, "El equipo no es compatible para almacenar los datos de las checadas. Utilice un equipo compatible.", Toast.LENGTH_LONG).show();
        if (!directorio.exists())
            creado = directorio.mkdirs();
        return creado;
    }

    private static String crearContenidoArchivo(RealmResults<realmESPersonal> resultsESPersonal){
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
        int i = 0;
        String archivo = "";
        for (realmESPersonal persona : aPersonal){
            if (i == aPersonal.size() - 1)
                archivo += persona.getNoEmpleado()  + "-" + persona.getNoTarjeta() + "-" + persona.getPUEClave() + "-" + persona.getFechaHoraEntrada() + "-" +persona.getFaseIngreso();
            else
                archivo += persona.getNoEmpleado()  + "-" + persona.getNoTarjeta() + "-" + persona.getPUEClave() + "-" + persona.getFechaHoraEntrada() + "-" +persona.getFaseIngreso() + "\n~";
            i++;
        }
        return archivo;
    }

    private static boolean crearArchivo(String contenido, Context context, String nombreArchivo){
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/CCURE/BACKUP", nombreArchivo);
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(contenido.getBytes());
            outputStream.close();
            return true;
        } catch (IOException e) {
            resultadoDialog("ERROR", "Ocurrio un error al crear el archivo de backup: "+e.getMessage(), context);
            return false;
        }
    }

    private static String obtenerNombreUltimoArchivoCreado(Context context){
        PREF_ARCHIVO = context.getSharedPreferences("CCURE", context.MODE_PRIVATE);
        return PREF_ARCHIVO.getString("NOMBRE_ARCHIVO", "NOEXISTE");
    }

    private static String crearNombreArchivo(){
        return "BACKUP"+ obtenerFecha()+ ".txt";
    }

    private static void guardarNombreArchivo(String nombreArchivo, Context context){
        PREF_ARCHIVO = context.getSharedPreferences("CCURE", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = PREF_ARCHIVO.edit();
        editor.putString("NOMBRE_ARCHIVO", nombreArchivo);
        editor.apply();
    }

    private static void eliminarArchivoDuplicado(Context context) {
        try{
            File file = new File(Environment.getExternalStorageDirectory()+ "/CCURE/BACKUP/" + obtenerNombreUltimoArchivoCreado(context));
            if ( archivoCreadoHoy(context)) {
                file.delete();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private static boolean archivoCreadoHoy(Context context){
        String nombreUltimoArchivo = obtenerNombreUltimoArchivoCreado(context);
        String nombreArchivoACrear = obtenerFecha();
        String fechaUltimoArchivo = nombreUltimoArchivo.substring(6,16);
        String fechaNuevoArchivo = nombreArchivoACrear.substring(0,10);
        if (fechaNuevoArchivo.equals(fechaUltimoArchivo))
            return true;
        else
            return false;

    }

    private static String obtenerFecha(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss");
        return dateFormat.format(date);
    }

    private static void resultadoDialog(String titulo, String mensaje, Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
