package fr.merlin.phone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


public class YesNoSampleActivity extends Activity {
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
        
        //Put up the Yes/No message box
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder
    	.setTitle("Update your 'CAMEROON' contacts now")
    	.setMessage("Accès à la liste des contacts à mettre à jour ...")
    	.setIcon(android.R.drawable.ic_dialog_alert)
    	.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int which) {			      	
    	    	//Yes button clicked, do something
//    	    	Toast.makeText(YesNoSampleActivity.this, "Start", Toast.LENGTH_SHORT)
//    	    	.show();
    	    	Intent ma = new Intent(getApplicationContext(),MainActivity.class);
    	    	startActivityForResult(ma,1);
    	    }
    	})
//    	.setNegativeButton("No", null)						//Do nothing on no
    	.show();
        
    	    	
    	// Continue code after the Yes/No dialog
    	// ....
    	
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1){
			this.finish();
		}

	}
}