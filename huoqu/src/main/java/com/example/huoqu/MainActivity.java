package com.example.huoqu;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.huoqu.bean.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private TextView tv;
    private ListView lv_main;
    private List<AppInfo> data;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        tv = (TextView) findViewById(R.id.tv);
        lv_main = (ListView) findViewById(R.id.lv_main);

        //初始化成员变量
        data = getAllAppInfos();
        adapter = new AppAdapter();
        //显示列表
        lv_main.setAdapter(adapter);


        //给ListView设置item的点击监听
        lv_main.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            /**
             * parent : ListView
             * view : 当前行的item视图对象
             * position : 当前行的下标
             */

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //提示当前行的应用名称
                String appName = data.get(position).getAppName();
                //提示
                Toast.makeText(MainActivity.this, appName, Toast.LENGTH_SHORT).show();

               //先判断我们拉起（跳转）的第三方APP是否存在
                if (isApkInstalled(MainActivity.this, data.get(position).getPackageName())) {
                    ////A应用直接拉起B应用
                    Intent intent = getPackageManager().getLaunchIntentForPackage(data.get(position).getPackageName());
                    if (intent != null) {
                        //如果不添加这个启动模式，有时候返回顺序是混乱的
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                } else {
                    //不存在APP则让它跳转到应用商店
                    launchAppDetail(data.get(position).getPackageName(),data.get(position).getPackageName());
                }
            }
        });

        //给LitView设置Item的长按监听
        lv_main.setOnItemLongClickListener(this);
    }


    //先判断我们拉起（跳转）的第三方APP是否存在
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //不存在APP则让它跳转到应用商店
    public void launchAppDetail(String appPkg, String marketPkg) {
        try {
            if (TextUtils.isEmpty(appPkg)) return;
            Uri uri = Uri.parse("market://details?id=" + appPkg);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (!TextUtils.isEmpty(marketPkg)) {
                intent.setPackage(marketPkg);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //适配器
    class AppAdapter extends BaseAdapter {

        public int getCount() {
            return data.size();
        }


        public Object getItem(int position) {
            return data.get(position);
        }


        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        //返回带数据当前行的Item视图对象

        public View getView(int position, View convertView, ViewGroup parent) {

            //1. 如果convertView是null, 加载item的布局文件
            if (convertView == null) {
                Log.e("TAG", "getView() load layout");
                convertView = View.inflate(MainActivity.this, R.layout.item_main, null);
            }
            //2. 得到当前行数据对象
            AppInfo appInfo = data.get(position);
            //3. 得到当前行需要更新的子View对象
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            TextView textView = (TextView) convertView.findViewById(R.id.tv_item_name);
            //4. 给视图设置数据
            imageView.setImageDrawable(appInfo.getIcon());
            textView.setText(appInfo.getAppName());

            //返回convertView
            return convertView;
        }

    }

    //获取信息集合
    protected List<AppInfo> getAllAppInfos() {
        List<AppInfo> list = new ArrayList<AppInfo>();
        // 得到应用的packgeManager
        PackageManager packageManager = getPackageManager();
        // 创建一个主界面的intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 得到包含应用信息的列表
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(
                intent, 0);
        // 遍历
        for (ResolveInfo ri : ResolveInfos) {
            // 得到包名
            String packageName = ri.activityInfo.packageName;
            // 得到图标
            Drawable icon = ri.loadIcon(packageManager);
            // 得到应用名称
            String appName = ri.loadLabel(packageManager).toString();
            // 封装应用信息对象
            AppInfo appInfo = new AppInfo(packageName, icon, appName);
            // 添加到list
            list.add(appInfo);
        }
        return list;
    }


    public boolean onItemLongClick(AdapterView<?> parent, View view,
                                   int position, long id) {
        //删除当前行
        //删除当前行的数据
        data.remove(position);
        //更新列表
        //lv_main.setAdapter(adapter);//显示列表, 不会使用缓存的item的视图对象
        adapter.notifyDataSetChanged();//通知更新列表, 使用所有缓存的item的视图对象
        return true;
    }

}
