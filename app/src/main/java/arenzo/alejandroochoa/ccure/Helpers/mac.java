package arenzo.alejandroochoa.ccure.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;


public final class mac {

    private static String MAC = "MAC";
    private static String TAG = "mac";
    public static String mac;

    public static void setMac(Context context){
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        String MAC = info.getMacAddress();
        String[] aMac = MAC.split(":");
        mac = "";
        for (int i = 0 ; i < aMac.length ; i++){
            mac += aMac[i];
        }
    }

    public static void guardarRespuestaMacLocalmente(String resultado, SharedPreferences.Editor editor){
        editor.putString(MAC, resultado);
        editor.apply();
    }

    public static String obtenerEstadoMac(SharedPreferences preferences){
        return preferences.getString(MAC, "D");
    }

    public static void resultadoDialogNoPermitidoMac(final Activity activity){
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
