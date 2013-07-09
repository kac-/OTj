package com.kactech.otj;

import java.io.File;

import org.junit.Test;

import com.kactech.otj.examples.ExamplesUtils;

public class EnvelopeTest {
	@Test
	public void t0() throws Exception {
		Utils.init();
		File dir = new File("a_client");
		System.out.println("client dir: " + dir);
		EClient client = new EClient(dir, ExamplesUtils.findServer("OT"));
		client.setAssetType(ExamplesUtils.findAsset("silver").assetID);
		client.init();
	}
}
