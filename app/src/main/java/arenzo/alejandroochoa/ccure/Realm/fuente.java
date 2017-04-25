package arenzo.alejandroochoa.ccure.Realm;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by AlejandroMissael on 25/04/2017.
 */

public class fuente {

    public static Typeface myFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }
}
