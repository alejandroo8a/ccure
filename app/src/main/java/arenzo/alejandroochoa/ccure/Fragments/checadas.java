package arenzo.alejandroochoa.ccure.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
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

import com.kyleduo.switchbutton.SwitchButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import arenzo.alejandroochoa.ccure.Helpers.conexion;
import arenzo.alejandroochoa.ccure.Modelos.validarEmpleado;
import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.Realm.realmPuerta;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa, txtNombreAlerta;
    private SwitchButton sbTipoChecada;
    private ImageView imgFotoPerfil, imgFondoAcceso, imgPerfilAlerta;
    private Button btnAceptarAlerta, btnCancelarAlerta, btnBuscarEmpleado;
    ProgressDialog anillo = null;

    private String tipoChecada;
    private String nombreCaseta;
    private int PUEId;
    private String puertaClave;
    private String PUEClave;
    private SharedPreferences PREF_CHECADAS;
    private String URL;
    private String numeroEmpleado;
    private String GRUId;
    private int totalGRUId;
    private ArrayList<String> aGRUIDs = new ArrayList<>();

    private realmPersonalPuerta personal;
    RealmController realmController;

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
        txtCaseta = view.findViewById(R.id.txtCaseta);
        txtResultadoChecada = view.findViewById(R.id.txtResultadoChecada);
        txtNombre = view.findViewById(R.id.txtNombre);
        txtPuestoEmpresa = view.findViewById(R.id.txtPuestoEmpresa);
        sbTipoChecada =  view.findViewById(R.id.sbTipoChecada);
        imgFotoPerfil = view.findViewById(R.id.imgFotoPerfil);
        imgFondoAcceso = view.findViewById(R.id.imgFondoAcceso);
        btnBuscarEmpleado = view.findViewById(R.id.btnBuscarEmpleado);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PREF_CHECADAS = getContext().getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        URL = PREF_CHECADAS.getString("URL", "");
        totalGRUId = PREF_CHECADAS.getInt("TOTALGRUID", 0);
        numeroEmpleado = PREF_CHECADAS.getString("NUMERO_EMPLEADO","0");
        activarTipoChecada();
        configurarChecadas();
        obtenerGRUIDs();
        realmController = new RealmController(getActivity().getApplication());
        btnBuscarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtNoEmpleado.getText().toString().equals("0")) {
                    ocultarTeclado();
                    mostrarCargandoAnillo();
                    realmPersonalPuerta personal = buscarPersonalLocal(edtNoEmpleado.getText().toString());
                    realmPersonalInfo detallesPersonal = buscarDetallePersonaLocal(edtNoEmpleado.getText().toString());
                    if (personal != null) {
                        buscarPUEClave();
                        ocultarCargandoAnillo();
                        mostrarAlertaEmpleado(getContext(), txtResultadoChecada, imgFondoAcceso, detallesPersonal, personal);
                    } else {
                        conexion conexion = new conexion();
                        if (conexion.isAvaliable(getContext())) {
                            validarEmpleado();
                        } else {
                            guardarResultadoChecadaDenegadoNoInternet(edtNoEmpleado.getText().toString());
                        }
                    }
                    limpiarEditText();
                }else{
                    Toast.makeText(getActivity(), "El número de empleado no puede ser 0", Toast.LENGTH_SHORT).show();
                }
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
    }

    private void buscarPUEClave(){
        realmPuerta puerta = realmController.obtenerPUEClave(PUEId, GRUId);
        if (puerta != null)
            PUEClave = puerta.getPUEClave();
    }

    private void obtenerGRUIDs(){
        for (int i = 0 ; i <totalGRUId ; i++){
            int x = i;
            x++;
            aGRUIDs.add(PREF_CHECADAS.getString("GRUIDACTUAL"+x,"0"));
        }
    }

    public void mostrarAlertaEmpleado(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal){
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
                guardarResultadoChecadaCorrecta(detallesPersonal, personal);
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                guardarResultadoChecadaDenegado(detallesPersonal, personal);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

    public void mostrarAlertaEmpleadoValidado(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso, final validarEmpleado empleado, final String idCaseta, final String numeroEmpleado, final String tipoChecada){
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
                guardarResultadoChecadaValidada(empleado, "P", idCaseta, numeroEmpleado, tipoChecada);
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                guardarResultadoChecadaValidada(empleado, "D", idCaseta, numeroEmpleado, tipoChecada);
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }
//YA ESTA
    private void guardarResultadoChecadaCorrecta(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal){
        if (personal != null)
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(),PUEClave, "P", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        else
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), " ", PUEClave, "P", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
    }

    private void guardarResultadoChecadaValidada(final validarEmpleado empleado, final String faseIngreso, final String idCaseta, final String numeroEmpleado, final String tipoChecada){
        realmController.insertarPersonalNuevo(empleado.getEmpleado().getNoEmpleado(), " ",empleado.getEmpleado().getPUEClave(), faseIngreso, "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);

    }
//YA ESTA
    private void guardarResultadoChecadaDenegado(final realmPersonalInfo detallesPersonal, final realmPersonalPuerta personal){
        if (personal != null)
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), personal.getNoTarjeta(),PUEClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
        else
            realmController.insertarPersonalNuevo(detallesPersonal.getNoEmpleado(), " ", PUEClave, "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
    }
//YA ESTA
    private void guardarResultadoChecadaDenegadoNoInternet(String noEmpleado){
        realmController.insertarPersonalNuevo(noEmpleado, " ", " ", "D", "N", "",PREF_CHECADAS.getString("NUMERO_EMPLEADO","0"), tipoChecada);
    }

    public void guardarResultadoChecadaNoEncontrado(String noEmpleado, Context context, String idCaseta, String numeroEmpleado, String tipoChecada){
        PREF_CHECADAS = context.getSharedPreferences("CCURE", getContext().MODE_PRIVATE);
        realmController = new RealmController();
        realmController.insertarPersonalNuevo(noEmpleado, " ", " ", "D", "N", "",numeroEmpleado, tipoChecada);
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

    private realmPersonalPuerta buscarPersonalLocal(String numeroEmpleado){
        for (int i = 0 ; i < aGRUIDs.size() ; i++){
            realmPersonalPuerta personalPuerta = realmController.obtenerPersonalManual(numeroEmpleado, aGRUIDs.get(i));
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

    private void validarEmpleado(){
        helperRetrofit helper = new helperRetrofit(URL);
        helper.ValidarEmpleado(edtNoEmpleado.getText().toString()," ",aGRUIDs, getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada, String.valueOf(PUEId), numeroEmpleado, tipoChecada);
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
        if(!empleado.getEmpleado().getFoto().equals("NO")){
            decodificarBase64Alerta(empleado.getEmpleado().getFoto(), imgPerfilAlerta);
        }else
            ponerImagenDefaultAlerta(imgPerfilAlerta);
    }

    private void configurarAlerta(TextView txtNombreAlerta, ImageView imgPerfilAlerta, realmPersonalInfo empleado){
        txtNombreAlerta.setText(empleado.getNombre());
        if(!empleado.getFoto().equals("NO")){
            decodificarBase64Alerta(empleado.getFoto(), imgPerfilAlerta);
        }else
            ponerImagenDefaultAlerta(imgPerfilAlerta);
    }

    private void ponerImagenDefaultAlerta(ImageView imgPerfilAlerta){
        Picasso.with(getContext()).load(R.drawable.ic_card).into(imgPerfilAlerta);
    }
}
