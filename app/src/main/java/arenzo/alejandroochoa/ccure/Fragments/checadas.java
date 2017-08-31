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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Modelos.respuestaValidarEmpleado;
import arenzo.alejandroochoa.ccure.Modelos.validarEmpleado;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmESPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonal;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmValidaciones;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import io.realm.RealmResults;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado, edtNoTarjeta;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa, txtNombreAlerta, txtNoEmpleado;
    private SwitchButton sbTipoChecada;
    private ImageView imgFotoPerfil, imgFondoAcceso, imgPerfilAlerta, imgFondoGuardia, imgPortadaChecadas, imgConexion;
    private Button btnAceptarAlerta, btnCancelarAlerta, btnBuscarEmpleado, btnCero, btnUno, btnDos, btnTres, btnCuatro, btnCinco, btnSeis, btnSiete, btnOcho, btnNueve, btnBorrar;
    private ToggleButton tbnTipoLectura;
    ProgressDialog anillo = null;
    conexion conexion = null;

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

    static public String noEmpleado = "";

    RealmController realmController;

    BroadcastReceiver informacionPantalla, estadoRed;

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
        imgConexion = view.findViewById(R.id.imgConexion);
        imgPortadaChecadas = view.findViewById(R.id.imgPortadaChecadas);
        btnBuscarEmpleado = view.findViewById(R.id.btnBuscarEmpleado);
        btnCero = view.findViewById(R.id.btnCero);
        btnUno = view.findViewById(R.id.btnUno);
        btnDos = view.findViewById(R.id.btnDos);
        btnTres = view.findViewById(R.id.btnTres);
        btnCuatro = view.findViewById(R.id.btnCuatro);
        btnCinco = view.findViewById(R.id.btnCinco);
        btnSeis = view.findViewById(R.id.btnSeis);
        btnSiete = view.findViewById(R.id.btnSiete);
        btnOcho = view.findViewById(R.id.btnOcho);
        btnNueve = view.findViewById(R.id.btnNueve);
        btnBorrar = view.findViewById(R.id.btnBorrar);
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
        configurarReceiberRed();
        obtenerGRUIDs();
        bloquearTeclado();
        cargarImagenDeMemoria();
        conexion = new conexion();
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
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    edtNoTarjeta.requestFocus();
                    sbTipoChecada.setBackColorRes(R.color.entrada);
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
        edtNoTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(edtNoTarjeta.getWindowToken(), 0);
            }
        });
        tbnTipoLectura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                configurarModoLectura();
            }
        });

        btnCero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "0";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "1";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "2";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "3";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "4";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnCinco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "5";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnSeis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "6";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnSiete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "7";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnOcho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "8";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnNueve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noEmpleado += "9";
                edtNoEmpleado.setText(noEmpleado);
            }
        });
        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!noEmpleado.equals("")) {
                    noEmpleado = noEmpleado.substring(0, noEmpleado.length() - 1);
                    edtNoEmpleado.setText(noEmpleado);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getActivity().registerReceiver(estadoRed, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
        getActivity().registerReceiver(informacionPantalla, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(estadoRed);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(informacionPantalla);
    }

    private void bloquearTeclado(){
        edtNoEmpleado.setInputType(InputType.TYPE_NULL);
    }

    public void configurarReceiberPantalla() {
        informacionPantalla = new BroadcastReceiver() { // init your Receiver
            @Override
            public void onReceive(Context context, Intent intent) {
                sincronizarRed();
            }
        };
    }

    public void configurarReceiberRed() {
        estadoRed = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                conexion conexion = new conexion();
                if(conexion.isAvaliable(getContext()) && conexion.isOnline(anillo)){
                    imgConexion.setImageDrawable(getResources().getDrawable(R.drawable.cuadro_verde));
                }else{
                    imgConexion.setImageDrawable(getResources().getDrawable(R.drawable.cuadro_rojo));
                }
            }
        };
    }

    private void configurarModoLectura(){
        if (txtNoEmpleado.getText().toString().equals("No. Tarjeta")){
            edtNoTarjeta.setVisibility(View.GONE);
            edtNoEmpleado.setVisibility(View.VISIBLE);
            btnBuscarEmpleado.setVisibility(View.VISIBLE);
            tbnTipoLectura.setTextOff("Checar con tarjeta");
            tbnTipoLectura.setChecked(false);
            txtNoEmpleado.setText("No. Empleado");
            mostrarBotonesNumeracion();
            edtNoEmpleado.requestFocus();
        }else{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            edtNoTarjeta.requestFocus();
            edtNoTarjeta.setVisibility(View.VISIBLE);
            edtNoEmpleado.setVisibility(View.GONE);
            btnBuscarEmpleado.setVisibility(View.GONE);
            tbnTipoLectura.setTextOn("Olvidó tarjeta");
            tbnTipoLectura.setChecked(true);
            txtNoEmpleado.setText("No. Tarjeta");
            ocultarBotonesNumeracion();
            edtNoTarjeta.requestFocus();
        }
    }

    private void buscarPersonalManual(){
        imgPortadaChecadas.setVisibility(View.GONE);
        if (!edtNoEmpleado.getText().toString().equals("0") && edtNoEmpleado.getText().length() > 0) {
            ocultarTeclado();
            mostrarCargandoAnillo();
            realmPersonalPuerta personal = buscarPersonalLocalManual(edtNoEmpleado.getText().toString());
            realmPersonalInfo detallesPersonal = buscarDetallePersonaLocal(edtNoEmpleado.getText().toString());
            if (personal != null) {
                ocultarCargandoAnillo();
                mostrarAlertaEmpleadoManual(getContext(), txtResultadoChecada, imgFondoAcceso, detallesPersonal, personal, puertaClave, null);
            } else {
                if (conexion.isAvaliable(getContext())) {
                    validarEmpleadoManual();
                } else {
                    buscarEnValidacionesManual(detallesPersonal);
                }
            }
            mostrarDatosGuardia();
        }else{
            Toast.makeText(getActivity(), "El número de empleado no puede ser 0", Toast.LENGTH_SHORT).show();
        }
    }

    public void buscarEnValidacionesManual(realmPersonalInfo detallesPersonal ){
        realmValidaciones personalValidado = realmController.obtenerPersonalManualValidado(edtNoEmpleado.getText().toString(), puertaClave);
        if(personalValidado != null && !personalValidado.getNombre().equals("PERSONAL/CONTRATISTA")){
            mostrarAlertaEmpleadoManual(getContext(), txtResultadoChecada, imgFondoAcceso, detallesPersonal, null, puertaClave, personalValidado);
        }else{
            mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
            vibrarCelular(getContext());
            guardarResultadoChecadaDenegadoNoInternetManual(edtNoEmpleado.getText().toString(), detallesPersonal);
        }
        ocultarCargandoAnillo();
    }

    public void buscarEnValidacionesYaValidadoManual(String noEmpleado,  String PUEClave, String numeroEmpleado, String tipoChecada, View view){
        realmValidaciones personalValidado = realmController.obtenerPersonalManualValidado(noEmpleado, PUEClave);
        if(personalValidado != null && !personalValidado.getNombre().equals("PERSONAL/CONTRATISTA")){
            mostrarAlertaEmpleadoManual(getContext(), txtResultadoChecada, imgFondoAcceso, null, null, puertaClave, personalValidado);
        }else{
            mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
            vibrarCelular(getContext());
            guardarResultadoChecadaNoEncontradoManual(noEmpleado, PUEClave, numeroEmpleado, tipoChecada, view);
        }
        ocultarCargandoAnillo();
    }

    private void buscarPersonalRfid() {
        imgPortadaChecadas.setVisibility(View.GONE);
        ocultarTeclado();
        mostrarCargandoAnillo();
        final String noTarjeta = edtNoTarjeta.getText().toString().trim();
        realmPersonalPuerta personal = buscarPersonalLocalRfid(noTarjeta);
        if (personal != null) {
            realmPersonal detallesPersonal = buscarDetallePersonaLocalRfid(noTarjeta);
            ocultarCargandoAnillo();
            edtNoTarjeta.setText("");
            mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            guardarResultadoChecadaCorrectaRfid(detallesPersonal, puertaClave, null);
        } else {
            if (conexion.isAvaliable(getContext())) {
                validarEmpleadoRfid(noTarjeta);
            } else {
                buscarEnValidacionesRfid(noTarjeta);
            }
        }
        mostrarDatosGuardia();
    }

    public void buscarEnValidacionesRfid(String noTarjeta ){
        realmValidaciones personalValidado = realmController.obtenerPersonalRfidValidado(noTarjeta, puertaClave);
        if(personalValidado != null){
            guardarResultadoChecadaCorrectaRfid(null, puertaClave, personalValidado);
        }else{
            mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
            vibrarCelular(getContext());
            guardarResultadoChecadaDenegadoNoInternetRfid(noTarjeta);
            edtNoTarjeta.setText("");
        }
        ocultarCargandoAnillo();
    }

    public void buscarEnValidacionesYaValidadoRfid(String noTarjeta,  String PUEClave, String numeroEmpleado, String tipoChecada){
        realmValidaciones personalValidado = realmController.obtenerPersonalRfidValidado(noTarjeta, PUEClave);
        if(personalValidado != null && !personalValidado.getNombre().equals("PERSONAL/CONTRATISTA")){
            guardarResultadoChecadaCorrectaRfid(null, PUEClave, personalValidado);
        }else{
            mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
            vibrarCelular(getContext());
            guardarResultadoChecadaNoEncontradoRfid(noTarjeta, PUEClave, numeroEmpleado, tipoChecada,txtNombre, txtPuestoEmpresa, imgFotoPerfil);
        }
        ocultarCargandoAnillo();
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

    public void mostrarAlertaEmpleadoManual(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave, final realmValidaciones personalValidado){
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
                if(personalValidado == null)
                    guardarResultadoChecadaCorrectaManual(detallesPersonal, personal, PUEClave, null);
                else
                    guardarResultadoChecadaCorrectaManual(detallesPersonal, null, PUEClave, personalValidado);
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                if(personalValidado == null)
                    guardarResultadoChecadaDenegadoManual(detallesPersonal, personal, PUEClave, null);
                else
                    guardarResultadoChecadaDenegadoManual(detallesPersonal, personal, PUEClave, personalValidado);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

    public void mostrarAlertaEmpleadoValidadoManual(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final validarEmpleado empleado, final String PUEClave, final String numeroEmpleado, final String tipoChecada, final TextView txtNombre, final TextView txtPuestoEmpresa, final ImageView imgFotoPerfil, final View viewPrincipal){
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
                guardarResultadoChecadaValidadaManual(empleado, "P", PUEClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, viewPrincipal);
                builder.dismiss();
                checadas.noEmpleado = "";
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                guardarResultadoChecadaValidadaManual(empleado, "D", PUEClave, numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, viewPrincipal);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

    private void guardarResultadoChecadaCorrectaManual(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave, final realmValidaciones personalValidado){
         if(detallesPersonal != null && personal != null) {
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(), PUEClave, "P", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada,detallesPersonal.getFoto(), detallesPersonal.getNombre(), detallesPersonal.getPuesto());
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
        }else{
             realmController.insertarPersonalNuevo(personalValidado.getNoEmpleado(), personalValidado.getNoTarjeta(), PUEClave, "P", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, personalValidado.getFoto(), personalValidado.getNombre(), personalValidado.getPuesto());
             mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalValidado.getNombre(), personalValidado.getPuesto(), personalValidado.getFoto());
        }
        limpiarEditTextNoEmpleado();
    }

    private void guardarResultadoChecadaDenegadoManual(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal, final String PUEClave, final realmValidaciones personalValidado){
        if(detallesPersonal != null && personal != null) {
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(), PUEClave, "D", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, detallesPersonal.getFoto(), detallesPersonal.getNombre(), detallesPersonal.getPuesto());
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
        }else{
            realmController.insertarPersonalNuevo(personalValidado.getNoEmpleado(), personalValidado.getNoTarjeta(), PUEClave, "D", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, personalValidado.getFoto(), personalValidado.getNombre(), personalValidado.getPuesto());
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalValidado.getNombre(), personalValidado.getPuesto(), personalValidado.getFoto());
        }
    }

    public void guardarResultadoChecadaValidadaManual(final validarEmpleado empleado, final String faseIngreso, final String PUEClave, final String numeroEmpleado, final String tipoChecada, TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil, View view){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(empleado.getEmpleado().getNoEmpleado(), " ",PUEClave, faseIngreso, "N", "",numeroEmpleado, tipoChecada, empleado.getEmpleado().getFoto(), empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto());
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto(), empleado.getEmpleado().getFoto());
        if (faseIngreso.equals("P")){
            limpiarEditTextNoEmpleado(view);
        }
    }

    private void guardarResultadoChecadaDenegadoNoInternetManual(String noEmpleado, realmPersonalInfo detallesPersonal){
        ocultarCargandoAnillo();
        if(detallesPersonal!=null) {
            realmController.insertarPersonalNuevo(noEmpleado, "empty", puertaClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada, detallesPersonal.getFoto(), detallesPersonal.getNombre(), detallesPersonal.getPuesto());
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, detallesPersonal.getNombre(), detallesPersonal.getPuesto(), detallesPersonal.getFoto());
        }else {
            realmPersonalInfo personalInfo = realmController.obtenerPersonalInfoManual(noEmpleado);
            if(personalInfo != null) {
                realmController.insertarPersonalNuevo(noEmpleado, "empty", puertaClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada, "empty", personalInfo.getPuesto(), personalInfo.getFoto());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalInfo.getNombre(), personalInfo.getPuesto(), personalInfo.getFoto());
            }else {
                realmController.insertarPersonalNuevo(noEmpleado, "empty", puertaClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }
        limpiarEditTextNoEmpleado();
    }

    public void guardarResultadoChecadaNoEncontradoManual(String noEmpleado,  String PUEClave, String numeroEmpleado, String tipoChecada, View view){
        realmController = new RealmController();
        realmPersonalInfo personalInfo = realmController.obtenerPersonalInfoManual(noEmpleado);
        if(personalInfo != null) {
            realmController.insertarPersonalNuevo(noEmpleado, "empty", PUEClave, "D", "N", "",numeroEmpleado, tipoChecada, "empty", personalInfo.getNombre(), personalInfo.getPuesto());
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalInfo.getNombre(), personalInfo.getPuesto(), personalInfo.getFoto());
        }else {
            realmController.insertarPersonalNuevo(noEmpleado, "empty", PUEClave, "D", "N", "",numeroEmpleado, tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
            mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
        }
        limpiarEditTextNoEmpleado(view);
    }

    //*****RFID LECTURAS******

    public void guardarResultadoChecadaCorrectaRfid(final realmPersonal personal,  final String PUEClave, final realmValidaciones personalValidado){
        if(personal != null && !personal.getNoEmpleado().equals("0")) {
            realmPersonalInfo personalInfo = realmController.obtenerPersonalInfoRfid(personal.getNoEmpleado());
            if (personalInfo != null) {
                realmController.insertarPersonalNuevo(personalInfo.getNoEmpleado(), personal.getNoTarjeta(), puertaClave, "D", "N", "", numeroEmpleado, tipoChecada, personalInfo.getFoto(), personalInfo.getNombre(), personalInfo.getPuesto());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalInfo.getNombre(), personalInfo.getPuesto(), personalInfo.getFoto());
            } else {
                realmController.insertarPersonalNuevo("empty", personal.getNoTarjeta(), puertaClave, "D", "N", "", numeroEmpleado, tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }else {
            if (personal != null) {
                realmController.insertarPersonalNuevo(personal.getNoEmpleado(), personal.getNoTarjeta(), PUEClave, "P", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, personal.getFoto(), personal.getNombre(), personal.getEmpresa());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personal.getNombre(), personal.getEmpresa(), personal.getFoto());
            } else {
                realmController.insertarPersonalNuevo(personalValidado.getNoEmpleado(), personalValidado.getNoTarjeta(), PUEClave, "P", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, personalValidado.getFoto(), personalValidado.getNombre(), personalValidado.getPuesto());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalValidado.getNombre(), personalValidado.getPuesto(), personalValidado.getFoto());
            }
        }
    }

    public void guardarResultadoChecadaValidadaRfid(final validarEmpleado empleado, final String faseIngreso, final String PUEClave, final String numeroEmpleado, final String tipoChecada, TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil, String noTarjeta){
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(empleado.getEmpleado().getNoEmpleado(), noTarjeta, PUEClave, faseIngreso, "N", "",numeroEmpleado, tipoChecada, empleado.getEmpleado().getFoto(), empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto());
        mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, empleado.getEmpleado().getNombre(), empleado.getEmpleado().getPuesto(), empleado.getEmpleado().getFoto());
    }

    private void guardarResultadoChecadaDenegadoNoInternetRfid(String noTarjeta){
        ocultarCargandoAnillo();
        realmPersonal personal = realmController.obtenerPersonalRfid(noTarjeta);
        if(personal != null && !personal.getNoEmpleado().equals("0")) {
            realmPersonalInfo personalInfo = realmController.obtenerPersonalInfoRfid(personal.getNoEmpleado());
            if (personalInfo != null) {
                realmController.insertarPersonalNuevo(personalInfo.getNoEmpleado(), noTarjeta, puertaClave, "D", "N", "", numeroEmpleado, tipoChecada, personalInfo.getFoto(), personalInfo.getNombre(), personalInfo.getPuesto());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalInfo.getNombre(), personalInfo.getPuesto(), personalInfo.getFoto());
            } else {
                realmController.insertarPersonalNuevo("empty", noTarjeta, puertaClave, "D", "N", "", numeroEmpleado, tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }else {
            if (personal != null) {
                realmController.insertarPersonalNuevo(personal.getNoEmpleado(), personal.getNoTarjeta(), puertaClave, "D", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, personal.getFoto(), personal.getNombre(), personal.getEmpresa());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personal.getNombre(), personal.getEmpresa(), personal.getFoto());
            } else {
                realmController.insertarPersonalNuevo("empty", noTarjeta, puertaClave, "D", "N", "", PREF_CHECADAS.getString("NUMERO_EMPLEADO", "0"), tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }
    }

    public void guardarResultadoChecadaNoEncontradoRfid(String NoTarjeta,  String PUEClave, String numeroEmpleado, String tipoChecada, TextView txtNombre, TextView txtPuestoEmpresa, ImageView imgFotoPerfil){
        realmController = new RealmController();
        realmPersonal personal = realmController.obtenerPersonalRfid(NoTarjeta);
        if(personal != null && !personal.getNoEmpleado().equals("0")){
            realmPersonalInfo personalInfo = realmController.obtenerPersonalInfoRfid(personal.getNoEmpleado());
            if (personalInfo != null) {
                realmController.insertarPersonalNuevo(personalInfo.getNoEmpleado(), NoTarjeta, PUEClave, "D", "N", "", numeroEmpleado, tipoChecada, personalInfo.getFoto(), personalInfo.getNombre(), personalInfo.getPuesto());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personalInfo.getNombre(), personalInfo.getPuesto(), personalInfo.getFoto());
            } else {
                realmController.insertarPersonalNuevo("empty", NoTarjeta, PUEClave, "D", "N", "", numeroEmpleado, tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }else {
            if (personal != null) {
                realmController.insertarPersonalNuevo(personal.getNoEmpleado(), NoTarjeta, PUEClave, "D", "N", "", numeroEmpleado, tipoChecada, personal.getFoto(), personal.getNombre(), personal.getEmpresa());
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, personal.getNombre(), personal.getEmpresa(), personal.getFoto());
            } else {
                realmController.insertarPersonalNuevo("empty", NoTarjeta, PUEClave, "D", "N", "", numeroEmpleado, tipoChecada, "empty", "PERSONAL/CONTRATISTA", "NO ENCONTRADO");
                mostrarPersonal(txtNombre, txtPuestoEmpresa, imgFotoPerfil, "PERSONAL/CONTRATISTA", "NO ENCONTRADO", "empty");
            }
        }
    }

    private void decodificarBase64(String imagen){
        byte[] decodificado = Base64.decode(imagen,Base64.DEFAULT);
        Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
        imgFotoPerfil.setImageBitmap(decodificadoMap);
    }

    private void decodificarBase64Alerta(String imagen, ImageView imgPerfilAlerta){
        if(imagen != null && !imagen.equals("empty")) {
            byte[] decodificado = Base64.decode(imagen, Base64.DEFAULT);
            Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
            imgPerfilAlerta.setImageBitmap(decodificadoMap);
        }else
            imgPerfilAlerta.setImageDrawable(getResources().getDrawable(R.drawable.ic_card));
    }

    private void activarTipoChecada(){
        sbTipoChecada.setChecked(true);
        sbTipoChecada.setBackColorRes(R.color.entrada);
        tipoChecada = "1";
        nombreCaseta = PREF_CHECADAS.getString("NOMBREPUERTAENTRADA","");
        PUEId = PREF_CHECADAS.getInt("IDPUERTAENTRADA", 0);
        puertaClave = PREF_CHECADAS.getString("CLAVEPUERTAENTRADA","");
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        edtNoTarjeta.requestFocus();
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

    private realmPersonal buscarDetallePersonaLocalRfid(String noTarjeta){
        return realmController.obtenerInfoPersonalRfid(noTarjeta);
    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void validarEmpleadoManual(){
        helperRetrofit helper = new helperRetrofit(URL);
        helper.ValidarEmpleadoManual(edtNoEmpleado.getText().toString(),"empty",puertaClave, getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada,  numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, getView(), this);
    }

    private void validarEmpleadoRfid(String noTarjeta){
        helperRetrofit helper = new helperRetrofit(URL);
        helper.ValidarEmpleadoRfid("empty", noTarjeta,puertaClave, getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada, String.valueOf(PUEId), numeroEmpleado, tipoChecada, txtNombre, txtPuestoEmpresa, imgFotoPerfil, this);
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
        if (!PREF_CHECADAS.getString("FOTO","empty").equals("empty"))
            decodificarBase64(PREF_CHECADAS.getString("FOTO","NO"));
        else
            ponerImagenDefault();
    }

    private void ponerImagenDefault(){
        Picasso.with(getContext()).load(R.drawable.ic_card).into(imgFotoPerfil);
    }

    private void configurarAlertaValidada(TextView txtNombreAlerta, ImageView imgPerfilAlerta, validarEmpleado empleado){
        txtNombreAlerta.setText(empleado.getEmpleado().getNombre());
        if(!empleado.getEmpleado().getFoto().equals("empty")){
            decodificarBase64Alerta(empleado.getEmpleado().getFoto(), imgPerfilAlerta);
        }else
            ponerImagenDefaultAlerta(imgPerfilAlerta);
    }

    private void configurarAlerta(TextView txtNombreAlerta, ImageView imgPerfilAlerta, realmPersonalInfo empleado){
        txtNombreAlerta.setText(empleado.getNombre());
        if(!empleado.getFoto().equals("empty")){
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

    public void eliminarChecadaPersonal(String fecha, final RealmController realmController){
        realmController.eliminarRegistroPersonal(fecha);
    }

    private void mostrarBotonesNumeracion(){
        btnCero.setVisibility(View.VISIBLE);
        btnUno.setVisibility(View.VISIBLE);
        btnDos.setVisibility(View.VISIBLE);
        btnTres.setVisibility(View.VISIBLE);
        btnCuatro.setVisibility(View.VISIBLE);
        btnCinco.setVisibility(View.VISIBLE);
        btnSeis.setVisibility(View.VISIBLE);
        btnSiete.setVisibility(View.VISIBLE);
        btnOcho.setVisibility(View.VISIBLE);
        btnNueve.setVisibility(View.VISIBLE);
        btnBorrar.setVisibility(View.VISIBLE);
    }

    private void ocultarBotonesNumeracion(){
        btnCero.setVisibility(View.GONE);
        btnUno.setVisibility(View.GONE);
        btnDos.setVisibility(View.GONE);
        btnTres.setVisibility(View.GONE);
        btnCuatro.setVisibility(View.GONE);
        btnCinco.setVisibility(View.GONE);
        btnSeis.setVisibility(View.GONE);
        btnSiete.setVisibility(View.GONE);
        btnOcho.setVisibility(View.GONE);
        btnNueve.setVisibility(View.GONE);
        btnBorrar.setVisibility(View.GONE);
    }

    private void limpiarEditTextNoEmpleado(){
        noEmpleado = "";
        edtNoEmpleado.setText("");
        txtNoEmpleado.setText("No. Empleado");
        configurarModoLectura();
    }

    private void limpiarEditTextNoEmpleado(View view){
        ponerReferenciasObjetos(view);
        noEmpleado = "";
        edtNoEmpleado.setText("");
        txtNoEmpleado.setText("No. Empleado");
        configurarModoLectura();
    }

    private void ponerReferenciasObjetos(View view){
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
        btnCero = view.findViewById(R.id.btnCero);
        btnUno = view.findViewById(R.id.btnUno);
        btnDos = view.findViewById(R.id.btnDos);
        btnTres = view.findViewById(R.id.btnTres);
        btnCuatro = view.findViewById(R.id.btnCuatro);
        btnCinco = view.findViewById(R.id.btnCinco);
        btnSeis = view.findViewById(R.id.btnSeis);
        btnSiete = view.findViewById(R.id.btnSiete);
        btnOcho = view.findViewById(R.id.btnOcho);
        btnNueve = view.findViewById(R.id.btnNueve);
        btnBorrar = view.findViewById(R.id.btnBorrar);
        tbnTipoLectura = view.findViewById(R.id.tbnTipoLectura);
    }

    private void cargarImagenDeMemoria(){
        Picasso.with(getContext()).load(new File(Environment.getExternalStorageDirectory()+"/CCURE/portada.jpg")).error(R.drawable.im_logo_penia).into(imgPortadaChecadas);
    }
}
