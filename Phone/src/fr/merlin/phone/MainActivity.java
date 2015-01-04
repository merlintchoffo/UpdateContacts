package fr.merlin.phone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

    private static final Uri PURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    private static final String HPN = ContactsContract.Contacts.HAS_PHONE_NUMBER;
    private static final String CID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    private static final String DNAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private static final String PNUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
    private static final String PHONETYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
    private static final int MAX_NUMBER_ENTRIES = 10;

    private String phList;
    
    
	public static String contactId;
	public static String name;
	public static String phoneNumber;
	public static int phoneType;
	
	private TextView tv;
	ListView vue;
	private Button info;
	private Button quit;

	// on déclare une arrayList de string pour stocker tous les contacts à updater
	ArrayList<String> liste_noms = new ArrayList<String>();
	ArrayList<String> contacts = new ArrayList<String>();
	ArrayList<String> liste_contactId = new ArrayList<String>();
	
	ContentResolver cr;
	Cursor phones;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) findViewById(R.id.msgTextView01);
		tv.setVisibility(TextView.INVISIBLE);

		// Allow for up to MAX_NUMBER_ENTRIES email and phone entries for a contact
//      ph = new String[MAX_NUMBER_ENTRIES];
//        phType = new String[MAX_NUMBER_ENTRIES];
        
		cr = getContentResolver();
		phones = cr.query(PURI, null, null, null, null);
		int nContacts = phones.getCount();
		Log.i("UI", String.valueOf(nContacts));

		//On récupère une ListView de notre layout en XML, c'est la vue qui représente la liste
		vue = (ListView) findViewById(R.id.listview);


		/*
		 * On doit donner à notre adaptateur une liste du type « List<Map<String, ?> » :
		 * - la clé doit forcément être une chaîne de caractères
		 * - en revanche, la valeur peut être n'importe quoi, un objet ou un
			entier par exemple,  si c'est un objet, on affichera son contenu avec la méthode «	toString() »

			Dans notre cas, la valeur sera une chaîne de caractères, puisque
			le nom et le numéro de téléphone sont entreposés dans des chaînes de caractères
		 */
		List<HashMap<String, String>> liste = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> element;

		while (phones.moveToNext()) {
			contactId = phones.getString(phones.getColumnIndex(CID));
			name = phones.getString(phones.getColumnIndex(DNAME));
			phoneNumber = phones.getString(phones.getColumnIndex(PNUM));
//			phoneType = phones.getInt(phones.getColumnIndex(PHONETYPE));

			Log.i("nom ",name);
			Log.i("num ",phoneNumber);
//			Log.i("group",String.valueOf(phoneType));

			String phoneNumber_m = phoneNumber.replaceAll("\\s+", "");
			Pattern pattern = Pattern.compile("\\+237\\d{8}$|00237\\d{8}$");
			Matcher matcher = pattern.matcher(phoneNumber_m);
			boolean isMatch = matcher.find();

			if (isMatch){
				
				phList = name + ":" + phoneNumber;
				queryPhTypeById();

				/*
				 * On entrepose nos données dans un tableau qui contient deux colonnes :
				 * - la première contiendra le nom de l'utilisateur
				 * - la seconde contiendra le numéro de téléphone de l'utilisateur
				 */
				liste_contactId.add(contactId);
				liste_noms.add(name);
				contacts.add(phList);


				String[] repertoire = new String[]{name, phoneNumber};
				//Pour chaque personne dans notre répertoire…
				//			for(int i = 0 ; i < repertoire.length ; i++) {
				//… on crée un élément pour la liste…
				element = new HashMap<String, String>();
				/*
				 * … on déclare que la clé est « text1 » (j'ai choisi ce mot au
			hasard, sans sens technique particulier)
				 * pour le nom de la personne (première dimension du tableau de
			valeurs)…
				 */
				element.put("text1", repertoire[0]);
				/* … on déclare que la clé est « text2 »
				 * pour le numéro de cette personne (seconde dimension du tableau de
			valeurs)
				 */
				element.put("text2", repertoire[1]);
				liste.add(element);
				ListAdapter adapter = new SimpleAdapter(this,liste,android.R.layout.simple_list_item_2, new String[] {"text1", "text2"},new int[] {android.R.id.text1, android.R.id.text2 });
				//Valeurs à insérer: liste 
				/*
				 * Layout de chaque élément (là, il s'agit d'un layout par défaut* pour avoir deux textes l'un au-dessus de l'autre, c'est pourquoi
			on * n'affiche que le nom et le numéro d'une personne):android.R.layout.simple_list_item_2
				 */

				/*
				 * Les clés des informations à afficher pour chaque élément :
				 * - la valeur associée à la clé « text1 » sera la première
			information
				 * - la valeur associée à la clé « text2 » sera la seconde
			information: new String[] {"text1", "text2"}			

			/* Enfin, les layouts à appliquer à chaque widget de notre élément
				 * (ce sont des layouts fournis par défaut) :
				 * - la première information appliquera le layout «
			android.R.id.text1 »
				 * - la seconde information appliquera le layout «
			android.R.id.text2 »
				 */	
				//Pour finir, on donne à la ListView le SimpleAdapter
				vue.setAdapter(adapter);

			}
		}
		
		tv.append(" " + String.valueOf(liste_contactId.size()));
		tv.setVisibility(TextView.VISIBLE);
		phones.close();

		this.info = (Button) this.findViewById(R.id.showProgress);
		this.info.setOnClickListener(this);
		this.quit = (Button) this.findViewById(R.id.btnCancel);
		this.quit.setOnClickListener(this);
		
	}						
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0){
			setResult(1);
			finish();
		}

	}
	

	
	private String queryPhTypeById(){

		String[] ph = new String[MAX_NUMBER_ENTRIES];
		String[] phType = new String[MAX_NUMBER_ENTRIES];
		int phcounter = 0;

		if (Integer.parseInt(phones.getString(phones.getColumnIndex(HPN))) > 0) {	        	
			Cursor pCur = cr.query(PURI,  null, CID + " = ?",  new String[]{contactId}, null);
			while (pCur.moveToNext()) {
				ph[phcounter] = pCur.getString(pCur.getColumnIndex(PNUM));
				if (ph[phcounter].equals(phoneNumber)){
					phType[phcounter]  = pCur.getString(pCur.getColumnIndex(PHONETYPE));
					phList = phList + ":" + phType[phcounter]  + "\n";
					}
					else 
					phcounter ++; 
					}
			pCur.close();
				} 
		return phList;
		
			}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId())
		{
			case R.id.showProgress:	
				info.setBackgroundColor(Color.RED);
				Intent uc = new Intent(getApplicationContext(),UpdateContacts.class);
				uc.putStringArrayListExtra("keyContactId", liste_contactId);
//				uc.putStringArrayListExtra("keyName", liste_noms);
				uc.putStringArrayListExtra("keyInfoContact", contacts);
				
				startActivityForResult(uc, 0);
				break;
			
			case R.id.btnCancel:
				Toast.makeText(MainActivity.this, "Start", Toast.LENGTH_SHORT).show();
				finish();
				break;
				
			default:
				break;
		}
		
		
	}
		
}
	
	
