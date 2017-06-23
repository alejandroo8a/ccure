package arenzo.alejandroochoa.ccure.Fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kyleduo.switchbutton.SwitchButton;

import org.w3c.dom.Text;

import arenzo.alejandroochoa.ccure.R;
import arenzo.alejandroochoa.ccure.Realm.RealmController;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalInfo;
import arenzo.alejandroochoa.ccure.Realm.realmPersonalPuerta;
import arenzo.alejandroochoa.ccure.WebService.helperRetrofit;
import arenzo.alejandroochoa.ccure.WebService.retrofit;
import io.realm.Realm;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa, txtNombreAlerta;
    private SwitchButton sbTipoChecada;
    private ImageView imgFotoPerfil, imgFondoAcceso, imgPerfilAlerta;
    private Button btnAceptarAlerta, btnCancelarAlerta;
    ProgressDialog anillo = null;

    private Boolean tipoChecada;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        decodificarBase64();
        activarTipoChecada();
        edtNoEmpleado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "before: "+i);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(++i == 5){
                    mostrarCargandoAnillo();
                    realmPersonalPuerta personal = buscarPersonalLocal(edtNoEmpleado.getText().toString());
                    realmPersonalInfo detallesPersonal = buscarDetallePersonaLocal(edtNoEmpleado.getText().toString());
                    ocultarTeclado();
                    limpiarEditText();
                    if (detallesPersonal != null){
                        ocultarCargandoAnillo();
                        mostrarAlertaEmpleado(getContext(), txtResultadoChecada, imgFondoAcceso);
                    }else{
                        validarEmpleado();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sbTipoChecada.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sbTipoChecada.setBackColorRes(R.color.accent);
                    tipoChecada = true;
                }
                else {
                    sbTipoChecada.setBackColorRes(R.color.primary);
                    tipoChecada = false;
                }
            }
        });
    }

    public void mostrarAlertaEmpleado(final Context context, final TextView txtResultadoChecada, final ImageView imgFondoAcceso){
        View view = LayoutInflater.from(context).inflate(R.layout.item_checada, null);
        final AlertDialog builder = new AlertDialog.Builder(context).create();
        builder.setView(view);
        btnAceptarAlerta = view.findViewById(R.id.btnAceptarAlerta);
        btnCancelarAlerta = view.findViewById(R.id.btnCancelarAlerta);
        txtNombreAlerta = view.findViewById(R.id.txtNombreAlerta);
        imgPerfilAlerta = view.findViewById(R.id.imgPerfilAlerta);
        btnAceptarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO GUARDAR ENTRADA EN LA BD
                builder.dismiss();
                mostrarPermitido(txtResultadoChecada, imgFondoAcceso);
            }
        });
        btnCancelarAlerta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                mostrarDenegado(txtResultadoChecada, imgFondoAcceso);
                vibrarCelular(context);
            }
        });
        builder.show();
    }

    private void decodificarBase64(){
        byte[] decodificado = Base64.decode("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",Base64.DEFAULT);
        Bitmap decodificadoMap = BitmapFactory.decodeByteArray(decodificado, 0, decodificado.length);
        imgFotoPerfil.setImageBitmap(decodificadoMap);
    }

    private void activarTipoChecada(){
        sbTipoChecada.setChecked(true);
        sbTipoChecada.setBackColorRes(R.color.accent);
        tipoChecada = true;
    }

    private realmPersonalPuerta buscarPersonalLocal(String numeroEmpleado){
        RealmController.with(getActivity());
        return RealmController.getInstance().obtenerPersonalManual(numeroEmpleado);
    }

    private realmPersonalInfo buscarDetallePersonaLocal(String numeroEmpleado){
        RealmController.with(getActivity());
        return RealmController.getInstance().obtenerInformacionPersonal(numeroEmpleado);
    }

    private void ocultarTeclado(){
        InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void limpiarEditText(){
        edtNoEmpleado.setText("");
    }

    private void validarEmpleado(){
        helperRetrofit helper = new helperRetrofit(retrofit.URL);
        helper.ValidarEmpleado("17777","235","P3MINA", getContext(), this.anillo, imgFondoAcceso,txtResultadoChecada);
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
}
