package com.rhapsody.xhan.toolbardemo;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.manuelpeinado.fadingactionbar.view.ObservableScrollView;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;


public class MainActivity extends ActionBarActivity implements OnScrollChangedCallback {
	Toolbar toolbar;
	String[] drawerItems;
	ActionBarDrawerToggle drawerToggle;
	DrawerLayout drawerLayout;
	ObservableScrollView scrollView;
	ColorDrawable toolbarBackground;
	View header;
	private int mLastDampedScroll;
	int toolbarAlpha;
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		toolbar = (Toolbar)findViewById(R.id.toolbar);
		toolbar.setTitle("My Toolbar");

		toolbarBackground = new ColorDrawable(Color.BLUE);
		toolbar.setBackgroundDrawable(toolbarBackground);
		setSupportActionBar(toolbar);
		getSupportActionBar().setBackgroundDrawable(toolbarBackground);
		drawerItems = getResources().getStringArray(R.array.left_drawer_items);
		ListView drawerList = (ListView)findViewById(R.id.left_drawer);
		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick (AdapterView<?> parent, View view, int position, long id) {
				String itemText = drawerItems[position];
				getSupportActionBar().setTitle(itemText);
				drawerLayout.closeDrawer(GravityCompat.START);
			}
		});
		drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItems));
		drawerLayout = (DrawerLayout)findViewById(R.id.drawer_root);
		drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
			@Override
			public void onDrawerOpened (View drawerView) {
				super.onDrawerOpened(drawerView);
				updateToolbarTransparency(255);
			}

			@Override
			public void onDrawerClosed (View drawerView) {
				super.onDrawerClosed(drawerView);
				updateToolbarTransparency(toolbarAlpha);
			}

			@Override
			public void onDrawerSlide (View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				float alphaDelta = ((float)(255 - toolbarAlpha)) * slideOffset;
				int tmpAlpha = Float.valueOf(alphaDelta + toolbarAlpha).intValue();
				updateToolbarTransparency(tmpAlpha);
			}
		};
		drawerLayout.setDrawerListener(drawerToggle);

		getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.branded_app_logo_actionbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayUseLogoEnabled(true);

		header = findViewById(R.id.title_image);
		scrollView = (ObservableScrollView)findViewById(R.id.scroll_container);
		scrollView.setOnScrollChangedCallback(this);
		onScroll(-1, 0);
		toolbar.bringToFront();

	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		drawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		drawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateActionBarTransparency(float scrollRatio) {
		toolbarAlpha = (int) (scrollRatio * 255);
		updateToolbarTransparency(toolbarAlpha);
	}
	private void updateToolbarTransparency(int alpha) {
		Drawable background = toolbar.getBackground();
		background.setAlpha(alpha);
		toolbar.setBackgroundDrawable(background);
		getSupportActionBar().setBackgroundDrawable(background);
	}

	private void updateParallaxEffect(int scrollPosition) {
		float damping = 0.5f;
		int dampedScroll = (int) (scrollPosition * damping);

		int offset = mLastDampedScroll - dampedScroll;
		header.offsetTopAndBottom(-offset);

		mLastDampedScroll = dampedScroll;
	}

	private int interpolate(int from, int to, float param) {
		return (int) (from * param + to * (1 - param));
	}

	@Override
	public void onScroll (int l, int scrollPosition) {
		int headerHeight = header.getHeight() - toolbar.getHeight();
		float ratio = 0;
		if (scrollPosition > 0 && headerHeight > 0)
			ratio = (float) Math.min(Math.max(scrollPosition, 0), headerHeight) / headerHeight;

		updateActionBarTransparency(ratio);
		updateParallaxEffect(scrollPosition);
	}
}
