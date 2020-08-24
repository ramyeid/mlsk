//package org.machinelearning.swissknife.ui;
//
//import javax.swing.*;
//import javax.swing.border.Border;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class RealTimeSeriesUI {
//    private JPanel pnlMain;
//    private JPanel pnlTools;
//    private JFrame frame;
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new RealTimeSeriesUI().createAndShowGUI();
//            }
//        });
//    }
//
//    public void createAndShowGUI() {
//        JButton button = new JButton("Tools");
//        button.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent event) {
//                boolean visible = pnlTools.isVisible();
//                pnlTools.setVisible(!visible);
//                frame.pack();
//            }
//        });
//
//        pnlTools = createToolsPanel();
//        pnlMain = createMainPanel();
//
//        JToolBar toolBar = new JToolBar();
//        toolBar.add(button);
//
//        JPanel contentPane = new JPanel();
//        contentPane.setLayout(new BorderLayout());
//        contentPane.setOpaque(true);
//        contentPane.add(toolBar, BorderLayout.NORTH);
//        contentPane.add(pnlMain, BorderLayout.WEST);
//        contentPane.add(pnlTools, BorderLayout.EAST);
//
//        pnlMain.setVisible(true);
//        pnlTools.setVisible(false);
//
//        JFrame.setDefaultLookAndFeelDecorated(true);
//        frame = new JFrame("Slide Out Panel Demo");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setContentPane(contentPane);
//        frame.pack();
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    private JPanel createMainPanel() {
//        JPanel panel = new JPanel();
//        panel.setBorder(BorderFactory.createTitledBorder("Main"));
//        panel.add(new JLabel("Field 1"));
//        panel.add(new JTextField(20));
//        panel.add(new JLabel("Field 2"));
//        panel.add(new JTextField(20));
//        panel.setSize(1000, 600);
//
//        return panel;
//    }
//
//    private JPanel createToolsPanel() {
//        JPanel panel = new JPanel();
//        panel.setBackground(Color.YELLOW);
//        Border b1 = BorderFactory.createTitledBorder("Tools");
//        Border b2 = BorderFactory.createLineBorder(Color.BLUE, 2);
//        panel.setBorder(BorderFactory.createCompoundBorder(b2, b1));
//        panel.add(new JLabel("Thing 1"));
//        panel.add(new JLabel("Thing 2"));
//        panel.setSize(400, 600);
//
//        return panel;
//    }
//
////    public static void main(String [] args) {
////        TimeSeriesAnalysisServiceClient timeSeriesAnalysisServiceClient = mock(TimeSeriesAnalysisServiceClient.class);
////        when(timeSeriesAnalysisServiceClient.forecast(any())).thenReturn(buildTimeSeries());
////        when(timeSeriesAnalysisServiceClient.predict(any())).thenReturn(buildTimeSeries());
////        TimeSeriesPanel timeSeriesPanel = new TimeSeriesPanel(timeSeriesAnalysisServiceClient);
////        JFrame frame = new JFrame();
////        frame.getContentPane().add(timeSeriesPanel);
////        frame.setVisible(true);
////        frame.setSize(600, 600);
////        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////    }
////
////
////    private static TimeSeries buildTimeSeries() {
////        TimeSeriesRow timeSeriesRow = new TimeSeriesRow("1960-01", 1.0);
////        TimeSeriesRow timeSeriesRow1 = new TimeSeriesRow("1960-02", 1.0);
////        TimeSeriesRow timeSeriesRow2 = new TimeSeriesRow("1960-03", 1.0);
////        TimeSeriesRow timeSeriesRow3 = new TimeSeriesRow("1960-04", 1.0);
////        TimeSeriesRow timeSeriesRow4 = new TimeSeriesRow("1960-05", 1.0);
////        return new TimeSeries(asList(timeSeriesRow, timeSeriesRow1, timeSeriesRow2, timeSeriesRow3, timeSeriesRow4),
////                              "Date", "Passengers", "yyyy-MM");
////    }
//}