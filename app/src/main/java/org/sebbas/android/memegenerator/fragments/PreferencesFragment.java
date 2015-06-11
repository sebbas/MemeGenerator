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

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.fragment_preferences);
    }
}