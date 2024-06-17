SSIM - MAVEN BUILD
------------------
Properties
You can either update the properties file located at:

ssim/ssim-model/src/main/resources/repository-model-beans.properties

or:

create a System environmental property called SSIM_PROPS and assign it's values to an external properties files such as ssim.properties.

The properites file consist of the following attributes:

import.dir=/Users/johnderinger/ssim/upload
solr.host=localhost
solr.port=8983
solr.dataimport.url=http://localhost:8983/solr/dataimport?command=full-import

session.timeout=20

ssim.warehouse.db.driver=com.mysql.jdbc.Driver
ssim.warehouse.db.url=jdbc:mysql://localhost/SSIM_DB?useUnicode=true
ssim.warehouse.db.user=root
ssim.warehouse.db.password=z2f4JUhu
ssim.warehouse.db.validation=select 1 from mysql.user


You can leave the import.dir blank if you are not testing file uploads and may leave the Solr attributes 'as is' if you do not have a Solr instance running.

- Maven build
  - Running mvn install at the parent ssim maven module should create the ssim-model jar and install it locally in your maven cache. It should also create two war files, ssim-repository-service-1.0.0.war & ssim-repository-webapp-1.0.0.war.

- Start MySQL. You should be able to connect to MySQL with the connection info from the properties above.
 - Create the SSIM database.
   - You can either run the database creation script "ssim/ssim-model/doc/sql/create.sql" to create the database tables.
   or
   - Create the database and allow the application to automatically generate the database tables on start-up.
      - CREATE database SSIM_DB;

 - Note: JPA is configured to create the database tables on application startup which will cause your any data, within the databse, to be removed on each restart. If you do not want your data continuously removed you can modify the JPA/hibernate "hibernate.hbm2ddl.auto" property in "ssim/ssim-model/src/main/resources/META-INF/persistence.xml" - change "create" or "create-drop" to "validate"

- Deploy the two war files to a Tomcat webapps directory and start.  Start-up should create the database schema, if not already created.

- After the Web application and Web Service are running a list of the available service endpoints is available using the URL:
http://localhost:8086/ssim-repository-service-1.0.0/rest/encounterAPI?_wadl
This assumes your Tomcat instance in running on localhost. port: 8086.

The service interface source is located at: ssim/ssim-service/src/main/java/com/sri/ssim/service/EncounterRestInterface.java


