package arenzo.alejandroochoa.ccure;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class loginManual extends AppCompatActivity implements vista {

    private final static String TAG = "loginManual";

    private TextView txtFechaLoginManual;
    private EditText edtNumeroEmpleado;
    private Button btnLoginManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_manual);
        centrarTituloActionBar();
        cargarElementos();
        eventosVista();
    }


    private void cargarElementos(){
        txtFechaLoginManual = (TextView) findViewById(R.id.txtFechaLoginManual);
        edtNumeroEmpleado = (EditText) findViewById(R.id.edtNumeroEmpleado);
        btnLoginManual = (Button)findViewById(R.id.btnLoginManual);
    }

    @Override
    public void eventosVista(){
        btnLoginManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarNucleo();
            }
        });
    }

    private void cargarNucleo(){
        Intent intent = new Intent(this, nucleo.class);
        startActivity(intent);
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
}
