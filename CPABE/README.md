# CPABE
This software is a Java realization for "ciphertext-policy attribute based
encryption" (CP-ABE).

It is based on the https://github.com/Bysmyyr/cpabe implementation.

You are responsible for the problem caused by using the code.

## Installation guide
To use this software, you will need to have the Java Pairing Based Cryptography Library (jPBC) library installed. You can get it from the following page:

   [http://gas.dia.unisa.it/projects/jpbc/](http://gas.dia.unisa.it/projects/jpbc/)

The library is provided as mavenized project, then it is easy to build the library jar with dependencies using the *pom.xml* file as usual in a maven project.

## Important note
This library is used by the Device-Entity (see [here](https://github.com/FINCONS-IBD/MQTT-SeDEM/tree/master/Device-Entity)) and ABE-Proxy (see [here](https://github.com/FINCONS-IBD/MQTT-SeDEM/tree/master/ABE-Proxy)) components. To permit a right interaction between these two components it is important to use the same version of the CPABE Library.
