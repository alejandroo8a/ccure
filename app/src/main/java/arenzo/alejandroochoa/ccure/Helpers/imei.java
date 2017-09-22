package arenzo.alejandroochoa.ccure.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;


public final class imei {

    private static String IMEI = "IMEI";
    public static String imei;

    public static void setImei(Context context){
        TelephonyManager systemService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = systemService.getDeviceId();
    }

    public static void guardarRespuestaImeiLocalmente(String resultado, SharedPreferences.Editor editor){
        editor.putString(IMEI, resultado);
        editor.apply();
    }

    public static String obtenerEstadoImei(SharedPreferences preferences){
        return preferences.getString(IMEI, "I");
    }

    public static void resultadoDialogNoPermitidoImei(final Activity activity){
        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(activity);
        dialog.setTitle("ATENCIÓN")
                .setCancelable(false)
                .setMessage("El dispositivo no tiene acceso para ser usado. Dirijase con su administrador para la activación o reactivación de este dispositivo.")
                .setPositiveButton("Cerrar aplicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cerrarAplicacion(activity);
                    }
                })
                .show();
    }

    private static void cerrarAplicacion(Activity activity){
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        Intent intent = activity.getBaseContext().getPackageManager().getLaunchIntentForPackage(activity.getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        activity.startActivity(intent);
    }
}
