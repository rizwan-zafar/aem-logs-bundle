# Sample AEM project template

This is a project template for AEM-based applications. It is intended as a best-practice set of examples as well as a potential starting point to develop your own functionality.

## Modules

The main parts of the template are:

* core: Java bundle containing all core functionality like OSGi services, listeners or schedulers, as well as component-related Java code such as servlets or request filters.
* it.tests: Java based integration tests
* ui.apps: contains the /apps (and /etc) parts of the project, ie JS&CSS clientlibs, components, and templates
* ui.content: contains sample content using the components from the ui.apps
* ui.config: contains runmode specific OSGi configs for the project
* ui.frontend: an optional dedicated front-end build mechanism (Angular, React or general Webpack project)
* ui.tests: Selenium based UI tests
* all: a single content package that embeds all of the compiled modules (bundles and content packages) including any vendor dependencies
* analyse: this module runs analysis on the project which provides additional validation for deploying into AEMaaCS

## How to build

To build all the modules run in the project root directory the following command with Maven 3:

    mvn clean install

To build all the modules and deploy the `all` package to a local instance of AEM, run in the project root directory the following command:

    mvn clean install -PautoInstallPackage -Padobe-public

Or to deploy it to a publish instance, run

    mvn clean install -PautoInstallSinglePackagePublish


 ## Guide : 
 You can access the link https://HOSTNAME:PORT/bin/aem-slinglogs to view logs. By default, it displays all lines from the error.log file.

In this view:
- Blue lines indicate informational messages.
- Red lines highlight errors.
- Yellow lines highlight warnings.

To retrieve a specific number of lines, you can include a query parameter in the URL. Additionally, you can specify the name of the particular file you wish to access using another query parameter.
https://localhost:4502/bin/aem-slinglogs?lines=40&file=access.log
