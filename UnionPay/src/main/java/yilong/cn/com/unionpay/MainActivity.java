package yilong.cn.com.unionpay;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.unionpay.UPPayAssistEx;

public class MainActivity extends Activity implements View.OnClickListener{

    public UnionPayClassUtils unionPayClassUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn0 = (Button) findViewById(R.id.btn0);
        btn0.setTag(0);
        btn0.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

//        if(view.getId()==R.id.btn0){
//            unionPayClassUtils = new UnionPayClassUtils(this);
//            unionPayClassUtils.initPay();
//        }

    }
}
