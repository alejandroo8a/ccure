package arenzo.alejandroochoa.ccure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class configuracion extends Fragment {

    private final static String TAG = "configuracion";

    private EditText edtNombreDispositivo, edtWebService, edtURLExportacion;
    private Spinner spPuertas;
    private Button btnGuardarConfiguracion;

    public configuracion() {
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
        View view = inflater.inflate(R.layout.fragment_configuracion, container, false);
        edtNombreDispositivo = (EditText)view.findViewById(R.id.edtNombreDispositivo);
        edtWebService = (EditText)view.findViewById(R.id.edtWebService);
        edtURLExportacion = (EditText)view.findViewById(R.id.edtURLExportacion);
        return view;
    }

}
