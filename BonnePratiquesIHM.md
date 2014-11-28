# Bonnes pratiques de développement IHM sitools2 #

### Ouverture d'un composant : ###

 	//On récupère le sitoolsController
	var sitoolsController = Desktop.getApplication().getController	('core.SitoolsController'); 
	//On ouvre le composant
	sitoolsController.openComponent(jsObject, componentsConfig, windowConfig);


### DatasetView ###

#### Definition d'un sorter :

	{
		property : columnAlias
		direction : ASC ou DESC
	}

Résultat dans la requête :

	sort:[{"field":"dataset","direction":"ASC"}]

#### Definition d'un filtre de formulaire (formFilter):

	{
		type : this.type, 
    	code : this.code, 
    	value : value1 | value2..., 
    	userDimension : this.userDimension, 
    	userUnit : this.userUnit
	}

Résultat dans la requête :
	
	p[0]=type|code|value|userDimension|userUnit

#### Definition d'un filtre de formulaire concept (formFilterConcept):

	{
		type : this.type, 
    	code : this.code, // concept name
		dictionary : this.dictionary,
    	value : value1 | value2..., 
    	userDimension : this.userDimension, 
    	userUnit : this.userUnit,		
	}

Résultat dans la requête :
	
	c[0]=type|dictionary, concept|value|userDimension|userUnit


#### Definition d'un filtre dans la dataview (gridFilter):

La définition se décompose en 2 parties :

Configuration du filtre (nécessaire pour ouvrir le FilterService avec la configuration sauvegardé)

	[{
		"columnAlias" : "dateobs",
		"data" : {
			"comparison" : "gte",
			"value" : "2000-11-13T00:00:00.000",
			"type" : "date"
		}
	}, {
		"columnAlias" : "dateobs",
		"data" : {
			"comparison" : "lte",
			"value" : "2014-11-13T00:00:00.000",
			"type" : "date"
		}
	}]


Definition du filtre (nécessaire pour produire la requête)

	{
		"columnAlias" : "dateobs",
		"value" : {
			"from" : "2000-11-13T00:00:00",
			"to" : "2014-11-13T00:00:00"
		},
		"type" : "date"
	}

Résultat dans la requête :
	
	filter[0][columnAlias]=dateobs&filter[0][data][comparison]=gte&filter[0][data][value]=2000-11-13T00:00:00.000&filter[0][data][type]=date&filter[1][columnAlias]=dateobs&filter[1][data][comparison]=lte&filter[1][data][value]=2014-11-13T00:00:00.000&filter[1][data][type]=date

Définition d'une sélection

*Une sélection est toujours relative à une liste de tri et une liste de sorters*

	{
		ranges : [[0,10], [15,25]],
		nbRecordsSelection : 10,
		startIndex : 25
	}


Liste des parametres nécessaires pour ouverture d'une dataview :

	{
		dataset =>  dataset {
			id
			name
			sitoolsAttachementForUsers
			columnModel
			dictionaryMappings
			datasetViewConfig		
		} 
		formParams => Liste de filtres de type formulaire
		gridFilters => Liste de filtres de type grid
		gridFiltersCfg => Liste de configuration de filtres de type grid
		sortInfo => Liste des tris,
		ranges => Liste de ranges,
		formConceptParams => List de filtres de type formulaire concept
	}


### Panier ###


Example de définition d'une sélection dans le panier pour un dataset 

	{
		"selectionName" : "Headers",
		"selectionId" : "Headers",
		"datasetId" : "7d4571ba-6055-4ac2-86a8-9897fde17a10",
		"dataUrl" : "/headers",
		"datasetName" : "Headers",
		"selections" : "ranges=[[0,16]]&filter%5B0%5D%5BcolumnAlias%5D=dataset&filter%5B0%5D%5Bdata%5D%5Bcomparison%5D=LIKE&filter%5B0%5D%5Bdata%5D%5Bvalue%5D=A00%25&filter%5B0%5 D%5Bdata%5D%5Btype%5D=string&sort={\"ordersList\":[{\"field\":\"dataset\",\"direction\":\"DESC\"}]}",
		"ranges" : "[[0,16]]",
		"dataToExport" : ["preview"],
		"startIndex" : 0,
		"nbRecords" : 17,
		"orderDate" : "2014-11-07T14:48:34.381",
		"colModel" : [{
				"columnAlias" : "dataset",
				"header" : "dataset"
			}, {
				"columnAlias" : "preview",
				"header" : "preview"
			}, {
				"columnAlias" : "targname",
				"header" : "targname"
			}
		],
		"filters" : [{
				"columnAlias" : "dataset",
				"data" : {
					"comparison" : "LIKE",
					"value" : "A00%",
					"type" : "string"
				}
			}
		],
		"storeSort" : {
			"field" : "dataset",
			"direction" : "DESC"
		},
		"filtersCfg" : [{
				"columnAlias" : "dataset",
				"value" : "A00%",
				"type" : "string"
			}
		]
	}