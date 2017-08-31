package arenzo.alejandroochoa.ccure.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import arenzo.alejandroochoa.ccure.Modelos.agrupador;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmAgrupador;
import arenzo.alejandroochoa.ccure.Realm.realmAgrupadorPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmDispositivo;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import io.realm.RealmResults;

public class configuracion extends Fragment {

    private final static String TAG = "configuracion";
    private arenzo.alejandroochoa.ccure.Helpers.datosConfiguracion datosConfiguracion;

    ProgressDialog anillo = null;
    private EditText edtNombreDispositivo, edtWebService, edtURLExportacion;
    private Spinner spPuertas;
    private Button btnGuardarConfiguracion, btnActualizarPuertasC;
    private SharedPreferences PREF_CONFIGURACION;
    RealmController realmController;

    public configuracion() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        edtNombreDispositivo = (EditText) view.findViewById(R.id.edtNombreDispositivoUnico);
        edtWebService = (EditText) view.findViewById(R.id.edtWebServiceUnico);
        edtURLExportacion = (EditText) view.findViewById(R.id.edtURLExportacionUnico);
        spPuertas = (Spinner) view.findViewById(R.id.spPuertasUnico);
        btnGuardarConfiguracion = (Button) view.findViewById(R.id.btnGuardarConfiguracion);
        btnActualizarPuertasC = (Button) view.findViewById(R.id.btnActualizarPuertasC );
        PREF_CONFIGURACION = getContext().getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventosVista();
        cargarDatosVista();
    }

    private void eventosVista() {
        btnGuardarConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
        btnActualizarPuertasC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sincronizarNuevasPuertas();
            }
        });
    }

    public void cargarDatosVista() {
        realmController = new RealmController(getActivity().getApplication());
        realmDispositivo dispositivo = realmController.obtenerDispositivo();
        if (dispositivo != null) {
            edtNombreDispositivo.setText(dispositivo.getDescripcion().toString());
            edtWebService.setText(dispositivo.getURLWebService().toString());
            edtURLExportacion.setText(dispositivo.getURLExportacion());
        }
        llenarSpinnerAgrupador();
    }

    public void cargarDatosVistaSincronizacion() {
        realmController = new RealmController(getActivity().getApplication());
        realmDispositivo dispositivo = realmController.obtenerDispositivo();
        if (dispositivo != null) {
            edtNombreDispositivo.setText(dispositivo.getDescripcion().toString());
            edtWebService.setText(dispositivo.getURLWebService().toString());
            edtURLExportacion.setText(dispositivo.getURLExportacion());
        }
        llenarSpinnerAgrupadorSincronizado();
    }

    private void guardarDatos() {
        if (edtNombreDispositivo.length() > 0 && edtURLExportacion.length() > 0 && edtWebService.length() > 0) {
            realmDispositivo dispositivo = realmController.obtenerDispositivo();
            final int idAgrupador = realmController.obtenerIdAgrupador(spPuertas.getSelectedItem().toString());
            RealmResults<realmAgrupadorPuerta> aAgrupadorPuertas = realmController.obtenerAgrupadoresPuertas(idAgrupador);
            boolean saberEstadoInsercion;
            if (dispositivo == null) {
                saberEstadoInsercion = realmController.insertarConfiguracion(edtNombreDispositivo.getText().toString(), "A", idAgrupador, edtWebService.getText().toString(), edtURLExportacion.getText().toString(), "CONFIGURACION");
            } else {
                saberEstadoInsercion = realmController.actualizarConfiguracion(edtNombreDispositivo.getText().toString(), "A", idAgrupador, edtWebService.getText().toString(), edtURLExportacion.getText().toString(), "CONFIGURACION");
            }
            if (saberEstadoInsercion) {
                if (aAgrupadorPuertas.size() > 0) {
                    realmPuerta puerta1 = realmController.obtenerPuerta(aAgrupadorPuertas.get(0).getPUEId());
                    realmPuerta puerta2 = realmController.obtenerPuerta(aAgrupadorPuertas.get(1).getPUEId());
                    guardarPuerta(puerta1, puerta2, idAgrupador);
                    RealmResults<realmPuerta> aPuertas = realmController.obtenerPuertas(aAgrupadorPuertas.get(0).getPUEId());
                    iterarPuertas(aPuertas);
                } else {
                    crearDialog("ERROR", "Sus datos no se guardaron, el agrupador que seleccionó no contiene puertas.");
                    return;
                }
                guardarURL(edtURLExportacion.getText().toString());
                guardarYaExisteConfiguracionUrlNombrePuerta(edtWebService.getText().toString(), spPuertas.getSelectedItem().toString());
                crearDialog("ÉXITO", "Sus datos se guardaron correctamente.");
            } else {
                crearDialog("ERROR", "Sus datos no se guardaron, intentelo de nuevo.");
            }
        } else {
            Toast.makeText(getContext(), "Complete todos los campos para guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearDialog(String titulo, String mensaje) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void guardarURL(String url) {
        SharedPreferences.Editor editor = PREF_CONFIGURACION.edit();
        editor.putString("URL", url);
        editor.commit();
    }

    private void guardarYaExisteConfiguracionUrlNombrePuerta(String url, String nombrePuerta) {
        SharedPreferences.Editor editor = PREF_CONFIGURACION.edit();
        editor.putBoolean("CONFIGURADO", true);
        editor.putString("URL", url);
        editor.putString("NOMBREPUERTA", nombrePuerta);
        editor.commit();
    }

    private void guardarPuerta(realmPuerta puerta1, realmPuerta puerta2, int idAgrupador) {
        SharedPreferences.Editor editor = PREF_CONFIGURACION.edit();
        editor.putString("NOMBREPUERTAENTRADA", puerta1.getDescripcion());
        editor.putString("NOMBREPUERTASALIDA", puerta2.getDescripcion());
        editor.putInt("IDPUERTAENTRADA", puerta1.getPUEId());
        editor.putInt("IDPUERTASALIDA", puerta2.getPUEId());
        editor.putString("CLAVEPUERTAENTRADA", puerta1.getPUEClave());
        editor.putString("CLAVEPUERTASALIDA", puerta2.getPUEClave());
        editor.putString("GRUIDENTRADA", puerta1.getGRUID());
        editor.putString("GRUIDSALIDA", puerta2.getGRUID());
        editor.putInt("IDAGRUPADOR", idAgrupador);
        editor.commit();
    }

    private void iterarPuertas(RealmResults<realmPuerta> aPuertas) {
        for (int i = 0; i < aPuertas.size(); i++) {
            guardarGRUId(i, aPuertas.get(i));
        }
    }

    private void guardarGRUId(int total, realmPuerta puerta) {
        total++;
        SharedPreferences.Editor editor = PREF_CONFIGURACION.edit();
        editor.putString("GRUIDACTUAL" + total, puerta.getGRUID());
        editor.putInt("TOTALGRUID", total);
        editor.commit();
    }


    private void llenarSpinnerAgrupador() {
        List<realmAgrupador> aAgrupadores = realmController.obtenerAgrupadores();
        ArrayList<String> aAgrupadoresDescripcion = new ArrayList<>();
        int posicionPuertaSeleccionada = obtenerPosicionAgrupadorSeleccionado(aAgrupadores);
        for (realmAgrupador agrupador : aAgrupadores) {
            aAgrupadoresDescripcion.add(agrupador.getDescripcion());
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.item_spinner, aAgrupadoresDescripcion);
        spPuertas.setAdapter(adapter);
        spPuertas.setSelection(posicionPuertaSeleccionada);
    }

    private void llenarSpinnerAgrupadorSincronizado() {
        List<realmAgrupador> aAgrupadores = realmController.obtenerAgrupadores();
        ArrayList<String> aAgrupadoresDescripcion = new ArrayList<>();
        for (realmAgrupador agrupador : aAgrupadores) {
            aAgrupadoresDescripcion.add(agrupador.getDescripcion());
        }
        ArrayAdapter adapter = new ArrayAdapter(getContext(), R.layout.item_spinner, aAgrupadoresDescripcion);
        spPuertas.setAdapter(adapter);
    }

    private int obtenerPosicionAgrupadorSeleccionado(List<realmAgrupador> aAgrupadores) {
        int idAgrupador = PREF_CONFIGURACION.getInt("IDAGRUPADOR", 0);
        for (int i = 0; i < aAgrupadores.size(); i++) {
            if (aAgrupadores.get(i).getAGRId() == idAgrupador) {
                return i;
            }
        }
        return 0;
    }

    private void sincronizarNuevasPuertas(){
        mostrarCargandoAnillo("Obteniendo puertas...");
        realmController.borrarTablasSincronizacionPuertas();
        helperRetrofit helper = new helperRetrofit(PREF_CONFIGURACION.getString("URL",""));
        helper.actualizarPuertasConfiguracion(this, anillo);
    }

    private void mostrarCargandoAnillo(String mensaje){
        this.anillo = ProgressDialog.show(getContext(), "Sincronizando", mensaje, true, false);
    }
}
