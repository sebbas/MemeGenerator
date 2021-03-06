package org.sebbas.android.memegenerator.activities;

import android.app.ActivityManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.ExploreFragment;
import org.sebbas.android.memegenerator.fragments.GalleryFragment;
import org.sebbas.android.memegenerator.fragments.GifFragment;
import org.sebbas.android.memegenerator.fragments.MemeFragment;
import org.sebbas.android.memegenerator.fragments.MoreFragment;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public abstract class BaseActivity extends AppCompatActivity implements ActionMode.Callback {

    private MenuItem mSearchMenuItem;

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }

    protected void setCustomLollipopActionBar() {
        // Customize task bar which is visible in multitask mode in Android Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setTaskDescription(new ActivityManager.TaskDescription(
                    null, null, getResources().getColor(R.color.primaryDark)));
        }
    }

    public void setupToolbar(String fragmentTag) {
        int titleResource = 0;
        int menuResource = 0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        switch (fragmentTag) {
            case MemeFragment.TAG:
            case MemeFragment.TAG_CHILD_ONE:
            case MemeFragment.TAG_CHILD_TWO:
            case MemeFragment.TAG_CHILD_THREE:
                titleResource = R.string.memes;
                menuResource = R.menu.menu_memes;
                break;
            case GifFragment.TAG:
            case GifFragment.TAG_CHILD_ONE:
            case GifFragment.TAG_CHILD_TWO:
            case GifFragment.TAG_CHILD_THREE:
                titleResource = R.string.gifs;
                menuResource = R.menu.menu_gifs;
                break;
            case ExploreFragment.TAG:
            case ExploreFragment.TAG_CHILD_ONE:
            case ExploreFragment.TAG_CHILD_TWO:
                titleResource = R.string.explore;
                menuResource = R.menu.menu_explore;
                break;
            case GalleryFragment.TAG:
            case GalleryFragment.TAG_CHILD_ONE:
            case GalleryFragment.TAG_CHILD_TWO:
            case GalleryFragment.TAG_CHILD_THREE:
                titleResource = R.string.gallery;
                menuResource = R.menu.menu_gallery;
                break;
            case MoreFragment.TAG:
                titleResource = R.string.chart;
                break;
            case EditActivity.TAG:
                titleResource = R.string.editor;
                menuResource = R.menu.menu_edit;
                toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
                break;
            default:
                titleResource = R.string.app_name;
                menuResource = R.menu.menu_memes;
                break;
        }

        // Make sure that toolbar is clear
        toolbar.getMenu().clear();

        // Setup toolbar title
        toolbar.setTitle(titleResource);

        // Setup toolbar menu
        if (menuResource != 0) {
            toolbar.inflateMenu(menuResource);
        }
    }

    public void registerToolbarCallback(final ToolbarCallback toolbarCallback) {
        final Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        // Setup toolbar actions
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_search:
                        setupSearchView(menuItem, toolbarCallback);
                        break;
                    case R.id.menu_image:
                        break;
                    case R.id.menu_video:
                        break;
                    case R.id.menu_border:
                        toolbar.startActionMode(BaseActivity.this);
                        break;
                }
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarCallback.onToolbarBackPressed();
            }
        });
    }

    public void unregisterToolbarCallback() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(null);
    }

    private void setupSearchView(MenuItem menuItem, final ToolbarCallback toolbarCallback) {
        mSearchMenuItem = menuItem;

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return toolbarCallback.onQueryTextSubmit(s);
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return toolbarCallback.onQueryTextChange(s);
            }
        });
    }

    public void closeSearchView() {
        if (mSearchMenuItem != null) {
            MenuItemCompat.collapseActionView(mSearchMenuItem);
        }
    }

    abstract public int getMainPagerPosition();

    abstract public int getLastFragmentPositionMain();

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.getMenuInflater().inflate(R.menu.menu_memes, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub

        mode.setTitle("CheckBox is Checked");
        return false;
    }
}
