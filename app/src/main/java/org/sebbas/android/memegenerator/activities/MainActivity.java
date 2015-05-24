package org.sebbas.android.memegenerator.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.sebbas.android.memegenerator.LineItem;
import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.EditorFragment;
import org.sebbas.android.memegenerator.fragments.MainFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.ItemClickCallback;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

import java.util.List;

public class MainActivity extends BaseActivity implements ItemClickCallback {

    private static final String TAG = "MainActivity";

    private int mBaseTranslationY;
    private Toolbar mToolbarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbarView = (Toolbar) findViewById(R.id.toolbar);

        if (savedInstanceState == null) {
            // This makes the actionbar in multitask mode in Android Lollipop look a bit nicer
            setCustomLollipopActionBar();

            MainFragment mainFragment = MainFragment.newInstance();

            // Attach the just obtained fragment to the frame layout
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.main_container, mainFragment, MainFragment.class.getName());
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fragmentTransaction.commit();
        }

    }

    /*
     * ItemClickCallback
     */
    @Override
    public void onItemClick(int position, List<LineItem> lineItems) {
        LineItem lineItem = lineItems.get(position);
        String imageUrl = lineItem.
                getImageUrl();

        EditorFragment editorFragment = EditorFragment.newInstance(imageUrl);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        fragmentTransaction.add(R.id.main_container, editorFragment, EditorFragment.class.getName());
        fragmentTransaction.addToBackStack(EditorFragment.class.getName());
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }
}