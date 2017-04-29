package arenzo.alejandroochoa.ccure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;


public class checadas extends Fragment {

    private final static String TAG = "checadas";

    private EditText edtGuardia, edtPuerta, edtNumeroEmpleado;
    private TextView txtNombreTrabajador, txtTarjetaEmpleado, txtCuentaEmpleado, txtModo;
    private Button btnCambiar;

    public checadas() {
        // Required empty public constructor
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
        edtGuardia = (EditText)view.findViewById(R.id.edtGuardia);
        edtPuerta = (EditText)view.findViewById(R.id.edtPuerta);
        edtNumeroEmpleado = (EditText)view.findViewById(R.id.edtNumeroEmpleado);
        txtNombreTrabajador = (TextView)view.findViewById(R.id.txtNombreTrabajador);
        txtTarjetaEmpleado = (TextView)view.findViewById(R.id.txtTarjetaEmpleado);
        txtCuentaEmpleado = (TextView)view.findViewById(R.id.txtCuentaEmpleado);
        txtModo = (TextView)view.findViewById(R.id.txtModo);
        btnCambiar = (Button)view.findViewById(R.id.btnCambiar);
        return view;
    }

}
