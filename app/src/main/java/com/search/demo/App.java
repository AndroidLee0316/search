package com.search.demo;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.support.multidex.MultiDex;

import com.alibaba.android.arouter.launcher.ARouter;
import com.pasc.business.search.SearchManager;
import com.pasc.lib.imageloader.PascImageLoader;
import com.pasc.lib.net.NetConfig;
import com.pasc.lib.net.NetManager;

import java.util.Iterator;

/**
 * @author yangzijian
 * @date 2019/2/17
 * @des
 * @modify
 **/
public class App extends Application {
  public static App instance;

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    if (getPackageName().equals(getPIDName(this))) {
      initARouter(this, true);
      final NetConfig config = new NetConfig.Builder(this)
          //.baseUrl("http://basesmt-caas.yun.city.pingan.com")
          .baseUrl("http://smt-stg1.yun.city.pingan.com")
          .headers(HeaderUtil.getHeaders(this, true, null))
          .isDebug(true)
          .gson(GsonUtil.getConvertGson())
          .build();
      NetManager.init(config);
      initImageLoader();
      SearchManager.instance()
          .initSearch(this, new ApiGetImpl(), "1.6.4", true, true).setHideNetworkSearch(false);
    }
  }

  @SuppressLint("ResourceType")
  private void initImageLoader() {
    PascImageLoader.getInstance()
        .init(this, PascImageLoader.GLIDE_CORE, Color.parseColor("#f7f7f7"));
  }

  /**
   * 初始化路由
   */
  public static void initARouter(Application application, boolean debug) {
    if (debug) {
      ARouter.openLog();     // 打印日志
      ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
    }
    ARouter.init(application); // 尽可能早，推荐在Application中初始化
  }

  @Override
  public void onTerminate() {
    super.onTerminate();
    destroyRouter();
  }

  /**
   * 销毁路由
   */
  public static void destroyRouter() {
    ARouter.getInstance().destroy();
  }

  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    MultiDex.install(base);
  }

  private String getPIDName(Context context) {
    int pid = android.os.Process.myPid();
    ActivityManager mActivityManager =
        (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    Iterator var3 = mActivityManager.getRunningAppProcesses().iterator();
    ActivityManager.RunningAppProcessInfo appProcess;
    do {
      if (!var3.hasNext()) {
        return "";
      }

      appProcess = (ActivityManager.RunningAppProcessInfo) var3.next();
    } while (appProcess.pid != pid);

    return appProcess.processName;
  }
}
