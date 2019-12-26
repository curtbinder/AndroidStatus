/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Curt Binder
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package info.curtbinder.reefangel.phone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import info.curtbinder.reefangel.service.NotificationService;
import info.curtbinder.reefangel.service.UpdateService;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String ALARM_TYPE = "alarm_type";
    public static final int UPDATE = 10;
    public static final int NOTIFICATION = 20;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "onReceive");
        int type = intent.getIntExtra(ALARM_TYPE, UPDATE);
        if ( type == NOTIFICATION ) {
            Log.d("AlarmReceiver", "NotificationService");
            intent.setClass(context, NotificationService.class);
            NotificationService.enqueueWork(context, intent);
        } else {
            Log.d("AlarmReceiver", "UpdateService");
            intent.setClass(context, UpdateService.class);
            UpdateService.enqueueWork(context, intent);
        }
    }
}
