package arenzo.alejandroochoa.ccure;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;


public class sincronizacion extends Fragment {

    private final static String TAG = "sincronizacion";

    private RadioButton rdRed, rdArchivo;
    private Button btnSincronizar;

    public sincronizacion() {
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
        View view = inflater.inflate(R.layout.fragment_sincronizacion, container, false);
        rdRed = (RadioButton)view.findViewById(R.id.rdRed);
        rdArchivo = (RadioButton)view.findViewById(R.id.rdArchivo);
        btnSincronizar = (Button)view.findViewById(R.id.btnSincronizar);
        return view;
    }

}
