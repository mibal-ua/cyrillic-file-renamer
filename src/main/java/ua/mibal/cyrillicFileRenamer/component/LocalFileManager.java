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

import static java.lang.String.format;
import java.io.File;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class LocalFileManager {

    public static File[] getFilesFromDirectory(final String pathToCatalog, final DataPrinter dataPrinter) {
        File directory = new File(pathToCatalog);
        File[] directoryFiles = null;
        try {
            directoryFiles = directory.listFiles();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        if (directoryFiles == null) {
            extracted(pathToCatalog, dataPrinter, "no access");
        }
        if (directoryFiles.length == 0) {
            extracted(pathToCatalog, dataPrinter);
        } else if (directoryFiles.length == 1 && (directoryFiles[0].getName().equals(".DS_Store") ||
                                                  directoryFiles[0].getName().equals(".DS_Store"))) {
            extracted(pathToCatalog, dataPrinter);
        } else if (directoryFiles.length == 2 && (directoryFiles[0].getName().equals(".DS_Store") ||
                                                  directoryFiles[1].getName().equals(".DS_Store")) &&
                   (directoryFiles[0].getName().equals("renamedToLatin") ||
                    directoryFiles[1].getName().equals("renamedToLatin"))) {
            extracted(pathToCatalog, dataPrinter);
        }
        return directoryFiles;
    }

    private static void extracted(final String pathToCatalog, final DataPrinter dataPrinter, final String message) {
        dataPrinter.printErrorMessage(format(
            "\nThere is %s in directory: '%s'.\n", message, pathToCatalog));
        dataPrinter.exit();
    }

    private static void extracted(final String pathToCatalog, final DataPrinter dataPrinter) {
        extracted(pathToCatalog, dataPrinter, "no files");
    }
}
