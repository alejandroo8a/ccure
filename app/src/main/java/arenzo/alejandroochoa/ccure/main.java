package arenzo.alejandroochoa.ccure;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class main extends AppCompatActivity implements vista {

    //TODO HACER LOGIN

    private final static String TAG = "main";

    private Button btnOlvideTarjetaLogin;
    private TextView txtFechaLogin;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cargarElementos();
        centrarTituloActionBar();
        eventosVista();
    }

    private void cargarElementos(){
        txtFechaLogin = (TextView)findViewById(R.id.txtFechaLogin);
        btnOlvideTarjetaLogin = (Button)findViewById(R.id.btnOlvideTarjetaLogin);
    }

    private void cargarLoginManual(){
        Intent intent = new Intent(this, loginManual.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
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

    @Override
    public void eventosVista() {
        btnOlvideTarjetaLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarLoginManual();
            }
        });
    }

}
