WSN Device Drivers
==================

The WSN Device Drivers is a fork of the device drivers from the [testbed-runtime][] project.

What do I need?
---------------
   * Git
   * JDK >= 1.6
   * Maven 2.2 or 3.0

All library dependencies are downloaded by Maven.

Git Workflow
------------

The following commands show how to clone the WSN Device Drivers repository and how to make first changes.

    $ git clone git@github.com:itm/wsn-device-drivers.git
    $ cd rsc
    $ (edit files)
    $ git add (files)
    $ git commit -a -m "Explain what I changed"
    $ git status
    $ git push origin master

This project is using [gitflow][]. Simply type the following command in the command line after the cloning.

	$ git flow init
	
Accept all default values that are provided by gitflow.

Build and Start the WSN Device Drivers with Maven
-------------------------------------------------

On the command-line go to the Remote Sensor Control directory. Perform a clean build on the project to make sure, that all Maven
artifacts are installed in your local Maven repository (~/.m2/repository). If you are running Maven for the first time,
this will take a while as Maven downloads all project dependencies from the internet.

    $ cd wsn-device-drivers
    $ mvn clean install

More Documentation
==================
Take a look at our [wiki][].

[wiki]:https://github.com/itm/wsn-device-drivers/wiki
[testbed-runtime]:https://github.com/itm/testbed-runtime
[gitflow]:https://github.com/nvie/gitflow
