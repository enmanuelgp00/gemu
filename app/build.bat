@echo off
 
javac -d build\classes --class-path main\java main\java\gemu\Gemu.java
if %errorlevel% equ 0 (
	jar -c -f jar\gemu.jar --main-class gemu.Gemu -C build\classes .
)