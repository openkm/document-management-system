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

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.automation.AutomationException;
import com.openkm.bean.*;
import com.openkm.core.*;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.db.DbDocumentModule;
import com.openkm.util.PathUtils;
import com.openkm.util.WarUtils;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.*;
import org.apache.chemistry.opencmis.commons.data.Properties;
import org.apache.chemistry.opencmis.commons.data.RepositoryInfo;
import org.apache.chemistry.opencmis.commons.definitions.*;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.*;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.impl.server.ObjectInfoImpl;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.apache.chemistry.opencmis.commons.server.ObjectInfoHandler;
import org.apache.chemistry.opencmis.commons.spi.Holder;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

/**
 * CMIS Service Implementation.
 */
public class CmisRepository {
	private static Logger log = LoggerFactory.getLogger(CmisRepository.class);
	private static final String ROOT_ID = "/" + Repository.ROOT;
	private static final String USER_UNKNOWN = "<unknown>";
	private static final String CMIS_READ = "cmis:read";
	private static final String CMIS_WRITE = "cmis:write";
	private static final String CMIS_DELETE = "cmis:delete";
	private static final String CMIS_ALL = "cmis:all";

	/** Repository id */
	private final String repositoryId;

	/** Types */
	private final CmisTypeManager types;

	/** Repository info */
	private final RepositoryInfoImpl repositoryInfo;

	public CmisRepository(String repositoryId, CmisTypeManager types) {
		// check repository id
		if ((repositoryId == null) || (repositoryId.trim().length() == 0)) {
			throw new IllegalArgumentException("Invalid repository id!");
		}

		this.repositoryId = repositoryId;
		this.types = types;

		repositoryInfo = new RepositoryInfoImpl();

		repositoryInfo.setId("default");
		repositoryInfo.setName("OpenKM");
		repositoryInfo.setDescription("OpenKM CMIS Interface");
		repositoryInfo.setCmisVersionSupported("1.0");
		repositoryInfo.setProductName("OpenKM - Knowledge Management");
		repositoryInfo.setProductVersion(WarUtils.getAppVersion().toString());
		repositoryInfo.setVendorName("OpenKM");
		repositoryInfo.setRootFolder(ROOT_ID);
		repositoryInfo.setThinClientUri(Config.APPLICATION_URL);

		RepositoryCapabilitiesImpl capabilities = new RepositoryCapabilitiesImpl();
		capabilities.setCapabilityAcl(CapabilityAcl.DISCOVER);
		capabilities.setAllVersionsSearchable(false);
		capabilities.setCapabilityJoin(CapabilityJoin.NONE);
		capabilities.setSupportsMultifiling(false);
		capabilities.setSupportsUnfiling(false);
		capabilities.setSupportsVersionSpecificFiling(false);
		capabilities.setIsPwcSearchable(false);
		capabilities.setIsPwcUpdatable(false);
		capabilities.setCapabilityQuery(CapabilityQuery.NONE);
		capabilities.setCapabilityChanges(CapabilityChanges.NONE);
		capabilities.setCapabilityContentStreamUpdates(CapabilityContentStreamUpdates.ANYTIME);
		capabilities.setSupportsGetDescendants(true);
		capabilities.setSupportsGetFolderTree(true);
		capabilities.setCapabilityRendition(CapabilityRenditions.NONE);

		NewTypeSettableAttributesImpl typeSetAttributes = new NewTypeSettableAttributesImpl();
		typeSetAttributes.setCanSetControllableAcl(false);
		typeSetAttributes.setCanSetControllablePolicy(false);
		typeSetAttributes.setCanSetCreatable(false);
		typeSetAttributes.setCanSetDescription(false);
		typeSetAttributes.setCanSetDisplayName(false);
		typeSetAttributes.setCanSetFileable(false);
		typeSetAttributes.setCanSetFulltextIndexed(false);
		typeSetAttributes.setCanSetId(false);
		typeSetAttributes.setCanSetIncludedInSupertypeQuery(false);
		typeSetAttributes.setCanSetLocalName(false);
		typeSetAttributes.setCanSetLocalNamespace(false);
		typeSetAttributes.setCanSetQueryable(false);
		typeSetAttributes.setCanSetQueryName(false);

		capabilities.setNewTypeSettableAttributes(typeSetAttributes);
		repositoryInfo.setCapabilities(capabilities);

		AclCapabilitiesDataImpl aclCapability = new AclCapabilitiesDataImpl();
		aclCapability.setSupportedPermissions(SupportedPermissions.BASIC);
		aclCapability.setAclPropagation(AclPropagation.OBJECTONLY);

		// permissions
		List<PermissionDefinition> permissions = new ArrayList<PermissionDefinition>();
		permissions.add(createPermission(CMIS_READ, "Read"));
		permissions.add(createPermission(CMIS_WRITE, "Write"));
		permissions.add(createPermission(CMIS_DELETE, "Delete"));
		permissions.add(createPermission(CMIS_ALL, "All"));
		aclCapability.setPermissionDefinitionData(permissions);

		// mapping
		List<PermissionMapping> list = new ArrayList<PermissionMapping>();
		list.add(createMapping(PermissionMapping.CAN_CREATE_DOCUMENT_FOLDER, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_CREATE_FOLDER_FOLDER, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_CONTENT_DOCUMENT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_OBJECT, CMIS_DELETE));
		list.add(createMapping(PermissionMapping.CAN_DELETE_TREE_FOLDER, CMIS_DELETE));
		list.add(createMapping(PermissionMapping.CAN_GET_ACL_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_ALL_VERSIONS_VERSION_SERIES, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_CHILDREN_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_DESCENDENTS_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_FOLDER_PARENT_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PARENTS_FOLDER, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_GET_PROPERTIES_OBJECT, CMIS_READ));
		list.add(createMapping(PermissionMapping.CAN_MOVE_OBJECT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_MOVE_SOURCE, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_MOVE_TARGET, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_SET_CONTENT_DOCUMENT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_UPDATE_PROPERTIES_OBJECT, CMIS_WRITE));
		list.add(createMapping(PermissionMapping.CAN_VIEW_CONTENT_OBJECT, CMIS_READ));

		Map<String, PermissionMapping> map = new LinkedHashMap<String, PermissionMapping>();
		for (PermissionMapping pm : list) {
			map.put(pm.getKey(), pm);
		}

		aclCapability.setPermissionMappingData(map);
		repositoryInfo.setAclCapabilities(aclCapability);
	}

	private static PermissionDefinition createPermission(String permission, String description) {
		PermissionDefinitionDataImpl pd = new PermissionDefinitionDataImpl();
		pd.setId(permission);
		pd.setDescription(description);
		return pd;
	}

	private static PermissionMapping createMapping(String key, String permission) {
		PermissionMappingDataImpl pm = new PermissionMappingDataImpl();
		pm.setKey(key);
		pm.setPermissions(Collections.singletonList(permission));
		return pm;
	}

	// --- the public stuff ---

	/**
	 * Returns the repository id.
	 */
	public String getRepositoryId() {
		return repositoryId;
	}

	/**
	 * CMIS getRepositoryInfo.
	 */
	public RepositoryInfo getRepositoryInfo(CallContext context) {
		return repositoryInfo;
	}

	/**
	 * CMIS getTypesChildren.
	 */
	public TypeDefinitionList getTypesChildren(CallContext context, String typeId, boolean includePropertyDefinitions, BigInteger maxItems,
	                                           BigInteger skipCount) {
		log.debug("getTypesChildren");
		return types.getTypesChildren(context, typeId, includePropertyDefinitions, maxItems, skipCount);
	}

	/**
	 * CMIS getTypeDefinition.
	 */
	public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
		log.debug("getTypeDefinition({})", typeId);
		return types.getTypeDefinition(context, typeId);
	}

	/**
	 * CMIS getTypesDescendants.
	 */
	public List<TypeDefinitionContainer> getTypesDescendants(CallContext context, String typeId, BigInteger depth,
	                                                         Boolean includePropertyDefinitions) {
		log.debug("getTypesDescendants");
		return types.getTypesDescendants(context, typeId, depth, includePropertyDefinitions);
	}

	/**
	 * Create* dispatch for AtomPub.
	 */
	public ObjectData create(CallContext context, Properties properties, String folderId, ContentStream contentStream,
	                         VersioningState versioningState, ObjectInfoHandler objectInfos) {
		log.debug("create({}, {})", properties, folderId);

		String typeId = getTypeId(properties);
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		String objectId = null;
		if (type.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) {
			objectId = createDocument(context, properties, folderId, contentStream, versioningState);
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_FOLDER) {
			objectId = createFolder(context, properties, folderId);
		} else {
			throw new CmisObjectNotFoundException("Cannot create object of type '" + typeId + "'!");
		}

		return compileObjectType(context, getNode(objectId), null, false, false, objectInfos);
	}

	/**
	 * CMIS createDocument.
	 */
	public String createDocument(CallContext context, Properties properties, String folderId, ContentStream contentStream,
	                             VersioningState versioningState) {
		log.debug("createDocument({}, {})", properties, folderId);

		// check properties
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisInvalidArgumentException("Properties must be set!");
		}

		// check versioning state
		// if (VersioningState.NONE != versioningState) {
		// throw new CmisConstraintException("Versioning not supported!");
		// }

		// check type
		String typeId = getTypeId(properties);
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// compile the properties
		Properties props = compileProperties(typeId, context.getUsername(), millisToCalendar(System.currentTimeMillis()),
				context.getUsername(), properties);

		// check the name
		String name = getStringProperty(properties, PropertyIds.NAME);
		if (!isValidName(name)) {
			throw new CmisNameConstraintViolationException("Name is not valid!");
		}

		try {
			// get parent Folder
			if (!OKMFolder.getInstance().isValid(null, folderId)) {
				throw new CmisObjectNotFoundException("Parent is not a folder!");
			}

			// create the file
			Document newDoc = OKMDocument.getInstance().createSimple(null, folderId + "/" + name, contentStream.getStream());

			// write properties
			writePropertiesFile(newDoc, props);

			return newDoc.getPath();
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException("Could not create document!");
		} catch (RepositoryException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (DatabaseException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (ItemExistsException e) {
			throw new CmisNameConstraintViolationException("Document already exists");
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException("No write permission!");
		} catch (ExtensionException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (AutomationException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (IOException e) {
			throw new CmisStorageException("Could not create document: " + e.getMessage());
		} catch (Exception e) {
			throw new CmisStorageException("Could not create document!");
		}
	}

	/**
	 * CMIS createDocumentFromSource.
	 */
	public String createDocumentFromSource(CallContext context, String sourceId, Properties properties, String folderId,
	                                       VersioningState versioningState) {
		InputStream is = null;

		try {
			// get parent Folder
			if (!OKMFolder.getInstance().isValid(null, folderId)) {
				throw new CmisObjectNotFoundException("Parent is not a folder!");
			}

			// get source Document
			if (!OKMDocument.getInstance().isValid(null, sourceId)) {
				throw new CmisObjectNotFoundException("Source is not a document!");
			}

			// file name
			Document srcDoc = OKMDocument.getInstance().getProperties(null, sourceId);
			String name = PathUtils.getName(srcDoc.getPath());

			// get properties
			PropertiesImpl sourceProperties = new PropertiesImpl();
			readCustomProperties(srcDoc, sourceProperties, null, new ObjectInfoImpl());

			// get the type id
			String typeId = getIdProperty(sourceProperties, PropertyIds.OBJECT_TYPE_ID);
			if (typeId == null) {
				typeId = CmisTypeManager.DOCUMENT_TYPE_ID;
			}

			// copy properties
			PropertiesImpl newProperties = new PropertiesImpl();
			for (PropertyData<?> prop : sourceProperties.getProperties().values()) {
				if ((prop.getId().equals(PropertyIds.OBJECT_TYPE_ID)) || (prop.getId().equals(PropertyIds.CREATED_BY))
						|| (prop.getId().equals(PropertyIds.CREATION_DATE)) || (prop.getId().equals(PropertyIds.LAST_MODIFIED_BY))) {
					continue;
				}

				newProperties.addProperty(prop);
			}

			// replace properties
			if (properties != null) {
				// find new name
				String newName = getStringProperty(properties, PropertyIds.NAME);
				if (newName != null) {
					if (!isValidName(newName)) {
						throw new CmisNameConstraintViolationException("Name is not valid!");
					}
					name = newName;
				}

				// get the property definitions
				TypeDefinition type = types.getType(typeId);
				if (type == null) {
					throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
				}

				// replace with new values
				for (PropertyData<?> prop : properties.getProperties().values()) {
					PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

					// do we know that property?
					if (propType == null) {
						throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
					}

					// can it be set?
					if ((propType.getUpdatability() != Updatability.READWRITE)) {
						throw new CmisConstraintException("Property '" + prop.getId() + "' cannot be updated!");
					}

					// empty properties are invalid
					if (isEmptyProperty(prop)) {
						throw new CmisConstraintException("Property '" + prop.getId() + "' must not be empty!");
					}

					newProperties.addProperty(prop);
				}
			}

			addPropertyId(newProperties, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
			addPropertyString(newProperties, typeId, null, PropertyIds.CREATED_BY, context.getUsername());
			addPropertyDateTime(newProperties, typeId, null, PropertyIds.CREATION_DATE, millisToCalendar(System.currentTimeMillis()));
			addPropertyString(newProperties, typeId, null, PropertyIds.LAST_MODIFIED_BY, context.getUsername());

			// create the Document
			is = OKMDocument.getInstance().getContent(null, srcDoc.getUuid(), false);
			Document newDoc = OKMDocument.getInstance().createSimple(null, folderId + "/" + name, is);

			// write properties
			writePropertiesFile(newDoc, newProperties);

			return newDoc.getPath();
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException("Could not create document!");
		} catch (RepositoryException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (DatabaseException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (ItemExistsException e) {
			throw new CmisNameConstraintViolationException("Document already exists");
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException("No write permission!");
		} catch (ExtensionException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (AutomationException e) {
			throw new CmisStorageException("Could not create document!");
		} catch (IOException e) {
			throw new CmisStorageException("Could not read or write content: " + e.getMessage(), e);
		} catch (Exception e) {
			throw new CmisStorageException("Could not create document!");
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	/**
	 * CMIS createFolder.
	 */
	public String createFolder(CallContext context, Properties properties, String folderId) {
		log.debug("createFolder({}, {})", properties, folderId);

		// check properties
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisInvalidArgumentException("Properties must be set!");
		}

		// check type
		String typeId = getTypeId(properties);
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// compile the properties
		Properties props = compileProperties(typeId, context.getUsername(), millisToCalendar(System.currentTimeMillis()),
				context.getUsername(), properties);

		// check the name
		String name = getStringProperty(properties, PropertyIds.NAME);
		if (!isValidName(name)) {
			throw new CmisNameConstraintViolationException("Name is not valid.");
		}

		// get parent File
		try {
			if (!OKMFolder.getInstance().isValid(null, folderId)) {
				throw new CmisObjectNotFoundException("Parent is not a folder!");
			}

			// create the folder
			Folder newFld = OKMFolder.getInstance().createSimple(null, folderId + "/" + name);

			// write properties
			writePropertiesFile(newFld, props);

			return newFld.getPath();
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException("Could not create folder!");
		} catch (RepositoryException e) {
			throw new CmisStorageException("Could not create folder", e);
		} catch (DatabaseException e) {
			throw new CmisStorageException("Could not create folder", e);
		} catch (ItemExistsException e) {
			throw new CmisNameConstraintViolationException("Folder already exists", e);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException("No write permission", e);
		} catch (ExtensionException e) {
			throw new CmisStorageException("Could not create folder", e);
		} catch (AutomationException e) {
			throw new CmisStorageException("Could not create folder", e);
		}
	}

	/**
	 * CMIS moveObject.
	 */
	public ObjectData moveObject(CallContext context, Holder<String> objectId, String targetFolderId, ObjectInfoHandler objectInfos) {
		log.debug("moveObject({}, {})", objectId, targetFolderId);
		Node node = null;

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		String newPath = targetFolderId + "/" + PathUtils.getName(objectId.getValue());

		try {
			if (OKMFolder.getInstance().isValid(null, objectId.getValue())) {
				OKMFolder.getInstance().move(null, objectId.getValue(), targetFolderId);
				node = OKMFolder.getInstance().getProperties(null, newPath);
			} else if (OKMDocument.getInstance().isValid(null, objectId.getValue())) {
				OKMDocument.getInstance().move(null, objectId.getValue(), targetFolderId);
				node = OKMDocument.getInstance().getProperties(null, newPath);
			} else {
				throw new CmisObjectNotFoundException(objectId.getValue());
			}

			objectId.setValue(newPath);
			return compileObjectType(context, node, null, false, false, objectInfos);
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException("Could not move node", e);
		} catch (RepositoryException e) {
			throw new CmisStorageException("Could not move node", e);
		} catch (DatabaseException e) {
			throw new CmisStorageException("Could not move node", e);
		} catch (ItemExistsException e) {
			throw new CmisNameConstraintViolationException("Node already exists", e);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException("No write permission", e);
		} catch (ExtensionException e) {
			throw new CmisStorageException("Could not move node", e);
		} catch (AutomationException e) {
			throw new CmisStorageException("Could not move node", e);
		} catch (LockException e) {
			throw new CmisStorageException("Could not move node", e);
		}
	}

	/**
	 * CMIS setContentStream and deleteContentStream.
	 */
	public void setContentStream(CallContext context, Holder<String> objectId, Boolean overwriteFlag, ContentStream contentStream) {
		log.debug("setContentStream({}, {})", objectId, overwriteFlag);

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		// check overwrite
		// boolean owf = (overwriteFlag == null ? true : overwriteFlag.booleanValue());
		// if (!owf && file.length() > 0) {
		// throw new CmisContentAlreadyExistsException("Content already exists!");
		// }

		try {
			if (!OKMDocument.getInstance().isValid(null, objectId.getValue())) {
				throw new CmisStreamNotSupportedException("Not a document");
			}

			if (!OKMDocument.getInstance().isCheckedOut(null, objectId.getValue())) {
				OKMDocument.getInstance().checkout(null, objectId.getValue());
			}

			new DbDocumentModule().checkin(null, objectId.getValue(), contentStream.getStream(), contentStream.getLength(), "CMIS Client",
					null);
		} catch (Exception e) {
			throw new CmisStorageException("Could not write content: " + e.getMessage(), e);
		}
	}

	/**
	 * CMIS deleteObject.
	 */
	public void deleteObject(CallContext context, String objectId) {
		log.debug("deleteObject({})", objectId);

		try {
			if (OKMFolder.getInstance().isValid(null, objectId)) {
				OKMFolder.getInstance().delete(null, objectId);
			} else if (OKMDocument.getInstance().isValid(null, objectId)) {
				OKMDocument.getInstance().delete(null, objectId);
			} else {
				throw new CmisObjectNotFoundException(objectId);
			}
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(objectId, e);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (DatabaseException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (LockException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (ExtensionException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (AutomationException e) {
			throw new CmisStorageException("Deletion failed!", e);
		}
	}

	/**
	 * CMIS deleteTree.
	 */
	public FailedToDeleteData deleteTree(CallContext context, String folderId, Boolean continueOnFailure) {
		log.debug("deleteTree({})", folderId);
		FailedToDeleteDataImpl result = new FailedToDeleteDataImpl();
		result.setIds(new ArrayList<String>());

		try {
			if (OKMFolder.getInstance().isValid(null, folderId)) {
				OKMFolder.getInstance().delete(null, folderId);
			} else {
				throw new CmisObjectNotFoundException(folderId);
			}
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(folderId, e);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (DatabaseException e) {
			throw new CmisStorageException("Deletion failed!", e);
		} catch (LockException e) {
			throw new CmisStorageException("Deletion failed!", e);
		}

		return result;
	}

	/**
	 * CMIS updateProperties.
	 */
	public ObjectData updateProperties(CallContext context, Holder<String> objectId, Properties properties, ObjectInfoHandler objectInfos) {
		log.debug("updateProperties({}, {})", objectId, properties);

		if (objectId == null) {
			throw new CmisInvalidArgumentException("Id is not valid!");
		}

		// get the document or folder
		Node node = getNode(objectId.getValue());

		// get and check the new name
		String newName = getStringProperty(properties, PropertyIds.NAME);
		boolean isRename = (newName != null) && (!PathUtils.getName(node.getPath()).equals(newName));
		if (isRename && !isValidName(newName)) {
			throw new CmisNameConstraintViolationException("Name is not valid!");
		}

		// get old properties
		PropertiesImpl oldProperties = new PropertiesImpl();
		readCustomProperties(node, oldProperties, null, new ObjectInfoImpl());

		// get the type id
		String typeId = getIdProperty(oldProperties, PropertyIds.OBJECT_TYPE_ID);
		if (typeId == null) {
			if (node instanceof Folder) {
				typeId = CmisTypeManager.FOLDER_TYPE_ID;
			} else {
				typeId = CmisTypeManager.DOCUMENT_TYPE_ID;
			}
		}

		// get the creator
		String creator = getStringProperty(oldProperties, PropertyIds.CREATED_BY);
		if (creator == null) {
			creator = context.getUsername();
		}

		// get creation date
		GregorianCalendar creationDate = getDateTimeProperty(oldProperties, PropertyIds.CREATION_DATE);
		if (creationDate == null) {
			creationDate = millisToCalendar(node.getCreated().getTimeInMillis());
		}

		// compile the properties
		Properties props = updateProperties(typeId, creator, creationDate, context.getUsername(), oldProperties, properties);

		// write properties
		writePropertiesFile(node, props);

		// rename file or folder if necessary
		Node newNode = node;
		if (isRename) {
			try {
				if (node instanceof Folder) {
					newNode = OKMFolder.getInstance().rename(null, node.getUuid(), newName);
				} else {
					newNode = OKMDocument.getInstance().rename(null, node.getUuid(), newName);
				}
			} catch (PathNotFoundException e) {
				throw new CmisObjectNotFoundException(e.getMessage(), e);
			} catch (AccessDeniedException e) {
				throw new CmisPermissionDeniedException(e.getMessage(), e);
			} catch (ItemExistsException e) {
				throw new CmisNameConstraintViolationException(e.getMessage(), e);
			} catch (RepositoryException e) {
				throw new CmisStorageException("Update perties failed!", e);
			} catch (DatabaseException e) {
				throw new CmisStorageException("Update perties failed!", e);
			} catch (LockException e) {
				throw new CmisStorageException("Update perties failed!", e);
			} catch (ExtensionException e) {
				throw new CmisStorageException("Update perties failed!", e);
			} catch (AutomationException e) {
				throw new CmisStorageException("Update perties failed!", e);
			}

			// set new id
			objectId.setValue(newNode.getPath());
		}

		return compileObjectType(context, newNode, null, false, false, objectInfos);
	}

	/**
	 * CMIS getObject.
	 */
	public ObjectData getObject(CallContext context, String objectId, String versionServicesId, String filter,
	                            Boolean includeAllowableActions, Boolean includeAcl, ObjectInfoHandler objectInfos) {
		log.debug("getObject({}, {}, {})", new Object[]{objectId, versionServicesId, filter});

		// check id
		if ((objectId == null) && (versionServicesId == null)) {
			throw new CmisInvalidArgumentException("Object Id must be set.");
		}

		if (objectId == null) {
			// this works only because there are no versions in a file system
			// and the object id and version series id are the same
			objectId = versionServicesId;
		}

		// get the document or folder
		Node node = getNode(objectId);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean iacl = (includeAcl == null ? false : includeAcl.booleanValue());

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// gather properties
		return compileObjectType(context, node, filterCollection, iaa, iacl, objectInfos);
	}

	/**
	 * CMIS getAllowableActions.
	 */
	public AllowableActions getAllowableActions(CallContext context, String objectId) {
		log.debug("getAllowableActions({})", objectId);

		// get the document or folder
		Node node = getNode(objectId);

		return compileAllowableActions(node);
	}

	/**
	 * CMIS getACL.
	 */
	public Acl getAcl(CallContext context, String objectId) {
		log.debug("getAcl({})", objectId);

		// get the document or folder
		Node node = getNode(objectId);

		return compileAcl(node);
	}

	/**
	 * CMIS getContentStream.
	 */
	public ContentStream getContentStream(CallContext context, String objectId, BigInteger offset, BigInteger length) {
		log.debug("getContentStream({}, {}, {})", new Object[]{objectId, offset, length});

		if ((offset != null) || (length != null)) {
			throw new CmisInvalidArgumentException("Offset and Length are not supported!");
		}

		try {
			// get the document
			if (!OKMDocument.getInstance().isValid(null, objectId)) {
				throw new CmisStreamNotSupportedException("Not a file!");
			}

			Document doc = OKMDocument.getInstance().getProperties(null, objectId);
			InputStream is = OKMDocument.getInstance().getContent(null, objectId, false);

			// compile data
			ContentStreamImpl result = new ContentStreamImpl();
			result.setFileName(PathUtils.getName(doc.getPath()));
			result.setLength(BigInteger.valueOf(doc.getActualVersion().getSize()));
			result.setMimeType(doc.getMimeType());
			result.setStream(is);

			return result;
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException("No read permission!");
		} catch (RepositoryException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}

	/**
	 * CMIS getChildren.
	 */
	public ObjectInFolderList getChildren(CallContext context, String folderId, String filter, Boolean includeAllowableActions,
	                                      Boolean includePathSegment, BigInteger maxItems, BigInteger skipCount, ObjectInfoHandler objectInfos) {
		log.debug("getChildren({})", folderId);

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

		// skip and max
		int skip = (skipCount == null ? 0 : skipCount.intValue());
		if (skip < 0) {
			skip = 0;
		}

		int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
		if (max < 0) {
			max = Integer.MAX_VALUE;
		}

		try {
			if (!OKMFolder.getInstance().isValid(null, folderId)) {
				throw new CmisObjectNotFoundException("Not a folder!");
			}

			// get the folder
			Folder fld = OKMFolder.getInstance().getProperties(null, folderId);

			// set object info of the the folder
			if (context.isObjectInfoRequired()) {
				compileObjectType(context, fld, null, false, false, objectInfos);
			}

			// prepare result
			ObjectInFolderListImpl result = new ObjectInFolderListImpl();
			result.setObjects(new ArrayList<ObjectInFolderData>());
			result.setHasMoreItems(false);
			int count = 0;

			// iterate through children (folders)
			for (Folder child : OKMFolder.getInstance().getChildren(null, fld.getPath())) {
				count++;

				if (skip > 0) {
					skip--;
					continue;
				}

				if (result.getObjects().size() >= max) {
					result.setHasMoreItems(true);
					continue;
				}

				// build and add child object
				ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
				objectInFolder.setObject(compileObjectType(context, child, filterCollection, iaa, false, objectInfos));

				if (ips) {
					objectInFolder.setPathSegment(PathUtils.getName(child.getPath()));
				}

				result.getObjects().add(objectInFolder);
			}

			// iterate through children (documents)
			for (Document child : OKMDocument.getInstance().getChildren(null, fld.getPath())) {
				count++;

				if (skip > 0) {
					skip--;
					continue;
				}

				if (result.getObjects().size() >= max) {
					result.setHasMoreItems(true);
					continue;
				}

				// build and add child object
				ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
				objectInFolder.setObject(compileObjectType(context, child, filterCollection, iaa, false, objectInfos));

				if (ips) {
					objectInFolder.setPathSegment(PathUtils.getName(child.getPath()));
				}

				result.getObjects().add(objectInFolder);
			}

			result.setNumItems(BigInteger.valueOf(count));
			return result;
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}

	/**
	 * CMIS getDescendants.
	 */
	public List<ObjectInFolderContainer> getDescendants(CallContext context, String folderId, BigInteger depth, String filter,
	                                                    Boolean includeAllowableActions, Boolean includePathSegment, ObjectInfoHandler objectInfos, boolean foldersOnly) {
		log.debug("getDescendants or getFolderTree");

		// check depth
		int d = (depth == null ? 2 : depth.intValue());
		if (d == 0) {
			throw new CmisInvalidArgumentException("Depth must not be 0!");
		}
		if (d < -1) {
			d = -1;
		}

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean ips = (includePathSegment == null ? false : includePathSegment.booleanValue());

		try {
			if (!OKMFolder.getInstance().isValid(null, folderId)) {
				throw new CmisObjectNotFoundException("Not a folder!");
			}

			// get the folder
			Folder fld = OKMFolder.getInstance().getProperties(null, folderId);

			// set object info of the the folder
			if (context.isObjectInfoRequired()) {
				compileObjectType(context, fld, null, false, false, objectInfos);
			}

			// get the tree
			List<ObjectInFolderContainer> result = new ArrayList<ObjectInFolderContainer>();
			gatherDescendants(context, fld, result, foldersOnly, d, filterCollection, iaa, ips, objectInfos);

			return result;
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}

	/**
	 * CMIS getFolderParent.
	 */
	public ObjectData getFolderParent(CallContext context, String folderId, String filter, ObjectInfoHandler objectInfos) {
		List<ObjectParentData> parents = getObjectParents(context, folderId, filter, false, false, objectInfos);

		if (parents.size() == 0) {
			throw new CmisInvalidArgumentException("The root folder has no parent!");
		}

		return parents.get(0).getObject();
	}

	/**
	 * CMIS getObjectParents.
	 */
	public List<ObjectParentData> getObjectParents(CallContext context, String objectId, String filter, Boolean includeAllowableActions,
	                                               Boolean includeRelativePathSegment, ObjectInfoHandler objectInfos) {
		log.debug("getObjectParents({}, {})", objectId, filter);

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// set defaults if values not set
		boolean iaa = (includeAllowableActions == null ? false : includeAllowableActions.booleanValue());
		boolean irps = (includeRelativePathSegment == null ? false : includeRelativePathSegment.booleanValue());

		// get the document or folder
		Node node = getNode(objectId);

		// don't climb above the root folder
		if (ROOT_ID.equals(node.getPath())) {
			return Collections.emptyList();
		}

		// set object info of the the object
		if (context.isObjectInfoRequired()) {
			compileObjectType(context, node, null, false, false, objectInfos);
		}

		try {
			// get parent folder
			Folder parent = OKMFolder.getInstance().getProperties(null, PathUtils.getParent(node.getPath()));
			ObjectData object = compileObjectType(context, parent, filterCollection, iaa, false, objectInfos);

			ObjectParentDataImpl result = new ObjectParentDataImpl();
			result.setObject(object);
			if (irps) {
				result.setRelativePathSegment(PathUtils.getName(parent.getPath()));
			}

			return Collections.singletonList((ObjectParentData) result);
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}

	/**
	 * CMIS getObjectByPath.
	 */
	public ObjectData getObjectByPath(CallContext context, String folderPath, String filter, boolean includeAllowableActions,
	                                  boolean includeACL, ObjectInfoHandler objectInfos) {
		log.debug("getObjectByPath({}, {}, {})", new Object[]{folderPath, filter, includeAllowableActions});

		// split filter
		Set<String> filterCollection = splitFilter(filter);

		// check path
		if (folderPath == null || !folderPath.startsWith("/")) {
			throw new CmisInvalidArgumentException("Invalid folder path!");
		}

		// get the document or folder
		Node node = getNode(folderPath);

		return compileObjectType(context, node, filterCollection, includeAllowableActions, includeACL, objectInfos);
	}

	// --- helper methods ---

	/**
	 * Gather the children of a folder.
	 */
	private void gatherDescendants(CallContext context, Folder fld, List<ObjectInFolderContainer> list, boolean foldersOnly, int depth,
	                               Set<String> filter, boolean includeAllowableActions, boolean includePathSegments, ObjectInfoHandler objectInfos)
			throws AccessDeniedException, PathNotFoundException, RepositoryException, DatabaseException {
		// iterate through children
		for (Folder child : OKMFolder.getInstance().getChildren(null, fld.getPath())) {
			// add to list
			ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
			objectInFolder.setObject(compileObjectType(context, child, filter, includeAllowableActions, false, objectInfos));

			if (includePathSegments) {
				objectInFolder.setPathSegment(PathUtils.getName(child.getPath()));
			}

			ObjectInFolderContainerImpl container = new ObjectInFolderContainerImpl();
			container.setObject(objectInFolder);

			list.add(container);

			// move to next level
			if (depth != 1) {
				container.setChildren(new ArrayList<ObjectInFolderContainer>());
				gatherDescendants(context, child, container.getChildren(), foldersOnly, depth - 1, filter, includeAllowableActions,
						includePathSegments, objectInfos);
			}
		}

		// folders only?
		if (!foldersOnly) {
			for (Document child : OKMDocument.getInstance().getChildren(null, fld.getPath())) {
				// add to list
				ObjectInFolderDataImpl objectInFolder = new ObjectInFolderDataImpl();
				objectInFolder.setObject(compileObjectType(context, child, filter, includeAllowableActions, false, objectInfos));

				if (includePathSegments) {
					objectInFolder.setPathSegment(PathUtils.getName(child.getPath()));
				}

				ObjectInFolderContainerImpl container = new ObjectInFolderContainerImpl();
				container.setObject(objectInFolder);

				list.add(container);
			}
		}
	}

	/**
	 * Compiles an object type object from a file or folder.
	 */
	private ObjectData compileObjectType(CallContext context, Node node, Set<String> filter, boolean includeAllowableActions,
	                                     boolean includeAcl, ObjectInfoHandler objectInfos) {
		ObjectDataImpl result = new ObjectDataImpl();
		ObjectInfoImpl objectInfo = new ObjectInfoImpl();

		result.setProperties(compileProperties(node, filter, objectInfo));

		if (includeAllowableActions) {
			result.setAllowableActions(compileAllowableActions(node));
		}

		if (includeAcl) {
			result.setAcl(compileAcl(node));
			result.setIsExactAcl(true);
		}

		if (context.isObjectInfoRequired()) {
			objectInfo.setObject(result);
			objectInfos.addObjectInfo(objectInfo);
		}

		return result;
	}

	/**
	 * Gathers all base properties of a file or folder.
	 */
	private Properties compileProperties(Node node, Set<String> orgfilter, ObjectInfoImpl objectInfo) {
		// copy filter
		Set<String> filter = (orgfilter == null ? null : new HashSet<String>(orgfilter));

		// find base type
		String typeId = null;

		if (node instanceof Folder) {
			typeId = CmisTypeManager.FOLDER_TYPE_ID;
			objectInfo.setBaseType(BaseTypeId.CMIS_FOLDER);
			objectInfo.setTypeId(typeId);
			objectInfo.setContentType(null);
			objectInfo.setFileName(null);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(false);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(true);
			objectInfo.setSupportsFolderTree(true);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		} else {
			typeId = CmisTypeManager.DOCUMENT_TYPE_ID;
			objectInfo.setBaseType(BaseTypeId.CMIS_DOCUMENT);
			objectInfo.setTypeId(typeId);
			objectInfo.setHasAcl(true);
			objectInfo.setHasContent(true);
			objectInfo.setHasParent(true);
			objectInfo.setVersionSeriesId(null);
			objectInfo.setIsCurrentVersion(true);
			objectInfo.setRelationshipSourceIds(null);
			objectInfo.setRelationshipTargetIds(null);
			objectInfo.setRenditionInfos(null);
			objectInfo.setSupportsDescendants(false);
			objectInfo.setSupportsFolderTree(false);
			objectInfo.setSupportsPolicies(false);
			objectInfo.setSupportsRelationships(false);
			objectInfo.setWorkingCopyId(null);
			objectInfo.setWorkingCopyOriginalId(null);
		}

		// let's do it
		try {
			PropertiesImpl result = new PropertiesImpl();

			// id
			String id = node.getPath();
			addPropertyId(result, typeId, filter, PropertyIds.OBJECT_ID, id);
			objectInfo.setId(id);

			// name
			String name = PathUtils.getName(node.getPath());
			addPropertyString(result, typeId, filter, PropertyIds.NAME, name);
			objectInfo.setName(name);

			// created and modified by
			addPropertyString(result, typeId, filter, PropertyIds.CREATED_BY, USER_UNKNOWN);
			addPropertyString(result, typeId, filter, PropertyIds.LAST_MODIFIED_BY, USER_UNKNOWN);
			objectInfo.setCreatedBy(USER_UNKNOWN);

			// creation and modification date
			GregorianCalendar lastModified = millisToCalendar(node.getCreated().getTimeInMillis());
			addPropertyDateTime(result, typeId, filter, PropertyIds.CREATION_DATE, lastModified);
			addPropertyDateTime(result, typeId, filter, PropertyIds.LAST_MODIFICATION_DATE, lastModified);
			objectInfo.setCreationDate(lastModified);
			objectInfo.setLastModificationDate(lastModified);

			// change token - always null
			addPropertyString(result, typeId, filter, PropertyIds.CHANGE_TOKEN, null);

			// CMIS 1.1 properties
			addPropertyString(result, typeId, filter, PropertyIds.DESCRIPTION, null);
			addPropertyIdList(result, typeId, filter, PropertyIds.SECONDARY_OBJECT_TYPE_IDS, null);

			// directory or file
			if (node instanceof Folder) {
				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_FOLDER.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, CmisTypeManager.FOLDER_TYPE_ID);
				addPropertyString(result, typeId, filter, PropertyIds.PATH, node.getPath());

				// folder properties
				if (ROOT_ID.equals(node.getPath())) {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, null);
					objectInfo.setHasParent(false);
				} else {
					addPropertyId(result, typeId, filter, PropertyIds.PARENT_ID, PathUtils.getParent(node.getPath()));
					objectInfo.setHasParent(true);
				}

				addPropertyIdList(result, typeId, filter, PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS, null);
			} else {
				Document doc = (Document) node;

				// base type and type name
				addPropertyId(result, typeId, filter, PropertyIds.BASE_TYPE_ID, BaseTypeId.CMIS_DOCUMENT.value());
				addPropertyId(result, typeId, filter, PropertyIds.OBJECT_TYPE_ID, CmisTypeManager.DOCUMENT_TYPE_ID);

				// file properties
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_IMMUTABLE, false);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_MAJOR_VERSION, true);
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_LATEST_MAJOR_VERSION, true);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_LABEL, PathUtils.getName(node.getPath()));
				addPropertyId(result, typeId, filter, PropertyIds.VERSION_SERIES_ID, node.getUuid());
				addPropertyBoolean(result, typeId, filter, PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, false);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, null);
				addPropertyString(result, typeId, filter, PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, null);
				addPropertyString(result, typeId, filter, PropertyIds.CHECKIN_COMMENT, "");

				addPropertyInteger(result, typeId, filter, PropertyIds.CONTENT_STREAM_LENGTH, doc.getActualVersion().getSize());
				addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_MIME_TYPE, doc.getMimeType());
				addPropertyString(result, typeId, filter, PropertyIds.CONTENT_STREAM_FILE_NAME, PathUtils.getName(doc.getPath()));

				objectInfo.setHasContent(true);
				objectInfo.setContentType(doc.getMimeType());
				objectInfo.setFileName(PathUtils.getName(node.getPath()));

				addPropertyId(result, typeId, filter, PropertyIds.CONTENT_STREAM_ID, null);
			}

			// read custom properties
			// readCustomProperties(node, result, filter, objectInfo);

			if (filter != null) {
				if (!filter.isEmpty()) {
					log.debug("Unknown filter properties: {}", filter);
				}
			}

			return result;
		} catch (Exception e) {
			if (e instanceof CmisBaseException) {
				throw (CmisBaseException) e;
			}

			throw new CmisRuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Reads and adds properties.
	 */
	private void readCustomProperties(Node node, PropertiesImpl properties, Set<String> filter, ObjectInfoImpl objectInfo) {
		ObjectData obj = new ObjectDataImpl();

		if (obj.getProperties() != null) {
			// add it to properties
			for (PropertyData<?> prop : obj.getProperties().getPropertyList()) {
				// overwrite object info
				if (prop instanceof PropertyString) {
					String firstValueStr = ((PropertyString) prop).getFirstValue();
					if (PropertyIds.NAME.equals(prop.getId())) {
						objectInfo.setName(firstValueStr);
					} else if (PropertyIds.OBJECT_TYPE_ID.equals(prop.getId())) {
						objectInfo.setTypeId(firstValueStr);
					} else if (PropertyIds.CREATED_BY.equals(prop.getId())) {
						objectInfo.setCreatedBy(firstValueStr);
					} else if (PropertyIds.CONTENT_STREAM_MIME_TYPE.equals(prop.getId())) {
						objectInfo.setContentType(firstValueStr);
					} else if (PropertyIds.CONTENT_STREAM_FILE_NAME.equals(prop.getId())) {
						objectInfo.setFileName(firstValueStr);
					}
				}

				if (prop instanceof PropertyDateTime) {
					GregorianCalendar firstValueCal = ((PropertyDateTime) prop).getFirstValue();
					if (PropertyIds.CREATION_DATE.equals(prop.getId())) {
						objectInfo.setCreationDate(firstValueCal);
					} else if (PropertyIds.LAST_MODIFICATION_DATE.equals(prop.getId())) {
						objectInfo.setLastModificationDate(firstValueCal);
					}
				}

				// check filter
				if (filter != null) {
					if (!filter.contains(prop.getId())) {
						continue;
					} else {
						filter.remove(prop.getId());
					}
				}

				// don't overwrite id
				if (PropertyIds.OBJECT_ID.equals(prop.getId())) {
					continue;
				}

				// don't overwrite base type
				if (PropertyIds.BASE_TYPE_ID.equals(prop.getId())) {
					continue;
				}

				// add it
				properties.replaceProperty(prop);
			}
		}
	}

	/**
	 * Checks and compiles a property set that can be written to disc.
	 */
	private Properties compileProperties(String typeId, String creator, GregorianCalendar creationDate, String modifier,
	                                     Properties properties) {
		PropertiesImpl result = new PropertiesImpl();
		Set<String> addedProps = new HashSet<String>();

		if ((properties == null) || (properties.getProperties() == null)) {
			throw new CmisConstraintException("No properties!");
		}

		// get the property definitions
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// check if all required properties are there
		for (PropertyData<?> prop : properties.getProperties().values()) {
			PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

			// do we know that property?
			if (propType == null) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
			}

			// can it be set?
			if ((propType.getUpdatability() == Updatability.READONLY)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
			}

			// empty properties are invalid
			if (isEmptyProperty(prop)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' must not be empty!");
			}

			// add it
			result.addProperty(prop);
			addedProps.add(prop.getId());
		}

		// check if required properties are missing
		for (PropertyDefinition<?> propDef : type.getPropertyDefinitions().values()) {
			if (!addedProps.contains(propDef.getId()) && (propDef.getUpdatability() != Updatability.READONLY)) {
				if (!addPropertyDefault(result, propDef) && propDef.isRequired()) {
					throw new CmisConstraintException("Property '" + propDef.getId() + "' is required!");
				}
			}
		}

		addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
		addPropertyString(result, typeId, null, PropertyIds.CREATED_BY, creator);
		addPropertyDateTime(result, typeId, null, PropertyIds.CREATION_DATE, creationDate);
		addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY, modifier);

		return result;
	}

	/**
	 * Checks and updates a property set that can be written to disc.
	 */
	private Properties updateProperties(String typeId, String creator, GregorianCalendar creationDate, String modifier,
	                                    Properties oldProperties, Properties properties) {
		PropertiesImpl result = new PropertiesImpl();

		if (properties == null) {
			throw new CmisConstraintException("No properties!");
		}

		// get the property definitions
		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		// copy old properties
		for (PropertyData<?> prop : oldProperties.getProperties().values()) {
			PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

			// do we know that property?
			if (propType == null) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
			}

			// only add read/write properties
			if ((propType.getUpdatability() != Updatability.READWRITE)) {
				continue;
			}

			result.addProperty(prop);
		}

		// update properties
		for (PropertyData<?> prop : properties.getProperties().values()) {
			PropertyDefinition<?> propType = type.getPropertyDefinitions().get(prop.getId());

			// do we know that property?
			if (propType == null) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is unknown!");
			}

			// can it be set?
			if ((propType.getUpdatability() == Updatability.READONLY)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' is readonly!");
			}

			if ((propType.getUpdatability() == Updatability.ONCREATE)) {
				throw new CmisConstraintException("Property '" + prop.getId() + "' can only be set on create!");
			}

			// default or value
			if (isEmptyProperty(prop)) {
				addPropertyDefault(result, propType);
			} else {
				result.addProperty(prop);
			}
		}

		addPropertyId(result, typeId, null, PropertyIds.OBJECT_TYPE_ID, typeId);
		addPropertyString(result, typeId, null, PropertyIds.CREATED_BY, creator);
		addPropertyDateTime(result, typeId, null, PropertyIds.CREATION_DATE, creationDate);
		addPropertyString(result, typeId, null, PropertyIds.LAST_MODIFIED_BY, modifier);

		return result;
	}

	private static boolean isEmptyProperty(PropertyData<?> prop) {
		if ((prop == null) || (prop.getValues() == null)) {
			return true;
		}

		return prop.getValues().isEmpty();
	}

	private void addPropertyId(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyIdList(PropertiesImpl props, String typeId, Set<String> filter, String id, List<String> value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIdImpl(id, value));
	}

	private void addPropertyString(PropertiesImpl props, String typeId, Set<String> filter, String id, String value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyStringImpl(id, value));
	}

	private void addPropertyInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, long value) {
		addPropertyBigInteger(props, typeId, filter, id, BigInteger.valueOf(value));
	}

	private void addPropertyBigInteger(PropertiesImpl props, String typeId, Set<String> filter, String id, BigInteger value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyIntegerImpl(id, value));
	}

	private void addPropertyBoolean(PropertiesImpl props, String typeId, Set<String> filter, String id, boolean value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyBooleanImpl(id, value));
	}

	private void addPropertyDateTime(PropertiesImpl props, String typeId, Set<String> filter, String id, GregorianCalendar value) {
		if (!checkAddProperty(props, typeId, filter, id)) {
			return;
		}

		props.addProperty(new PropertyDateTimeImpl(id, value));
	}

	private boolean checkAddProperty(Properties properties, String typeId, Set<String> filter, String id) {
		if ((properties == null) || (properties.getProperties() == null)) {
			throw new IllegalArgumentException("Properties must not be null!");
		}

		if (id == null) {
			throw new IllegalArgumentException("Id must not be null!");
		}

		TypeDefinition type = types.getType(typeId);
		if (type == null) {
			throw new IllegalArgumentException("Unknown type: " + typeId);
		}
		if (!type.getPropertyDefinitions().containsKey(id)) {
			throw new IllegalArgumentException("Unknown property: " + id);
		}

		String queryName = type.getPropertyDefinitions().get(id).getQueryName();

		if ((queryName != null) && (filter != null)) {
			if (!filter.contains(queryName)) {
				return false;
			} else {
				filter.remove(queryName);
			}
		}

		return true;
	}

	/**
	 * Adds the default value of property if defined.
	 */
	@SuppressWarnings("unchecked")
	private static boolean addPropertyDefault(PropertiesImpl props, PropertyDefinition<?> propDef) {
		if ((props == null) || (props.getProperties() == null)) {
			throw new IllegalArgumentException("Props must not be null!");
		}

		if (propDef == null) {
			return false;
		}

		List<?> defaultValue = propDef.getDefaultValue();
		if ((defaultValue != null) && (!defaultValue.isEmpty())) {
			switch (propDef.getPropertyType()) {
				case BOOLEAN:
					props.addProperty(new PropertyBooleanImpl(propDef.getId(), (List<Boolean>) defaultValue));
					break;
				case DATETIME:
					props.addProperty(new PropertyDateTimeImpl(propDef.getId(), (List<GregorianCalendar>) defaultValue));
					break;
				case DECIMAL:
					props.addProperty(new PropertyDecimalImpl(propDef.getId(), (List<BigDecimal>) defaultValue));
					break;
				case HTML:
					props.addProperty(new PropertyHtmlImpl(propDef.getId(), (List<String>) defaultValue));
					break;
				case ID:
					props.addProperty(new PropertyIdImpl(propDef.getId(), (List<String>) defaultValue));
					break;
				case INTEGER:
					props.addProperty(new PropertyIntegerImpl(propDef.getId(), (List<BigInteger>) defaultValue));
					break;
				case STRING:
					props.addProperty(new PropertyStringImpl(propDef.getId(), (List<String>) defaultValue));
					break;
				case URI:
					props.addProperty(new PropertyUriImpl(propDef.getId(), (List<String>) defaultValue));
					break;
				default:
					throw new RuntimeException("Unknown datatype! Spec change?");
			}

			return true;
		}

		return false;
	}

	/**
	 * Compiles the allowable actions for a folder or document.
	 */
	private AllowableActions compileAllowableActions(Node node) {
		boolean isRoot = ROOT_ID.equals(node.getPath());
		Set<Action> aas = EnumSet.noneOf(Action.class);

		addAction(aas, Action.CAN_GET_OBJECT_PARENTS, !isRoot);
		addAction(aas, Action.CAN_GET_PROPERTIES, true);
		addAction(aas, Action.CAN_UPDATE_PROPERTIES, checkPermission(node, Permission.WRITE));
		addAction(aas, Action.CAN_MOVE_OBJECT, checkPermission(node, Permission.WRITE) && !isRoot);
		addAction(aas, Action.CAN_DELETE_OBJECT, checkPermission(node, Permission.DELETE) && !isRoot);
		addAction(aas, Action.CAN_GET_ACL, true);

		if (node instanceof Folder) {
			addAction(aas, Action.CAN_GET_DESCENDANTS, true);
			addAction(aas, Action.CAN_GET_CHILDREN, true);
			addAction(aas, Action.CAN_GET_FOLDER_PARENT, !isRoot);
			addAction(aas, Action.CAN_GET_FOLDER_TREE, true);
			addAction(aas, Action.CAN_CREATE_DOCUMENT, checkPermission(node, Permission.WRITE));
			addAction(aas, Action.CAN_CREATE_FOLDER, checkPermission(node, Permission.WRITE));
			addAction(aas, Action.CAN_DELETE_TREE, checkPermission(node, Permission.DELETE));
		} else {
			addAction(aas, Action.CAN_GET_CONTENT_STREAM, true);
			addAction(aas, Action.CAN_SET_CONTENT_STREAM, checkPermission(node, Permission.WRITE));
			addAction(aas, Action.CAN_DELETE_CONTENT_STREAM, checkPermission(node, Permission.WRITE));
			addAction(aas, Action.CAN_GET_ALL_VERSIONS, true);
		}

		AllowableActionsImpl result = new AllowableActionsImpl();
		result.setAllowableActions(aas);

		return result;
	}

	private static boolean checkPermission(Node node, int perm) {
		return (node.getPermissions() & perm) == perm;
	}

	private static boolean checkPermission(int permissions, int perm) {
		return (permissions & perm) == perm;
	}

	private static void addAction(Set<Action> aas, Action action, boolean condition) {
		if (condition) {
			aas.add(action);
		}
	}

	/**
	 * Compiles the ACL for a file or folder.
	 */
	private Acl compileAcl(Node node) {
		AccessControlListImpl result = new AccessControlListImpl();
		result.setAces(new ArrayList<Ace>());

		try {
			for (Map.Entry<String, Integer> ue : OKMAuth.getInstance().getGrantedUsers(null, node.getUuid()).entrySet()) {
				// create principal
				AccessControlPrincipalDataImpl principal = new AccessControlPrincipalDataImpl();
				principal.setPrincipalId(ue.getKey());

				// create ACE
				AccessControlEntryImpl entry = new AccessControlEntryImpl();
				entry.setPrincipal(principal);
				entry.setPermissions(new ArrayList<String>());
				entry.getPermissions().add(CMIS_READ);

				if (checkPermission(ue.getValue(), Permission.WRITE)) {
					entry.getPermissions().add(CMIS_WRITE);
				}

				if (checkPermission(ue.getValue(), Permission.DELETE)) {
					entry.getPermissions().add(CMIS_DELETE);
				}

				entry.setDirect(true);

				// add ACE
				result.getAces().add(entry);
			}

			return result;
		} catch (Exception e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}

	/**
	 * Writes the properties for a document or folder.
	 */
	private static void writePropertiesFile(Node node, Properties properties) {
		// create object
		ObjectDataImpl object = new ObjectDataImpl();
		object.setProperties(properties);

		try {
			// Use setProperties
		} catch (Exception e) {
			throw new CmisStorageException("Couldn't store properties!", e);
		}
	}

	// --- internal stuff ---

	/**
	 * Converts milliseconds into a calendar object.
	 */
	private static GregorianCalendar millisToCalendar(long millis) {
		GregorianCalendar result = new GregorianCalendar();
		result.setTimeZone(TimeZone.getTimeZone("GMT"));
		result.setTimeInMillis((long) (Math.ceil((double) millis / 1000) * 1000));

		return result;
	}

	/**
	 * Checks if the given name is valid for a file system.
	 */
	private static boolean isValidName(String name) {
		if ((name == null) || (name.length() == 0) || (name.indexOf(File.separatorChar) != -1)
				|| (name.indexOf(File.pathSeparatorChar) != -1)) {
			return false;
		}

		return true;
	}

	/**
	 * Splits a filter statement into a collection of properties. If <code>filter</code> is <code>null</code>, empty or
	 * one of the properties
	 * is '*' , an empty collection will be returned.
	 */
	private static Set<String> splitFilter(String filter) {
		if (filter == null) {
			return null;
		}

		if (filter.trim().length() == 0) {
			return null;
		}

		Set<String> result = new HashSet<String>();
		for (String s : filter.split(",")) {
			s = s.trim();
			if (s.equals("*")) {
				return null;
			} else if (s.length() > 0) {
				result.add(s);
			}
		}

		// set a few base properties
		// query name == id (for base type properties)
		result.add(PropertyIds.OBJECT_ID);
		result.add(PropertyIds.OBJECT_TYPE_ID);
		result.add(PropertyIds.BASE_TYPE_ID);

		return result;
	}

	/**
	 * Gets the type id from a set of properties.
	 */
	private static String getTypeId(Properties properties) {
		PropertyData<?> typeProperty = properties.getProperties().get(PropertyIds.OBJECT_TYPE_ID);
		if (!(typeProperty instanceof PropertyId)) {
			throw new CmisInvalidArgumentException("Type id must be set!");
		}

		String typeId = ((PropertyId) typeProperty).getFirstValue();
		if (typeId == null) {
			throw new CmisInvalidArgumentException("Type id must be set!");
		}

		return typeId;
	}

	/**
	 * Returns the first value of an id property.
	 */
	private static String getIdProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);

		if (!(property instanceof PropertyId)) {
			return null;
		}

		return ((PropertyId) property).getFirstValue();
	}

	/**
	 * Returns the first value of an string property.
	 */
	private static String getStringProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);

		if (!(property instanceof PropertyString)) {
			return null;
		}

		return ((PropertyString) property).getFirstValue();
	}

	/**
	 * Returns the first value of an datetime property.
	 */
	private static GregorianCalendar getDateTimeProperty(Properties properties, String name) {
		PropertyData<?> property = properties.getProperties().get(name);

		if (!(property instanceof PropertyDateTime)) {
			return null;
		}

		return ((PropertyDateTime) property).getFirstValue();
	}

	/**
	 * Returns the Node object by id or throws an appropriate exception.
	 */
	private Node getNode(String id) {
		log.debug("getNode({})", id);

		try {
			if (OKMFolder.getInstance().isValid(null, id)) {
				return OKMFolder.getInstance().getProperties(null, id);
			} else if (OKMDocument.getInstance().isValid(null, id)) {
				return OKMDocument.getInstance().getProperties(null, id);
			} else {
				throw new CmisObjectNotFoundException(id);
			}
		} catch (AccessDeniedException e) {
			throw new CmisPermissionDeniedException(e.getMessage(), e);
		} catch (PathNotFoundException e) {
			throw new CmisObjectNotFoundException(e.getMessage(), e);
		} catch (RepositoryException e) {
			throw new CmisStorageException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw new CmisStorageException(e.getMessage(), e);
		}
	}
}
