package arenzo.alejandroochoa.ccure.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Fragments.configuracion;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Helpers.vista;

public class main extends AppCompatActivity implements vista {

    //TODO HACER LOGIN
    final static int CODIGO_ESCRITURA = 100;
    private final static String TAG = "main";
    private SharedPreferences PREF_MAIN;

    private Button btnOlvideTarjetaLogin;


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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onPostResume() {
        super.onPostResume();
        comprobarPermisos();
    }

    private void cargarElementos(){
        btnOlvideTarjetaLogin = (Button)findViewById(R.id.btnOlvideTarjetaLogin);
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
    }

    //Comprueba si el permiso esta en el manifest
    private boolean checkPermission(String permission){
        int result = this.checkCallingOrSelfPermission(permission);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void comprobarConfiguracion(){
        if (!PREF_MAIN.getBoolean("CONFIGURADO", false)){
            mostrarConfiguracion();
        }
    }

    private void mostrarConfiguracion(){
        Intent intent = new Intent(this, configuracionUnica.class);
        startActivity(intent);
        finish();
    }

}