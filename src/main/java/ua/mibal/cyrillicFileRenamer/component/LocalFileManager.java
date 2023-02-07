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
import static java.util.Objects.requireNonNull;
import java.io.File;

/**
 * @author Michael Balakhon
 * @link http://t.me/mibal_ua
 */
public class LocalFileManager implements FileManager {

    private final static String[] IGNORED_FILE_NAMES = {
        "Thumbs.db", "$RECYCLE.BIN", "desktop.ini", "cyrillic-file-renamer-"
    };

    private final DataPrinter dataPrinter;

    public LocalFileManager(final DataPrinter dataPrinter) {
        this.dataPrinter = requireNonNull(dataPrinter);
    }

    public File[] getFilesFromDirectory(final String pathToCatalog) {
        final File directory = new File(pathToCatalog);
        File[] directoryFiles = null;
        try {
            directoryFiles = directory.listFiles();
        } catch (SecurityException e) {
            printErrorAndExit(pathToCatalog, "no access");
        }

        if (directoryFiles.length == 0) {
            printErrorAndExit(pathToCatalog, "no files");
        }
        if (directoryFiles.length == 1 && isIgnoredFile(directoryFiles[0].getName())) {
            printErrorAndExit(pathToCatalog, "no files");
        }
        if (directoryFiles.length == 2 && (directoryFiles[0].getName().equals(".DS_Store") ||
                                           directoryFiles[1].getName().equals(".DS_Store")) &&
            (directoryFiles[0].getName().equals("renamedToLatin") ||
             directoryFiles[1].getName().equals("renamedToLatin"))) {
            printErrorAndExit(pathToCatalog, "no files");
        }
        return directoryFiles;
    }

    public boolean isIgnoredFile(final String fileName) {
        if (fileName.charAt(0) == '.') {
            return true;
        }
        for (final String ignoredFileName : IGNORED_FILE_NAMES) {
            if (fileName.contains(ignoredFileName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public File createResultingDirectory(final String pathToCatalog) {
        final File file = new File(pathToCatalog + "/renamedToLatin");
        if (file.mkdir()) {
            return file;
        }
        return null;
    }

    private void printErrorAndExit(final String pathToCatalog, final String message) {
        dataPrinter.printErrorMessage(format(
            "\nThere is %s in directory: '%s'.\n", message, pathToCatalog));
        dataPrinter.exit();
    }
}
