# The Cyrillic file renamer Project

## About project

This app is designed to help users rename files
with Cyrillic characters in their names, so they
can be displayed properly on any computer, even
if the system does not support Cyrillic encoding.

The program supports only the Ukrainian language
because russia and byelorussia started a war against
Ukraine.

## Build instructions

- Build distributions using Maven tool:

```bash
mvn clean package
```

- Go to `target` dir;
- Use the following archives:
    - Windows `cyrillic-file-renamer-${version}-windows.zip`
    - macOS `cyrillic-file-renamer-${version}-macos.tar.gz`
    - Linux `cyrillic-file-renamer-${version}-linux.tar.gz`

## Run instructions

- Unzip the distribution archive (see above);
- Go to unzipped directory;
- Run the app by double-click on the start script:
    - `start.cmd` for Windows;
    - `start.sh` for macOS or Linux;

## Arguments manual

For using custom args in Terminal, use following info:

1. Args are divided into 2 types:
    - Path: `/Users/admin/path/to/dir` or `this`;
    - Transliteration standard: `OFFICIAL`, `EXTENDED`;
2. Run app with `java` util;

   > in distribution package already
   exists [JRE](https://en.wikipedia.org/wiki/Java_(software_platform)#Java_Runtime_Environment), to use it
   enter `jre/bin/java...` in Terminal or if you have installed Java 15+ version, just use `java...`

3. Correct args usage:

- `java -jar cyrillic-file-renamer-${version}-${myOS}.jar /Users/admin/path/to/ OFFICIAL`
- `jre/bin/java -jar cyrillic-file-renamer-${version}-${myOS}.jar extended this`

4. For use `this`-option scripts you must move folder with script to the folder with cyrillic files;
   <img width="503" alt="Screenshot 2023-02-14 at 20 36 00" src="https://user-images.githubusercontent.com/59470968/218839289-221f9bbe-99c6-4904-904a-f2b209275942.png">
