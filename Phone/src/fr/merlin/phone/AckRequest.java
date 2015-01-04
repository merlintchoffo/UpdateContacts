package fr.merlin.phone;


import android.os.AsyncTask;

public class AckRequest extends AsyncTask<String, String, String>  {

		public interface RequestListen
		{
			public void onGetRequestEnd(String result);
		}
		
		private int maxnumbercontacts;
		private RequestListen listener;
		private String result;
		
		public AckRequest(int maxNumberContacts, RequestListen listnListener)
		{
			this.maxnumbercontacts = maxNumberContacts;
			this.listener = listnListener;
		}
		
		@Override
		protected String doInBackground(String... arg0)
		{
			result = "Opération de mise à jour terminée : détection et mise à jour de " + maxnumbercontacts + " contact(s) préfixé(s) par 237.";
			return null;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			this.listener.onGetRequestEnd(this.result);
		}
	}
