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
package com.kactech.otj.script;

import javax.script.Bindings;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import com.kactech.otj.Client;

@SuppressWarnings("rawtypes")
public class ScriptFilter implements Client.Filter {
	CompiledScript compiled;
	Integer priority;
	int mask;
	Class type;

	public ScriptFilter(CompiledScript compiled) throws ScriptException, ClassNotFoundException {
		this.compiled = compiled;
		Bindings bind = compiled.getEngine().createBindings();
		compiled.eval(bind);
		Double v = (Double) bind.get("priority");
		if (v != null)
			this.priority = v.intValue();
		v = (Double) bind.get("mask");
		this.mask = v == null ? Client.EVENT_STD : v.intValue();
		String t = (String) bind.get("type");
		t = t.replace("com.kactech.otj.MSG.", "com.kactech.otj.MSG$");
		this.type = Class.forName(t);
		bind.clear();
	}

	@Override
	public Object filter(Object obj, Client client) {
		Bindings bind = compiled.getEngine().createBindings();
		bind.put("toFilter", obj);
		bind.put("client", client);
		try {
			compiled.eval(bind);
		} catch (ScriptException e) {
			throw new RuntimeException(e);
		}
		obj = bind.get("filtered");
		bind.clear();
		return obj;
	}

	@Override
	public int getMask() {
		return mask;
	}

	public Integer getPriority() {
		return priority;
	}

	public Class getType() {
		return type;
	}
}