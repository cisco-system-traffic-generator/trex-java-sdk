# trex-java-sdk
Java client SDK provides an implementation for
* https://trex-tgn.cisco.com/trex/doc/trex_rpc_server_spec.html
* https://trex-tgn.cisco.com/trex/doc/trex_scapy_rpc_server.html (Future plans)


### Bootstrap project
you can copy and edit examples/bootstrap directory and compile/run separately
see examples/bootstrap/README.md

### How to use in Maven project
Add a dependency to the pom.xml as like below:
```
<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/maven-4.0.0.xsd">
  ...
  <dependencies>
    <dependency>
      <groupId>com.cisco.trex</groupId>
      <artifactId>trex-java-sdk</artifactId>
      <version>1.28</version>
    </dependency>
       ...
  </dependencies>
  ...
</project>
```
