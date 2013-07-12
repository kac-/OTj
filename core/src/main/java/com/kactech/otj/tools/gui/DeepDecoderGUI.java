/*******************************************************************************
 *              OTj
 * Low-level client-side library for Open Transactions in Java
 * 
 * Copyright (C) 2013 by Piotr Kopeć (kactech)
 * 
 * EMAIL: pepe.kopec@gmail.com
 * 
 * BITCOIN: 1ESADvST7ubsFce7aEi2B6c6E2tYd4mHQp
 * 
 * OFFICIAL PROJECT PAGE: https://github.com/kactech/OTj
 * 
 * -------------------------------------------------------
 * 
 * LICENSE:
 * This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero
 * General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your
 * option) any later version.
 * 
 * ADDITIONAL PERMISSION under the GNU Affero GPL version 3
 * section 7: If you modify this Program, or
 * any covered work, by linking or combining it with other
 * code, such other code is not for that reason alone subject
 * to any of the requirements of the GNU Affero GPL version 3.
 * (==> This means if you are only using the OTj, then you
 * don't have to open-source your code--only your changes to
 * OTj itself must be open source. Similar to
 * LGPLv3, except it applies to software-as-a-service, not
 * just to distributing binaries.)
 * Anyone using my library is given additional permission
 * to link their software with any BSD-licensed code.
 * 
 * -----------------------------------------------------
 * 
 * You should have received a copy of the GNU Affero General
 * Public License along with this program. If not, see:
 * http://www.gnu.org/licenses/
 * 
 * If you would like to use this software outside of the free
 * software license, please contact Piotr Kopeć.
 * 
 * DISCLAIMER:
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Affero General Public License for
 * more details.
 ******************************************************************************/
package com.kactech.otj.tools.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.kactech.otj.tools.DeepDecoder;

/**
 * GUI for {@link com.kactech.otj.tools.DeepDecoder}
 * 
 * @author Piotr Kopeć (kactech)
 * 
 */
@SuppressWarnings("serial")
public class DeepDecoderGUI extends JPanel implements ActionListener {
	JTextArea text = new JTextArea();
	JButton decode = new JButton("decode");

	public DeepDecoderGUI() {
		super(new BorderLayout());
		decode.addActionListener(this);
		text.setEditable(true);
		add(decode, BorderLayout.NORTH);
		add(new JScrollPane(text), BorderLayout.CENTER);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String str = text.getText();

		try {
			File file = File.createTempFile("ddgui", ".xml");
			file.deleteOnExit();
			DeepDecoder dd = new DeepDecoder(new PrintWriter(file), "   ");
			dd.process(str);
			dd.flush();
			dd.close();
			Desktop.getDesktop().browse(file.toURI());
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	static void createAndShowGUI() {
		JFrame f = new JFrame();
		DeepDecoderGUI dd = new DeepDecoderGUI();
		f.getContentPane().add(dd);
		f.setSize(800, 600);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
