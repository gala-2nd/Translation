package MyClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;

public class SharedHelper {

    private Context mContext;

    public SharedHelper() {
    }

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }

    public void save(String auth_key) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("auth_key",auth_key);
        editor.commit();
        Toast.makeText(mContext, "Written into SharedPreference", Toast.LENGTH_SHORT).show();
    }

    public HashMap<String, String> read() {
        HashMap<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Context.MODE_PRIVATE);
        data.put("auth_key", sp.getString("auth_key", ""));
        return data;
    }
}
