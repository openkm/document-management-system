/**
 * OpenKM, Open Document Management System (http://www.openkm.com)
 * Copyright (c) 2006-2017 Paco Avila & Josep Llort
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.cmis;

import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.IncludeRelationships;
import org.apache.chemistry.opencmis.commons.enums.UnfileObject;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNotSupportedException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ObjectListImpl;
import org.apache.chemistry.opencmis.commons.impl.server.AbstractCmisService;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * CMIS Service Implementation.
 */
public class CmisServiceImpl extends AbstractCmisService {
	private static Logger log = LoggerFactory.getLogger(CmisServiceImpl.class);
	private CmisRepository repository;
	private CallContext context;

	public CmisServiceImpl(CmisRepository repository) {
		this.repository = repository;
	}

	/**
	 * Sets the call context.
	 *
	 * This method should only be called by the service factory.
	 */
	public void setCallContext(CallContext context) {
		this.context = context;
	}

	/**
	 * Gets the call context.
	 */
	public CallContext getCallContext() {
		return context;
	}

	/**
	 * Get repository implementation.
	 */
	public CmisRepository getRepository() {
		return repository;
	}

	// --- Repository Service ---

	@Override
	public RepositoryInfo getRepositoryInfo(String repositoryId, ExtensionsData extension) {
		return getRepository().getRepositoryInfo(getCallContext());
	}

	@Override
	public List<RepositoryInfo> getRepositoryInfos(ExtensionsData extension) {
		log.debug("getRepositoryInfos({})", extension);

		try {
			List<RepositoryInfo> infos = new ArrayList<RepositoryInfo>();
			infos.add(getRepository().getRepositoryInfo(getCallContext()));
			return infos;
		} catch (Exception e) {
			throw new CmisRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public TypeDefinitionList getTypeChildren(String repositoryId, String typeId, Boolean includePropertyDefinitions, BigInteger maxItems,
	                                          BigInteger skipCount, ExtensionsData extension) {
		log.debug("getTypeChildren({}, {}, {}, {}, {}, {})", new Object[]{repositoryId, typeId, includePropertyDefinitions, maxItems,
				skipCount, extension});
		return getRepository().getTypesChildren(getCallContext(), typeId, includePropertyDefinitions, maxItems, skipCount);
	}

	@Override
	public TypeDefinition getTypeDefinition(String repositoryId, String typeId, ExtensionsData extension) {
		log.debug("getTypeDefinition({}, {}, {})", new Object[]{repositoryId, typeId, extension});
		return getRepository().getTypeDefinition(getCallContext(), typeId);
	}

	@Override
	public List<TypeDefinitionContainer> getTypeDescendants(String repositoryId, String typeId, BigInteger depth,
	                                                        Boolean includePropertyDefinitions, ExtensionsData extension) {
		log.debug("getTypeDescendants({}, {}, {})", new Object[]{repositoryId, typeId, depth});
		return getRepository().getTypesDescendants(getCallContext(), typeId, depth, includePropertyDefinitions);
	}

	// --- Navigation Service ---

	@Override
	public ObjectInFolderList getChildren(String repositoryId, String folderId, String filter, String orderBy,
	                                      Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment,
	                                      BigInteger maxItems, BigInteger skipCount, ExtensionsData extension) {
		log.debug("getChildren({}, {}, {}, {})", new Object[]{repositoryId, folderId, filter, orderBy});
		return getRepository().getChildren(getCallContext(), folderId, filter, includeAllowableActions, includePathSegment, maxItems,
				skipCount, this);
	}

	@Override
	public List<ObjectInFolderContainer> getDescendants(String repositoryId, String folderId, BigInteger depth, String filter,
	                                                    Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment,
	                                                    ExtensionsData extension) {
		log.debug("getDescendants({}, {}, {})", new Object[]{repositoryId, folderId, depth});
		return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions, includePathSegment, this,
				false);
	}

	@Override
	public ObjectData getFolderParent(String repositoryId, String folderId, String filter, ExtensionsData extension) {
		log.debug("getFolderParent({}, {}, {})", new Object[]{repositoryId, folderId, filter});
		return getRepository().getFolderParent(getCallContext(), folderId, filter, this);
	}

	@Override
	public List<ObjectInFolderContainer> getFolderTree(String repositoryId, String folderId, BigInteger depth, String filter,
	                                                   Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePathSegment,
	                                                   ExtensionsData extension) {
		log.debug("getFolderTree({}, {}, {})", new Object[]{repositoryId, folderId, depth});
		return getRepository().getDescendants(getCallContext(), folderId, depth, filter, includeAllowableActions, includePathSegment, this,
				true);
	}

	@Override
	public List<ObjectParentData> getObjectParents(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
	                                               IncludeRelationships includeRelationships, String renditionFilter, Boolean includeRelativePathSegment, ExtensionsData extension) {
		log.debug("getObjectParents({}, {}, {}, {})", new Object[]{repositoryId, objectId, filter, includeAllowableActions});
		return getRepository().getObjectParents(getCallContext(), objectId, filter, includeAllowableActions, includeRelativePathSegment,
				this);
	}

	@Override
	public ObjectList getCheckedOutDocs(String repositoryId, String folderId, String filter, String orderBy,
	                                    Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, BigInteger maxItems,
	                                    BigInteger skipCount, ExtensionsData extension) {
		log.debug("getCheckedOutDocs({}, {}, {})", new Object[]{repositoryId, folderId, filter});
		ObjectListImpl result = new ObjectListImpl();

		result.setHasMoreItems(false);
		result.setNumItems(BigInteger.ZERO);
		List<ObjectData> emptyList = Collections.emptyList();
		result.setObjects(emptyList);

		return result;
	}

	// --- Object Service ---

	@Override
	public String create(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
	                     VersioningState versioningState, List<String> policies, ExtensionsData extension) {
		log.debug("create({}, {}, {})", new Object[]{repositoryId, properties, folderId});
		ObjectData object = getRepository().create(getCallContext(), properties, folderId, contentStream, versioningState, this);
		return object.getId();
	}

	@Override
	public String createDocument(String repositoryId, Properties properties, String folderId, ContentStream contentStream,
	                             VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces, ExtensionsData extension) {
		log.debug("createDocument({}, {}, {})", new Object[]{repositoryId, properties, folderId});
		return getRepository().createDocument(getCallContext(), properties, folderId, contentStream, versioningState);
	}

	@Override
	public String createDocumentFromSource(String repositoryId, String sourceId, Properties properties, String folderId,
	                                       VersioningState versioningState, List<String> policies, Acl addAces, Acl removeAces, ExtensionsData extension) {
		log.debug("createDocumentFromSource({}, {}, {}, {})", new Object[]{repositoryId, sourceId, properties, folderId});
		return getRepository().createDocumentFromSource(getCallContext(), sourceId, properties, folderId, versioningState);
	}

	@Override
	public String createFolder(String repositoryId, Properties properties, String folderId, List<String> policies, Acl addAces,
	                           Acl removeAces, ExtensionsData extension) {
		log.debug("createFolder({}, {}, {})", new Object[]{repositoryId, properties, folderId});
		return getRepository().createFolder(getCallContext(), properties, folderId);
	}

	@Override
	public void deleteContentStream(String repositoryId, Holder<String> objectId, Holder<String> changeToken, ExtensionsData extension) {
		log.debug("deleteContentStream({}, {}, {})", new Object[]{repositoryId, objectId, changeToken});
		throw new CmisNotSupportedException("Not supported!");
	}

	@Override
	public void deleteObjectOrCancelCheckOut(String repositoryId, String objectId, Boolean allVersions, ExtensionsData extension) {
		log.debug("deleteObjectOrCancelCheckOut({}, {}, {})", new Object[]{repositoryId, objectId, allVersions});
		getRepository().deleteObject(getCallContext(), objectId);
	}

	@Override
	public FailedToDeleteData deleteTree(String repositoryId, String folderId, Boolean allVersions, UnfileObject unfileObjects,
	                                     Boolean continueOnFailure, ExtensionsData extension) {
		log.debug("deleteTree({}, {}, {})", new Object[]{repositoryId, folderId, allVersions});
		return getRepository().deleteTree(getCallContext(), folderId, continueOnFailure);
	}

	@Override
	public AllowableActions getAllowableActions(String repositoryId, String objectId, ExtensionsData extension) {
		log.debug("getAllowableActions({}, {})", new Object[]{repositoryId, objectId});
		return getRepository().getAllowableActions(getCallContext(), objectId);
	}

	@Override
	public ContentStream getContentStream(String repositoryId, String objectId, String streamId, BigInteger offset, BigInteger length,
	                                      ExtensionsData extension) {
		log.debug("getContentStream({}, {}, {})", new Object[]{repositoryId, objectId, streamId});
		return getRepository().getContentStream(getCallContext(), objectId, offset, length);
	}

	@Override
	public ObjectData getObject(String repositoryId, String objectId, String filter, Boolean includeAllowableActions,
	                            IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds, Boolean includeAcl,
	                            ExtensionsData extension) {
		log.debug("getObject({}, {}, {})", new Object[]{repositoryId, objectId, filter});
		return getRepository().getObject(getCallContext(), objectId, null, filter, includeAllowableActions, includeAcl, this);
	}

	@Override
	public ObjectData getObjectByPath(String repositoryId, String path, String filter, Boolean includeAllowableActions,
	                                  IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds, Boolean includeAcl,
	                                  ExtensionsData extension) {
		log.debug("getObjectByPath({}, {}, {})", new Object[]{repositoryId, path, filter});
		return getRepository().getObjectByPath(getCallContext(), path, filter, includeAllowableActions, includeAcl, this);
	}

	@Override
	public Properties getProperties(String repositoryId, String objectId, String filter, ExtensionsData extension) {
		log.debug("getProperties({}, {}, {})", new Object[]{repositoryId, objectId, filter});
		ObjectData object = getRepository().getObject(getCallContext(), objectId, null, filter, false, false, this);
		return object.getProperties();
	}

	@Override
	public List<RenditionData> getRenditions(String repositoryId, String objectId, String renditionFilter, BigInteger maxItems,
	                                         BigInteger skipCount, ExtensionsData extension) {
		log.debug("getRenditions({}, {}, {})", new Object[]{repositoryId, objectId, renditionFilter});
		return Collections.emptyList();
	}

	@Override
	public void moveObject(String repositoryId, Holder<String> objectId, String targetFolderId, String sourceFolderId,
	                       ExtensionsData extension) {
		log.debug("moveObject({}, {}, {})", new Object[]{repositoryId, objectId, targetFolderId});
		getRepository().moveObject(getCallContext(), objectId, targetFolderId, this);
	}

	@Override
	public void setContentStream(String repositoryId, Holder<String> objectId, Boolean overwriteFlag, Holder<String> changeToken,
	                             ContentStream contentStream, ExtensionsData extension) {
		log.debug("setContentStream({}, {}, {})", new Object[]{repositoryId, objectId, overwriteFlag});
		getRepository().setContentStream(getCallContext(), objectId, overwriteFlag, contentStream);
	}

	@Override
	public void updateProperties(String repositoryId, Holder<String> objectId, Holder<String> changeToken, Properties properties,
	                             ExtensionsData extension) {
		log.debug("updateProperties({}, {}, {})", new Object[]{repositoryId, objectId, changeToken});
		getRepository().updateProperties(getCallContext(), objectId, properties, this);
	}

	// --- Versioning Service ---

	@Override
	public List<ObjectData> getAllVersions(String repositoryId, String objectId, String versionSeriesId, String filter,
	                                       Boolean includeAllowableActions, ExtensionsData extension) {
		log.debug("getAllVersions({}, {}, {})", new Object[]{repositoryId, objectId, versionSeriesId});
		ObjectData theVersion = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions,
				false, this);
		return Collections.singletonList(theVersion);
	}

	@Override
	public ObjectData getObjectOfLatestVersion(String repositoryId, String objectId, String versionSeriesId, Boolean major, String filter,
	                                           Boolean includeAllowableActions, IncludeRelationships includeRelationships, String renditionFilter, Boolean includePolicyIds,
	                                           Boolean includeAcl, ExtensionsData extension) {
		log.debug("getObjectOfLatestVersion({}, {}, {})", new Object[]{repositoryId, objectId, versionSeriesId});
		return getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, includeAllowableActions, includeAcl, this);
	}

	@Override
	public Properties getPropertiesOfLatestVersion(String repositoryId, String objectId, String versionSeriesId, Boolean major,
	                                               String filter, ExtensionsData extension) {
		log.debug("getPropertiesOfLatestVersion({}, {}, {})", new Object[]{repositoryId, objectId, versionSeriesId});
		ObjectData object = getRepository().getObject(getCallContext(), objectId, versionSeriesId, filter, false, false, null);
		return object.getProperties();
	}

	// --- ACL Service ---

	@Override
	public Acl getAcl(String repositoryId, String objectId, Boolean onlyBasicPermissions, ExtensionsData extension) {
		log.debug("getAcl({}, {}, {})", new Object[]{repositoryId, objectId, onlyBasicPermissions});
		return getRepository().getAcl(getCallContext(), objectId);
	}
}
