package PasswordScanner;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.prefs.Preferences;
import java.awt.Toolkit;
import java.time.LocalTime;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Console;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.UIManager;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

public class Locker {

    public Locker() {

    }
    private static final ReentrantLock lock = new ReentrantLock();
    private static boolean is_BF_enabled;
    private static Preferences memory = Preferences.userRoot();
    private static LocalTime currentTime = LocalTime.now();
    private static int[] hhssbefore = {memory.getInt("hh", -1), memory.getInt("mm", -1)};
    private static int attempts = memory.getInt("attempts", 0);

    private static void enableAscii() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                ProcessBuilder processbuilder = new ProcessBuilder("cmd", "/c", "reg add \"HKCU\\Console\" /v VirtualTerminalLevel /t REG_DWORD /d 1 /f > NUL 2>&1");
                processbuilder.inheritIO().start().waitFor();
            } catch (Exception ex) {

            }
        }
    }
/***
 * Shows a CLI based protection for your jar
 * @param PIN PIN Code
 */
    public static void protect(int PIN) {
        if (System.console() != null) {
            lock.lock();
            try {
                Console console = System.console();
                int result = -2;
                // Check if the console is available
                if (console == null) {
                    System.out.println("No console available. Please run this program in a terminal.");
                    System.exit(1);
                }
                enableAscii();
                System.out.println("This Java application is protected!");
                while (result != PIN) {
                    final String MOVE_CURSOR_UP = "\033[F";
                    final String CLEAR_LINE = "\033[2K";
                    final String MOVE_CURSOR_FORWARD = "\033[11C";
                    char[] passwordChars = console.readPassword("Enter PIN: ");
                    StringBuilder input = new StringBuilder();
                    // Mask digits with '*' and store actual input
                    System.out.print(CLEAR_LINE + MOVE_CURSOR_UP + MOVE_CURSOR_FORWARD);
                    for (char ch
                            : passwordChars) {
                        if (Character.isDigit(ch)) {
                            input.append(ch);

                            System.out.print("*");
                        } else {

                        }
                    }
                    try {
                        result = Integer.parseInt(input.toString());
                    } catch (NumberFormatException e) {
                        result = -1; // Return an invalid PIN if input is empty or invalid
                    }

                    if (result != PIN) {
                        if (result != -1) {
                            System.out.print("\n");
                        } else {
                            System.out.println("\nWrong passcode! Try again:");
                        }
                    }
                }
                // Convert the input string to an integer
            } finally {
                lock.unlock();
                System.out.print("\033[2J\033[H");
                System.out.flush();
            }
        } else {
            System.out.println("\u001B[33;2mProtection applies only outside the IDE\u001B[0m");
        }
    }
/***
 * Shows a CLI based protection for your jar
 * @param PIN PIN Code
 * @param Owner The owner name who locked the jar, this can be an email or an actual name
 * @throws IOException
 * @throws BackingStoreException 
 */
    public static void protect(int PIN, String Owner) throws IOException, BackingStoreException {
        if (System.console() != null) {
            lock.lock();
            try {
                Console console = System.console();
                int result = -2;
                // Check if the console is available
                if (console == null) {
                    System.out.println("\u001B[31mNo console available. Please run this program in a terminal.\u001B[0m");
                    System.exit(1);
                }
                enableAscii();

                if (is_BF_enabled) {
                    if (hhssbefore[0] != -1 && hhssbefore[1] != -1) {
                        LocalTime savedtime = LocalTime.of(hhssbefore[0], hhssbefore[1]);
                        Duration dur = Duration.between(savedtime, currentTime);
                        if (dur.toMinutes() < 4) {
                            System.out.println("\u001B[31mYou need to wait for about 4 minutes before retrying!\nPress [ENTER] to exit...\u001B[0m");
                            System.in.read();
                            System.exit(0);
                        }
                    }
                }
                System.out.println("This Java application is protected!\nOwned by: " + Owner);
                while (result != PIN) {
                    final String MOVE_CURSOR_UP = "\033[F";
                    final String CLEAR_LINE = "\033[2K";
                    final String MOVE_CURSOR_FORWARD = "\033[11C";
                    char[] passwordChars = console.readPassword("Enter PIN: ");
                    StringBuilder input = new StringBuilder();
                    // Mask digits with '*' and store actual input
                    System.out.print(CLEAR_LINE + MOVE_CURSOR_UP + MOVE_CURSOR_FORWARD);
                    for (char ch
                            : passwordChars) {
                        if (Character.isDigit(ch)) {
                            input.append(ch);

                            System.out.print("*");
                        } else {

                        }
                    }
                    try {
                        result = Integer.parseInt(input.toString());
                    } catch (NumberFormatException e) {
                        result = -1; // Return an invalid PIN if input is empty or invalid
                    }
                    if (result != PIN && result != -1) {
                        System.out.println("\nWrong passcode! Try again:");
                        if (is_BF_enabled) {
                            attempts++;
                            if (attempts == 4) {
                                memory.putInt("hh", currentTime.getHour());
                                memory.putInt("mm", currentTime.getMinute());
                                System.out.println("\u001B[31m4 failed attempts, please contact the owner of this jar.\nPress [ENTER] to exit...\u001B[0m");
                                System.in.read();
                                System.exit(1);
                            }
                        }
                    } else {
                        if (is_BF_enabled) {
                            System.out.println("");
                            attempts = 0;
                            memory.putInt("attempts", attempts);
                            memory.putInt("hh", -1);
                            memory.putInt("mm", -1);
                        }
                    }
                }
                // Convert the input string to an integer
            } finally {
                lock.unlock();
                System.out.print("\033[2J\033[H");
                System.out.flush();
            }
        } else {
            System.out.println("\u001B[33;2mProtection applies only outside the IDE\u001B[0m");
        }
    }

    private static void showInvalidPINGUI(JDialog window, boolean fp) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        }
        JButton OK = new JButton();
        OK.setFocusPainted(fp);
        OK.setPreferredSize(new Dimension(73, 28));
        OK.setText("OK");
        Font currentFont = OK.getFont();          // Get current font
        Font newFont = currentFont.deriveFont(14f);    // Create new font with size 16 (float)
        OK.setFont(newFont);                      // Set new font on dialogOK
        Object[] options = {OK};
        JOptionPane msg;
        JLabel messageLabel = new JLabel("Invalid PIN, Please try again");
        messageLabel.setFont(newFont);
        if (fp == false) {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), options, null);
        } else {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), options, options[0]);
        }
        JDialog dmsg = msg.createDialog(window, "Unlock failed");
        OK.addActionListener(ex -> {
            msg.setValue(JOptionPane.OK_OPTION);
            dmsg.hide();// Close the dialog
        });
        dmsg.show();

    }

    private static void ProtectBruteForce(JDialog window, boolean fp) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        }
        JButton OK = new JButton();
        OK.setFocusPainted(fp);
        OK.setPreferredSize(new Dimension(73, 28));
        OK.setText("OK");
        Font currentFont = OK.getFont();          // Get current font
        Font newFont = currentFont.deriveFont(14f);    // Create new font with size 16 (float)
        OK.setFont(newFont);
        Object[] options = {OK};
        JLabel messageLabel = new JLabel("4 failed attempts, please contact the owner of this jar. You may try again after 4 minutes\nPress OK to close this aplication");
        messageLabel.setFont(newFont);
        JOptionPane msg;

        if (fp == false) {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), options, null);
        } else {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"), options, options[0]);
        }
        msg.setFont(newFont);
        JDialog dmsg = msg.createDialog(window, "Brutal force protection");
        OK.addActionListener(ex -> {
            msg.setValue(JOptionPane.OK_OPTION);
            dmsg.hide();// Close the dialog
            System.exit(0);
        });
        dmsg.show();

    }

    private static void showBruteForceTimeout(JDialog window, boolean fp) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
        }
        JButton OK = new JButton();
        OK.setFocusPainted(fp);
        OK.setPreferredSize(new Dimension(73, 28));
        OK.setText("OK");
        Font currentFont = OK.getFont();          // Get current font
        Font newFont = currentFont.deriveFont(14f);    // Create new font with size 16 (float)
        OK.setFont(newFont);                      // Set new font on dialogOK
        Object[] options = {OK};
        JOptionPane msg;
        JLabel messageLabel = new JLabel("You need to wait for about 4 minutes before retrying");
        messageLabel.setFont(newFont);
        if (fp == false) {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.errorIcon"), options, null);
        } else {
            msg = new JOptionPane(messageLabel, JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"), options, options[0]);
        }

        JDialog dmsg = msg.createDialog(window, "Brutal force protection");
        OK.addActionListener(ex -> {
            msg.setValue(JOptionPane.OK_OPTION);
            dmsg.hide();// Close the dialog
            System.exit(0);
        });
        dmsg.show();
        dmsg.setModal(true);

    }
    /***
     * Shows a GUI based protection for your jar
     * @param PIN PIN Code
     * @param Owner The owner name who locked the jar, this can be an email or an actual name
     * @param Is_cancelable If the user can quit the dialog
     * @param Which_jframe Target frame to show to
     */
    public static void protectGUI(int PIN, String Owner, boolean Is_cancelable, JFrame Which_jframe) {
        
        if (System.console() != null) {
            lock.lock();
            try {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InstantiationException ex) {
                    Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                    Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Locker.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {

            }
            UIManager.put("OptionPane.background", new Color(252, 252, 252));
            UIManager.put("control", new Color(244, 244, 244));
            JDialog window = new JDialog(Which_jframe, "JSecurity", true);

            try {

                window.getContentPane().setLayout(null);
                window.setResizable(false);
                if (Which_jframe != null) {
                    window.setLocation((int) Which_jframe.getLocation().getX(), (int) Which_jframe.getLocation().getY());
                }
                
                window.setSize(322, 220);
                if (is_BF_enabled) {
                    if (hhssbefore[0] != -1 && hhssbefore[1] != -1) {
                        LocalTime savedtime = LocalTime.of(hhssbefore[0], hhssbefore[1]);
                        Duration dur = Duration.between(savedtime, currentTime);
                        if (dur.toMinutes() < 4) {
                            window.setModal(false);
                            window.show();
                            showBruteForceTimeout(window, false);
                        }
                    }
                }
                JLabel msgText = new JLabel();
                window.add(msgText);
                msgText.setSize(280, 40);
                msgText.setLocation(25, 15);
                if (Owner == null) {
                    Owner = "Unknown";
                }
                msgText.setText("<html><b>This Java\u2122 application is protected!</b><br><b>Owner: </b>" + Owner + "</html>");
                msgText.setFont(new Font("SansSerif", Font.PLAIN, 13));
                JLabel dialogIcon = new JLabel();
                window.add(dialogIcon);
                window.setAlwaysOnTop(true);
                dialogIcon.setSize(50, 50);
                dialogIcon.setLocation(10, 70);                
                JPasswordField passBox = new JPasswordField();
                passBox.setFont(new Font("SansSerif", Font.PLAIN, 10));
                passBox.setEchoChar('\u25cf');
                window.add(passBox);
                passBox.setSize(290, 28);
                passBox.setLocation(15, 75);
                JButton dialogOK = new JButton();
                window.add(dialogOK);
                dialogOK.setText("OK");
                Font currentFont = dialogOK.getFont();          // Get current font
                Font newFont = currentFont.deriveFont(14f);    // Create new font with size 16 (float)
                dialogOK.setFont(newFont);
                dialogOK.setSize(90, 32);
                dialogOK.setLocation(216, 130);
                JButton dialogCANCEL = new JButton();
                window.add(dialogCANCEL);
                dialogCANCEL.setSize(95, 32);
                dialogCANCEL.setLocation(114, 130);
                dialogCANCEL.setText("Cancel");
                dialogCANCEL.setFont(newFont);
                dialogCANCEL.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        System.exit(0);
                    }
                });
                dialogOK.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!passBox.getText().isEmpty() && (Integer.parseInt(passBox.getText()) != PIN)) {
                            showInvalidPINGUI(window, false);
                            Toolkit.getDefaultToolkit().beep();
                            if (is_BF_enabled) {
                                attempts++;
                                memory.putInt("attempts", attempts);
                                if (attempts == 4) {
                                    memory.putInt("hh", currentTime.getHour());
                                    memory.putInt("mm", currentTime.getMinute());
                                    ProtectBruteForce(window, false);
                                }
                            }
                        } else {
                            if (!passBox.getText().isEmpty()) {
                                window.hide();
                                if (is_BF_enabled) {
                                    attempts = 0;
                                    memory.putInt("attempts", attempts);
                                    memory.putInt("hh", -1);
                                    memory.putInt("mm", -1);
                                }
                            }
                        }
                    }
                });
                passBox.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c)) {
                            e.consume();
                        }
                    }
                });
                passBox.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            if (!passBox.getText().isEmpty() && (Integer.parseInt(passBox.getText()) != PIN)) {
                                showInvalidPINGUI(window, true);
                                Toolkit.getDefaultToolkit().beep();
                                attempts++;
                                memory.putInt("attempts", attempts);
                                if (attempts == 4) {
                                    memory.putInt("hh", currentTime.getHour());
                                    memory.putInt("mm", currentTime.getMinute());
                                    ProtectBruteForce(window, false);
                                }
                            } else {
                                if (!passBox.getText().isEmpty()) {
                                    window.hide();
                                    attempts = 0;
                                    memory.putInt("attempts", attempts);
                                    memory.putInt("hh", -1);
                                    memory.putInt("mm", -1);
                                }

                            }
                        }
                    }
                });
                window.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);
                    }
                });
                window.show();
            } finally {
                lock.unlock();
            }
        } else {
            enableAscii();
            System.out.println("\u001B[33;2mProtection applies only outside the IDE\u001B[0m");
        }
    }
 /***
     * Shows a GUI based protection for your jar
     * @param PIN PIN Code
     * @param Is_cancelable If the user can quit the dialog
     * @param Which_jframe Target frame to show to
     */
    public static void protectGUI(int PIN, boolean Is_cancelable, JFrame Which_jframe) {
        protectGUI(PIN, "None", Is_cancelable, Which_jframe);
    }
 /***
     * Shows a CLI based protection for your jar
     * @param PIN PIN Code
     * @param Owner The owner name who locked the jar, this can be an email or an actual name
     * @param Is_brute_force_protection_enabled This makes sure the jar cant be easily run by brute force. After 4 wrong attempts the jar refuses to unlock for about 5 minutes
     */
    public static void protect(int PIN, String Owner, boolean Is_brute_force_protection_enabled) throws IOException, BackingStoreException {
        is_BF_enabled = Is_brute_force_protection_enabled;
        protect(PIN, Owner);
    }
    /***
     * Shows a GUI based protection for your jar
     * @param PIN PIN Code
     * @param Owner The owner name who locked the jar, this can be an email or an actual name
     * @param Is_brute_force_protection_enabled This makes sure the jar cant be easily run by brute force. After 4 wrong attempts the jar refuses to unlock for about 5 minutes
     *@param Which_jframe Target jframe to show to
     *@param Is_cancelable If the user can quit the dialog
     */
    public static void protectGUI(int PIN, String Owner, boolean Is_cancelable, JFrame Which_jframe, boolean Is_brute_force_protection_enabled) {
        is_BF_enabled = Is_brute_force_protection_enabled;
        protectGUI(PIN, Owner, Is_cancelable, Which_jframe);
    }
}
