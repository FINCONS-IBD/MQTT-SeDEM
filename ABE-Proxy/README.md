# ABE-Proxy
The ABE Proxy is the new architectural component responsible to perform the heavy ABE encryption operations and the integration of CP-ABE and AES encryption techniques, so to speed up the encryption/decryption processing and reduce resource needs on the client side.

It is a web based application that uses novel attribute-based encryption techniques in order to protect access to information, which is encrypted according to user’s provided access policies and the generation of decryption keys tied to the user’s profiles.

## Installation guide
In order to install the application, it is necessary to deploy the war obtained after the build process in the Tomcat environment, and to copy the Public Key generated by CPABE Service in the folder ABE-Proxy\WebContent\WEB-INF\cpabe_keys\pub_10 specified.

Another important configurable parameter is the expiration time that indicates the life-time of the associated symmetric key. When this life-time expires, it is strongly recommended to renegotiate a new symmetric key between data source and ABE proxy in order to guarantee that the communication continues reliable and secure, and to achieve the well-known Perfect Forward Secrecy. This parameter is set in the ABE Proxy’s *web.xm*l file, and is in millisecond format. Following an example:
	
	<init-param>
		<param-name>expiration_time</param-name>
		<param-value>5000</param-value>
	</init-param>

The ABE Proxy provides two public endpoint through two different sockets:

 - *ABE-Proxy/generate_shared_secret*: performs the agreement of a symmetric key between the data source and the ABE-Proxy using *Elliptic Curve Diffie-Hellman Ephemeral Static*, combined with the *Concat Key-Derivation Function*.
 - *ABE-Proxy/cpabe_information*: the data source delegates the CP-ABE encryption operations to the ABE-Proxy.
 
## Software dependencies
 The ABE-Proxy component has two important dependencies:
 
 - *CPABE library* (see [here](https://github.com/FINCONS-IBD/MQTT-SeDEM/tree/master/CPABE)) that provides the CP-ABE encryption and decryption algorithms and entities. 
 - *Key Storage Service* component: in which the ABE-Proxy saves the CP-ABE encrypted AES symmetric keys. It is a fully configurable component (specified in the *web.xml* configuration file); in the project the NOSQL OrientDB database has been identified to fulfill these needs.

For more details about the building procedures required to set-up the single components please refer to the single projects descriptions.
 
