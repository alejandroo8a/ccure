package arenzo.alejandroochoa.ccure.Fragments;


import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmDispositivo;

public class configuracion extends Fragment {

    //TODO FALTA OBTENER ID DEL USUARIO - OBTENER LA FASE - OBTENER EL AGRUPADOR (AGRId) - CARGAR SPINNER

    private final static String TAG = "configuracion";
    private arenzo.alejandroochoa.ccure.Helpers.datosConfiguracion datosConfiguracion;

    private EditText edtNombreDispositivo, edtWebService, edtURLExportacion;
    private Spinner spPuertas;
    private Button btnGuardarConfiguracion;
    private SharedPreferences PREF_CONFIGURACION;

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
        edtNombreDispositivo = (EditText)view.findViewById(R.id.edtNombreDispositivoUnico);
        edtWebService = (EditText)view.findViewById(R.id.edtWebServiceUnico);
        edtURLExportacion = (EditText)view.findViewById(R.id.edtURLExportacionUnico);
        spPuertas = (Spinner)view.findViewById(R.id.spPuertasUnico);
        btnGuardarConfiguracion = (Button)view.findViewById(R.id.btnGuardarConfiguracion);
        PREF_CONFIGURACION = getContext().getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventosVista();
        cargarDatosVista();
    }

    private void eventosVista(){
        btnGuardarConfiguracion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
    }

    private void cargarDatosVista(){
        RealmController.with(getActivity());
        realmDispositivo dispositivo = RealmController.getInstance().obtenerDispositivo();
        if (dispositivo != null){
            edtNombreDispositivo.setText(dispositivo.getDescripcion().toString());
            edtWebService.setText(dispositivo.getURLWebService().toString());
            edtURLExportacion.setText(dispositivo.getURLExportacion());
        }
    }

    private void guardarDatos() {
        if (edtNombreDispositivo.length() > 0 && edtURLExportacion.length() > 0 && edtWebService.length() > 0) {
            realmDispositivo dispositivo = RealmController.getInstance().obtenerDispositivo();
            final int idAgrupador = RealmController.getInstance().obtenerIdAgrupador(spPuertas.getSelectedItem().toString());
            boolean saberEstadoInsercion;
            if (dispositivo == null) {
                saberEstadoInsercion = RealmController.getInstance().insertarConfiguracion(edtNombreDispositivo.getText().toString(), "A", idAgrupador, edtWebService.getText().toString(), edtURLExportacion.getText().toString(), "CONFIGURACION");
            } else {
                saberEstadoInsercion = RealmController.getInstance().actualizarConfiguracion(edtNombreDispositivo.getText().toString(), "A", idAgrupador, edtWebService.getText().toString(), edtURLExportacion.getText().toString(), "CONFIGURACION");
            }
            if (saberEstadoInsercion) {
                guardarURL(edtURLExportacion.getText().toString());
                crearDialog("Éxito", "Sus datos se guardaron correctamente");
            } else {
                crearDialog("Error", "Sus datos no se guardaron, intentelo de nuevo.");
            }
        } else {
            Toast.makeText(getContext(), "Complete todos los campos para guardar", Toast.LENGTH_SHORT).show();
        }
    }

    private void crearDialog(String titulo,String mensaje){
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getContext());
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void guardarURL(String url){
        SharedPreferences.Editor editor = PREF_CONFIGURACION.edit();
        editor.putString("URL", url);
        editor.commit();
    }

}
