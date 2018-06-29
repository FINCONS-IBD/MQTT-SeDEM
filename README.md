# MQTT-SeDEM
MQTT based Secure Data Exchange Midlleware (SeDEM). A Java Library to perform secure publish/subscribe operations against an MQTT-complaint Broker.

This library provide Java classes to perfrom publish and subscribe MQTT operations by means of the Event class it defines, which provide an Event creation method and implements an event callback listener. It makes easy all the operations against a MQTT Broker Server.
> This work is part of **TagItSmart!** H2020 European Project,  and in particular is developed in the **TagItSecure!** Open Call.

## Building and installation procedure
The library is developed as Maven Project then the building procedure consists 
simply to use the .pom file as usual in a Maven project.
The result of maven build operation will provide a mavenized JAR including the dependencies, then the application that use the library must import also this jar.

## Initial Configuration

To permit a dynamic configuration of Logger and Application, the configuration files are external to the application jar. Then it is important using the following VM Arguments while running the main application (either in a .bat / .sh run file or as VM Arguments specified in the IDE).

    -Drabbitmq_config_file=C:\resources\conf.properties -Dlog4j.configuration=file:C:\resources\log4j.properties

**NOTE:** if the library will be installed on a Linux O.S. it is important to use a Linux well formed path

#### Application configuration
The first step before using the library is the editing of the configuration parameters detailed below. The file named conf.properties is located in the external resource folder of the library (see the "Initial Configuration" introduction section).  Follow an example of configuration file:

    MQTT_USERNAME=username
    MQTT_PASSWORD=password
    MQTT_CLIENT_ID=client_id_####
    MQTT_QOS=1
    MQTT_BROKER=broker.url.com
    MQTT_PORT=8883
    MQTT_PROTOCOL=ssl
    MQTT_CLEARSESSION=true
    MQTT_RETAINED=true
    MQTT_TIME_TO_WAIT=100000
    MQTT_TIMEOUT=30
    MQTT_KEEP_ALIVE=125
    CPABE_AES_ENCRYPTION=true
    CPABE_POLICY=c:IT
    CPABE_ANTICIPATED_KEY=true
    CPABE_ANTICIPATED_KEY_IN_SECONDS=20
    KEY_TYPE=EC
    CRYPTOGRAPHIC_CURVE=P-256
    ALG=ECDH-ES
    ENC=A128GCM
    PROXY_PROTOCOL=http
    PROXY_IP=(abe-proxy-ip)
    PROXY_PORT=(abe-proxy-port)
    PROXY_ID=ABE-Proxy
    STORAGE_TYPE=database
    DB_IP=(key-storage-ip)
    DB_PORT=(key-storage-port)
    DB_AUTH_USER=(key-storage-username)
    DB_AUTH_PWD=(key-storage-password)
    DB_DATABASE=(key-storage-database)
    DB_TABLE=(key-storage-table)
    PAYLOAD_PATTERN=[{"value": "{"headers":{},"payload":""}"}]
    PLACEHOLDER_PATTERN="{"headers":{},"payload":""}"
    CONSUMER.PUBLIC_PARAM=C:/resources/Key/pub_10
    CONSUMER.CPABE_PRIV_KEY=C:/Key/personal_key
Details about parameters:
-   _MQTT_USERNAME_  - username authorised to connect to MQTT Broker Server endpoint
-   _MQTT_PASSWORD_  - the password to authenticate against the MQTT Broker Server
-   _MQTT_CLIENT_ID_  - the unique client string identifier
-   _MQTT_QOS_  - Quality of Service level: 0 – at most once or 1 – at least once
-   _MQTT_BROKER_  - the endpoint address of the MQTT Broker Server
-   _MQTT_PORT_  - the port number where the MQTT Borker Service is listening on
-   _MQTT_PROTOCOL_  - the protocol used to perform the MQTT Broker connection
-   _MQTT_CLEARSESSION_  - the flag to specify if the persistence of the messages and subscriptions is required
-   _MQTT_RETAINED_  - the flag to specify the persistence of the message. All the messages flagged as retained will be stored by the broker
-   _MQTT_TIME_TO_WAIT_  - measured in milliseconds, it is the maximum time to wait for an action to complete
-   _MQTT_TIMEOUT_  - measured in seconds, it defines the maximum time interval the client will wait for the network connection to the MQTT server to be established
-   _MQTT_KEEP_ALIVE_  - measured in seconds, defines the ping interval to keep the connection opened. It is important to know the max keep alive of the server (e.g. 120s on Mosquitto)
-   _CPABE_AES_ENCRYPTION_  - flag to enable secure exchange of events
-   _CPABE_POLICY_  - the personal cpabe policy
-   _CPABE_ANTICIPATED_KEY_  - flag to enable the calculation of the anticipated key (the next key to be used)
-   _CPABE_ANTICIPATED_KEY_IN_SECONDS_  - the number of second before the key expiration date to start the anticipated key calculation
-   _KEY_TYPE, CRYPTOGRAPHIC_CURVE, ALG, ENC_  - details about the key type
-   _PROXY_PROTOCOL, PROXY_IP, PROXY_PORT, PROXY_ID_  - Abe Proxy endpoint details
-   _STORAGE_TYPE, DB_IP, DB_PORT, DB_AUTH_USER, DB_AUTH_PWD, DB_DATABASE, DB_TABLE_  - Key Storage endpoint details
-   _PAYLOAD_PATTERN_  and  _PLACEHOLDER_PATTERN_  - the message pattern details
-   _CONSUMER.PUBLIC_PARAM_  - the location of cpabe public key
-   _CONSUMER.CPABE_PRIV_KEY_  - the location of cpabe private key

#### Logging configuration
In the external resource folder of the library (see the "Initial Configuration" introduction section) is present a logging configuration file named _log4j.properties_. It is possible edit this configuration file to change the file size and location and dimension, as well as logging level of the logger. Following an example of the config logging file:

    # Root logger option
    log4j.rootLogger=INFO, stdout, file
    # Redirect log messages to console
    log4j.appender.stdout=org.apache.log4j.ConsoleAppender
    log4j.appender.stdout.Target=System.out
    log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
    log4j.appender.stdout.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
    # Redirect log messages to a log file, support file rolling.
    log4j.appender.file=org.apache.log4j.RollingFileAppender
    log4j.appender.file.File=C\:\\rabbitmq_client.log
    #log4j.appender.file.File=/opt/logs/rabbitmq_client.log
    log4j.appender.file.MaxFileSize=5MB
    log4j.appender.file.MaxBackupIndex=10
    log4j.appender.file.layout=org.apache.log4j.PatternLayout
    log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
**NOTE:** if the library is installed on a Linux O.S. it is important to change the log file path (see the commented line)

### Library dependencies

The library has 2 internal dependencies:

-   log4j, a Java Logging Library
-   Eclipse Paho library to interact with the MQTT Broker Server

The other folders present in the repository (ABE-Proxy, CPABE and Device-Entity) represent the other components/libraries involved in the MQTT-SeDEM architecture then it is important setup the entire architecture to permit a right interaction between the MQTT-SeDEM and the other components.

### An example of use

In the  _com.fincons.mqtt.client.test_  package is present a simple example of Publish and Subscribe Scenario including a Simple MQTT Callback class that is invoked when a new MQTT message arrives. Please refer to the classes included in the package above for more details.

**NOTE:** For more details about the library usage please refers to [JavaDoc](https://github.com/FINCONS-IBD/MQTT-SeDEM/tree/master/MQTT-SeDEM/javadoc) folder.