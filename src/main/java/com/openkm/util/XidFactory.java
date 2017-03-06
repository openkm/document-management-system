/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017  Paco Avila & Josep Llort
 * <p>
 * No bytes were intentionally harmed during the development of this application.
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.util;

import javax.transaction.xa.Xid;
import java.nio.ByteBuffer;

public class XidFactory {
	private static final int FORMAT_ID = 0x4f4b4d; // OKM
	private static int count = 0;

	private XidFactory() {
	}

	public static synchronized Xid createXid() {
		return new Xid() {
			public int getFormatId() {
				return FORMAT_ID;
			}

			public byte[] getGlobalTransactionId() {
				byte[] gti = new byte[64];
				ByteBuffer bb = ByteBuffer.wrap(gti);
				bb.putLong(Thread.currentThread().getId());
				bb.putLong(System.currentTimeMillis());
				bb.putLong(count++);
				return gti;
			}

			public byte[] getBranchQualifier() {
				byte[] bq = new byte[64];
				ByteBuffer bb = ByteBuffer.wrap(bq);
				bb.putLong(Thread.currentThread().getId());
				bb.putLong(System.currentTimeMillis());
				bb.putInt(count++);
				return bq;
			}
		};
	}
}
