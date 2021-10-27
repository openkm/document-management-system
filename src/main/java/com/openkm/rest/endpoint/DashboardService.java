package com.openkm.rest.endpoint;

import com.openkm.module.DashboardModule;
import com.openkm.module.ModuleManager;
import com.openkm.rest.GenericException;
import com.openkm.rest.util.DashboardDocumentResultList;
import com.openkm.rest.util.DashboardFolderResultList;
import com.openkm.rest.util.DashboardMailResultList;
import com.openkm.rest.util.QueryParamsList;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
@Api(value = "dashboard-service")
@Path("/dashboard")
public class DashboardService {
	private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

	@GET
	@Path("/getUserCheckedOutDocuments")
	public DashboardDocumentResultList getUserCheckedOutDocuments() throws GenericException {
		try {
			log.debug("getUserCheckedOutDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserCheckedOutDocuments(null));
			log.debug("getUserCheckedOutDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserLastModifiedDocuments")
	public DashboardDocumentResultList getUserLastModifiedDocuments() throws GenericException {
		try {
			log.debug("getUserLastModifiedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserLastModifiedDocuments(null));
			log.debug("getUserLastModifiedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserLockedDocuments")
	public DashboardDocumentResultList getUserLockedDocuments() throws GenericException {
		try {
			log.debug("getUserLockedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserLockedDocuments(null));
			log.debug("getUserLockedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserSubscribedDocuments")
	public DashboardDocumentResultList getUserSubscribedDocuments() throws GenericException {
		try {
			log.debug("getUserSubscribedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserSubscribedDocuments(null));
			log.debug("getUserSubscribedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserSubscribedFolders")
	public DashboardFolderResultList getUserSubscribedFolders() throws GenericException {
		try {
			log.debug("getUserSubscribedFolders()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardFolderResultList dfrl = new DashboardFolderResultList();
			dfrl.getList().addAll(dm.getUserSubscribedFolders(null));
			log.debug("getUserSubscribedFolders: {}", dfrl);
			return dfrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("getUserLastUploadedDocuments")
	public DashboardDocumentResultList getUserLastUploadedDocuments() throws GenericException {
		try {
			log.debug("getUserLastUploadedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserLastUploadedDocuments(null));
			log.debug("getUserLastUploadedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserLastDownloadedDocuments")
	public DashboardDocumentResultList getUserLastDownloadedDocuments() {
		try {
			log.debug("getUserLastDownloadedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserLastDownloadedDocuments(null));
			log.debug("getUserLastDownloadedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserLastImportedMails")
	public DashboardMailResultList getUserLastImportedMails() throws GenericException {
		try {
			log.debug("getUserLastImportedMails()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardMailResultList dmrl = new DashboardMailResultList();
			dmrl.getList().addAll(dm.getUserLastImportedMails(null));
			log.debug("getUserLastImportedMails: {}", dmrl);
			return dmrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserLastImportedMailAttachments")
	public DashboardDocumentResultList getUserLastImportedMailAttachments() {
		try {
			log.debug("getUserLastImportedMailAttachments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getUserLastImportedMailAttachments(null));
			log.debug("getUserLastImportedMailAttachments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getUserSearchs")
	public QueryParamsList getUserSearchs() throws GenericException {
		try {
			log.debug("getUserSearchs()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			QueryParamsList qpl = new QueryParamsList();
			qpl.getList().addAll(dm.getUserSearchs(null));
			log.debug("getUserSearchs: {}", qpl);
			return qpl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("findUserSearches")
	public DashboardDocumentResultList findUserSearches(@QueryParam("qpId") int qpId) throws GenericException {
		try {
			log.debug("find({})", qpId);
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.find(null, qpId));
			log.debug("find: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastWeekTopDownloadedDocuments")
	public DashboardDocumentResultList getLastWeekTopDownloadedDocuments() throws GenericException {
		try {
			log.debug("getLastWeekTopDownloadedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastWeekTopDownloadedDocuments(null));
			log.debug("getLastWeekTopDownloadedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastMonthTopDownloadedDocuments")
	public DashboardDocumentResultList getLastMonthTopDownloadedDocuments() throws GenericException {
		try {
			log.debug("getLastMonthTopDownloadedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastMonthTopDownloadedDocuments(null));
			log.debug("getLastMonthTopDownloadedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastWeekTopModifiedDocuments")
	public DashboardDocumentResultList getLastWeekTopModifiedDocuments() throws GenericException {
		try {
			log.debug("getLastWeekTopModifiedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastWeekTopModifiedDocuments(null));
			log.debug("getLastWeekTopModifiedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastMonthTopModifiedDocuments")
	public DashboardDocumentResultList getLastMonthTopModifiedDocuments() throws GenericException {
		try {
			log.debug("getLastMonthTopModifiedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastMonthTopModifiedDocuments(null));
			log.debug("getLastMonthTopModifiedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastModifiedDocuments")
	public DashboardDocumentResultList getLastModifiedDocuments() throws GenericException {
		try {
			log.debug("getLastModifiedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastModifiedDocuments(null));
			log.debug("getLastModifiedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}

	@GET
	@Path("/getLastUploadedDocuments")
	public DashboardDocumentResultList getLastUploadedDocuments() throws GenericException {
		try {
			log.debug("getLastUploadedDocuments()");
			DashboardModule dm = ModuleManager.getDashboardModule();
			DashboardDocumentResultList ddrl = new DashboardDocumentResultList();
			ddrl.getList().addAll(dm.getLastUploadedDocuments(null));
			log.debug("getLastUploadedDocuments: {}", ddrl);
			return ddrl;
		} catch (Exception e) {
			throw new GenericException(e);
		}
	}
}
