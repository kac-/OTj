package com.kactech.otj.android;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import org.jeromq.ZContext;
import org.jeromq.ZMQ;
import org.jeromq.ZMQ.Socket;
import org.jeromq.ZMsg;
import org.junit.Test;

import com.kactech.otj.Engines;
import com.kactech.otj.MSG;
import com.kactech.otj.OT;
import com.kactech.otj.Utils;
import com.kactech.otj.examples.ExamplesUtils;
import com.kactech.otj.model.BasicConnectionInfo;
import com.kactech.otj.model.SigningSupport;

public class DebugInvalidEnvelope {
	//@Test
	public void d0() throws Exception {
		String s;
		s = Utils.read("testData", "android", "invalidEnvelope.b64.txt");
		byte[] by;
		by = Utils.base64Decode(s);
		//		System.arraycopy(by, 7, by, 0, 20);
		System.out.println(new String(by));
		by = Utils.unpack(by, byte[].class);

		KeyPair kp = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		System.out.println(Engines.gson.toJson(kp));
	}

	//@Test
	public void d1() throws Exception {
		Utils.init();
		KeyPair kp = Engines.gson.fromJson(Utils.read("testData", "testKeyPair.json"), KeyPair.class);
		ZContext ctx = new ZContext();
		Socket server = ctx.createSocket(ZMQ.REP);
		server.bind("tcp://192.168.1.111:7089");

		//System.out.printf("I: echo service is ready at %s\n", args[0]);
		while (true) {
			ZMsg msg = ZMsg.recvMsg(server);
			if (msg == null)
				break; // Interrupted
			String str;
			byte[] by = msg.pop().getData();
			by = Utils.base64Decode(by);
			by = Utils.unpack(by, byte[].class);
			str = Utils.open(by, kp.getPrivate());
			//str = Utils.string(by, Utils.UTF8);
			System.out.println(str);
			msg.send(server);
			break;
		}
		if (Thread.currentThread().isInterrupted())
			System.out.printf("W: interrupted\n");

		ctx.destroy();
	}

	//@Test
	public void d2() throws Exception {
		KeyPair kp = Engines.gson.fromJson(Utils.read("testData", "testKeyPair.json"), KeyPair.class);
		BasicConnectionInfo bci = new BasicConnectionInfo("123", kp.getPublic(), "tcp://192.168.1.111:7089");
		System.out.println(Engines.gson.toJson(bci));
		System.out.println(Engines.gson.toJson(ExamplesUtils.getServers()));
	}
	/*
==> Received a createUserAccount message. Nym: xOBROITUzXs4IxqpsTWvSxehX4mVGmk8viUtV5l6h4n ...
LoadFromString: While loading keyCredential, failed trying to find expected Master Credential ID: hP9ajU2BNQqqNM07jeofDQSGxalggq8mVuON8B4D9CS
ProcessUserCommand: @createUserAccount: Failure loading nym xOBROITUzXs4IxqpsTWvSxehX4mVGmk8viUtV5l6h4n from credential string.
Failure loading public credentials for Nym: xOBROITUzXs4IxqpsTWvSxehX4mVGmk8viUtV5l6h4n
ProcessMessage_ZMQ: Unable to process user command: createUserAccount
	 */
	@Test
	public void d3() throws Exception {
		Utils.init();
		String s;
		SigningSupport ss = Engines.parse(Utils.read("testData","android","invalidCreateUserAccount-1.txt"));
		System.out.println(ss);
		MSG.CreateUserAccount cua = ((MSG.Message)ss).getCreateUserAccount();
		OT.User user = cua.getCredentialList().getEntity();
		System.out.println(Engines.gson.toJson(cua));
		s = cua.getCredentialList().getEntity().getNymIDSource().getUnarmored();
		PublicKey publicKey = Utils.fromRawPublicInfo(s, true);
		System.out.println(Utils.toNymID(publicKey));
	}
}
