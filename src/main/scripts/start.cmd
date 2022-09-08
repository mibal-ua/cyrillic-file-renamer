@rem
@rem Copyright 2022 http://t.me/mibal_ua
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem You may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem        http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem
@echo off
@rem -------------------------------------------------------------------------------------------------------------------
setlocal
@rem -------------------------------------------------------------------------------------------------------------------
@rem Try to use `java.exe` from JRE if `jre\bin\java.exe` exists:
if exist jre\bin\java.exe (
    set JAVA_CMD=jre\bin\java
)
@rem -------------------------------------------------------------------------------------------------------------------
if not defined JAVA_CMD (
    @rem Throw error if `java.exe` is not configured:
    echo -------------------------------------------------------------------------- >&2
    echo `java.exe` not defined! Install or configure JVM before using this script! >&2
    echo -------------------------------------------------------------------------- >&2
    set RETURN_CODE=1
) else (
    @rem Run tic-tac-toe game:
    %JAVA_CMD% -jar ${project.build.finalName}-release.jar %*
    set RETURN_CODE=0
)
@rem -------------------------------------------------------------------------------------------------------------------
exit /b %RETURN_CODE%