package arenzo.alejandroochoa.ccure.Realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Date;
import java.util.List;

import arenzo.alejandroochoa.ccure.WebService.oChecada;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmResults;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class RealmController {
    private final static String TAG = "RealmController";
    private static RealmController instance;
    private final Realm realm;
    private int siguienteId = 0;
    private Boolean saberEstadoConsulta = false;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RealmController with(Fragment fragment) {
        if (instance == null) {
            instance = new RealmController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RealmController with(Activity activity) {
        if (instance == null) {
            instance = new RealmController(activity.getApplication());
        }
        return instance;
    }

    public static RealmController with(Application application) {

        if (instance == null) {
            instance = new RealmController(application);
        }
        return instance;
    }

    public static RealmController getInstance() {

        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm istance
    public void refresh() {
        realm.refresh();
    }

     //OBTENER EL ULTIMO ID DE UNA TABLA Y GENERAR UNO NUEVO
    public int obtenerIdDispositivo(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(realmDispositivo.class).max("DISId");
                if(currentIdNum == null) {
                    siguienteId = 1;
                } else {
                    siguienteId = currentIdNum.intValue() + 1;
                }
            }
        });
        return siguienteId;
    }

    //INSERCIONES

    public boolean insertarConfiguracion(final String descripcion, final String fase, final int AGRId, final String URLWebService, final String URLExportacion, final String MUsuarioId){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void execute(Realm realm) {
                realmDispositivo dispositivo = realm.createObject(realmDispositivo.class);
                dispositivo.setDISId(1);
                dispositivo.setDescripcion(descripcion);
                dispositivo.setFase(fase);
                dispositivo.setAGRId(AGRId);
                dispositivo.setURLWebService(URLWebService);
                dispositivo.setURLExportacion(URLExportacion);
                dispositivo.setMFechaHora(obtenerFecha());
                dispositivo.setMUsuarioId(MUsuarioId);
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    //ACTUALIZACIONES

    public boolean actualizarConfiguracion(final String descripcion, final String fase, final int AGRId, final String URLWebService, final String URLExportacion, final String MUsuarioId){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void execute(Realm realm) {
                realmDispositivo dispositivo = realm.where(realmDispositivo.class).equalTo("DISId",1).findFirst();
                dispositivo.setDescripcion(descripcion);
                dispositivo.setFase(fase);
                dispositivo.setAGRId(AGRId);
                dispositivo.setURLWebService(URLWebService);
                dispositivo.setURLExportacion(URLExportacion);
                dispositivo.setMFechaHora(obtenerFecha());
                dispositivo.setMUsuarioId(MUsuarioId);
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean actualizarChecadasEnviadas(){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<realmESPersonal> results = RealmController.getInstance().obtenerRegistros();
                for (realmESPersonal persona : results){
                    persona.setFase("E");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    //SELECCIONAR

    public realmDispositivo obtenerDispositivo(){
        return realm.where(realmDispositivo.class).equalTo("DISId",1).findFirst();
    }

    public RealmResults<realmESPersonal> obtenerRegistros(){
        return realm.where(realmESPersonal.class).equalTo("Fase","N").findAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private String obtenerFecha(){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }

    //ELIMINAR

    public boolean borrarTablasSincronizacion(){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.clear(realmPuerta.class);
                realm.clear(realmPersonal.class);
                realm.clear(realmPersonalInfo.class);
                realm.clear(realmPersonalPuerta.class);
                realm.clear(realmESPersonal.class);
                realm.clear(realmNotificacion.class);
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }


}
