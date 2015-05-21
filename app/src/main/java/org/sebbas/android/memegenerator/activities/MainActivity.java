package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.MainFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // This makes the actionbar in multitask mode in Android Lollipop look a bit nicer
        setCustomLollipopActionBar();

        MainFragment mainFragment = MainFragment.newInstance(this);

        // Attach the just obtained fragment to the frame layout
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, mainFragment);
        fragmentTransaction.commit();
    }

    /*
     * ItemClickCallback
     */
    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.getImageUrl();

        EditorFragment editorFragment = EditorFragment.newInstance(imageUrl);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.main_container, editorFragment);
        fragmentTransaction.commit();
    }
}