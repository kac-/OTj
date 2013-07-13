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
package com.kactech.otj.examples.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kactech.otj.Client;
import com.kactech.otj.EClient;
import com.kactech.otj.MSG;
import com.kactech.otj.MSG.GetNymboxResp;
import com.kactech.otj.OT;
import com.kactech.otj.Utils;
import com.kactech.otj.examples.App_otj;
import com.kactech.otj.examples.ExamplesUtils;
import com.kactech.otj.examples.UserMessagesFilter;
import com.kactech.otj.log4j.MemoryAppender;

@SuppressWarnings("serial")
public class OTjAlpha extends JPanel implements ActionListener {
	static final Logger logger = LoggerFactory.getLogger(OTjAlpha.class);
	EClient client;
	ThreeField server;
	ThreeField asset;
	JTextField balance = new JTextField(4);
	JButton reload = new JButton("reload");
	CopyField nymID = new CopyField("nymID", true, 4);
	CopyField accountID = new CopyField("accountID", true, 4);
	CopyField sendTO = new CopyField("send to", false, 4);
	JTextField amount = new JTextField(4);
	JButton send = new JButton("send");
	JButton reloadNym = new JButton("reload Nym");
	JButton donate = new URLButton("donate OTj", "https://blockchain.info/address/1ESADvST7ubsFce7aEi2B6c6E2tYd4mHQp");
	URLButton sources = new URLButton("sources", "https://github.com/kactech/OTj");
	JButton message = new JButton("message");
	SendMessageDialog messageDialog = new SendMessageDialog(null);
	UserMessagesFilter messageFilter = new UserMessagesFilter();

	public OTjAlpha(EClient client) {
		this.client = client;

		Box box = Box.createVerticalBox();
		box.setAlignmentX(Component.LEFT_ALIGNMENT);

		Box h;
		JLabel l;

		server = new ThreeField(4, "server", new URLButton(client.getConnInfo().getName(),
				ExamplesUtils.getContractURI(client.getConnInfo().getID())));
		server.setText(client.getConnInfo().getID());
		box.add(server);

		asset = new ThreeField(4, "asset", new URLButton(ExamplesUtils.findAsset(client.getAssetType()).assetName,
				ExamplesUtils.getContractURI(client.getAssetType())));
		asset.setText(client.getAssetType());
		box.add(asset);

		h = Box.createHorizontalBox();
		h.setAlignmentX(LEFT_ALIGNMENT);
		l = new JLabel("balance");
		l.setPreferredSize(new Dimension(100, 20));
		h.add(l);
		balance.setEditable(false);
		h.add(balance);
		h.add(reload);
		h.setMaximumSize(h.getPreferredSize());
		box.add(h);

		box.add(nymID);
		box.add(accountID);
		box.add(sendTO);

		h = Box.createHorizontalBox();
		h.setAlignmentX(LEFT_ALIGNMENT);
		l = new JLabel("amount");
		l.setPreferredSize(new Dimension(100, 20));
		h.add(l);
		h.add(amount);
		h.add(send);
		h.setMaximumSize(h.getPreferredSize());
		box.add(h);

		h = Box.createHorizontalBox();
		h.setAlignmentX(LEFT_ALIGNMENT);
		h.add(message);
		h.add(reloadNym);
		box.add(h);

		h = Box.createHorizontalBox();
		h.setAlignmentX(LEFT_ALIGNMENT);
		h.add(donate);
		h.add(sources);
		box.add(h);

		final JTextArea logArea = new JTextArea(MemoryAppender.getDoc());
		JScrollPane pane = new JScrollPane(logArea);
		pane.setAlignmentX(LEFT_ALIGNMENT);
		pane.setPreferredSize(new Dimension(400, 300));
		pane.setAutoscrolls(true);
		box.add(pane);
		{// scroll to the last line
			MemoryAppender.getDoc().addDocumentListener(new DocumentListener() {

				@Override
				public void removeUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub

				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					logArea.setCaretPosition(e.getDocument().getLength());
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					// TODO Auto-generated method stub

				}
			});
		}

		add(box);

		reload.addActionListener(this);
		send.addActionListener(this);
		reloadNym.addActionListener(this);
		message.addActionListener(this);
		messageDialog.getSend().addActionListener(this);
		setButtonsEnabled(false);

		messageDialog.setModal(false);
		messageDialog.setUndecorated(false);
		messageDialog.setResizable(true);
		messageDialog.pack();
		messageDialog.setAlwaysOnTop(true);
		messageDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

	}

	public void init() {

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					client.init();
					client.getClient().addFilter(messageFilter, GetNymboxResp.class, 0);
					Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								logger.info("shutdown");
								client.saveState();
								client.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}));
					process(reload);
					//process(reloadNym);
				} catch (Exception ex) {
					logger.error("initial reload", ex);
				} finally {
					setButtonsEnabled(true);
				}
			}
		}).start();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		setButtonsEnabled(false);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					process(e.getSource());
				} catch (Exception ex) {
					logger.error("damn", ex);
				} finally {
					setButtonsEnabled(true);
				}
			}
		}).start();
	}

	void process(Object src) throws Exception {

		if (src == reload) {
			logger.info("reload");
			client.processInbox();
			OT.Account acc;
			acc = client.getAccount();
			balance.setText(acc.getBalance().getAmount().toString());
			nymID.setText(client.getClient().getUserAccount().getNymID());
			accountID.setText(acc.getAccountID());
			for (UserMessagesFilter.UserMessage msg : messageFilter.getAndClearMessages())
				logger.info("*****\nmail from {}\n{}\n*****", msg.from, msg.text);
		} else if (src == send) {
			logger.info("send");
			Long amount = Long.parseLong(this.amount.getText().trim());
			String str = sendTO.getText().trim();
			byte[] bytes = null;
			if (!str.isEmpty())
				try {
					bytes = Utils.base62Decode(str).toByteArray();
				} catch (Exception e) {
				}
			if (bytes == null)
				logger.error("incorrect recipient accountID");
			else {
				client.notarizeTransaction(str, amount);
			}
		} else if (src == reloadNym) {
			logger.info("reload Nym");
			client.reloadState();
		} else if (src == message) {
			messageDialog.setLocation(getX() + getWidth(), getY());
			messageDialog.setVisible(true);
		} else if (src == messageDialog.getSend()) {
			String nymID = messageDialog.getSendTo().trim();
			if (!App_otj.mayBeValid(nymID)) {
				String msg = "invalid recipient nymID";
				logger.error(msg);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			String txt = messageDialog.buildText();
			if (txt.isEmpty()) {
				String msg = "empty message";
				logger.error(msg);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			MSG.CheckUserResp cu = client.getClient().checkUser(nymID);
			if (!cu.getSuccess()) {
				String msg = "recipient not found";
				logger.error(msg);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
			PublicKey recipientPublicKey = null;
			if (cu.getHasCredentials()) {
				OT.User credentialList = cu.getCredentialList().getEntity();
				//String nymIDSource = credentialList.getNymIDSource().getRaw();
				//recipientPublicKey = (RSAPublicKey) Utils.fromIDSource(nymIDSource);
				String keyID = credentialList.getKeyCredential().getID();
				OT.KeyCredential cred = (OT.KeyCredential) cu.getCredentials().get(keyID);

				//System.out.println(Engines.gson.toJson(cred));
				List<OT.KeyValue> list = cred.getMasterSigned().getPublicContents().getPublicInfos();
				for (OT.KeyValue kv : list) {
					if (kv.getKey().equals("E"))
						recipientPublicKey = Utils.fromRawPublicInfo(kv.getValue(), false);
				}
				if (recipientPublicKey == null) {
					String msg = "recipient encryption key not found";
					logger.error(msg);
					JOptionPane.showMessageDialog(this, msg);
					return;
				}
			} else {
				String str = Utils.unarmor(cu.getNymPublicKey(), true);
				byte[] packed = Utils.base64Decode(str);
				str = Utils.unpack(packed, String.class);
				recipientPublicKey = Utils.pemReadRSAPublicKey(str);
			}
			MSG.SendUserMessageResp resp = client.getClient().sendUserMessage(txt,
					nymID, recipientPublicKey);
			if (resp.getSuccess()) {
				logger.info("message sent");
				messageDialog.setVisible(false);
			}
			else {
				String msg = "message not sent";
				logger.error(msg);
				JOptionPane.showMessageDialog(this, msg);
				return;
			}
		}
	}

	void setButtonsEnabled(boolean enabled) {
		reload.setEnabled(enabled);
		send.setEnabled(enabled);
		reloadNym.setEnabled(enabled);
		message.setEnabled(enabled);
		messageDialog.getSend().setEnabled(enabled);
	}

	static void createAndShow() {
		Utils.init();
		Client.DEBUG_JSON = true;
		OTjAlpha alpha = new OTjAlpha(buildClient());
		JFrame f = new JFrame();
		f.setTitle("OTjAlpha - Copyright (C) 2013 by Piotr Kopeć (kactech)");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(alpha);
		f.pack();

		f.setVisible(true);
		f.pack();

		alpha.init();
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				createAndShow();
			}
		});
	}

	public static EClient buildClient() {
		EClient client = new EClient(new File("alpha_client"), ExamplesUtils.findServer("OT 8coin"));
		client.setAssetType(ExamplesUtils.findAsset("silver").assetID);//silver grams
		return client;
	}
}
