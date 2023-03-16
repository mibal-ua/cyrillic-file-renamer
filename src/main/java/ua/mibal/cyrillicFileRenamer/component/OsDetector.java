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

package ua.mibal.cyrillicFileRenamer.component;

import ua.mibal.cyrillicFileRenamer.model.programMode.OS;
import static ua.mibal.cyrillicFileRenamer.model.programMode.OS.UNIX;
import static ua.mibal.cyrillicFileRenamer.model.programMode.OS.WINDOWS;

/**
 * @author Mykhailo Balakhon
 * @link t.me/mibal_ua
 */
public class OsDetector {

    public static OS detect() {
        final String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return WINDOWS;
        }
        if (osName.contains("nix") || osName.contains("nux") ||
            osName.contains("aix") || osName.contains("mac")) {
            return UNIX;
        }
        throw new UnsupportedOperationException("Your OS '" + osName + "' is unsupported");
    }
}
