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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;

import com.kactech.otj.EClient;
import com.kactech.otj.MSG;
import com.kactech.otj.OT;
import com.kactech.otj.Utils;

public class Faucet {

	public static class IrcListener extends ListenerAdapter implements Closeable {
		static final DateFormat DF = new SimpleDateFormat("hh:mm:ssa dd-MM-yyyy zzz");
		static {
			DF.setTimeZone(TimeZone.getTimeZone("UTC"));
		}
		PircBotX bot;
		EClient client;
		boolean joined;
		Timer timer = new Timer();
		UserMessagesFilter messageFilter = new UserMessagesFilter();

		public IrcListener(PircBotX bot, EClient client) {
			super();
			this.bot = bot;
			this.client = client;
			this.client.getClient().addFilter(messageFilter, MSG.GetNymboxResp.class, 0);
		}

		@Override
		public void onMessage(MessageEvent event) throws Exception {
			String msg = event.getMessage();
			if (msg.startsWith(bot.getName() + ':') || msg.startsWith("faucet!")) {
				String prefix = event.getUser().getNick() + ": ";
				OT.Account acc = client.getCachedAccount();
				bot.sendMessage(
						event.getChannel(),
						prefix + "SERVER: "
								+ client.getClient().getServerID() + " "
								+ ExamplesUtils.getContractURI(client.getClient().getServerID()));
				bot.sendMessage(
						event.getChannel(),
						prefix + "ASSET: "
								+ acc.getAssetTypeID() + " "
								+ ExamplesUtils.getContractURI(acc.getAssetTypeID()));
				bot.sendMessage(event.getChannel(), prefix + "BALANCE: "
						+ acc.getBalance().getAmount()
						+ " AT " + DF.format(new Date(acc.getBalance().getDate() * 1000)));
				bot.sendMessage(event.getChannel(), prefix + "mail your ACCOUNT_ID to NYM "
						+ client.getClient().getUserAccount().getNymID() + " for free asset units "
						+ "http://qrfree.kaywa.com/?l=1&s=8&d=" + client.getClient().getUserAccount().getNymID());
				bot.sendMessage(event.getChannel(), prefix + "OTj project: https://github.com/kactech/OTj");
				bot.sendMessage(event.getChannel(), prefix + "tip OTj: "
						+ "http://bit.ly/15r8qkI THANKS!");
			}
		}

		@Override
		public void onJoin(JoinEvent event) throws Exception {
			if (joined)
				return;
			System.out.println("JOIN " + event);
			joined = true;
			final Channel chan = event.getChannel();
			synchronized (client) {
				client.getAccount();
			}
			timer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					synchronized (client) {
						try {
							client.processInbox();
							for (UserMessagesFilter.UserMessage msg : messageFilter.getAndClearMessages()) {
								String s = msg.text;
								s = s.replace("Subject:", "").trim();
								if (!mayBeValid(s)) {
									bot.sendMessage(chan, "mail from " + substr(msg.from, 5)
											+ " ERROR: invalid ACCOUNT_ID: " + substr(s, 5));
								} else {
									long amount = 100;
									if (client.getCachedAccount().getBalance().getAmount() < amount) {
										bot.sendMessage(chan, "insufficient funds for trasfrer " + amount
												+ " to account " + substr(s, 5));
									} else {
										boolean sent = client.notarizeTransaction(s, amount);
										if (sent)
											bot.sendMessage(chan, "sent " + amount + " to account " + substr(s, 5));
										else
											bot.sendMessage(chan, "not sent to account '" + substr(s, 5)
													+ "' due to error");
									}
								}
								client.getAccount();
							}
							//client.notarizeTransaction(100, amount);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}, 1000 * 10, 1000 * 60);
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					// synchronization not needed
					try {
						showBalance(chan, client.getCachedAccount());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}, 1000 * 60, 1000 * 60 * 60);
		}

		void showBalance(Channel chan, OT.Account acc) {
			bot.sendMessage(chan, "BALANCE: "
					+ acc.getBalance().getAmount()
					+ " AT " + DF.format(new Date(acc.getBalance().getDate() * 1000)));
		}

		String substr(String s, int len) {
			if (s.length() < len)
				return s;
			return s.substring(0, len) + "...";
		}

		@Override
		public void close() throws IOException {
			timer.cancel();
		}
	}

	static EClient client;

	static Options options = new Options();
	static
	{
		options.addOption(OptionBuilder.hasArg().withArgName("#CHANNEL").withLongOpt("channel")
				.withDescription("<#CHANNEL> = irc channel, ['#opentransactions']").create('c'));
		options.addOption("h", "help", false, "print this help");
	}

	static void help() {
		HelpFormatter hf = new HelpFormatter();
		hf.printHelp("otj-faucet [-h | (<options>)]", options);
	}

	public static void main(String[] args) throws Exception {
		String channel =
				"#opentransactions"
				//"#kactech_test"
		//
		;
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
		if (cmd.hasOption('c'))
			channel = cmd.getOptionValue('c');
		Utils.init();
		client = new EClient(new File("faucet_client"),
				ExamplesUtils.findServer("OT 8coin")
				//ExamplesUtils.findServer("van")
				);
		client.setAssetType(ExamplesUtils.findAsset("silver").assetID);

		client.init();
		PircBotX bot = new PircBotX();

		bot.setName("OTjFaucet");
		bot.setLogin("fast");
		bot.setVerbose(true);
		bot.setAutoNickChange(true);
		bot.setCapEnabled(true);

		final IrcListener listener = new IrcListener(bot, client);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

			@Override
			public void run() {
				if (client == null)
					return;
				client.saveState();
				try {
					client.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					client = null;
				}
				try {
					listener.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}));
		bot.getListenerManager().addListener(listener);
		try {
			bot.connect("irc.freenode.net");
			bot.joinChannel(channel);
			if (false)
				while (true) {
					Thread.sleep(1000);
				}
			//bot.quitServer("end of test");

			while (bot.isConnected())
				Thread.sleep(100);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			System.out.println("finally");
			bot.disconnect();
			client.saveState();
			client.close();
			client = null;
		}
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
