package arenzo.alejandroochoa.ccure.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Helpers.vista;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmUsuario;
import io.realm.RealmResults;

public class main extends AppCompatActivity implements vista {


    final static int CODIGO_ESCRITURA = 100;
    private final static String TAG = "main";
    private SharedPreferences PREF_MAIN;
    private Boolean hacerBusqueda = true;

    private Button btnOlvideTarjetaLogin;
    private EditText edtTarjeta;
    private ImageView imgPortadaMain;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PREF_MAIN = getSharedPreferences("CCURE", getApplicationContext().MODE_PRIVATE);
        comprobarConfiguracion();
        cargarElementos();
        centrarTituloActionBar();
        eventosVista();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        edtTarjeta.requestFocus();
        cargarImagenDeMemoria();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        comprobarPermisos();
    }

    private void cargarElementos(){
        btnOlvideTarjetaLogin = (Button)findViewById(R.id.btnOlvideTarjetaLogin);
        edtTarjeta = (EditText) findViewById(R.id.edtTarjeta);
        imgPortadaMain = (ImageView) findViewById(R.id.imgPortadaMain);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarPermisos(){
        if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            //Se comprueba que fue aceptado
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                return;
        }else{
            if(!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                //No se le ha preguntado aun
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODIGO_ESCRITURA);
            }else{
                //Ha denegado
                alertaPermisos();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CODIGO_ESCRITURA){
            String permission = permissions[0];
            int result = grantResults[0];
            if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (result == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        // Se comprueba que fue aceptado
                        return;
                    }
                }else
                    alertaPermisos();
            }
        }else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void alertaPermisos(){
        AlertDialog.Builder alerta = new AlertDialog.Builder(this)
                .setTitle("Permiso denegado")
                .setCancelable(false)
                .setMessage("Debe de aceptar los permisos para usar la aplicación")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activarPermiso();
                    }
                });
        alerta.show();
    }

    private void activarPermiso(){
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
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
        editor.commit();

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

    //Comprueba si el permiso esta en el manifest
    private boolean checkPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
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

}
