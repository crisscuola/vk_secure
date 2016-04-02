package com.example.kirill.techpark16;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

/**
 * Created by kirill on 16.03.16.
 */
public class SideMenu {

    public  static DrawerBuilder getDrawer(final AppCompatActivity activity, Toolbar toolbar) {

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.header)
                .withSelectionListEnabledForSingleProfile(false)
                .addProfiles(
                        new ProfileDrawerItem().withName("Kirill Matveev").withEmail("testest@gmail.com")
                                .withIcon(activity.getResources().getDrawable(R.drawable.profile))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                }).build();

        DrawerBuilder result = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .withHeader(R.layout.drawer_header)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_dialogs).withIcon(FontAwesome.Icon.faw_comments).withBadge("4"),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_friends).withIcon(FontAwesome.Icon.faw_users),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withIdentifier(1)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 2) {
                            Intent friendsActivity = new Intent(view.getContext(), TestFriendListActivity.class);
                            activity.startActivity(friendsActivity);
                        } else if (position == 3) {
                            Intent settingsActivity = new Intent(view.getContext(), SettingsActivity.class);
                            activity.startActivity(settingsActivity);
                        } else if (position == 1) {
                            Intent testDialogsListActivity = new Intent(view.getContext(), TestDialogsListActivity.class);
                            activity.startActivity(testDialogsListActivity);
                        } else if (drawerItem.getIdentifier() == 1) {
                            Toast.makeText(activity, "TODO: link to github", Toast.LENGTH_SHORT).show();
                        }
                        return false;
                    }

                })
                .withAccountHeader(headerResult);

        return result;
    }



}
