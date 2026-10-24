package com.automation.desktop;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
public class FrameAutomationApp extends JFrame {
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"Select", "Test case", "Result"}, 0) {
        public Class<?> getColumnClass(int c) { return c == 0 ? Boolean.class : String.class; }
        public boolean isCellEditable(int row, int c) { return c == 0; }
    };
    private final String[] methods = {"frameByName", "frameByIndex", "frameByElement", "nestedFramesAndParent", "returnToDefaultContent"};
    private final JTextArea log = new JTextArea();
    private final JButton run = new JButton("Run selected in Chrome");
    private final JButton all = new JButton("Select all");
    private final JButton none = new JButton("Clear selection");
    private final JCheckBox headless = new JCheckBox("Headless Chrome", false);
    private final JLabel status = new JLabel("Choose test cases, then click Run selected in Chrome.");
    private boolean running;
    public FrameAutomationApp() {
        super("Frame Automation Lab — Selenium + JUnit");
        setSize(1000, 700); setLocationRelativeTo(null); setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() { public void windowClosing(WindowEvent e) {
            if (running) JOptionPane.showMessageDialog(FrameAutomationApp.this, "Wait for the running tests to finish before closing.");
            else dispose();
        }});
        String[] labels = {"Switch by frame name and save a profile", "Switch by frame index", "Switch using an iframe WebElement", "Nested frames and return to parent", "Return from nested frames to main document"};
        for (String label : labels) model.addRow(new Object[]{true, label, "Not run"});
        JTable table = new JTable(model); table.setRowHeight(32);
        table.getColumnModel().getColumn(0).setMaxWidth(65);
        table.getColumnModel().getColumn(1).setPreferredWidth(580);
        log.setEditable(false); log.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JPanel top = new JPanel(new BorderLayout(8, 8));
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        top.add(new JLabel("Select the browser-frame scenarios to execute. Each test gets a fresh Chrome session."), BorderLayout.NORTH);
        top.add(new JScrollPane(table), BorderLayout.CENTER);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(all); controls.add(none); controls.add(headless); controls.add(run);
        top.add(controls, BorderLayout.SOUTH);
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, top, new JScrollPane(log)); split.setResizeWeight(0.5);
        add(split, BorderLayout.CENTER); add(status, BorderLayout.SOUTH);
        all.addActionListener(e -> select(true)); none.addActionListener(e -> select(false));
        run.addActionListener(e -> { if (table.isEditing()) table.getCellEditor().stopCellEditing(); execute(); });
    }
    private void select(boolean value) { for (int i=0; i<model.getRowCount(); i++) model.setValueAt(value, i, 0); }
    private void execute() {
        List<Integer> rows = new ArrayList<>();
        for (int i=0; i<model.getRowCount(); i++) if (Boolean.TRUE.equals(model.getValueAt(i,0))) rows.add(i);
        if (rows.isEmpty()) { JOptionPane.showMessageDialog(this,"Select at least one test case."); return; }
        final boolean hidden = headless.isSelected();
        running=true; run.setEnabled(false); all.setEnabled(false); none.setEnabled(false); headless.setEnabled(false);
        for(int i=0;i<model.getRowCount();i++) model.setValueAt(rows.contains(i)?"Queued":"Not selected",i,2);
        log.setText(""); status.setText("Running selected tests… Chrome will open unless headless is enabled.");
        new SwingWorker<Integer,String>() {
            protected Integer doInBackground() throws Exception {
                int failures=0;
                for (int row : rows) {
                    SwingUtilities.invokeLater(() -> model.setValueAt("Running",row,2));
                    String method=methods[row];
                    Path report=Paths.get("build","reports","run-"+System.currentTimeMillis(),method).toAbsolutePath();
                    Files.createDirectories(report);
                    List<String> command = new ArrayList<>();
                    command.add(Paths.get(System.getProperty("java.home"),"bin",System.getProperty("os.name").startsWith("Windows")?"java.exe":"java").toString());
                    command.add("-Dheadless="+hidden); command.add("-cp"); command.add(System.getProperty("java.class.path"));
                    command.add("org.junit.platform.console.ConsoleLauncher"); command.add("execute");
                    command.add("--select-method=com.automation.tests.FrameTest#"+method);
                    command.add("--reports-dir="+report); command.add("--fail-if-no-tests"); command.add("--disable-ansi-colors");
                    publish("\n=== "+method+" ===\n");
                    Process process=new ProcessBuilder(command).redirectErrorStream(true).start();
                    try (BufferedReader reader=new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                        String line; while((line=reader.readLine())!=null) publish(line+"\n");
                    }
                    int exit=process.waitFor(); if(exit!=0) failures++;
                    String result=exit==0?"PASS":"FAIL / ERROR";
                    SwingUtilities.invokeLater(() -> model.setValueAt(result,row,2));
                    publish("Reports: "+report+"\n");
                }
                return failures;
            }
            protected void process(List<String> lines) { for(String line:lines) log.append(line); log.setCaretPosition(log.getDocument().getLength()); }
            protected void done() {
                try { int failures=get(); status.setText("Finished: "+(rows.size()-failures)+" passed, "+failures+" failed / errored. Reports: build/reports"); }
                catch(Exception e) { log.append("Launcher error: "+e+"\n"); status.setText("Execution stopped: see the log.");
                    for(int row:rows) if(!"PASS".equals(model.getValueAt(row,2))&&!"FAIL / ERROR".equals(model.getValueAt(row,2))) model.setValueAt("ERROR / NOT RUN",row,2);
                }
                running=false; run.setEnabled(true); all.setEnabled(true); none.setEnabled(true); headless.setEnabled(true);
            }
        }.execute();
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> { try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored) {} new FrameAutomationApp().setVisible(true); }); }
}
