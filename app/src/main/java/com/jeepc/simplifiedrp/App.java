package com.jeepc.simplifiedrp;

import android.app.Application;
import android.content.Context;

/**
 * Created by jeepc on 2018/7/9.
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        hookClassloader();

    }

    private void hookClassloader() {
        try {
            Context oBase = this.getBaseContext();
            Object oPackageInfo = null;

            oPackageInfo = ReflectUtils.readField(oBase, "mPackageInfo");

            // 获取mPackageInfo.mClassLoader
            ClassLoader oClassLoader = (ClassLoader) ReflectUtils.readField(oPackageInfo, "mClassLoader");
            if (oClassLoader == null) {
                return;
            }
            ClassLoader cl = new SRPClassloader(oClassLoader.getParent(), oClassLoader);
            // 将新的ClassLoader写入mPackageInfo.mClassLoader
            ReflectUtils.writeField(oPackageInfo, "mClassLoader", cl);

            // 设置线程上下文中的ClassLoader为RePluginClassLoader
            // 防止在个别Java库用到了Thread.currentThread().getContextClassLoader()时，“用了原来的PathClassLoader”，或为空指针
            Thread.currentThread().setContextClassLoader(cl);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
