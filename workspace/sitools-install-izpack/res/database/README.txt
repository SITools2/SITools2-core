L’enregistrement et la gestion d’utilisateurs de Sitools v2 nécessite une base de données de type PostgreSQL ou MySQL,
incluse, mais qui doit être correctement configurée.
Pour PostgreSQL, il faut créer une base de données sur le serveur et ajouter un utilisateur « sitools ».
Il faut ensuite exécuter le script « pgsql_sitools.sql » (présent dans le dossier « database/PGSQL  » à la racine du projet). 
Pour MySQL il faut faire de même en exécutant l’ensemble des scripts présent dans le dossier « database/ MYSQL_CNES » sur un base de données créée précédemment.
Une fois la base créée et remplie, il faut configurer Sitools pour qu’il pointe vers cette base
(propriété « Starter.DATABASE_URL » du fichier sitools.properties ou directement dans l’installateur izPack lors de l’installation).
