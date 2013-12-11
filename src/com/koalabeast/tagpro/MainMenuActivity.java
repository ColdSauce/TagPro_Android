package com.koalabeast.tagpro;

import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainMenuActivity extends Activity implements OnNavigationListener {
	private ArrayAdapter<String> serverListAdapter;
	private JsonArray servers;

	// @Overriden
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		// Set up server list in the action bar
		ActionBar bar = getActionBar();
		serverListAdapter = new ArrayAdapter<String>(bar.getThemedContext(),
				android.R.layout.simple_spinner_item);

		serverListAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		bar.setListNavigationCallbacks(serverListAdapter, this);

		Ion.with(this, "http://tagpro.koalabeast.com/servers").asJsonArray()
				.setCallback(new FutureCallback<JsonArray>() {

					@Override
					public void onCompleted(Exception e, JsonArray result) {

						findViewById(R.id.loadingSpinnerLayout).setVisibility(View.GONE);
						if (e != null) {
							new NetworkErrorDialogFragment().show(getFragmentManager(),
									"NetworkErrorDialogFragment");
						} else {
							
							servers = result;

							findViewById(R.id.serverInfoLayout).setVisibility(View.VISIBLE);

							for (JsonElement serverElement : result) {
								JsonObject server = serverElement.getAsJsonObject();
								String serverName = server.get("name").getAsString();
								serverListAdapter.add(serverName);
							}
						}
					}

				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// TODO select server
		String serverName = servers.get(itemPosition).getAsJsonObject().get("name").getAsString();
		Toast.makeText(this, "Picked " + serverName, Toast.LENGTH_SHORT).show();
		return false;
	}

	public void switchToPlay(View button) {
		Intent in = new Intent(this, GameActivity.class);
		startActivity(in);
	}

}
