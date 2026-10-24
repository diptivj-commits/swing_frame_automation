@echo off
cd /d "%~dp0"
java -cp "FrameAutomationLab.jar;lib/*" com.automation.desktop.FrameAutomationApp
if errorlevel 1 pause
