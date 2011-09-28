WSN Device Drivers
==================

The WSN Device Drivers is a fork of the device drivers from the [testbed-runtime][] project.

What do I need?
---------------
   * Git
   * JDK >= 1.6
   * Maven 3.0

All library dependencies are downloaded by Maven.

Git Workflow
------------
This project is using [gitflow][]. Simply type the following command in the command line after the cloning.

	$ git flow init
	
Accept all default values that are provided by gitflow.

How to use
----------
Each driver implementation contains an example application for testing.

The project [wsn-device-utils][] has working command line applications.

[testbed-runtime]:https://github.com/itm/testbed-runtime
[gitflow]:https://github.com/nvie/gitflow
[wsn-device-utils]:https://github.com/itm/wsn-device-utils
