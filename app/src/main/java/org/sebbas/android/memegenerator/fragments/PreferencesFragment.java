package org.sebbas.android.memegenerator.fragments;

import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.view.View;

import org.sebbas.android.memegenerator.R;

public class PreferencesFragment extends PreferenceFragment {

    public static PreferencesFragment newInstance() {
        return new PreferencesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make sure default values are applied.  In a real app, you would
        // want this in a shared function that is used to retrieve the
        // SharedPreferences wherever they are needed.
        //PreferenceManager.setDefaultValues(getActivity(),
         //       R.xml.advanced_preferences, false);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
    }
}