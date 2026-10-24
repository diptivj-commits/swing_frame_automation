# Frame Automation Lab: Swing + Selenium + JUnit 5

## Start on Windows (no Ant installation needed to run)
1. Extract the entire ZIP into a writable folder. Keep FrameAutomationLab.jar and lib together.
2. Install Java 21 and Google Chrome. Confirm `java -version` works in Command Prompt.
3. Double-click run.bat. The first browser run may download ChromeDriver through Selenium Manager and requires internet access.
4. Select the test cases, leave Headless Chrome unchecked to see browser actions, then click Run selected in Chrome.
5. Each checked case opens a fresh Chrome session, executes, and closes it. Cases run sequentially. The table shows PASS or FAIL / ERROR, with detailed JUnit output below. Reports go to build/reports.

Only the selected tests run. The Swing interface stays responsive while execution happens in child JVMs. Close the app after execution finishes. Tests may complete quickly; the goal is deterministic assertions rather than artificial delays. Selection changes during a run affect only the next run.

## Scenarios
- frameByName: enter and save a name inside an iframe selected by its name.
- frameByIndex: switch to the first iframe and verify its heading.
- frameByElement: locate the iframe WebElement and switch into it.
- nestedFramesAndParent: switch two levels down, select a checkbox, and return to the parent.
- returnToDefaultContent: return directly from a nested frame to the top document.

The fixture server binds to 127.0.0.1 on an automatically assigned port and stops after each test. Browser fixtures are packaged in the app; no external practice site is required for frame tests. Chrome and ChromeDriver setup are still required. A browser startup failure is reported as a failed/errored test, not a pass.

Selenium automates browser frames, not Swing widgets. Swing is the test-selection and results interface. Native Swing UI testing would require a separate tool such as AssertJ Swing.

## Rebuild with Ant
Source and build files are included. A full JDK 21 and Apache Ant are needed only for editing/rebuilding:
```
ant check
ant package
ant desktop
ant -Dtest.pattern=.*FrameTest test
```
Ant downloads dependencies through Apache Ivy from Maven Central. Selenium 4.50.0 and JUnit Jupiter 5.11.4 are pinned in build.properties. No Maven or TestNG required. Legacy sample LoginTest and LocalSmokeTest are retained for extra practice, but the desktop only offers FrameTest methods. `ant test` runs all test classes, including those additional samples.

## Eclipse
Import as Existing Projects into Workspace. Run `ant resolve` first if libraries are missing. Add every JAR from lib through Project > Properties > Java Build Path > Libraries > Classpath > Add JARs. Use JDK 21. Run FrameAutomationApp as Java Application; run FrameTest as JUnit Test with JUnit 5. Do not add another JUnit library, because the console standalone JAR already includes JUnit. If running inside Eclipse, the child process inherits the launch classpath.

## Files
src/main/java/com/automation/desktop/FrameAutomationApp.java: Swing selection, background execution, results.
src/test/java/com/automation/tests/FrameTest.java: browser frame scenarios and local HTTP fixture server.
src/test/resources/fixtures/frames/: HTML pages.
src/test/java/com/automation/base/BaseTest.java: browser lifecycle and waits.
FrameAutomationLab.jar and lib/: executable classes/resources and dependencies.

## Source control
The downloaded dependency JARs and generated build output should not be committed. The archive includes them for convenient local running. Rebuild after changing Java or fixture files; run.bat runs the packaged JAR, not source files.
