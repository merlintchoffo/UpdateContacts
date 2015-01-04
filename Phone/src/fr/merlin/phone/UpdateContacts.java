package fr.merlin.phone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.OperationApplicationException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Data;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import fr.merlin.phone.AckRequest.RequestListen;

public class UpdateContacts extends Activity implements RequestListen {

	private ArrayList<String> contactId;
	private ArrayList<String> infoContacts;

	private TextView tv;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_contacts);

		tv = (TextView) findViewById(R.id.defTextView01);
		progressBar = (ProgressBar) findViewById(R.id.progress_bar);
		Bundle extras = getIntent().getExtras();
		contactId = extras.getStringArrayList("keyContactId");
		//		name = extras.getStringArrayList("keyName");
		//		phoneNumber = extras.getStringArrayList("keyNum");
		infoContacts = extras.getStringArrayList("keyInfoContact");

		AckRequest request = new AckRequest(contactId.size(),this);
		request.execute();
		//setContentView(R.layout.activity_update_contacts);

	}

//	private void showContactList(int index) {
//		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append("\n" + infoContacts.get(index).split(":")[0] + " " + infoContacts.get(index).split(":")[1]);
//		Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT)
//		.show();
//	}

	// returns true if the string contains exactly "true"
	public boolean containskeys(String s) {
		return s.matches(".*2376.*");
	}

	/** Retourne le label correspondant au type d'enregistrement
	 * Source des données de correspondance : 
	 * http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html */

	private String getPhoneType(String index){
		if(index.trim().equals( "1")){
			return String.valueOf(Phone.TYPE_HOME);
		} else if (index.trim().equals("2")){
			return String.valueOf(Phone.TYPE_MOBILE);
		} else if (index.trim().equals("3")){
			return String.valueOf(Phone.TYPE_WORK);
		} else if (index.trim().equals("7")){
			return String.valueOf(Phone.TYPE_OTHER);
		} else {
			return "?";
		}
	}  

	public void updateContact(String contactId, String newNumber, String phoneType, Context act)
			throws RemoteException, OperationApplicationException {

		// ASSERT: @contactId already has a work phone number
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		String selectPhone = Data.CONTACT_ID + "=? AND " + Data.MIMETYPE + "='"
				+ Phone.CONTENT_ITEM_TYPE + "'" + " AND " + Phone.TYPE + "=?";
		String[] phoneArgs = new String[] { contactId, phoneType };
		ops.add(ContentProviderOperation.newUpdate(Data.CONTENT_URI)
				.withSelection(selectPhone, phoneArgs)
				.withValue(Phone.NUMBER, newNumber).build());
		act.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
	}

	@Override
	public void onGetRequestEnd(String result) {
		// TODO Auto-generated method stub
		//		Toast.makeText(this.getApplicationContext(), "Request End", Toast.LENGTH_SHORT).show();
		Toast.makeText(this.getApplicationContext(), result, Toast.LENGTH_LONG).show();

		//		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		//		dialog.setMessage(result);
		//		dialog.setCancelable(true);
		//		dialog.create().show();

	}

	public class AckRequest extends AsyncTask<String, String, String>  {


		private int maxnumbercontacts;
		private RequestListen listener;
		//		private String result;

		public AckRequest(int maxNumberContacts, RequestListen listnListener)
		{
			this.maxnumbercontacts = maxNumberContacts;
			this.listener = listnListener;
		}

		@Override
		protected void onPreExecute () {

			// Hide the textview and display the progress bar while thread running
			tv.setVisibility(TextView.INVISIBLE);
			progressBar.setVisibility(ProgressBar.VISIBLE);

		}

		@Override
		protected String doInBackground(String... arg0)
		{
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Liste des contacts mis à jour : " + maxnumbercontacts + "\n");
			progressBar.setVisibility(ProgressBar.VISIBLE);
			for(int i = 0 ; i < maxnumbercontacts ; i++) {

				String name = infoContacts.get(i).split(":")[0];
				String phoneNumber = infoContacts.get(i).split(":")[1];
				String phoneNumber_m = infoContacts.get(i).split(":")[1].replaceAll("\\s+", "");
				String phoneType = infoContacts.get(i).split(":")[2].trim();

				// Append list of contacts to the StringBuilder object
				stringBuilder.append("\n"+name+" "+phoneNumber+" "+phoneType);

				//				showContactList(i);
				String[] splitString = (phoneNumber_m.split("237"));
				try {
					String newNumber = "+2376" + splitString[1];
					updateContact(contactId.get(i), newNumber, getPhoneType(phoneType), getApplicationContext());
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (OperationApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


			}
			//			result = "Opération de mise à jour terminée : détection et mise à jour de " + maxnumbercontacts + " contact(s) préfixé(s) par 237.";

			return stringBuilder.toString();		}



		@Override
		protected void onPostExecute(String s)
		{
			//super.onPostExecute(s);
			this.listener.onGetRequestEnd(s);
			tv.setText("Mise à jour terminée");
			tv.append("\n"+s);
			// Stop the progress bar and make the TextView visible
			progressBar.setVisibility(View.GONE);
			tv.setVisibility(TextView.VISIBLE);
			Button finish = (Button)findViewById(R.id.btnfinish);
			finish.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(0);
					finish();
				}});			
		}
	}
}
