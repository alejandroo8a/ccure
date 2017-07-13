package arenzo.alejandroochoa.ccure.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by AlejandroMissael on 29/04/2017.
 */

public class datosConfiguracion {

    private final static String TAG = "datosConfiguracion";

    private SharedPreferences sharedPreferences;

    private Context context;

    public datosConfiguracion(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
}
