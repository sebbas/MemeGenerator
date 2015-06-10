/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sebbas.android.memegenerator.activities;

import android.app.ActivityManager;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.sebbas.android.memegenerator.R;
import org.sebbas.android.memegenerator.fragments.BaseFragment;
import org.sebbas.android.memegenerator.fragments.RecyclerFragment;
import org.sebbas.android.memegenerator.interfaces.ToolbarCallback;

public abstract class BaseActivity extends ActionBarActivity {

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

    public void setupToolbar(int titleResource, int menuResource, boolean isUpEnabled) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Make sure that toolbar is clear
        toolbar.getMenu().clear();

        // Setup toolbar title
        String title = getResources().getString(titleResource);
        toolbar.setTitle(title);

        // Setup toolbar menu
        if (menuResource != 0) {
            toolbar.inflateMenu(menuResource);
        }

        if (isUpEnabled) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }

    public void registerToolbarCallback(BaseFragment baseFragment) {
        final ToolbarCallback toolbarCallback = (ToolbarCallback) baseFragment;
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);

        // Setup toolbar actions
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_search:
                        setupSearchView(menuItem, toolbarCallback);
                        break;
                    case R.id.menu_refresh:
                        //toolbarCallback.onRefreshClicked();
                        break;
                }
                return true;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarCallback.onBackPressed();
            }
        });
    }

    public void unregisterToolbarCallback() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(null);
    }

    private void setupSearchView(MenuItem menuItem, final ToolbarCallback toolbarCallback) {
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
}
