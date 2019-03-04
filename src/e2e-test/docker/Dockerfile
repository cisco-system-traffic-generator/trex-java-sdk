# 1. build the image:
#	sudo docker build -t trex-java-sdk-testing .
#
# 2. run the container:
#   sudo docker run -it --cap-add=ALL --privileged --rm -p 4500:4500 -p 4501:4501 -p 4507:4507 trex-java-sdk-testing
#
# 3. run TRex within just started container:
#	[root@<...> v2.49]# sudo ./t-rex-64 -i
#
# 4. run e2e tests on trex-java-sdk:
#	cd trex-java-sdk
#	$ echo $JAVA_HOME 
#	/usr/lib/jvm/java-8-oracle
#	$ ./gradlew e2eTest -i
#
# NOTE. iPV6ScanTest is flaky, it works only on the second or third try. It has to be fixed.


FROM trexcisco/trex

LABEL RUN docker run --privileged --cap-add=ALL --name NAME -e NAME=NAME -e IMAGE=IMAGE IMAGE
RUN rm -r /var/trex/v2.41
WORKDIR /var/trex
RUN curl http://trex-tgn.cisco.com/trex/release/latest | tar -zxvf -
COPY startup.sh /run/startup.sh
ENTRYPOINT "/run/startup.sh" && /bin/bash

