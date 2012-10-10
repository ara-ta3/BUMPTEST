package my.bump_test;

import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;

import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {
	private IBumpAPI api;
	@SuppressLint("ParserError")
	private final ServiceConnection connection = new ServiceConnection() {
	    @Override
	    public void onServiceConnected(ComponentName className, IBinder binder) {
	        api = IBumpAPI.Stub.asInterface(binder);
	        try {
	            api.configure("d8fcddd820f54e6e8c8b84000f1635ee", "Bump User");
	        } catch (RemoteException e) {
	        	e.printStackTrace();
	        	Log.d("RemoteException",e.toString());
	        }
	    }

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO 自動生成されたメソッド・スタブ
			
		}
	};
	
	private Button pushBtn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println(IBumpAPI.class.getName());
        bindService(new Intent(IBumpAPI.class.getName()),
                connection, 
                Context.BIND_AUTO_CREATE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
        filter.addAction(BumpAPIIntents.DATA_RECEIVED);
        filter.addAction(BumpAPIIntents.NOT_MATCHED);
        filter.addAction(BumpAPIIntents.MATCHED);
        filter.addAction(BumpAPIIntents.CONNECTED);
        registerReceiver(receiver, filter);
        pushBtn	= (Button)findViewById(R.id.pushbtn);
        pushBtn.setOnClickListener(new OnClickListener() {
			@SuppressLint("ParserError")
			@Override
			public void onClick(View v) {
				Toast.makeText(MainActivity.this, "TEST", Toast.LENGTH_LONG).show();
			}
		});
    }
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            try {
                if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
                    Log.i("Bump Test", "Received data from: " + api.userIDForChannelID(intent.getLongExtra("channelID", 0)));
                    Log.i("Bump Test", "Data: " + new String(intent.getByteArrayExtra("data")));
                } else if (action.equals(BumpAPIIntents.MATCHED)) {
                    api.confirm(intent.getLongExtra("proposedChannelID", 0), true);
                } else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
                    api.send(intent.getLongExtra("channelID", 0), "Hello, world!".getBytes());
                } else if (action.equals(BumpAPIIntents.CONNECTED)) {
                    api.enableBumping();
                }
                Log.d("RECEIVE","OK");
            } catch (RemoteException e) {}
            
        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }
//
//    
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
