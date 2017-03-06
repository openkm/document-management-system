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

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.definitions.PropertyDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinition;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionContainer;
import org.apache.chemistry.opencmis.commons.definitions.TypeDefinitionList;
import org.apache.chemistry.opencmis.commons.enums.*;
import org.apache.chemistry.opencmis.commons.exceptions.CmisInvalidArgumentException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.impl.WSConverter;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;
import org.apache.chemistry.opencmis.commons.server.CallContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type Manager
 *
 * @author pavila
 */
public class CmisTypeManager {
	private static Logger log = LoggerFactory.getLogger(CmisTypeManager.class);
	public static final String DOCUMENT_TYPE_ID = BaseTypeId.CMIS_DOCUMENT.value();
	public static final String FOLDER_TYPE_ID = BaseTypeId.CMIS_FOLDER.value();
	public static final String RELATIONSHIP_TYPE_ID = BaseTypeId.CMIS_RELATIONSHIP.value();
	public static final String POLICY_TYPE_ID = BaseTypeId.CMIS_POLICY.value();
	public static final String ITEM_TYPE_ID = BaseTypeId.CMIS_ITEM.value();
	public static final String SECONDARY_TYPE_ID = BaseTypeId.CMIS_SECONDARY.value();
	private static final String NAMESPACE = "http://chemistry.apache.org/opencmis/fileshare";
	private Map<String, TypeDefinitionContainerImpl> types;
	private List<TypeDefinitionContainer> typesList;

	public CmisTypeManager() {
		setup();
	}

	/**
	 * Creates the base types.
	 */
	private void setup() {
		types = new HashMap<String, TypeDefinitionContainerImpl>();
		typesList = new ArrayList<TypeDefinitionContainer>();

		// type mutability
		TypeMutabilityImpl typeMutability = new TypeMutabilityImpl();
		typeMutability.setCanCreate(false);
		typeMutability.setCanUpdate(false);
		typeMutability.setCanDelete(false);

		// folder type
		FolderTypeDefinitionImpl folderType = new FolderTypeDefinitionImpl();
		folderType.setBaseTypeId(BaseTypeId.CMIS_FOLDER);
		folderType.setIsControllableAcl(false);
		folderType.setIsControllablePolicy(false);
		folderType.setIsCreatable(true);
		folderType.setDescription("Folder");
		folderType.setDisplayName("Folder");
		folderType.setIsFileable(true);
		folderType.setIsFulltextIndexed(false);
		folderType.setIsIncludedInSupertypeQuery(true);
		folderType.setLocalName("Folder");
		folderType.setLocalNamespace(NAMESPACE);
		folderType.setIsQueryable(false);
		folderType.setQueryName("cmis:folder");
		folderType.setId(FOLDER_TYPE_ID);
		folderType.setTypeMutability(typeMutability);

		addBasePropertyDefinitions(folderType);
		addFolderPropertyDefinitions(folderType);

		addTypeInteral(folderType);

		// document type
		DocumentTypeDefinitionImpl documentType = new DocumentTypeDefinitionImpl();
		documentType.setBaseTypeId(BaseTypeId.CMIS_DOCUMENT);
		documentType.setIsControllableAcl(false);
		documentType.setIsControllablePolicy(false);
		documentType.setIsCreatable(true);
		documentType.setDescription("Document");
		documentType.setDisplayName("Document");
		documentType.setIsFileable(true);
		documentType.setIsFulltextIndexed(false);
		documentType.setIsIncludedInSupertypeQuery(true);
		documentType.setLocalName("Document");
		documentType.setLocalNamespace(NAMESPACE);
		documentType.setIsQueryable(false);
		documentType.setQueryName("cmis:document");
		documentType.setId(DOCUMENT_TYPE_ID);
		documentType.setTypeMutability(typeMutability);

		documentType.setIsVersionable(false);
		documentType.setContentStreamAllowed(ContentStreamAllowed.ALLOWED);

		addBasePropertyDefinitions(documentType);
		addDocumentPropertyDefinitions(documentType);

		addTypeInteral(documentType);

		// relationship types
		RelationshipTypeDefinitionImpl relationshipType = new RelationshipTypeDefinitionImpl();
		relationshipType.setBaseTypeId(BaseTypeId.CMIS_RELATIONSHIP);
		relationshipType.setIsControllableAcl(false);
		relationshipType.setIsControllablePolicy(false);
		relationshipType.setIsCreatable(false);
		relationshipType.setDescription("Relationship");
		relationshipType.setDisplayName("Relationship");
		relationshipType.setIsFileable(false);
		relationshipType.setIsIncludedInSupertypeQuery(true);
		relationshipType.setLocalName("Relationship");
		relationshipType.setLocalNamespace(NAMESPACE);
		relationshipType.setIsQueryable(false);
		relationshipType.setQueryName("cmis:relationship");
		relationshipType.setId(RELATIONSHIP_TYPE_ID);
		relationshipType.setTypeMutability(typeMutability);

		addBasePropertyDefinitions(relationshipType);

		// not supported - don't expose it
		// addTypeInteral(relationshipType);

		// policy type
		PolicyTypeDefinitionImpl policyType = new PolicyTypeDefinitionImpl();
		policyType.setBaseTypeId(BaseTypeId.CMIS_POLICY);
		policyType.setIsControllableAcl(false);
		policyType.setIsControllablePolicy(false);
		policyType.setIsCreatable(false);
		policyType.setDescription("Policy");
		policyType.setDisplayName("Policy");
		policyType.setIsFileable(false);
		policyType.setIsIncludedInSupertypeQuery(true);
		policyType.setLocalName("Policy");
		policyType.setLocalNamespace(NAMESPACE);
		policyType.setIsQueryable(false);
		policyType.setQueryName("cmis:policy");
		policyType.setId(POLICY_TYPE_ID);
		policyType.setTypeMutability(typeMutability);

		addBasePropertyDefinitions(policyType);

		// not supported - don't expose it
		// addTypeInteral(policyType);

		// item type
		ItemTypeDefinitionImpl itemType = new ItemTypeDefinitionImpl();
		itemType.setBaseTypeId(BaseTypeId.CMIS_ITEM);
		itemType.setIsControllableAcl(false);
		itemType.setIsControllablePolicy(false);
		itemType.setIsCreatable(true);
		itemType.setDescription("Item");
		itemType.setDisplayName("Item");
		itemType.setIsFileable(true);
		itemType.setIsIncludedInSupertypeQuery(true);
		itemType.setLocalName("Item");
		itemType.setLocalNamespace(NAMESPACE);
		itemType.setIsQueryable(false);
		itemType.setQueryName("cmis:item");
		itemType.setId(ITEM_TYPE_ID);
		itemType.setTypeMutability(typeMutability);

		addBasePropertyDefinitions(itemType);

		// not supported - don't expose it
		// addTypeInteral(itemType);

		// secondary type
		SecondaryTypeDefinitionImpl secondaryType = new SecondaryTypeDefinitionImpl();
		secondaryType.setBaseTypeId(BaseTypeId.CMIS_ITEM);
		secondaryType.setIsControllableAcl(false);
		secondaryType.setIsControllablePolicy(false);
		secondaryType.setIsCreatable(true);
		secondaryType.setDescription("Secondary");
		secondaryType.setDisplayName("Secondary");
		secondaryType.setIsFileable(false);
		secondaryType.setIsIncludedInSupertypeQuery(true);
		secondaryType.setLocalName("Secondary");
		secondaryType.setLocalNamespace(NAMESPACE);
		secondaryType.setIsQueryable(false);
		secondaryType.setQueryName("cmis:secondary");
		secondaryType.setId(SECONDARY_TYPE_ID);
		secondaryType.setTypeMutability(typeMutability);

		addBasePropertyDefinitions(secondaryType);

		// not supported - don't expose it
		// addTypeInteral(secondaryType);
	}

	/**
	 * Base properties.
	 */
	private static void addBasePropertyDefinitions(AbstractTypeDefinition type) {
		type.addPropertyDefinition(createPropDef(PropertyIds.BASE_TYPE_ID, "Base Type Id", "Base Type Id", PropertyType.ID,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.OBJECT_ID, "Object Id", "Object Id", PropertyType.ID, Cardinality.SINGLE,
				Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.OBJECT_TYPE_ID, "Type Id", "Type Id", PropertyType.ID, Cardinality.SINGLE,
				Updatability.ONCREATE, false, true));

		type.addPropertyDefinition(createPropDef(PropertyIds.NAME, "Name", "Name", PropertyType.STRING, Cardinality.SINGLE,
				Updatability.READWRITE, false, true));

		type.addPropertyDefinition(createPropDef(PropertyIds.DESCRIPTION, "Description", "Description", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CREATED_BY, "Created By", "Created By", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CREATION_DATE, "Creation Date", "Creation Date", PropertyType.DATETIME,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.LAST_MODIFIED_BY, "Last Modified By", "Last Modified By", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.LAST_MODIFICATION_DATE, "Last Modification Date", "Last Modification Date",
				PropertyType.DATETIME, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CHANGE_TOKEN, "Change Token", "Change Token", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, "Secondary Type Ids", "Secondary Type Ids",
				PropertyType.ID, Cardinality.MULTI, Updatability.READONLY, false, false));
	}

	/**
	 * Folder related properties.
	 */
	private static void addFolderPropertyDefinitions(FolderTypeDefinitionImpl type) {
		type.addPropertyDefinition(createPropDef(PropertyIds.PARENT_ID, "Parent Id", "Parent Id", PropertyType.ID, Cardinality.SINGLE,
				Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.ALLOWED_CHILD_OBJECT_TYPE_IDS, "Allowed Child Object Type Ids",
				"Allowed Child Object Type Ids", PropertyType.ID, Cardinality.MULTI, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.PATH, "Path", "Path", PropertyType.STRING, Cardinality.SINGLE,
				Updatability.READONLY, false, false));
	}

	/**
	 * Document related properties.
	 */
	private static void addDocumentPropertyDefinitions(DocumentTypeDefinitionImpl type) {
		type.addPropertyDefinition(createPropDef(PropertyIds.IS_IMMUTABLE, "Is Immutable", "Is Immutable", PropertyType.BOOLEAN,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.IS_LATEST_VERSION, "Is Latest Version", "Is Latest Version",
				PropertyType.BOOLEAN, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.IS_MAJOR_VERSION, "Is Major Version", "Is Major Version",
				PropertyType.BOOLEAN, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.IS_LATEST_MAJOR_VERSION, "Is Latest Major Version", "Is Latest Major Version",
				PropertyType.BOOLEAN, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.VERSION_LABEL, "Version Label", "Version Label", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, true));

		type.addPropertyDefinition(createPropDef(PropertyIds.VERSION_SERIES_ID, "Version Series Id", "Version Series Id", PropertyType.ID,
				Cardinality.SINGLE, Updatability.READONLY, false, true));

		type.addPropertyDefinition(createPropDef(PropertyIds.IS_VERSION_SERIES_CHECKED_OUT, "Is Verison Series Checked Out",
				"Is Verison Series Checked Out", PropertyType.BOOLEAN, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.VERSION_SERIES_CHECKED_OUT_ID, "Version Series Checked Out Id",
				"Version Series Checked Out Id", PropertyType.ID, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.VERSION_SERIES_CHECKED_OUT_BY, "Version Series Checked Out By",
				"Version Series Checked Out By", PropertyType.STRING, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CHECKIN_COMMENT, "Checkin Comment", "Checkin Comment", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CONTENT_STREAM_LENGTH, "Content Stream Length", "Content Stream Length",
				PropertyType.INTEGER, Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CONTENT_STREAM_MIME_TYPE, "MIME Type", "MIME Type", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CONTENT_STREAM_FILE_NAME, "Filename", "Filename", PropertyType.STRING,
				Cardinality.SINGLE, Updatability.READONLY, false, false));

		type.addPropertyDefinition(createPropDef(PropertyIds.CONTENT_STREAM_ID, "Content Stream Id", "Content Stream Id", PropertyType.ID,
				Cardinality.SINGLE, Updatability.READONLY, false, false));
	}

	/**
	 * Creates a property definition object.
	 */
	private static PropertyDefinition<?> createPropDef(String id, String displayName, String description, PropertyType datatype,
	                                                   Cardinality cardinality, Updatability updateability, boolean inherited, boolean required) {
		AbstractPropertyDefinition<?> result = null;

		switch (datatype) {
			case BOOLEAN:
				result = new PropertyBooleanDefinitionImpl();
				break;
			case DATETIME:
				result = new PropertyDateTimeDefinitionImpl();
				break;
			case DECIMAL:
				result = new PropertyDecimalDefinitionImpl();
				break;
			case HTML:
				result = new PropertyHtmlDefinitionImpl();
				break;
			case ID:
				result = new PropertyIdDefinitionImpl();
				break;
			case INTEGER:
				result = new PropertyIntegerDefinitionImpl();
				break;
			case STRING:
				result = new PropertyStringDefinitionImpl();
				break;
			case URI:
				result = new PropertyUriDefinitionImpl();
				break;
			default:
				throw new RuntimeException("Unknown datatype! Spec change?");
		}

		result.setId(id);
		result.setLocalName(id);
		result.setDisplayName(displayName);
		result.setDescription(description);
		result.setPropertyType(datatype);
		result.setCardinality(cardinality);
		result.setUpdatability(updateability);
		result.setIsInherited(inherited);
		result.setIsRequired(required);
		result.setIsQueryable(false);
		result.setIsOrderable(false);
		result.setQueryName(id);

		return result;
	}

	/**
	 * Adds a type to collection with inheriting base type properties.
	 */
	public boolean addType(TypeDefinition type) {
		if (type == null) {
			return false;
		}

		if (type.getBaseTypeId() == null) {
			return false;
		}

		// find base type
		TypeDefinition baseType = null;

		if (type.getBaseTypeId() == BaseTypeId.CMIS_DOCUMENT) {
			baseType = copyTypeDefintion(types.get(DOCUMENT_TYPE_ID).getTypeDefinition());
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_FOLDER) {
			baseType = copyTypeDefintion(types.get(FOLDER_TYPE_ID).getTypeDefinition());
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_ITEM) {
			baseType = copyTypeDefintion(types.get(ITEM_TYPE_ID).getTypeDefinition());
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_RELATIONSHIP) {
			baseType = copyTypeDefintion(types.get(RELATIONSHIP_TYPE_ID).getTypeDefinition());
		} else if (type.getBaseTypeId() == BaseTypeId.CMIS_POLICY) {
			baseType = copyTypeDefintion(types.get(POLICY_TYPE_ID).getTypeDefinition());
		} else {
			return false;
		}

		AbstractTypeDefinition newType = (AbstractTypeDefinition) copyTypeDefintion(type);

		// copy property definition
		for (PropertyDefinition<?> propDef : baseType.getPropertyDefinitions().values()) {
			((AbstractPropertyDefinition<?>) propDef).setIsInherited(true);
			newType.addPropertyDefinition(propDef);
		}

		// add it
		addTypeInteral(newType);

		log.info("Added type '" + newType.getId() + "'.");
		return true;
	}

	/**
	 * Adds a type to collection.
	 */
	private void addTypeInteral(AbstractTypeDefinition type) {
		if (type == null) {
			return;
		}

		if (types.containsKey(type.getId())) {
			// can't overwrite a type
			return;
		}

		TypeDefinitionContainerImpl tc = new TypeDefinitionContainerImpl();
		tc.setTypeDefinition(type);

		// add to parent
		if (type.getParentTypeId() != null) {
			TypeDefinitionContainerImpl tdc = types.get(type.getParentTypeId());
			if (tdc != null) {
				if (tdc.getChildren() == null) {
					tdc.setChildren(new ArrayList<TypeDefinitionContainer>());
				}
				tdc.getChildren().add(tc);
			}
		}

		types.put(type.getId(), tc);
		typesList.add(tc);
	}

	/**
	 * CMIS getTypesChildren.
	 */
	public TypeDefinitionList getTypesChildren(CallContext context, String typeId, boolean includePropertyDefinitions, BigInteger maxItems,
	                                           BigInteger skipCount) {
		TypeDefinitionListImpl result = new TypeDefinitionListImpl(new ArrayList<TypeDefinition>());

		int skip = (skipCount == null ? 0 : skipCount.intValue());
		if (skip < 0) {
			skip = 0;
		}

		int max = (maxItems == null ? Integer.MAX_VALUE : maxItems.intValue());
		if (max < 1) {
			return result;
		}

		if (typeId == null) {
			if (skip < 1) {
				result.getList().add(copyTypeDefintion(types.get(FOLDER_TYPE_ID).getTypeDefinition()));
				max--;
			}

			if ((skip < 2) && (max > 0)) {
				result.getList().add(copyTypeDefintion(types.get(DOCUMENT_TYPE_ID).getTypeDefinition()));
				max--;
			}

			result.setHasMoreItems((result.getList().size() + skip) < 2);
			result.setNumItems(BigInteger.valueOf(2));
		} else {
			TypeDefinitionContainer tc = types.get(typeId);

			if ((tc == null) || (tc.getChildren() == null)) {
				return result;
			}

			for (TypeDefinitionContainer child : tc.getChildren()) {
				if (skip > 0) {
					skip--;
					continue;
				}

				result.getList().add(copyTypeDefintion(child.getTypeDefinition()));

				max--;
				if (max == 0) {
					break;
				}
			}

			result.setHasMoreItems((result.getList().size() + skip) < tc.getChildren().size());
			result.setNumItems(BigInteger.valueOf(tc.getChildren().size()));
		}

		if (!includePropertyDefinitions) {
			for (TypeDefinition type : result.getList()) {
				type.getPropertyDefinitions().clear();
			}
		}

		return result;
	}

	/**
	 * CMIS getTypesDescendants.
	 */
	public List<TypeDefinitionContainer> getTypesDescendants(CallContext context, String typeId, BigInteger depth,
	                                                         Boolean includePropertyDefinitions) {
		List<TypeDefinitionContainer> result = new ArrayList<TypeDefinitionContainer>();

		// check depth
		int d = (depth == null ? -1 : depth.intValue());

		if (d == 0) {
			throw new CmisInvalidArgumentException("Depth must not be 0!");
		}

		if (typeId == null) {
			d = -1;
		}

		// set property definition flag to default value if not set
		boolean ipd = (includePropertyDefinitions == null ? false : includePropertyDefinitions.booleanValue());

		if (typeId == null) {
			result.add(getTypesDescendants(d, types.get(FOLDER_TYPE_ID), ipd));
			result.add(getTypesDescendants(d, types.get(DOCUMENT_TYPE_ID), ipd));
			// result.add(getTypesDescendants(depth,
			// fTypes.get(RELATIONSHIP_TYPE_ID), includePropertyDefinitions));
			// result.add(getTypesDescendants(depth, fTypes.get(POLICY_TYPE_ID),
			// includePropertyDefinitions));
		} else {
			TypeDefinitionContainer tc = types.get(typeId);

			if (tc != null) {
				result.add(getTypesDescendants(d, tc, ipd));
			}
		}

		return result;
	}

	/**
	 * Gathers the type descendants tree.
	 */
	private TypeDefinitionContainer getTypesDescendants(int depth, TypeDefinitionContainer tc, boolean includePropertyDefinitions) {
		TypeDefinitionContainerImpl result = new TypeDefinitionContainerImpl();
		TypeDefinition type = copyTypeDefintion(tc.getTypeDefinition());

		if (!includePropertyDefinitions) {
			type.getPropertyDefinitions().clear();
		}

		result.setTypeDefinition(type);

		if (depth != 0) {
			if (tc.getChildren() != null) {
				result.setChildren(new ArrayList<TypeDefinitionContainer>());

				for (TypeDefinitionContainer tdc : tc.getChildren()) {
					result.getChildren().add(getTypesDescendants(depth < 0 ? -1 : depth - 1, tdc, includePropertyDefinitions));
				}
			}
		}

		return result;
	}

	/**
	 * For internal use.
	 */
	public TypeDefinition getType(String typeId) {
		TypeDefinitionContainer tc = types.get(typeId);

		if (tc == null) {
			return null;
		}

		return tc.getTypeDefinition();
	}

	/**
	 * CMIS getTypeDefinition.
	 */
	public TypeDefinition getTypeDefinition(CallContext context, String typeId) {
		TypeDefinitionContainer tc = types.get(typeId);

		if (tc == null) {
			throw new CmisObjectNotFoundException("Type '" + typeId + "' is unknown!");
		}

		return copyTypeDefintion(tc.getTypeDefinition());
	}

	private static TypeDefinition copyTypeDefintion(TypeDefinition type) {
		return WSConverter.convert(WSConverter.convert(type));
	}
}
