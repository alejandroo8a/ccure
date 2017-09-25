package arenzo.alejandroochoa.ccure.Helpers;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import io.realm.RealmResults;

/**
 * Created by AlejandroMissael on 22/09/2017.
 */

public final class archivo {

    private static String TAG = "archivo";

    public static boolean crearDirectorio(Context context){
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

    public static String crearContenidoArchivo(RealmResults<realmESPersonal> resultsESPersonal){
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

    public static boolean crearArchivo(String contenido, Context context){
        try {
            File file = new File(Environment.getExternalStorageDirectory()+"/CCURE/BACKUP", nombreArchivo());
            FileOutputStream outputStream = new FileOutputStream(file);
            outputStream.write(contenido.getBytes());
            outputStream.close();
            return true;
        } catch (IOException e) {
            resultadoDialog("ERROR", "Ocurrio un error al crear el archivo de backup: "+e.getMessage(), context);
            return false;
        }
    }

    private static String leerArchivo(){
        File file = new File(Environment.getExternalStorageDirectory()+ "/CCURE/BACKUP/" + nombreArchivo());
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

    private static void resultadoDialog(String titulo, String mensaje, Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private static String nombreArchivo(){
        return "BACKUP"+ obtenerFecha()+ ".txt";
    }

    private static String obtenerFecha(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss");
        return dateFormat.format(date);
    }
}
