package arenzo.alejandroochoa.ccure.Fragments;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Modelos.validarEmpleado;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import io.realm.Realm;
import io.realm.RealmResults;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado, edtNoTarjeta;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa, txtNombreAlerta, txtNoEmpleado;
    private SwitchButton sbTipoChecada;
    private ImageView imgFotoPerfil, imgFondoAcceso, imgPerfilAlerta, imgFondoGuardia;
    private Button btnAceptarAlerta, btnCancelarAlerta, btnBuscarEmpleado;
    private ToggleButton tbnTipoLectura;
    ProgressDialog anillo = null;

    private String tipoChecada;
    private String nombreCaseta;
    private int PUEId;
    private String puertaClave;
    private SharedPreferences PREF_CHECADAS;
    private String URL;
    private String numeroEmpleado;
    private String GRUId;
    private int totalGRUId;
    private Boolean hacerBusqueda = true;
    private ArrayList<String> aGRUIDs = new ArrayList<>();

    private realmPersonalPuerta personal;
    RealmController realmController;

    BroadcastReceiver informacionPantalla;

    public checadas() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_checadas, container, false);
        edtNoEmpleado = view.findViewById(R.id.edtNoEmpleado);
        edtNoTarjeta = view.findViewById(R.id.edtNoTarjeta);
        txtCaseta = view.findViewById(R.id.txtCaseta);
        txtResultadoChecada = view.findViewById(R.id.txtResultadoChecada);
        txtNombre = view.findViewById(R.id.txtNombre);
        txtNoEmpleado = view.findViewById(R.id.txtNoEmpleado);
        txtPuestoEmpresa = view.findViewById(R.id.txtPuestoEmpresa);
        sbTipoChecada =  view.findViewById(R.id.sbTipoChecada);
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil);
        imgFondoAcceso = view.findViewById(R.id.imgFondoAcceso);
        imgFondoGuardia = view.findViewById(R.id.imgFondoGuardia);
        btnBuscarEmpleado = view.findViewById(R.id.btnBuscarEmpleado);
        tbnTipoLectura = view.findViewById(R.id.tbnTipoLectura);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ocultarDatosGuardia();
        PREF_CHECADAS = getContext().getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        URL = PREF_CHECADAS.getString("URL", "");
        totalGRUId = PREF_CHECADAS.getInt("TOTALGRUID", 0);
        numeroEmpleado = PREF_CHECADAS.getString("NUMERO_EMPLEADO","0");
        activarTipoChecada();
        configurarChecadas();
        configurarReceiberPantalla();
        obtenerGRUIDs();
        realmController = new RealmController(getActivity().getApplication());
        btnBuscarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buscarPersonalManual();
            }
        });
        sbTipoChecada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sbTipoChecada.setBackColorRes(R.color.accent);
                    tipoChecada = "1";
                    nombreCaseta = PREF_CHECADAS.getString("NOMBREPUERTAENTRADA","");
                    PUEId = PREF_CHECADAS.getInt("IDPUERTAENTRADA", 0);
                    puertaClave = PREF_CHECADAS.getString("CLAVEPUERTAENTRADA","");
                }
                else {
                    sbTipoChecada.setBackColorRes(R.color.primary);
                    tipoChecada = "2";
                    nombreCaseta = PREF_CHECADAS.getString("NOMBREPUERTASALIDA","");
                    PUEId = PREF_CHECADAS.getInt("IDPUERTASALIDA", 0);
                    puertaClave = PREF_CHECADAS.getString("CLAVEPUERTASALIDA","");
                }
                txtCaseta.setText(nombreCaseta);
            }
        });

        edtNoTarjeta.addTextChangedListener(new TextWatcher() {
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
        tbnTipoLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configurarModoLectura();
            }
        });
    }

    public void configurarReceiberPantalla() {
        informacionPantalla = new BroadcastReceiver() { // init your Receiver
            @Override
            public void onReceive(Context context, Intent intent) {
                sincronizarRed();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        getContext().registerReceiver(informacionPantalla, filter);
    }

    private void configurarModoLectura(){
        if (txtNoEmpleado.getText().toString().equals("No. Tarjeta")){
            edtNoTarjeta.setVisibility(View.GONE);
            edtNoEmpleado.setVisibility(View.VISIBLE);
            btnBuscarEmpleado.setVisibility(View.VISIBLE);
            tbnTipoLectura.setTextOff("Checar con tarjeta");
            tbnTipoLectura.setChecked(false);
            txtNoEmpleado.setText("No. Empleado");
        }else{
            edtNoTarjeta.setVisibility(View.VISIBLE);
            edtNoEmpleado.setVisibility(View.GONE);
            btnBuscarEmpleado.setVisibility(View.GONE);
            tbnTipoLectura.setTextOn("Olvidó tarjeta");
            tbnTipoLectura.setChecked(true);
            txtNoEmpleado.setText("No. Tarjeta");
        }
    }

    private void buscarPersonalManual(){
        if (!edtNoEmpleado.getText().toString().equals("0")) {
            ocultarTeclado();
            mostrarCargandoAnillo();
            realmPersonalPuerta personal = buscarPersonalLocalManual(edtNoEmpleado.getText().toString());
            realmPersonalInfo detallesPersonal = buscarDetallePersonaLocal(edtNoEmpleado.getText().toString());
            if (personal != null) {
                ocultarCargandoAnillo();
                mostrarDatosGuardia();
                mostrarAlertaEmpleadoManual(getContext(), txtResultadoChecada, imgFondoAcceso, detallesPersonal, personal, puertaClave);
            } else {
                conexion conexion = new conexion();
                if (conexion.isAvaliable(getContext())) {
                    validarEmpleadoManual();
                    mostrarDatosGuardia();
                } else {
                    mostrarDatosGuardia();
                    mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                    vibrarCelular(getContext());
                    guardarResultadoChecadaDenegadoNoInternetManual(edtNoEmpleado.getText().toString());
                }
            }
            limpiarEditText();
        }else{
            Toast.makeText(getActivity(), "El número de empleado no puede ser 0", Toast.LENGTH_SHORT).show();
        }
    }

    private void buscarPersonalRfid() {
        ocultarTeclado();
        mostrarCargandoAnillo();
        final String noTarjeta = edtNoTarjeta.getText().toString().trim();
        realmPersonalPuerta personal = buscarPersonalLocalRfid(noTarjeta);
        if (personal != null) {
            mostrarDatosGuardia();
            realmPersonalInfo detallesPersonal = buscarDetallePersonaLocal(personal.getNoEmpleado());
            ocultarCargandoAnillo();
            edtNoTarjeta.setText("");
            mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            guardarResultadoChecadaCorrectaRfid(detallesPersonal, personal, puertaClave);
        } else {
            conexion conexion = new conexion();
            if (conexion.isAvaliable(getContext())) {
                validarEmpleadoRfid(noTarjeta);
                mostrarDatosGuardia();
            } else {
                mostrarDatosGuardia();
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(getContext());
                guardarResultadoChecadaDenegadoNoInternetRfid(noTarjeta);
                edtNoTarjeta.setText("");
            }
        }
        limpiarEditText();
    }

    private void esperarLectura(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                buscarPersonalRfid();
                hacerBusqueda = true;
            }
        }, 500);

    }

    private void obtenerGRUIDs(){
        for (int i = 0 ; i <totalGRUId ; i++){
            int x = i;
            x++;
            aGRUIDs.add(PREF_CHECADAS.getString("GRUIDACTUAL"+x,"0"));
        }
    }

    public void mostrarAlertaEmpleadoManual(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave){
        View view = LayoutInflater.from(context).inflate(R.layout.item_checada, null);
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setView(view);
        btnAceptarAlerta = view.findViewById(R.id.btnAceptarAlerta);
        btnCancelarAlerta = view.findViewById(R.id.btnCancelarAlerta);
        txtNombreAlerta = view.findViewById(R.id.txtNombreAlerta);
        imgPerfilAlerta = view.findViewById(R.id.imgPerfilAlerta);
        configurarAlerta(txtNombreAlerta, imgPerfilAlerta, detallesPersonal);
        btnAceptarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarResultadoChecadaCorrectaManual(detallesPersonal, personal, PUEClave);
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                guardarResultadoChecadaDenegadoManual(detallesPersonal, personal, PUEClave);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

    public void mostrarAlertaEmpleadoValidadoManual(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final validarEmpleado empleado, final String PUEClave, final String numeroEmpleado, final String tipoChecada, final TextView txtNombre, final TextView txtPuestoEmpresa, final ImageView imgFotoPerfil){
        View view = LayoutInflater.from(context).inflate(R.layout.item_checada, null);
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setView(view);
        btnAceptarAlerta = view.findViewById(R.id.btnAceptarAlerta);
        btnCancelarAlerta = view.findViewById(R.id.btnCancelarAlerta);
        txtNombreAlerta = view.findViewById(R.id.txtNombreAlerta);
        imgPerfilAlerta = view.findViewById(R.id.imgPerfilAlerta);
        configurarAlertaValidada(txtNombreAlerta, imgPerfilAlerta, empleado);
        btnAceptarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarResultadoChecadaValidadaManual(empleado, "P", PUEClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil);
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                guardarResultadoChecadaValidadaManual(empleado, "D", PUEClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

//FUNCIONA
    private void guardarResultadoChecadaCorrectaManual(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave){
        if (personal != null)
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(),PUEClave, "P", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        else
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), " ", PUEClave, "P", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
    }
    //FUNCIONA
    private void guardarResultadoChecadaDenegadoManual(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave){
        if (personal != null)
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(),PUEClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        else
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), " ", PUEClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
    }
//CALAR
    public void guardarResultadoChecadaValidadaManual(final validarEmpleado empleado, final String faseIngreso, final String PUEClave, final String numeroEmpleado, final String tipoChecada, TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(empleado.getEmpleado().getNoEmpleado(), " ",PUEClave, faseIngreso, "N", "",numeroEmpleado, tipoChecada);
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto(), empleado.getEmpleado().getFoto());
    }

//FUNCIONA
    private void guardarResultadoChecadaDenegadoNoInternetManual(String noEmpleado){
        ocultarCargandoAnillo();
        realmController.insertarPersonalNuevo(noEmpleado, " ", puertaClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
    }
//FUNCIONA
    public void guardarResultadoChecadaNoEncontradoManual(String noEmpleado,  String PUEClave, String numeroEmpleado, String tipoChecada){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(noEmpleado, " ", PUEClave, "D", "N", "",numeroEmpleado, tipoChecada);
    }
    //RFID LECTURAS
    //FUNCIONA
    public void guardarResultadoChecadaCorrectaRfid(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave){
        realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(), PUEClave, "P", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada);
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
    }

    //CALAR
    public void guardarResultadoChecadaValidadaRfid(final validarEmpleado empleado, final String faseIngreso, final String PUEClave, final String numeroEmpleado, final String tipoChecada, TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(empleado.getEmpleado().getNoEmpleado(), " ",PUEClave, faseIngreso, "N", "",numeroEmpleado, tipoChecada);
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto(), empleado.getEmpleado().getFoto());
    }
    //FUNCIONA
    private void guardarResultadoChecadaDenegadoNoInternetRfid(String noTarjeta){
        ocultarCargandoAnillo();
        realmController.insertarPersonalNuevo(" ", noTarjeta, puertaClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
    }
    //FUNCIONA
    public void guardarResultadoChecadaNoEncontradoRfid(String NoTarjeta,  String PUEClave, String numeroEmpleado, String tipoChecada){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(" ", NoTarjeta, PUEClave, "D", "N", "",numeroEmpleado, tipoChecada);
    }

    private void decodificarBase64(String imagen){
        byte[] decodificado = Base64.decode(imagen,Base64.DEFAULT);
        Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
        imgFotoPerfil.setImageBitmap(decodificadoMap);
    }

    private void decodificarBase64Alerta(String imagen, ImageView imgPerfilAlerta){
        byte[] decodificado = Base64.decode(imagen,Base64.DEFAULT);
        Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
        imgPerfilAlerta.setImageBitmap(decodificadoMap);
    }

    private void activarTipoChecada(){
        sbTipoChecada.setChecked(true);
        sbTipoChecada.setBackColorRes(R.color.accent);
        tipoChecada = "1";
        nombreCaseta = PREF_CHECADAS.getString("NOMBREPUERTAENTRADA","");
        PUEId = PREF_CHECADAS.getInt("IDPUERTAENTRADA", 0);
        puertaClave = PREF_CHECADAS.getString("CLAVEPUERTAENTRADA","");
    }

    private realmPersonalPuerta buscarPersonalLocalManual(String numeroEmpleado){
        for (int i = 0 ; i < aGRUIDs.size() ; i++){
            realmPersonalPuerta personalPuerta = realmController.obtenerPersonalManual(numeroEmpleado, aGRUIDs.get(i));
            if (personalPuerta != null) {
                GRUId = aGRUIDs.get(i);
                return personalPuerta;
            }
        }
        return null;
    }

    private realmPersonalPuerta buscarPersonalLocalRfid(String numeroTarjeta){
        for (int i = 0 ; i < aGRUIDs.size() ; i++){
            realmPersonalPuerta personalPuerta = realmController.obtenerPersonalRfid(numeroTarjeta, aGRUIDs.get(i));
            if (personalPuerta != null) {
                GRUId = aGRUIDs.get(i);
                return personalPuerta;
            }
        }
        return null;
    }

    private realmPersonalInfo buscarDetallePersonaLocal(String numeroEmpleado){
        return realmController.obtenerInfoPersonal(numeroEmpleado);
    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void limpiarEditText(){
        edtNoEmpleado.setText("");
    }

    private void validarEmpleadoManual(){
        helperRetrofit helper = new helperRetrofit(URL);
        helper.ValidarEmpleadoManual(edtNoEmpleado.getText().toString(),"empty",puertaClave, getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada,  numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil);
    }

    private void validarEmpleadoRfid(String noTarjeta){
        helperRetrofit helper = new helperRetrofit(URL);
        helper.ValidarEmpleadoRfid("empty", noTarjeta,puertaClave, getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada, String.valueOf(PUEId), numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil);
        edtNoTarjeta.setText("");
    }

    private void mostrarCargandoAnillo(){
        this.anillo = ProgressDialog.show(getContext(), "Buscando", "Buscando personal...", true, false);
    }

    private void ocultarCargandoAnillo(){
        this.anillo.dismiss();
    }

    private void mostrarPermitido(TextView txtResultadoChecada, ImageView imgFondoAcceso){
        imgFondoAcceso.setColorFilter(Color.parseColor("#ff669900"));
        txtResultadoChecada.setText("Acceso correcto");
    }

    private void mostrarDenegado(TextView txtResultadoChecada, ImageView imgFondoAcceso){
        txtResultadoChecada.setText("Acceso Denegado");
        imgFondoAcceso.setColorFilter(Color.parseColor("#ffcc0000"));
    }

    private void mostrarPersonal(TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil, String nombre, String puesto, String foto ){
        txtNombre.setText(nombre);
        txtPuestoEmpresa.setText(puesto);
        decodificarBase64Alerta(foto, imgFotoPerfil);

    }

    static public void vibrarCelular(Context context){
        Vibrator vibrador = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrador.vibrate(1000);
    }

    private void configurarChecadas(){
        txtNombre.setText(PREF_CHECADAS.getString("NOMBRE","Sin nombre"));
        txtCaseta.setText(PREF_CHECADAS.getString("NOMBREPUERTAENTRADA","Sin nombre"));
        txtPuestoEmpresa.setText("Guardia de " + PREF_CHECADAS.getString("EMPRESA", "Sin empresa"));
        if (!PREF_CHECADAS.getString("FOTO","Base64Foto").equals("Base64Foto"))
            decodificarBase64(PREF_CHECADAS.getString("FOTO","NO"));
        else
            ponerImagenDefault();
    }

    private void ponerImagenDefault(){
        Picasso.with(getContext()).load(R.drawable.ic_card).into(imgFotoPerfil);
    }

    private void configurarAlertaValidada(TextView txtNombreAlerta, ImageView imgPerfilAlerta, validarEmpleado empleado){
        txtNombreAlerta.setText(empleado.getEmpleado().getNombre());
        if(!empleado.getEmpleado().getFoto().equals("")){
            decodificarBase64Alerta(empleado.getEmpleado().getFoto(), imgPerfilAlerta);
        }else
            ponerImagenDefaultAlerta(imgPerfilAlerta);
    }

    private void configurarAlerta(TextView txtNombreAlerta, ImageView imgPerfilAlerta, realmPersonalInfo empleado){
        txtNombreAlerta.setText(empleado.getNombre());
        if(!empleado.getFoto().equals("")){
            decodificarBase64Alerta(empleado.getFoto(), imgPerfilAlerta);
        }else
            ponerImagenDefaultAlerta(imgPerfilAlerta);
    }

    private void ponerImagenDefaultAlerta(ImageView imgPerfilAlerta){
        Picasso.with(getContext()).load(R.drawable.ic_card).into(imgPerfilAlerta);
    }

    private void ocultarDatosGuardia(){
        txtResultadoChecada.setVisibility(View.GONE);
        imgFotoPerfil.setVisibility(View.GONE);
        txtPuestoEmpresa.setVisibility(View.GONE);
        txtNombre.setVisibility(View.GONE);
        imgFondoAcceso.setVisibility(View.GONE);
        imgFondoGuardia.setVisibility(View.GONE);
    }

    private void mostrarDatosGuardia(){
        txtResultadoChecada.setVisibility(View.VISIBLE);
        imgFotoPerfil.setVisibility(View.VISIBLE);
        txtPuestoEmpresa.setVisibility(View.VISIBLE);
        txtNombre.setVisibility(View.VISIBLE);
        imgFondoAcceso.setVisibility(View.VISIBLE);
        imgFondoGuardia.setVisibility(View.VISIBLE);
    }

    private void sincronizarRed(){
        conexion conexion = new conexion();
        if (conexion.isAvaliable(getContext())) {
            RealmResults<realmESPersonal> resultado = realmController.obtenerRegistros();
            if (resultado.size() > 0) {
                helperRetrofit helper = new helperRetrofit(URL);
                for (int i = 0; i < resultado.size(); i++) {
                    realmESPersonal persona = resultado.get(i);
                    helper.actualizarChecadasReposo(persona.getNoEmpleado(), persona.getNoTarjeta(), persona.getPUEClave(), persona.getFechaHoraEntrada(), getContext(), persona.getFaseIngreso(), realmController);
                }
            }
        }
    }

    public void eliminarChecadaPersonal(String noEmpleado, String noTarjeta, String PUEClave, final RealmController realmController){
        realmController.eliminarRegistroPersonal(noEmpleado, noTarjeta, PUEClave);
    }


}
