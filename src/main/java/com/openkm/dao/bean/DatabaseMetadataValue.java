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

package com.openkm.dao.bean;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "OKM_DB_METADATA_VALUE")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DatabaseMetadataValue implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int MAX_LENGTH = 512;

	@Id
	@Column(name = "DMV_ID")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Index(name = "IDX_DB_MD_VAL_TABLE")
	@Column(name = "DMV_TABLE", length = 32)
	private String table;

	@Index(name = "IDX_DB_MD_VAL_COL00")
	@Column(name = "DMV_COL00", length = MAX_LENGTH)
	private String col00;

	@Index(name = "IDX_DB_MD_VAL_COL01")
	@Column(name = "DMV_COL01", length = MAX_LENGTH)
	private String col01;

	@Index(name = "IDX_DB_MD_VAL_COL02")
	@Column(name = "DMV_COL02", length = MAX_LENGTH)
	private String col02;

	@Index(name = "IDX_DB_MD_VAL_COL03")
	@Column(name = "DMV_COL03", length = MAX_LENGTH)
	private String col03;

	@Index(name = "IDX_DB_MD_VAL_COL04")
	@Column(name = "DMV_COL04", length = MAX_LENGTH)
	private String col04;

	@Index(name = "IDX_DB_MD_VAL_COL05")
	@Column(name = "DMV_COL05", length = MAX_LENGTH)
	private String col05;

	@Index(name = "IDX_DB_MD_VAL_COL06")
	@Column(name = "DMV_COL06", length = MAX_LENGTH)
	private String col06;

	@Index(name = "IDX_DB_MD_VAL_COL07")
	@Column(name = "DMV_COL07", length = MAX_LENGTH)
	private String col07;

	@Index(name = "IDX_DB_MD_VAL_COL08")
	@Column(name = "DMV_COL08", length = MAX_LENGTH)
	private String col08;

	@Index(name = "IDX_DB_MD_VAL_COL09")
	@Column(name = "DMV_COL09", length = MAX_LENGTH)
	private String col09;

	@Index(name = "IDX_DB_MD_VAL_COL10")
	@Column(name = "DMV_COL10", length = MAX_LENGTH)
	private String col10;

	@Index(name = "IDX_DB_MD_VAL_COL11")
	@Column(name = "DMV_COL11", length = MAX_LENGTH)
	private String col11;

	@Index(name = "IDX_DB_MD_VAL_COL12")
	@Column(name = "DMV_COL12", length = MAX_LENGTH)
	private String col12;

	@Index(name = "IDX_DB_MD_VAL_COL13")
	@Column(name = "DMV_COL13", length = MAX_LENGTH)
	private String col13;

	@Index(name = "IDX_DB_MD_VAL_COL14")
	@Column(name = "DMV_COL14", length = MAX_LENGTH)
	private String col14;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getCol00() {
		return col00;
	}

	public void setCol00(String col00) {
		this.col00 = col00;
	}

	public String getCol01() {
		return col01;
	}

	public void setCol01(String col01) {
		this.col01 = col01;
	}

	public String getCol02() {
		return col02;
	}

	public void setCol02(String col02) {
		this.col02 = col02;
	}

	public String getCol03() {
		return col03;
	}

	public void setCol03(String col03) {
		this.col03 = col03;
	}

	public String getCol04() {
		return col04;
	}

	public void setCol04(String col04) {
		this.col04 = col04;
	}

	public String getCol05() {
		return col05;
	}

	public void setCol05(String col05) {
		this.col05 = col05;
	}

	public String getCol06() {
		return col06;
	}

	public void setCol06(String col06) {
		this.col06 = col06;
	}

	public String getCol07() {
		return col07;
	}

	public void setCol07(String col07) {
		this.col07 = col07;
	}

	public String getCol08() {
		return col08;
	}

	public void setCol08(String col08) {
		this.col08 = col08;
	}

	public String getCol09() {
		return col09;
	}

	public void setCol09(String col09) {
		this.col09 = col09;
	}

	public String getCol10() {
		return col10;
	}

	public void setCol10(String col10) {
		this.col10 = col10;
	}

	public String getCol11() {
		return col11;
	}

	public void setCol11(String col11) {
		this.col11 = col11;
	}

	public String getCol12() {
		return col12;
	}

	public void setCol12(String col12) {
		this.col12 = col12;
	}

	public String getCol13() {
		return col13;
	}

	public void setCol13(String col13) {
		this.col13 = col13;
	}

	public String getCol14() {
		return col14;
	}

	public void setCol14(String col14) {
		this.col14 = col14;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id=");
		sb.append(id);
		sb.append(", table=");
		sb.append(table);
		sb.append(", col00=");
		sb.append(col00);
		sb.append(", col01=");
		sb.append(col01);
		sb.append(", col02=");
		sb.append(col02);
		sb.append(", col03=");
		sb.append(col03);
		sb.append(", col04=");
		sb.append(col04);
		sb.append(", col05=");
		sb.append(col05);
		sb.append(", col06=");
		sb.append(col06);
		sb.append(", col07=");
		sb.append(col07);
		sb.append(", col08=");
		sb.append(col08);
		sb.append(", col09=");
		sb.append(col09);
		sb.append(", col10=");
		sb.append(col10);
		sb.append(", col11=");
		sb.append(col11);
		sb.append(", col12=");
		sb.append(col12);
		sb.append(", col13=");
		sb.append(col13);
		sb.append(", col14=");
		sb.append(col14);
		sb.append("}");
		return sb.toString();
	}
}
