package arenzo.alejandroochoa.ccure.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmDispositivo;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import arenzo.alejandroochoa.ccure.WebService.retrofit;

public class configuracionUnica extends AppCompatActivity {
    private final static String TAG = "configuracionUnica";

    private EditText edtNombreDispositivoUnico, edtWebServiceUnico, edtURLExportacionUnico;
    private Spinner spPuertasUnico;
    private Button btnGuardarConfiguracionUnico;
    ProgressDialog anillo = null;

    private SharedPreferences PREF_CONFIGURACION_UNICA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion_unica);
        edtNombreDispositivoUnico = (EditText)findViewById(R.id.edtNombreDispositivoUnico);
        edtWebServiceUnico = (EditText)findViewById(R.id.edtWebServiceUnico);
        edtURLExportacionUnico = (EditText)findViewById(R.id.edtURLExportacionUnico);
        spPuertasUnico = (Spinner)findViewById(R.id.spPuertasUnico);
        btnGuardarConfiguracionUnico = (Button)findViewById(R.id.btnGuardarConfiguracionUnico);
        eventosVista();
        cargarDatosVista();
        centrarTituloActionBar();
        PREF_CONFIGURACION_UNICA = getSharedPreferences("CCURE", getApplicationContext().MODE_PRIVATE);
    }


    private void eventosVista(){
        btnGuardarConfiguracionUnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDatos();
            }
        });
    }

    private void cargarDatosVista(){
        RealmController.with(this);
        realmDispositivo dispositivo = RealmController.getInstance().obtenerDispositivo();
        if (dispositivo != null){
            edtNombreDispositivoUnico.setText(dispositivo.getDescripcion().toString());
            edtWebServiceUnico.setText(dispositivo.getURLWebService().toString());
            edtURLExportacionUnico.setText(dispositivo.getURLExportacion());
        }
    }

    private void guardarDatos() {
        if (edtNombreDispositivoUnico.length() > 0 && edtURLExportacionUnico.length() > 0 && edtWebServiceUnico.length() > 0) {
            realmDispositivo dispositivo = RealmController.getInstance().obtenerDispositivo();
            boolean saberEstadoInsercion = false;
            if (dispositivo == null) {
                saberEstadoInsercion = RealmController.getInstance().insertarConfiguracion(edtNombreDispositivoUnico.getText().toString(), "A", 1, edtWebServiceUnico.getText().toString(), edtURLExportacionUnico.getText().toString(), "1");
            } else {
                saberEstadoInsercion = RealmController.getInstance().actualizarConfiguracion(edtNombreDispositivoUnico.getText().toString(), "A", 1, edtWebServiceUnico.getText().toString(), edtURLExportacionUnico.getText().toString(), "1");
            }
            if (saberEstadoInsercion) {
                guardarYaExisteConfiguracion();
                obtenerTodosDatos();
            } else {
                crearDialogError("Error", "Sus datos no se guardaron, intentelo de nuevo.");
            }
        } else {
            Toast.makeText(getApplicationContext(), "Complete todos los campos para guardar", Toast.LENGTH_SHORT).show();
        }
    }


    private void crearDialogError(String titulo,String mensaje){
        AlertDialog.Builder dialog =  new AlertDialog.Builder(getApplicationContext());
        dialog.setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }

    private void centrarTituloActionBar() {
        ArrayList<View> textViews = new ArrayList<>();

        getWindow().getDecorView().findViewsWithText(textViews, getTitle(), View.FIND_VIEWS_WITH_TEXT);

        if(textViews.size() > 0) {
            AppCompatTextView appCompatTextView = null;
            if(textViews.size() == 1) {
                appCompatTextView = (AppCompatTextView) textViews.get(0);
            } else {
                for(View v : textViews) {
                    if(v.getParent() instanceof Toolbar) {
                        appCompatTextView = (AppCompatTextView) v;
                        break;
                    }
                }
            }

            if(appCompatTextView != null) {
                ViewGroup.LayoutParams params = appCompatTextView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                appCompatTextView.setLayoutParams(params);
                appCompatTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
        }
    }

    private void mostrarMain(Context context){
        Intent intent = new Intent(context, main.class);
        startActivity(intent);
        finish();
    }

    private void guardarYaExisteConfiguracion(){
        SharedPreferences.Editor editor = PREF_CONFIGURACION_UNICA.edit();
        editor.putBoolean("CONFIGURADO", true);
        editor.commit();
    }

    private void obtenerTodosDatos(){
        mostrarCargandoAnillo();
        helperRetrofit helper = new helperRetrofit(retrofit.URL);
        helper.obtenerPersonalInfo(getApplicationContext(), this.anillo);
    }

    private void mostrarCargandoAnillo(){
        this.anillo = ProgressDialog.show(this, "Sincronizando", "Obteniendo todos los datos iniciales...", true, false);
    }

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }
}
