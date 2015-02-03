# SCEPTA Server

SCEPTA is an open source project for policy authoring, deployment, execution and monitoring.

This repository provides the server for the project. The UI can be found [here](https://github.com/scepta/scepta-ui).



## Building from source

The first step is to fork and clone the project. Navigate to the [project repository](https://github.com/scepta/scepta-server) and press the Fork button to create a fork of the project under your own github account. Then create a local clone of your fork using:

```
git clone git@github.com:<your-username>/scepta-server.git
```

Using a command window, go into the _scepta-server_ folder and issue the following command:

```
mvn clean install
```

The server war file can be found in _assemblies/scepta-server/target_ folder.

This war file has currently only been tested in Wildfly 8.2.


