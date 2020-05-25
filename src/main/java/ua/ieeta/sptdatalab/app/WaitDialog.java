/* This file is part of SPT Data Lab.
*
* Copyright (C) 2020, University of Aveiro,
* DETI - Departament of Electronic, Telecommunications and Informatics.
*
* SPT Data Lab is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* SPT Data Lab is distributed "AS IS" in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with SPT Data Lab; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ua.ieeta.sptdatalab.app;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class WaitDialog {

    private JDialog modalDialog;

    public void openWaitingDialog(Component comp, String message) {

        modalDialog = new JDialog();
        
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        modalDialog.setContentPane(optionPane);
        
        modalDialog.setTitle("Please wait....");

        modalDialog.setLocationRelativeTo(comp);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        modalDialog.setLocation(dim.width / 2 - modalDialog.getSize().width / 2, dim.height / 2 - modalDialog.getSize().height / 2);
        modalDialog.setSize(new Dimension(400, 300));

        modalDialog.setModalityType(Dialog.ModalityType.MODELESS);

        modalDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        modalDialog.pack();
        modalDialog.setVisible(true);
        modalDialog.repaint();

    }

    public void closeWaitingDialog() {

        if (modalDialog != null) {
            modalDialog.dispose();
        }
    }

}
