package arenzo.alejandroochoa.ccure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtNoEmpleado;
    private TextView txtCaseta, txtResultadoChecada, txtNombre, txtPuestoEmpresa;
    private ToggleButton tbTipoChecada;
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
        tbTipoChecada = (ToggleButton) view.findViewById(R.id.tbTipoChecada);
        imgFotoPerfil = (ImageView)view.findViewById(R.id.imgFotoPerfil);
        return view;
    }

}
