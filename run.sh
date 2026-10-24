#!/bin/sh
cd "$(dirname "$0")" || exit 1
exec java -cp 'FrameAutomationLab.jar:lib/*' com.automation.desktop.FrameAutomationApp
