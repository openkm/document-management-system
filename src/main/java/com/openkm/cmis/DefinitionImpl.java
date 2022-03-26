package com.openkm.cmis;

import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.ContentStreamAllowed;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.*;

import static com.openkm.cmis.CmisTypeManager.*;

public class DefinitionImpl {

	public static DocumentTypeDefinitionImpl createDocumentDefinition(TypeMutabilityImpl typeMutability){
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
		return documentType;
	}

	public static RelationshipTypeDefinitionImpl createRelationDefinition(TypeMutabilityImpl typeMutability){
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
		return relationshipType;
	}

	public static PolicyTypeDefinitionImpl createPolicyDefinition(TypeMutabilityImpl typeMutability){
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
		return policyType;
	}

	public static ItemTypeDefinitionImpl createItemDefinition(TypeMutabilityImpl typeMutability){
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
		return itemType;
	}

	public static SecondaryTypeDefinitionImpl createSecondaryDefinition(TypeMutabilityImpl typeMutability){
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
		return secondaryType;
	}

	public static FolderTypeDefinitionImpl createFolderDefinition(TypeMutabilityImpl typeMutability){
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
		return folderType;
	}
}
