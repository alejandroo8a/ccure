package arenzo.alejandroochoa.ccure;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class nucleo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private final static String TAG = "nucleo";
    TextView txtNombreNavigation, txtNumeroEmpleadoNavigation,txtPuertaNavigation;
    CircularImageView imageProfesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nucleo);
        cargarElementos();
        centrarTituloActionBar();
    }

    private void cargarElementos(){
        Toolbar toolbar = añadirToolbar();
        añadirGestoNavigationDrawer(toolbar);
        View headerLayout = muestraChecadas();
        txtNombreNavigation = (TextView) headerLayout.findViewById(R.id.txtNombreNavigation);
        txtNumeroEmpleadoNavigation = (TextView) headerLayout.findViewById(R.id.txtNumeroEmpleadoNavigation);
        txtPuertaNavigation = (TextView) headerLayout.findViewById(R.id.txtPuertaNavigation);
        imageProfesor = (CircularImageView) headerLayout.findViewById(R.id.perfil);

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

    private View muestraChecadas(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main_content, new checadas());
        tx.commit();
        navigationView.setNavigationItemSelectedListener(this);
        return headerLayout;
    }

    //SE USA PARA CAPTURAR EL BOTON DE RETROSESO
    @Override
    public void onBackPressed() {
        //AlertDialog
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
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
