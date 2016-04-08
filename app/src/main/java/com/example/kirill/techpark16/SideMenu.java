package com.example.kirill.techpark16;

import android.support.v4.app.FragmentTransaction;
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

import java.util.ArrayList;

/**
 * Created by kirill on 16.03.16.
 */
public class SideMenu {

    public DrawerBuilder getDrawer(final AppCompatActivity activity, Toolbar toolbar) {

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
                        new PrimaryDrawerItem().withName(R.string.drawer_item_dialogs).withIcon(FontAwesome.Icon.faw_comments).withBadge("4").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_friends).withIcon(FontAwesome.Icon.faw_users).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_settings).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(3),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withIdentifier(1).withSelectable(true)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        FragmentsActivity.selectDrawerItem(position);
                        switch ((int) drawerItem.getIdentifier()) {
                            case 1:
                                Toast.makeText(activity, "Clicked Dialogs", Toast.LENGTH_SHORT).show();

                                DetailDialogFragment newFragment = DetailDialogFragment.getInstance(1, new ArrayList<String>(), new ArrayList<String>());
                                FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.fragment_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commitAllowingStateLoss();
                                break;

                            case 2:
                                Toast.makeText(activity, "Clicked Friends", Toast.LENGTH_SHORT).show();

                                FriendListFragment friendListFragment = FriendListFragment.getInstance(1, new ArrayList<String>(), new ArrayList<String>());
                                FragmentTransaction friend_transaction = activity.getSupportFragmentManager().beginTransaction();
                                friend_transaction.replace(R.id.fragment_container, friendListFragment);
                                friend_transaction.addToBackStack(null);
                                friend_transaction.commitAllowingStateLoss();
                                break;

                            case 3:
                                Toast.makeText(activity, "Clicked Settings", Toast.LENGTH_SHORT).show();

                        }

                        return false;
                    }

                })
                .withAccountHeader(headerResult);

        return result;
    }



}
