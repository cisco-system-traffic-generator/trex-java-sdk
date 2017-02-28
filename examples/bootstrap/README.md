# Trex Java client example
Example is using gradle to build, but you can easily adapt it to maven if needed

In order to run this example, you need TRex to be running on your host
in interactive mode( `./t-rex-64 -i` )

Edit src/main/java/com/cisco/trex/stateless/TRexClientApp.java
and replace trex.mycompany.com with an actual TRex hostname

### Building

    ./gradlew build

### Running

    ./gradlew run
    # Application will connect to the trex and show list of available commands

