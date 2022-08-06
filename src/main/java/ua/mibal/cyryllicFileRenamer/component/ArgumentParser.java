/*
 * Copyright (c) 2022. http://t.me/mibal_ua
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ua.mibal.cyryllicFileRenamer.component;

import ua.mibal.cyryllicFileRenamer.model.Lang;
import ua.mibal.cyryllicFileRenamer.model.OS;

import java.io.File;

import static java.lang.String.format;
import static ua.mibal.cyryllicFileRenamer.model.Lang.RU;
import static ua.mibal.cyryllicFileRenamer.model.Lang.UA;
import static ua.mibal.cyryllicFileRenamer.model.OS.UNIX;
import static ua.mibal.cyryllicFileRenamer.model.OS.WINDOWS;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class ArgumentParser {

    private String path;

    private Lang lang;

    private OS OS;

    public void parse(String[] args) {
        // add more parameters in future
        for (final String arg : args) {
            if (arg.equalsIgnoreCase("this")) {
                this.path = System.getProperty("user.dir");
            } else if (arg.equalsIgnoreCase(UA.name()) || arg.equalsIgnoreCase(RU.name())) {
                lang = Lang.valueOf(arg.toUpperCase());
            } else if (arg.equalsIgnoreCase(UNIX.name()) || arg.equalsIgnoreCase(WINDOWS.name())) {
                OS = ua.mibal.cyryllicFileRenamer.model.OS.valueOf(arg.toUpperCase());
            } else if (isThisAPath(arg)) {
                this.path = arg;
            } else {
                throw new IllegalArgumentException(format("Incorrect argument '%s'.", arg));
            }
        }
    }

    private boolean isThisAPath(final String arg) {
        if (!(arg.contains("\\") || arg.contains("/"))) {
            return false;
        }
        return new File(arg).exists();
    }

    public String getPath() {
        return path;
    }

    public Lang getLang() {
        return lang;
    }

    public OS getOS() {
        return OS;
    }
}
