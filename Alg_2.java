/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.alg_2;

import javax.swing.SwingUtilities;

public class Alg_2 {

    public static void main(String[] args) {
        // Launch the Library System Swing UI
        SwingUtilities.invokeLater(() -> {
            LibraryUI ui = new LibraryUI();
            ui.setVisible(true);
        });
    }
}
