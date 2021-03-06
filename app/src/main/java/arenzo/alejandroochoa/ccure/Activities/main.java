package arenzo.alejandroochoa.ccure.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Helpers.mac;
import arenzo.alejandroochoa.ccure.Helpers.vista;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmUsuario;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import io.realm.RealmResults;

public class main extends AppCompatActivity implements vista {


    private final static String TAG = "main";
    private SharedPreferences PREF_MAIN;
    private Boolean hacerBusqueda = true;

    private Button btnOlvideTarjetaLogin;
    private EditText edtTarjeta;
    private ImageView imgPortadaMain;
    private TextView txtVersion;

    String URL = "";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PREF_MAIN = getSharedPreferences("CCURE", MODE_PRIVATE);
        URL = PREF_MAIN.getString("URL", "");
        comprobarConfiguracion();
        cargarElementos();
        centrarTituloActionBar();
        eventosVista();
        ocultarTeclado();
        edtTarjeta.requestFocus();
        cargarImagenDeMemoria();
        comprobarEstadoMac();
        agregarNumeroVersion();
    }

    private void cargarElementos(){
        btnOlvideTarjetaLogin = (Button)findViewById(R.id.btnOlvideTarjetaLogin);
        edtTarjeta = (EditText) findViewById(R.id.edtTarjeta);
        imgPortadaMain = (ImageView) findViewById(R.id.imgPortadaMain);
        txtVersion = (TextView) findViewById(R.id.txtVersion);
    }

    private void agregarNumeroVersion(){
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersion.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void comprobarEstadoMac(){
        if( !URL.equals("")) {
            conexion conexion = new conexion();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (conexion.isAvaliable(getApplicationContext()) && conexion.isOnline(null)) {
                    verificarMac();
                } else {
                    final int estadoPermisoLecturaImei = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
                    if (estadoPermisoLecturaImei != PackageManager.PERMISSION_GRANTED) {
                        String estadoImei = mac.obtenerEstadoMac(PREF_MAIN);
                        mac.setMac(getApplicationContext());
                        if (estadoImei.equals("I"))
                            mac.resultadoDialogNoPermitidoMac(this);
                    } else
                        mac.resultadoDialogNoPermitidoMac(this);
                }
            } else {
                if (conexion.isAvaliable(getApplicationContext()) && conexion.isOnline(null)) {
                    verificarMac();
                } else {
                    String estadoImei = mac.obtenerEstadoMac(PREF_MAIN);
                    mac.setMac(getApplicationContext());
                    if (estadoImei.equals("I"))
                        mac.resultadoDialogNoPermitidoMac(this);
                }
            }
        }
    }

    private void cargarLoginManual(){
        Intent intent = new Intent(this, loginManual.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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

    @Override
    public void eventosVista() {
        btnOlvideTarjetaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarLoginManual();
            }
        });
        edtTarjeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(hacerBusqueda && editable.length()>=2) {
                    hacerBusqueda = false;
                    esperarLectura();
                }
            }
        });

        edtTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(edtTarjeta.getWindowToken(), 0);
            }
        });
    }

    private void esperarLectura(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                definitBusquedaUsuario();
                hacerBusqueda = true;
            }
        }, 500);

    }

    private void definitBusquedaUsuario(){
        if (existenDatos())
            buscarUsuario();
        else {
            edtTarjeta.setText("");
            mostrarDialogNoExistenDatos();
        }
    }

    private void buscarUsuario(){
        RealmController.with(this);
        String noTarjeta = edtTarjeta.getText().toString().trim();
        realmUsuario personal = RealmController.getInstance().obtenerUsuarioRfid(noTarjeta);
        edtTarjeta.setText("");
        if (personal != null){
            mostrarNucleo(personal);
        }else{
            Toast.makeText(this, "El usuario no tiene permitido ingresar a la aplicación, favor de contactar al administrador.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean existenDatos(){
        RealmController.with(this);
        RealmResults<realmPersonalInfo> aPersonalInfo = RealmController.getInstance().verificarExistenciaDatos();
        return aPersonalInfo.size() > 0;
    }

    private void mostrarNucleo(realmUsuario personal){
        guardarDatosPreferencias(personal);
        Intent intent = new Intent(this, nucleo.class);
        intent.putExtra("TIPO", personal.getTipo());
        startActivity(intent);
    }

    private void guardarDatosPreferencias(realmUsuario personal){
        SharedPreferences.Editor editor = PREF_MAIN.edit();
        realmPersonalInfo personalInfo = RealmController.getInstance().obtenerInfoPersonal(personal.getNoEmpleado());
        editor.putString("TIPO", personal.getTipo());
        editor.putString("NOMBRE", personal.getNombre());
        editor.putString("NUMERO_EMPLEADO", personal.getNoEmpleado());
        editor.putString("FOTO", personalInfo.getFoto());
        editor.putString("EMPRESA", personal.getEmpresa());
        editor.apply();

    }

    private void mostrarDialogNoExistenDatos(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ATENCIÓN")
                .setMessage("El equipo no tiene los datos suficientes para iniciar sesión, es necesario sincronizar antes de comenzar.")
                .setPositiveButton("Ir a sincronizar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RealmController.getInstance().borrarTablasSincronizacion();
                        Intent intent = new Intent(getApplicationContext(), configuracionUnica.class);
                        startActivity(intent);

                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void comprobarConfiguracion(){
        boolean configurado = PREF_MAIN.getBoolean("CONFIGURADO", false);
        Log.d(TAG, "comprobarConfiguracion: "+configurado);
        if (!configurado){
            mostrarConfiguracion();
        }
    }

    private void mostrarConfiguracion(){
        Intent intent = new Intent(this, configuracionUnica.class);
        startActivity(intent);
        finish();
    }

    private void cargarImagenDeMemoria(){
        Picasso.with(getApplicationContext()).load(new File(Environment.getExternalStorageDirectory()+"/CCURE/portada.jpg")).error(R.drawable.im_logo_penia).into(imgPortadaMain);
    }

    private void ocultarTeclado(){
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void verificarMac(){
        mac.setMac(getApplicationContext());
        final helperRetrofit helperRetrofit = new helperRetrofit(URL);
        helperRetrofit.validarMac(null, null, this, null, null, null, PREF_MAIN, mac.mac, "main");
    }

}
