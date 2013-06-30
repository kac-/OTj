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
package com.kactech.otj.examples;

import java.io.File;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.kactech.otj.Client;
import com.kactech.otj.EClient;
import com.kactech.otj.Utils;
import com.kactech.otj.model.ConnectionInfo;

@SuppressWarnings("static-access")
public class App_otj {
	static final String DEF_CLIENT_DIR = "client";
	static final String DEF_SERVER_NAME = "Transactions.com";
	static Options options = new Options();
	static
	{
		//options.addOption("t", false, "display current time");
		options.addOption(OptionBuilder.hasArg().withArgName("ARGS").withLongOpt("args")
				.withDescription("<ARGS> = \"args for command\"").create('a'));
		options.addOption(OptionBuilder.hasArg().withArgName("ID").withLongOpt("hisacct")
				.withDescription("<ID> = his_account_id_or_name").create('t'));
		options.addOption(OptionBuilder.hasArg().withArgName("ID").withLongOpt("mypurse")
				.withDescription("<ID> = asset_type_id_or_name").create('p'));
		options.addOption(OptionBuilder.hasArg().withArgName("DIR").withLongOpt("dir")
				.withDescription("<DIR> = client_state_dir [./" + DEF_CLIENT_DIR + "]").create('d'));
		options.addOption(OptionBuilder.hasArg().withArgName("ID").withLongOpt("server")
				.withDescription("<ID> = server_id_or_name [" + DEF_SERVER_NAME + "]").create('s'));
		options.addOption("h", "help", false, "print this help");
		options.addOption("x", "clean", false, "delete 'client' dir");
		options.addOption("n", "new", false, "create new asset account");
	}

	static void help() {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("otj [-h | (<options>) <command> (--args <args>)]", options);
	}

	public static void main(String[] args) throws Exception {
		String command = null;
		String hisacct = null;
		String hisacctName = null;
		String hisacctAsset = null;
		String asset = null;
		String assetName = null;
		List<String> argList = null;
		boolean newAccount = false;
		File dir = null;
		ConnectionInfo connection = null;

		CommandLineParser parser = new GnuParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (Exception e) {
			System.err.println("Command-line parsing error: " + e.getMessage());
			help();
			System.exit(-1);
		}
		if (cmd.hasOption('h')) {
			help();
			System.exit(0);
		}
		@SuppressWarnings("unchecked")
		List<String> list = cmd.getArgList();
		if (list.size() > 1) {
			System.err.println("only one command is supported, you've typed " + list);
			help();
			System.exit(-1);
		}
		if (list.size() > 0)
			command = list.get(0).trim();

		List<SampleAccount> accounts = ExamplesUtils.getSampleAccounts();

		if (cmd.hasOption('s')) {
			String v = cmd.getOptionValue('s').trim();
			connection = ExamplesUtils.findServer(v);
			if (connection == null) {
				System.err.println("unknown server: " + v);
				System.exit(-1);
			}
		} else {
			connection = ExamplesUtils.findServer(DEF_SERVER_NAME);
			if (connection == null) {
				System.err.println("default server not found server: " + DEF_SERVER_NAME);
				System.exit(-1);
			}
		}

		if (cmd.hasOption('t')) {
			String v = cmd.getOptionValue('t');
			for (SampleAccount ac : accounts)
				if (ac.accountName.startsWith(v)) {
					hisacct = ac.accountID;
					hisacctName = ac.accountName;
					hisacctAsset = ac.assetID;
					break;
				}
			if (hisacct == null)
				if (mayBeValid(v))
					hisacct = v;
				else {
					System.err.println("invalid hisacct: " + v);
					System.exit(-1);
				}
		}
		if (cmd.hasOption('p')) {
			String v = cmd.getOptionValue('p');
			for (SampleAccount ac : accounts)
				if (ac.assetName.startsWith(v)) {
					asset = ac.assetID;
					assetName = ac.assetName;
					break;
				}
			if (asset == null)
				if (mayBeValid(v))
					asset = v;
				else {
					System.err.println("invalid asset: " + v);
					System.exit(-1);
				}
		}

		if (cmd.hasOption('a')) {
			String v = cmd.getOptionValue('a');
			argList = new ArrayList<String>();
			boolean q = false;
			StringBuilder b = new StringBuilder();
			for (int i = 0; i < v.length(); i++) {
				char c = v.charAt(i);
				if (c == '"') {
					if (q) {
						argList.add(b.toString());
						b = null;
						q = false;
						continue;
					}
					if (b != null)
						argList.add(b.toString());
					b = new StringBuilder();
					q = true;
					continue;
				}
				if (c == ' ' || c == '\t') {
					if (q) {
						b.append(c);
						continue;
					}
					if (b != null)
						argList.add(b.toString());
					b = null;
					continue;
				}
				if (b == null)
					b = new StringBuilder();
				b.append(c);
			}
			if (b != null)
				argList.add(b.toString());
			if (q) {
				System.err.println("unclosed quote in args: " + v);
				System.exit(-1);
			}
		}

		dir = new File(cmd.hasOption('d') ? cmd.getOptionValue('d') : DEF_CLIENT_DIR);

		if (cmd.hasOption('x'))
			del(dir);

		newAccount = cmd.hasOption('n');
		System.out.println("server: " + connection.getEndpoint() + " " + connection.getID());
		System.out.println("command: '" + command + "'");
		System.out.println("args: " + argList);
		System.out.println("hisacct: " + hisacct);
		System.out.println("hisacctName: " + hisacctName);
		System.out.println("hisacctAsset: " + hisacctAsset);
		System.out.println("asset: " + asset);
		System.out.println("assetName: " + assetName);

		if (asset != null && hisacctAsset != null && !asset.equals(hisacctAsset)) {
			System.err.println("asset differs from hisacctAsset");
			System.exit(-1);
		}

		EClient client = new EClient(dir, connection);
		client.setAssetType(asset != null ? asset : hisacctAsset);
		client.setCreateNewAccount(newAccount);

		try {
			Security.addProvider(new BouncyCastleProvider());
			Client.DEBUG_JSON = true;
			client.init();

			if ("balance".equals(command))
				System.out.println("Balance: " + client.getAccount().getBalance().getAmount());
			else if ("acceptall".equals(command))
				client.processInbox();
			else if ("transfer".equals(command)) {
				if (hisacct == null)
					System.err.println("please specify --hisacct");
				else {
					int idx = argList != null ? argList.indexOf("amount") : -1;
					if (idx < 0)
						System.err.println("please specify amount");
					else if (idx == argList.size())
						System.err.println("amount argument needs value");
					else {
						Long amount = -1l;
						try {
							amount = new Long(argList.get(idx + 1));
						} catch (Exception e) {

						}
						if (amount <= 0)
							System.err.println("invalid amount");
						else {
							client.notarizeTransaction(hisacct, amount);
						}
					}
				}
			} else if ("reload".equals(command))
				client.reloadState();
			else if ("procnym".equals(command))
				client.processNymbox();
		} finally {
			client.saveState();
			client.close();
		}
	}

	static void del(File file) {
		if (file.isDirectory())
			for (File f : file.listFiles())
				del(f);
		file.delete();
	}

	public static boolean mayBeValid(String id) {
		try {
			if (id.length() < 20)
				throw new Exception();
			Utils.base62Decode(id);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
