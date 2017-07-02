package arenzo.alejandroochoa.ccure.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Helpers.vista;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmUsuario;

public class loginManual extends AppCompatActivity implements vista {

    private final static String TAG = "loginManual";


    private EditText edtNumeroEmpleado;
    private Button btnLoginManual;
    private SharedPreferences PREF_LOGIN_MANUAL;

    private String URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manual);
        PREF_LOGIN_MANUAL = getSharedPreferences("CCURE", getApplicationContext().MODE_PRIVATE);
        URL = PREF_LOGIN_MANUAL.getString("URL", "");
        centrarTituloActionBar();
        cargarElementos();
        eventosVista();
    }


    private void cargarElementos(){
        edtNumeroEmpleado = (EditText) findViewById(R.id.edtNumeroEmpleado);
        btnLoginManual = (Button)findViewById(R.id.btnLoginManual);
    }

    @Override
    public void eventosVista(){
        btnLoginManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarUsuario();
            }
        });
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
    
    private void buscarUsuario(){
        RealmController.with(this);
        realmUsuario personal = RealmController.getInstance().obtenerUsuario(edtNumeroEmpleado.getText().toString());
        if (personal != null){
            mostrarNucleo(personal);
        }else{
            Toast.makeText(this, "El usuario no tiene permitido ingresar a la aplicación, favor de contactar al administrador.", Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarNucleo(realmUsuario personal){
        guardarDatosPreferencias(personal);
        Intent intent = new Intent(this, nucleo.class);
        intent.putExtra("TIPO", personal.getTipo());
        startActivity(intent);
    }

    private void guardarDatosPreferencias(realmUsuario personal){
        SharedPreferences.Editor editor = PREF_LOGIN_MANUAL.edit();
        realmPersonalInfo personalInfo = RealmController.getInstance().obtenerInfoPersonal(personal.getNoEmpleado());
        editor.putString("TIPO", personal.getTipo());
        editor.putString("NOMBRE", personal.getNombre());
        editor.putString("NUMERO_EMPLEADO", personal.getNoEmpleado());
        editor.putString("FOTO", personalInfo.getFoto());
        editor.putString("EMPRESA", personal.getEmpresa());
        editor.commit();

    }
}
