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

package com.openkm.servlet.admin;

import com.openkm.bean.StatsInfo;
import com.openkm.core.Config;
import com.openkm.core.RepositoryInfo;
import com.openkm.util.FormatUtil;
import com.openkm.util.WebUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.ColumnArrangement;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Stats graphical servlet
 */
public class StatsGraphServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(StatsGraphServlet.class);
    public static final String DOCUMENTS = "0";
    public static final String DOCUMENTS_SIZE = "1";
    public static final String FOLDERS = "2";
    public static final String JVM_MEMORY = "3";
    public static final String DISK = "4";
    public static final String OS_MEMORY = "5";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String action = WebUtils.getString(request, "action", "graph");
		String type = WebUtils.getString(request, "t");
		JFreeChart chart = null;
		updateSessionManager(request);

		try {
			if ("refresh".equals(action)) {
				new RepositoryInfo().runAs(null);
				ServletContext sc = getServletContext();
				sc.getRequestDispatcher("/admin/stats.jsp").forward(request, response);
			} else {
				response.setContentType("image/png");
				OutputStream out = response.getOutputStream();

				if (DOCUMENTS.equals(type) || DOCUMENTS_SIZE.equals(type) || FOLDERS.equals(type)) {
					chart = repoStats(type);
				} else if (DISK.equals(type)) {
					chart = diskStats();
				} else if (JVM_MEMORY.equals(type)) {
					chart = jvmMemStats();
				} else if (OS_MEMORY.equals(type)) {
					chart = osMemStats();
				}

				if (chart != null) {
					// Customize title font
					chart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 16));

					// Match body {	background-color:#F6F6EE; }
					chart.setBackgroundPaint(new Color(246, 246, 238));

					// Customize no data
					PiePlot plot = (PiePlot) chart.getPlot();
					plot.setNoDataMessage("No data to display");

					// Customize labels
					plot.setLabelGenerator(null);

					// Customize legend
					LegendTitle legend = new LegendTitle(plot, new ColumnArrangement(), new ColumnArrangement());
					legend.setPosition(RectangleEdge.BOTTOM);
					legend.setFrame(BlockBorder.NONE);
					legend.setItemFont(new Font("Tahoma", Font.PLAIN, 12));
					chart.removeLegend();
					chart.addLegend(legend);

					if (DISK.equals(type) || JVM_MEMORY.equals(type) || OS_MEMORY.equals(type)) {
						ChartUtilities.writeChartAsPNG(out, chart, 225, 225);
					} else {
						ChartUtilities.writeChartAsPNG(out, chart, 250, 250);
					}
				}

				out.flush();
				out.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Generate disk statistics
	 */
	public JFreeChart diskStats() throws IOException, ServletException {
		String repHome = null;

		// Allow absolute repository path
		if ((new File(Config.REPOSITORY_HOME)).isAbsolute()) {
			repHome = Config.REPOSITORY_HOME;
		} else {
			repHome = Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME;
		}

		File df = new File(repHome);
		long total = df.getTotalSpace();
		long usable = df.getUsableSpace();
		long used = total - usable;
		String title = "Disk: " + FormatUtil.formatSize(total);

		log.debug("Total space: {}", FormatUtil.formatSize(total));
		log.debug("Usable space: {}", FormatUtil.formatSize(usable));
		log.debug("Used space: {}", FormatUtil.formatSize(used));

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Available (" + FormatUtil.formatSize(usable) + ")", usable * 100 / total);
		dataset.setValue("Used (" + FormatUtil.formatSize(used) + ")", used * 100 / total);

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Generate memory statistics
	 * http://blog.codebeach.com/2008/02/determine-available-memory-in-java.html
	 */
	public JFreeChart jvmMemStats() throws IOException, ServletException {
		Runtime runtime = Runtime.getRuntime();
		long max = runtime.maxMemory(); // maximum amount of memory that the JVM will attempt to use
		long available = runtime.totalMemory(); // total amount of memory in the JVM
		long free = runtime.freeMemory(); // amount of free memory in the JVM
		long used = max - available;
		long total = free + used;
		String title = "JVM memory: " + FormatUtil.formatSize(total);

		log.debug("JVM maximun memory: {}", FormatUtil.formatSize(max));
		log.debug("JVM available memory: {}", FormatUtil.formatSize(available));
		log.debug("JVM free memory: {}", FormatUtil.formatSize(free));
		log.debug("JVM used memory: {}", FormatUtil.formatSize(used));
		log.debug("JVM total memory: {}", FormatUtil.formatSize(total));

		DefaultPieDataset dataset = new DefaultPieDataset();
		dataset.setValue("Available (" + FormatUtil.formatSize(free) + ")", free * 100 / total);
		dataset.setValue("Used (" + FormatUtil.formatSize(used) + ")", used * 100 / total);

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Generate memory statistics
	 * http://casidiablo.net/capturar-informacion-sistema-operativo-java/
	 */
	public JFreeChart osMemStats() throws IOException, ServletException {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Sigar sigar = new Sigar();
		String title = null;

		try {
			Mem mem = sigar.getMem();
			long max = mem.getRam();
			long available = mem.getFree();
			long total = mem.getTotal();
			long used = mem.getUsed();
			long free = mem.getFree();
			title = "OS memory: " + FormatUtil.formatSize(total);

			log.debug("OS maximun memory: {}", FormatUtil.formatSize(max));
			log.debug("OS available memory: {}", FormatUtil.formatSize(available));
			log.debug("OS free memory: {}", FormatUtil.formatSize(free));
			log.debug("OS used memory: {}", FormatUtil.formatSize(used));
			log.debug("OS total memory: {}", FormatUtil.formatSize(total));

			dataset.setValue("Available (" + FormatUtil.formatSize(free) + ")", free * 100 / total);
			dataset.setValue("Used (" + FormatUtil.formatSize(used) + ")", used * 100 / total);
		} catch (SigarException se) {
			title = "OS memory: " + se.getMessage();
		} catch (UnsatisfiedLinkError ule) {
			title = "OS memory: (missing native libraries)";
		}

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Generate repository statistics
	 */
	public JFreeChart repoStats(String type) throws IOException, ServletException {
		String title = null;
		long[] sizes = null;
		double[] percents = null;
		DefaultPieDataset dataset = new DefaultPieDataset();

		if (DOCUMENTS.equals(type)) {
			StatsInfo si = RepositoryInfo.getDocumentsByContext();
			percents = si.getPercents();
			sizes = si.getSizes();
			title = "Documents by context";
		} else if (DOCUMENTS_SIZE.equals(type)) {
			StatsInfo si = RepositoryInfo.getDocumentsSizeByContext();
			percents = si.getPercents();
			sizes = si.getSizes();
			title = "Documents size by context";
		} else if (FOLDERS.equals(type)) {
			StatsInfo si = RepositoryInfo.getFoldersByContext();
			percents = si.getPercents();
			sizes = si.getSizes();
			title = "Folders by context";
		}

		if (title != null && sizes.length > 0 && percents.length > 0) {
			String taxonomySize = DOCUMENTS_SIZE.equals(type) ? FormatUtil.formatSize(sizes[0]) : Long.toString(sizes[0]);
			String personalSize = DOCUMENTS_SIZE.equals(type) ? FormatUtil.formatSize(sizes[1]) : Long.toString(sizes[1]);
			String templateSize = DOCUMENTS_SIZE.equals(type) ? FormatUtil.formatSize(sizes[2]) : Long.toString(sizes[2]);
			String trashSize = DOCUMENTS_SIZE.equals(type) ? FormatUtil.formatSize(sizes[3]) : Long.toString(sizes[3]);

			dataset.setValue("Taxonomy (" + taxonomySize + ")", percents[0]);
			dataset.setValue("Personal (" + personalSize + ")", percents[1]);
			dataset.setValue("Template (" + templateSize + ")", percents[2]);
			dataset.setValue("Trash (" + trashSize + ")", percents[3]);
		}

		return ChartFactory.createPieChart(title, dataset, true, false, false);
	}

	/**
	 * Convert a piechartdata to xml
	 *
	 * @author puspendu.banerjee@gmail.com 
	 */
	public String repoStatsXML(final String title, final DefaultPieDataset dataset) throws
			IOException, ServletException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("RepoStats");
		root.addElement("Title").addCDATA(title);
		Element dataSetElement = root.addElement("DataSet");

		for (int i = 0; i < dataset.getItemCount(); i++) {
			Element itemElement = dataSetElement.addElement("Item");
			itemElement.addElement("name").addCDATA(dataset.getKey(i).toString());
			itemElement.addAttribute("percent", dataset.getValue(i).toString());
			dataSetElement.add(itemElement);
		}

		return document.asXML();
	}
}
