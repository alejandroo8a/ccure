package arenzo.alejandroochoa.ccure.Realm;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class baseDatos extends Application{

    private final static String nombreBD = "ccure.realm";

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .name(nombreBD)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
        RealmController.with(this);
    }
}

