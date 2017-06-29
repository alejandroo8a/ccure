package arenzo.alejandroochoa.ccure.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Fragments.checadas;
import arenzo.alejandroochoa.ccure.Fragments.configuracion;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Fragments.sincronizacion;
import retrofit2.Retrofit;

public class nucleo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = "nucleo";
    TextView txtNombreNavigation, txtNumeroEmpleadoNavigation,txtPuertaNavigation;
    CircularImageView imgEmpleado;
    NavigationView nav_view;

    String tipoUsuario;
    private SharedPreferences PREF_NUCLEO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleo);
        cargarElementos();
        centrarTituloActionBar();
        habilitarItemsNavigation();
        PREF_NUCLEO = getSharedPreferences("CCURE", getApplicationContext().MODE_PRIVATE);
        configurarInformacionNavigationDrawer();
    }

    private void cargarElementos(){
        Toolbar toolbar = añadirToolbar();
        añadirGestoNavigationDrawer(toolbar);
        View headerLayout = crearVista();
        txtNombreNavigation = headerLayout.findViewById(R.id.txtNombreNavigation);
        txtNumeroEmpleadoNavigation = headerLayout.findViewById(R.id.txtNumeroEmpleadoNavigation);
        txtPuertaNavigation = headerLayout.findViewById(R.id.txtPuertaNavigation);
        imgEmpleado =  headerLayout.findViewById(R.id.perfil);
        nav_view = (NavigationView)findViewById(R.id.nav_view);
    }

    private Toolbar añadirToolbar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void añadirGestoNavigationDrawer(Toolbar toolbar){
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    private View crearVista(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        this.tipoUsuario = obtenerTipoUsuario();
        mostrarPantallaCorrecta(tx, this.tipoUsuario);
        tx.commit();
        navigationView.setNavigationItemSelectedListener(this);
        return headerLayout;
    }

    private void habilitarItemsNavigation(){
        Menu menuNav = nav_view.getMenu();
        MenuItem item = null;
        switch (this.tipoUsuario){
            case "G":
                item = menuNav.findItem(R.id.configuración);
                item.setEnabled(false);
                return;
            case "C":
                item = menuNav.findItem(R.id.checadas);
                item.setEnabled(false);
                item = menuNav.findItem(R.id.sincronizar);
                item.setEnabled(false);
                return;
            case "A":
                item = menuNav.findItem(R.id.checadas);
                item.setEnabled(false);
                item = menuNav.findItem(R.id.sincronizar);
                item.setEnabled(false);
                return;
        }
    }

    private String obtenerTipoUsuario(){
        return (getIntent().getExtras().getString("TIPO"));
    }

    private void mostrarPantallaCorrecta(FragmentTransaction tx ,String tipoUsuario){
        switch (tipoUsuario){
            case "G":
                tx.replace(R.id.main_content, new checadas());
                return;
            case "C":
                tx.replace(R.id.main_content, new configuracion());
                return;
            case "A":
                tx.replace(R.id.main_content, new configuracion());
                return;
        }
    }

    //SE USA PARA CAPTURAR EL BOTON DE RETROSESO
    @Override
    public void onBackPressed() {
        alertCerrarSesion();
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Fragment fragment = null;
        switch (id){
            case R.id.checadas:
                fragment = new checadas();
                break;
            case R.id.configuración:
                fragment = new configuracion();
                break;
            case R.id.sincronizar:
                fragment = new sincronizacion();
                break;
        }
        if(R.id.cerrarSesion != id){
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.main_content, fragment).commit();
        }else{
            alertCerrarSesion();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void alertCerrarSesion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Está seguro de cerrar sesión?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getApplicationContext(), main.class);
                        getApplicationContext().startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("Cancelar",null);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void configurarInformacionNavigationDrawer(){
        txtNombreNavigation.setText(PREF_NUCLEO.getString("NOMBRE","Sin nombre"));
        txtNumeroEmpleadoNavigation.setText(PREF_NUCLEO.getString("NUMERO_EMPLEADO","Sin número"));
        if (PREF_NUCLEO.getString("FOTO","NO").equals("NO"))
            decodificarBase64(PREF_NUCLEO.getString("FOTO","NO"));
        else
            ponerImagenDefault();
    }

    private void decodificarBase64(String imagen){
        byte[] decodificado = Base64.decode(imagen,Base64.DEFAULT);
        Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
        imgEmpleado.setImageBitmap(decodificadoMap);
    }

    private void ponerImagenDefault(){
        Picasso.with(this).load(R.drawable.ic_card).into(imgEmpleado);
    }
}
