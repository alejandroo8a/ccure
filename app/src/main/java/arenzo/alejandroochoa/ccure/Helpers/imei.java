package arenzo.alejandroochoa.ccure.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;


public final class imei {

    private static String IMEI = "IMEI";
    public static String imei;

    static void setImei(Context context){
        TelephonyManager systemService = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = systemService.getDeviceId();
    }

    static void guardarRespuestaImeiLocalmente(String resultado, SharedPreferences.Editor editor){
        editor.putString(IMEI, resultado);
        editor.apply();
    }

    static String obtenerEstadoImei(SharedPreferences preferences){
        return preferences.getString(IMEI, "I");
    }
}
