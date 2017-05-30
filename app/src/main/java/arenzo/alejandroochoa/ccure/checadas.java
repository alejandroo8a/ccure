package arenzo.alejandroochoa.ccure;


import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kyleduo.switchbutton.SwitchButton;

import org.w3c.dom.Text;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa;
    private SwitchButton sbTipoChecada;
    private ImageView imgFotoPerfil;

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
        edtNoEmpleado = (EditText)view.findViewById(R.id.edtNoEmpleado);
        txtCaseta = (TextView)view.findViewById(R.id.txtCaseta);
        txtResultadoChecada = (TextView)view.findViewById(R.id.txtResultadoChecada);
        txtNombre = (TextView)view.findViewById(R.id.txtNombre);
        txtPuestoEmpresa = (TextView)view.findViewById(R.id.txtPuestoEmpresa);
        sbTipoChecada = (SwitchButton) view.findViewById(R.id.sbTipoChecada);
        imgFotoPerfil = (ImageView)view.findViewById(R.id.imgFotoPerfil);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtNoEmpleado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

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
                }
                else {
                    sbTipoChecada.setBackColorRes(R.color.primary);
                }
            }
        });
    }

    private void mostrarAlertaEmpleado(){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.item_checada, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("Ingreso")
                .setView(view)
                .setPositiveButton("Aceptar", null)
                .setNeutralButton("Cancelar", null);
        AlertDialog alerta = builder.create();
        alerta.show();


    }
}
