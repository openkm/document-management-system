package com.openkm.rest.util;

import com.openkm.bean.DashboardMailResult;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "dashboardMailResults")
public class DashboardMailResultList {

	@XmlElement(name = "dashboardMailResult", required = true)
	List<DashboardMailResult> dashboardMailResults = new ArrayList<>();

	public List<DashboardMailResult> getList() {
		return dashboardMailResults;
	}
}
