package com.search.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pasc.business.search.ItemType;
import com.pasc.business.search.SearchManager;
import com.pasc.business.search.common.net.CommonBiz;
import com.pasc.business.search.common.net.CommonBizBase;
import com.pasc.business.search.router.Table;
import com.pasc.lib.search.db.history.SearchInfoBean;
import com.pasc.lib.search.util.LogUtil;
import com.pingan.smt.R;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import io.reactivex.functions.Consumer;

//import com.pingan.demo.paspeechsdk.R;

/**
 * @author yangzijian
 * @date 2019/2/19
 * @des
 * @modify
 **/
public class MainActivity extends AppCompatActivity {
  EditText editText;
  CheckBox checkbox;

  public void searchMain(View view) {
    Bundle bundle = new Bundle();
    bundle.putString(Table.Key.key_entranceLocation, Table.Value.EntranceLocation.enterprise_type);
    bundle.putString(Table.Key.key_entranceId, Table.Value.EntranceId.personal_home_test);
    ARouter.getInstance().build(Table.Path.path_search_home_router).with(bundle).navigation();
  }

  public void searchPerson(View view) {
    Bundle bundle = new Bundle();
    bundle.putString(Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
    bundle.putString(Table.Key.key_entranceId, Table.Value.EntranceId.personal_home_test);
    bundle.putBoolean(Table.Key.key_show_voice_anim, checkbox.isChecked());
    ARouter.getInstance().build(Table.Path.path_search_home_router).with(bundle).navigation();
  }

  public void wangTing(View view) {
    Bundle bundle = new Bundle();
    bundle.putString(Table.Key.key_entranceLocation, Table.Value.EntranceLocation.person_type);
    bundle.putString(Table.Key.key_search_type, ItemType.personal_work_guide);
    bundle.putBoolean(Table.Key.key_wang_ting_flag, true);
    ARouter.getInstance().build(Table.Path.path_search_more_router).with(bundle).navigation();
  }

  public void getHints(View view) {
    if (SearchManager.instance().isUseBaseApi()) {
      CommonBizBase.getAllSearchHint();
    } else {
      CommonBiz.getAllSearchHint();
    }
  }

  public void query(View view) {
    //        String keyword = editText.getText().toString();
    //        SearchBiz.queryLocal(keyword, "1","").subscribe(new Consumer<List<SearchSourceItem>>() {
    //            @Override
    //            public void accept(List<SearchSourceItem> sourceItems) throws Exception {
    //                System.out.println();
    //            }
    //        }, new Consumer<Throwable>() {
    //            @Override
    //            public void accept(Throwable throwable) throws Exception {
    //                System.out.println();
    //            }
    //        });

    new Thread(new Runnable() {
      @Override
      public void run() {
        List<SearchInfoBean> beans = SQLite.select().from(SearchInfoBean.class).queryList();
        LogUtil.log("searchHints Size " + beans.size());
      }
    }).start();

    SearchManager.instance()
        .getSearchHint(Table.Value.HomeType.business_home_page)
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String s) throws Exception {
            LogUtil.log("ssss  " + s);
          }
        }, new Consumer<Throwable>() {
          @Override
          public void accept(Throwable throwable) throws Exception {

          }
        });
  }

  public void gotoSiri(View view) {
    startActivity(new Intent(this, ASRAc.class));
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_layout);
    checkbox = findViewById(R.id.checkbox);
    editText = findViewById(R.id.edit);
  }
}
