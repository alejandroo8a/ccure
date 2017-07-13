package arenzo.alejandroochoa.ccure.Helpers;

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

    public boolean isOnline(){
        try{
            Process p = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int val = p.waitFor();
            boolean online = (val==0);
            return online;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
