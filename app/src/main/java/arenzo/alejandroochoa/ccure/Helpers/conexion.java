package arenzo.alejandroochoa.ccure.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

/**
 * Created by AlejandroMissael on 09/03/2017.
 */

public class conexion {


    public boolean isAvaliable(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        return (info != null &&info.isConnected());
    }

    public boolean isOnline(final ProgressDialog anillo){
        try{//192.168.200.105
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int val = p.waitFor();
            boolean online = (val==0);
            if (anillo!= null)
                anillo.dismiss();
            return online;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
