# MQTT SeDEM Browser
## A Java Web Application to demostrate the use of MQTT SeDEM Library
The MQTT SeDEM Browser is a web application that, subscribed to the TagITSmart! Stream Processing event topic, uses the SeDEM Libraries (see: https://github.com/FINCONS-IBD/MQTT-SeDEM) and displays each received event both in its encrypted and decrypted forms.
To this end the SeDEM Browser will use a CP-ABE key in line with the access policy provided by the data publisher.

## Installation Guide
In order to install the application, it is necessary to deploy the war obtained after the build process in the Tomcat environment.
## Initial Configuration

The configuration of the SeDEM Browser is provided by the editing of the web.xml file located in the WEB-INF folder. In particular: 

<ul>
<li><i>thing_id</i> - the EVRYTHNG thing identifier that will compose the MQTT topic to subscribe</li>
<li><i>mqtt_conf_dec_subscriber</i> - the full path where the first subscriber configuration file is located. N.B. in this file it is important set the CPABE_AES_ENCRYPTION to false disabling the decryption process. For the other setting please refer to the SeDEM Library documentation.</li>
<li><i>mqtt_conf_enc_subscriber</i> - the full path where the second subscriber configuration file is located. N.B. in this file it is important set the CPABE_AES_ENCRYPTION to true enabling the decryption process. For the other setting please refer to the SeDEM Library documentation.</li>
<li><i>log4j.properties</i> - the full path where the Logger configuration file is located. For more detail please refer to the SeDEM Library documentation.</li>
<li><i>SocketServerEncrypt</i> - the endpoint URL of the Encrypted Web Socket (e.g. ws://localhost:8080/SEDEM_Browser_MQTT/encryptedMessage) where the encrypted messages will be displayed</li>
<li><i>SocketServerDecypted</i> - the endpoint URL of the Decrypted Web Socket where the decrypted messages will be displayed</li>
</ul>

In the *com.fincons.message_viewer.resources* package it is possible to find three example configuration files mentioned above . For more details about the configuration procedure of the SeDEM Library please refer to the SeDEM Library documentation (see: https://github.com/FINCONS-IBD/MQTT-SeDEM)