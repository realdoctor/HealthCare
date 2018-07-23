/*
 * Copyright (C) 2015. Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.real.doctor.realdoc.widget.progress.processutil;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.real.doctor.realdoc.widget.progress.processutil.models.AndroidAppProcess;
import com.real.doctor.realdoc.widget.progress.processutil.models.AndroidProcess;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class to get a list of processes on Android.
 * <p/>
 * <p><b>Note:</b> Every method in this class should <i>not</i> be executed on the main thread.</p>
 */
public class ProcessManager {

    public static final String TAG = "AndroidProcesses";

    private static boolean loggingEnabled;

    /**
     * Toggle whether debug logging is enabled.
     *
     * @param enabled {@code true} to enable logging. This should be only be used for debugging purposes.
     * @see #isLoggingEnabled()
     * @see #log(String, Object...)
     * @see #log(Throwable, String, Object...)
     */
    public static void setLoggingEnabled(boolean enabled) {
        loggingEnabled = enabled;
    }

    /**
     * @return {@code true} if logging is enabled.
     * @see #setLoggingEnabled(boolean)
     */
    public static boolean isLoggingEnabled() {
        return loggingEnabled;
    }

    /**
     * Send a log message if logging is enabled.
     *
     * @param message the message to log
     * @param args    list of arguments to pass to the formatter
     */
    public static void log(String message, Object... args) {
        if (loggingEnabled) {
            Log.d(TAG, args.length == 0 ? message : String.format(message, args));
        }
    }

    /**
     * Send a log message if logging is enabled.
     *
     * @param error   An exception to log
     * @param message the message to log
     * @param args    list of arguments to pass to the formatter
     */
    public static void log(Throwable error, String message, Object... args) {
        if (loggingEnabled) {
            Log.d(TAG, args.length == 0 ? message : String.format(message, args), error);
        }
    }

    /**
     * Get a list of user apps running in the foreground.
     *
     * @param ctx the application context
     * @return a list of user apps that are in the foreground.
     */
    public static List<AndroidAppProcess> getRunningForegroundApps(Context ctx) {
        List<AndroidAppProcess> processes = new ArrayList<>();
        File[] files = new File("/proc").listFiles();
        PackageManager pm = ctx.getPackageManager();
        for (File file : files) {
            if (file.isDirectory()) {
                int pid;
                try {
                    pid = Integer.parseInt(file.getName());
                } catch (NumberFormatException e) {
                    continue;
                }
                try {
                    AndroidAppProcess process = new AndroidAppProcess(pid);
                    if (process.foreground
                            // ignore system processes. First app user starts at 10000.
                            && (process.uid < 1000 || process.uid > 9999)
                            // ignore processes that are not running in the default app process.
                            && !process.name.contains(":")
                            // Ignore processes that the user cannot launch.
                            && pm.getLaunchIntentForPackage(process.getPackageName()) != null) {
                        processes.add(process);
                    }
                } catch (AndroidAppProcess.NotAndroidAppProcessException ignored) {
                } catch (IOException e) {
                    log(e, "Error reading from /proc/%d.", pid);
                    // System apps will not be readable on Android 5.0+ if SELinux is enforcing.
                    // You will need root access or an elevated SELinux context to read all files under /proc.
                }
            }
        }
        return processes;
    }

    private ProcessManager() {
        throw new AssertionError("no instances");
    }

}
