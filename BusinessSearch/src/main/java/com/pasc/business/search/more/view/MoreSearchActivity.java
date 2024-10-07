package com.pasc.business.search.more.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.R;
import com.pasc.business.search.more.fragment.BanShiFragment;
import com.pasc.business.search.more.fragment.PolicyFragment;
import com.pasc.business.search.more.fragment.ServiceLocalFragment;
import com.pasc.business.search.more.fragment.ServiceNetFragment;
import com.pasc.business.search.more.fragment.ThemeNetFragment;
import com.pasc.business.search.more.fragment.UnionLocalFragment;
import com.pasc.business.search.more.fragment.UnionNetFragment;
import com.pasc.business.search.more.fragment.ZQHServiceFragment;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.util.FragmentUtils;
import com.pasc.lib.search.util.StatusBarUtils;
import com.pasc.lib.search.util.ToastUtil;

/**
 * @author yangzijian
 * @date 2019/2/21
 * @des
 * @modify
 **/
@Route(path = Table.Path.path_search_more_router)
public class MoreSearchActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        StatusBarUtils.setStatusBarColor(this, true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pasc_search_more_activity);
        Intent intent = getIntent();
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.moreContainer);
        if (fragment == null) {
            String themeId = "";
            if (intent != null) {
                themeId = intent.getStringExtra(Table.Key.key_themeConfigId);
            }

            if (themeId.equals(Table.Value.ThemeId.local)) {
                intent.putExtra(Table.Key.key_search_type, ItemType.personal_server);
                fragment = new ServiceLocalFragment();
            } else {
                intent.putExtra(Table.Key.key_search_type, ItemType.personal_server);
                fragment = new ThemeNetFragment();
            }

        }
        fragment.setArguments(intent.getExtras());
        FragmentUtils.showTargetFragment(getSupportFragmentManager(), fragment, R.id.moreContainer);
    }
}
