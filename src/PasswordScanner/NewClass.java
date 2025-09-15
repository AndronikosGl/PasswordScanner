/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PasswordScanner;
import PasswordScanner.Locker;
import static PasswordScanner.Locker.protectGUI;
import java.io.IOException;
import java.util.prefs.BackingStoreException;
public class NewClass {
    public static void main(String[] args) throws IOException, BackingStoreException {
        protectGUI(870, "author@gmail.com", true, null, true);
        System.out.println("HELLO WORLD!");
    }
}
