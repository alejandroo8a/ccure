package arenzo.alejandroochoa.ccure.Realm;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.Modelos.agrupadorPuerta;
import arenzo.alejandroochoa.ccure.Modelos.personalInfo;
import arenzo.alejandroochoa.ccure.Modelos.personalPuerta;
import arenzo.alejandroochoa.ccure.Modelos.puertas;
import arenzo.alejandroochoa.ccure.Modelos.tarjetasPersonal;
import arenzo.alejandroochoa.ccure.Modelos.usuario;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by AlejandroMissael on 22/04/2017.
 */

public class RealmController {
    private final static String TAG = "RealmController";
    private static RealmController instance;
    private static Realm realm;
    private Boolean saberEstadoConsulta = false;

    public RealmController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public RealmController() {
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

    //INSERCIONES
    public boolean insertarConfiguracion(final String descripcion, final String fase, final int AGRId, final String URLWebService, final String URLExportacion, final String MUsuarioId){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmDispositivo dispositivo = realm.createObject(realmDispositivo.class,1);
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

    public boolean insertarPersonalNuevo(final String NoEmpleado, final String NoTarjeta, final String PUEClave, final String FaseIngreso, final String Fase, final String Observaciones, final String MUsuarioId, final String TipoEntrada){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmESPersonal personal = realm.createObject(realmESPersonal.class);
                personal.setNoEmpleado(NoEmpleado);
                personal.setNoTarjeta(NoTarjeta);
                personal.setPUEClave(PUEClave);
                personal.setFechaHoraEntrada(obtenerFecha());
                personal.setFaseIngreso(FaseIngreso);
                personal.setFase(Fase);
                personal.setObservaciones(Observaciones);
                personal.setMFechaHora(obtenerFecha());
                personal.setMUsuarioId(MUsuarioId);
                personal.setTipoEntrada(TipoEntrada);
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarInfoPersonal(final List<personalInfo> aPersonalInfo){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (personalInfo persona : aPersonalInfo){
                    realmPersonalInfo rPersonal = realm.createObject(realmPersonalInfo.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNombre(persona.getNombre());
                    rPersonal.setFoto(persona.getFoto());
                    rPersonal.setPuesto(persona.getPuesto());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId("1");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarInfoPersonalArchivo(final List<personalInfo> aPersonalInfo, final String idUsuario){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (personalInfo persona : aPersonalInfo){
                    realmPersonalInfo rPersonal = realm.createObject(realmPersonalInfo.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNombre(persona.getNombre());
                    rPersonal.setFoto(persona.getFoto());
                    rPersonal.setPuesto(persona.getPuesto());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId(idUsuario);
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarTarjetasPersonal(final List<usuario> aTarjetasPersonal){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (usuario persona : aTarjetasPersonal){
                    realmPersonal rPersonal = realm.createObject(realmPersonal.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNoTarjeta(persona.getNoTarjeta());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarTarjetasPersonalArchivo(final List<tarjetasPersonal> aTarjetasPersonal, final String idUsuario){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (tarjetasPersonal persona : aTarjetasPersonal){
                    realmPersonal rPersonal = realm.createObject(realmPersonal.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNoTarjeta(persona.getNoTarjeta());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId(idUsuario);
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarUsuarios(final List<usuario> aUsuario){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (usuario usuario : aUsuario){
                    realmUsuario rUsuario = realm.createObject(realmUsuario.class);
                    rUsuario.setNoEmpleado(usuario.getNoEmpleado());
                    rUsuario.setNoTarjeta(usuario.getNoTarjeta());
                    rUsuario.setNombre(usuario.getNombre());
                    rUsuario.setEmpresa(usuario.getEmpresa());
                    rUsuario.setTipo(usuario.getTipo());
                    rUsuario.setFase("A");
                    rUsuario.setMFechaHora(obtenerFecha());
                    rUsuario.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarPersonalPuerta(final List<personalPuerta> aPersonalPuerta){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (personalPuerta persona : aPersonalPuerta){
                    realmPersonalPuerta rPersonal = realm.createObject(realmPersonalPuerta.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNoTarjeta(persona.getNoTarjeta());
                    rPersonal.setGRUId(persona.getGRUId());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarPersonalPuertaArchivo(final List<personalPuerta> aPersonalPuerta, final String idUsuario){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (personalPuerta persona : aPersonalPuerta){
                    realmPersonalPuerta rPersonal = realm.createObject(realmPersonalPuerta.class);
                    rPersonal.setNoEmpleado(persona.getNoEmpleado());
                    rPersonal.setNoTarjeta(persona.getNoTarjeta());
                    rPersonal.setGRUId(persona.getGRUId());
                    rPersonal.setFase("A");
                    rPersonal.setMFechaHora(obtenerFecha());
                    rPersonal.setMUsuarioId(idUsuario);
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarAgrupador(final List<agrupador> aAgrupador){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (agrupador agrupador : aAgrupador){
                    realmAgrupador rAgrupador = realm.createObject(realmAgrupador.class, agrupador.getAGRId());
                    rAgrupador.setDescripcion(agrupador.getDescripcion());
                    rAgrupador.setFase(agrupador.getFase());
                    rAgrupador.setMFechaHora(obtenerFecha());
                    rAgrupador.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarPuertas(final List<puertas> aPersonalPuerta){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (puertas puerta : aPersonalPuerta){
                    realmPuerta rPuerta = realm.createObject(realmPuerta.class);
                    rPuerta.setPUEId(puerta.getPUEId());
                    rPuerta.setPUEClave(puerta.getPUEClave());
                    rPuerta.setGRUID(puerta.getGRUID());
                    rPuerta.setDescripcion(puerta.getDescripcion());
                    rPuerta.setFase(puerta.getFase());
                    rPuerta.setGRUID(puerta.getGRUID());
                    rPuerta.setMFechaHora(obtenerFecha());
                    rPuerta.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarPuertasArchivo(final List<puertas> aPersonalPuerta, final String idUsuario){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (puertas puerta : aPersonalPuerta){
                    realmPuerta rPuerta = realm.createObject(realmPuerta.class);
                    rPuerta.setPUEId(puerta.getPUEId());
                    rPuerta.setPUEClave(puerta.getPUEClave());
                    rPuerta.setGRUID(puerta.getGRUID());
                    rPuerta.setDescripcion(puerta.getDescripcion());
                    rPuerta.setFase(puerta.getFase());
                    rPuerta.setGRUID(puerta.getGRUID());
                    rPuerta.setMFechaHora(obtenerFecha());
                    rPuerta.setMUsuarioId(idUsuario);
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    public boolean insertarAgrupadorPuerta(final List<agrupadorPuerta> aPersonalPuerta){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (agrupadorPuerta agrupadorPuerta : aPersonalPuerta){
                    realmAgrupadorPuerta rAgrupadorPuerta = realm.createObject(realmAgrupadorPuerta.class);
                    rAgrupadorPuerta.setAGRId(agrupadorPuerta.getAGRId());
                    rAgrupadorPuerta.setPUEId(agrupadorPuerta.getPUEId());
                    rAgrupadorPuerta.setTipo(agrupadorPuerta.getTipo());
                    rAgrupadorPuerta.setFase(agrupadorPuerta.getFase());
                    rAgrupadorPuerta.setFechaHora(obtenerFecha());
                    rAgrupadorPuerta.setMUsuarioId("CONFIGURACION");
                }
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    //ACTUALIZACIONES

    public boolean actualizarConfiguracion(final String descripcion, final String fase, final int AGRId, final String URLWebService, final String URLExportacion, final String MUsuarioId){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
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

    public realmPersonalPuerta obtenerPersonalManual(String numeroEmpleado, String grupo){
        return realm.where(realmPersonalPuerta.class).equalTo("NoEmpleado",numeroEmpleado).equalTo("GRUId", grupo).findFirst();
    }

    public realmPersonalPuerta obtenerPersonalRfid(String noTarjeta, String grupo){
        return realm.where(realmPersonalPuerta.class).equalTo("NoTarjeta",noTarjeta).equalTo("GRUId", grupo).findFirst();
    }

    public realmUsuario obtenerUsuario(String numeroEmpleado){
        return realm.where(realmUsuario.class).equalTo("NoEmpleado", numeroEmpleado).findFirst();
    }

    public realmPersonalInfo obtenerInfoPersonal(String numeroEmpleado){
        return realm.where(realmPersonalInfo.class).equalTo("NoEmpleado", numeroEmpleado).findFirst();
    }

    public int obtenerIdAgrupador(String descripcion){
        realmAgrupador agrupador = realm.where(realmAgrupador.class).equalTo("Descripcion", descripcion).findFirst();
        return agrupador.getAGRId();
    }

    public RealmResults<realmAgrupadorPuerta> obtenerAgrupadoresPuertas(int AGRID){
        return realm.where(realmAgrupadorPuerta.class).equalTo("AGRId", AGRID).findAll();
    }

    public realmPuerta obtenerPuerta(int PUEId){
        return realm.where(realmPuerta.class).equalTo("PUEId", PUEId).findFirst();
    }

    public RealmResults<realmPuerta> obtenerPuertas(int PUEId){
        return realm.where(realmPuerta.class).equalTo("PUEId", PUEId).findAll();
    }

    public RealmResults<realmAgrupador> obtenerAgrupadores(){
        return realm.where(realmAgrupador.class).findAll();
    }

    public RealmResults<realmPersonalInfo> verificarExistenciaDatos(){
        return realm.where(realmPersonalInfo.class).findAll();
    }

    public realmPuerta obtenerPUEClave(int PUEId, String GRUID){
        return realm.where(realmPuerta.class).equalTo("PUEId", PUEId).equalTo("GRUID", GRUID).findFirst();
    }

    //ELIMINAR

    public boolean borrarTablasSincronizacion(){
        saberEstadoConsulta = false;
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(realmPuerta.class);
                realm.delete(realmPersonal.class);
                realm.delete(realmPersonalInfo.class);
                realm.delete(realmPersonalPuerta.class);
                realm.delete(realmESPersonal.class);
                realm.delete(realmNotificacion.class);
                realm.delete(realmAgrupador.class);
                realm.delete(realmAgrupadorPuerta.class);
                realm.delete(realmUsuario.class);
                saberEstadoConsulta = true;
            }
        });
        return saberEstadoConsulta;
    }

    private String obtenerFecha(){
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        return dateFormat.format(date);
    }

}
