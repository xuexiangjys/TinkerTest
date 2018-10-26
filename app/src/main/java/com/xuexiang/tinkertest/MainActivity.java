package com.xuexiang.tinkertest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tinkerpatch.sdk.TinkerPatch;
import com.xuexiang.tinkertest.util.ReopenAppUtils;
import com.xuexiang.xaop.annotation.Permission;
import com.xuexiang.xutil.app.ActivityUtils;
import com.xuexiang.xutil.app.IntentUtils;
import com.xuexiang.xutil.app.PathUtils;

import static com.xuexiang.xaop.consts.PermissionConsts.STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_GET_PATCH_PACKAGE = 20;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fix:
                choosePatchApk();
                break;
            case R.id.btn_reopen:
                ReopenAppUtils.reopenApp(this);
                break;
            case R.id.btn_check_update:
                TinkerPatch.with().fetchPatchUpdate(true);
                break;
            default:
                break;
        }
    }

    @Permission(STORAGE)
    private void choosePatchApk() {
        ActivityUtils.startActivityForResult(this, IntentUtils.getDocumentPickerIntent(IntentUtils.DocumentType.ANY), REQUEST_CODE_GET_PATCH_PACKAGE);
    }

    @Override
    @SuppressLint("MissingPermission")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == REQUEST_CODE_GET_PATCH_PACKAGE) {
            if (data != null) {
                String path = PathUtils.getFilePathByUri(data.getData());
                TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
            }
        }
    }

}
