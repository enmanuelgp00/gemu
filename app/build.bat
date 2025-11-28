javac -d build -classpath main\java main\java\gemu\Gemu.java
if %errorlevel% equ 0 ( jar -c -f jar\Gemu.jar --main-class gemu.Gemu -C build . )