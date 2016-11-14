# ORE Dashboard - Technical Documentation

Open Source Risk Engine is an open source library for calculating risk.

ORE Dashboard is a web app for viewing ORE output.

This is the technical documentation for ORE Dashboard, aimed at programmers who intend to support and enhance the app.

# Table of Contents

[1. Prerequisites](#1-prerequisites)

&nbsp;&nbsp;&nbsp;&nbsp;[1.1 Operating System](#11-operating-system)

&nbsp;&nbsp;&nbsp;&nbsp;[1.2 Required Packages](#12-required-packages)

&nbsp;&nbsp;&nbsp;&nbsp;[1.3 Other Packages](#13-other-packages)

[2. Getting Started](#2-getting-started)

&nbsp;&nbsp;&nbsp;&nbsp;[2.1 Fork the git Repo](#21-fork-the-git-repo)

&nbsp;&nbsp;&nbsp;&nbsp;[2.2 Create a Heroku App](#22-create-a-heroku-app)

&nbsp;&nbsp;&nbsp;&nbsp;[2.3 Install the Heroku Toolbelt](#23-install-the-heroku-toolbelt)

&nbsp;&nbsp;&nbsp;&nbsp;[2.4 Initialize the App](#24-initialize-the-app)

[3. Set Up the Input Data](#3-set-up-the-input-data)

&nbsp;&nbsp;&nbsp;&nbsp;[3.1 Create the Hierarchy Definition](#31-create-the-hierarchy-definition)

&nbsp;&nbsp;&nbsp;&nbsp;[3.2 Create the Data Files](#32-create-the-data-files)

&nbsp;&nbsp;&nbsp;&nbsp;[3.3 Update the File List](#33-update-the-file-list)

&nbsp;&nbsp;&nbsp;&nbsp;[3.4 Commit to git](#34-commit-to-git)

[4. Cache](#4-cache)

&nbsp;&nbsp;&nbsp;&nbsp;[4.1 Hierarchies and Trees](#41-hierarchies-and-trees)

&nbsp;&nbsp;&nbsp;&nbsp;[4.2 Cache](#42-cache)

&nbsp;&nbsp;&nbsp;&nbsp;[4.3 Load](#43-load)

&nbsp;&nbsp;&nbsp;&nbsp;[4.4 Rest Endpoints](#44-rest-endpoints)

# 1. Prerequisites

## 1.1 Operating System

ORE Dashboard is a Java app which is platform neutral.  It was developed on Mac and Ubuntu, it should just work on Windows too.

## 1.2 Required Packages

You need to install the following before running ORE Dashboard:

* Java 1.8
* maven - dependency manager for Java

## 1.3 Other Packages

ORE Dashboard makes use of additional packages.  You do not need to install these yourself, they are included in the build:

* Spark Framework (Java REST server micro-framework)
* Echarts (Javascript data-viz package)

## Scripts included (some for future use):
* Bootstrap
* Font Awesome
* jQuery-Autocomplete
* FullCalendar
* Charts.js
* Bootstrap Colorpicker
* Cropper
* dataTables
* Date Range Picker for Bootstrap
* Dropzone
* easyPieChart
* ECharts
* bootstrap-wysiwyg
* Flot - Javascript plotting library for jQuery.
* gauge.js
* iCheck
* jquery.inputmask plugin
* Ion.RangeSlider
* jQuery
* jVectorMap
* moment.js
* Morris.js - pretty time-series line graphs
* jquery-nicescroll plugin
* PNotify - Awesome JavaScript notifications
* NProgress
* Pace
* Parsley
* bootstrap-progressbar
* select2
* Sidebar Transitions - simple off-canvas navigations
* Skycons - canvas based icons
* jQuery Sparklines plugin
* switchery - Turns HTML checkbox inputs into beautiful iOS style switches
* jQuery Tags Input Plugin
* Autosize - resizes text area to fit text
* validator - HTML from validator using jQuery
* jQuery Smart Wizard
* ...

# 2. Getting Started

This section comprises a tutorial demonstrating how to get ORE Dashboard up and running on your machine, and how to deploy it to the cloud.

## 2.1 Fork the git Repo

Go on to the github website and fork <https://github.com/OpenSourceRisk/Dashboard>

## 2.2 Create a Heroku App

Go on to the heroku website and create a new app.  You can either allow heroku to generate a random name for your app, or you can pick a name yourself.  For purposes of this tutorial, the heroku app is called __blooming-dawn-94116__.

## 2.3 Install the Heroku Toolbelt

This is a command line client that makes using the Heroku cloud environment very simple indeed.  You can get by without installing it but it means that you have to compile the app locally and deploy it manually to Heroku.
The Heroku CLI takes care of all the deployment and packaging straight from the GitHub repo.

Install the heroku toolbelt as explained at <https://toolbelt.heroku.com/>.

## 2.4 Initialize the App

Open a command window.  Create a directory for your work.  For purposes of this tutorial, the directory is called `/home/quaternion/demo`.  cd into the directory:

    cd /home/quaternion/demo

Clone from your new remote repo into the local directory:

    git clone https://github.com/OpenSourceRisk/Dashboard.git

cd into the project directory:

    cd /home/quaternion/demo/Dashboard

Set up heroku by logging in to your repo and linking the remote cloud version:

    heroku login
    heroku git:remote -a blooming-dawn-94116

Build the app (only necessary if you are testing on a local machine):

    mvn clean package

Run the app locally (after doing the build step, above):

    heroku local web

View the local instance in your browser:

<http://localhost:5000/>

Deploy the app - this pushes the code changes to the Heroku version of your git repo and deploys the app in the cloud:

    git push heroku master

View the remote instance in your browser:

<http://blooming-dawn-94116.herokuapp.com/>

__NB__:  You are not required to use Heroku.  ORE Dashboard is a pure Java app which could be deployed to any container, e.g. JBOSS, Tomcat. Spark is simply the REST framework with a built-in Jetty web server. The web server component can be disabled very easily in code if you want to run the JAR in another container.

# 3. Set Up the Input Data

ORE Dashboard views data files generated by the ORE application.  This section of the documentation explains how to set up the data files for use by the Dashboard.

The root directory for the data is...

    /home/quaternion/demo/Dashboard/src/main/resources/data/vX

...where X is the version number of the current data set.  The data files are maintained under version control.  They are also packaged into the jar file.  When the server starts up, it loads the data files from the jar file into memory.  All of this is explained in detail below.

## 3.1 Create the Hierarchy Definition

The data is organized into a hierarchy.  The structure of the hierarchy is stored in a file which the server loads at startup:

    /home/quaternion/demo/Dashboard/src/main/resources/data/vX/hierarchies.csv

This file was created by opening workbook `SamplePortfolio1_TradeMapping v3.xlsx` and saving the worksheet `Hierarchies` in CSV format.

## 3.2 Create the Data Files

Take the output from the ORE application and copy it to the root data directory, creating one subdirectory for each processing date, e.g:

    /home/quaternion/demo/Dashboard/src/main/resources/data/vX/20160701
    /home/quaternion/demo/Dashboard/src/main/resources/data/vX/20160702
    /home/quaternion/demo/Dashboard/src/main/resources/data/vX/20160703
    ...

At startup, the server iterates through the above directories and loads the data into cache.

## 3.3 Update the File List

When a java application loads static data files from a jar file, there is a limitation that the app is unable to discover the contents of the data directory.  The app must already know the name of a file before it can be loaded.

Therefore we maintain a list of all available data files.  The server loads up this list at startup and uses it to iterate through the data files.

After making any changes to the contents of the data directory, you need to update the file list:

    cd /home/quaternion/demo/Dashboard/src/main/resources
    find data/vX > data/vX/dirlist.txt

## 3.4 Commit to git

All of the changes that you made above need to be committed to the git repo.

# 4. Cache

This section of the documentation provides technical information on how the data is loaded into the server memory.

## 4.1 Hierarchies and Trees

Here is a partial rendering of the data structure:

![Hierarchy](/images/hierarchy.png "Hierarchy")

The data comprises a tree.  The tree is five levels deep, each level is referred to as a hierarchy: total, creditrating, counterparty, nettingset, trade.

The Dashboard implements two different modes for viewing the data:

* __hierarchy__: In this mode, the user selects one of the five hierarchies, and the Dashboard displays the data for all of the nodes in the selected hierarchy.  For example, if the user selects hierarchy creditrating, then the Dashboard displays the data for all of the credit ratings (A, AA, AAA, B, ...).
* __tree__: In this mode, the user selects one of the nodes in the tree, and the Dashboard displays the data for all of the children of the selected node.  For example, if the user selects node CUST\_D, then the Dashboard displays the data for all of the netting sets that belong to that client (CSA\_16, CSA\_17, ...).

## 4.2 Cache

Class Cache contains a member variable of type Node, which comprises the root node of the tree.  The Cache also contains a list of 5 Hierarchy objects, one for each level of the tree.  Each Hierarchy object holds a list of the Nodes in the relevant level:

![Cache](/images/cache.png "Cache")

Class ProcessingDate encapsulates the data for a list of nodes for a given date.  The list of nodes may be either:

* The children of a node
* The nodes belonging to a hierarchy

In other words:

* class Node contains a list of ProcessingDate objects to encapsulate the data for that node's children, by date.
* class Hierarchy contains a list of ProcessingDate objects to encapsulate the data for the nodes in that hierarchy, by date.

The data requirements for Nodes and Hierarchies differ slightly, this difference is captured by two subclasses of ProcessingDate:

* class ProcessingDateHierarchy encapsulates BarGraph and Xva.
* class ProcessingDateNode encapsulates BarGraph, Xva, and Exposure.
 
Each Node object also contains the TotalExposure data relevant to that Node (this data is independent of the processing date).

Now, let's try and put all of that together.  Consider the following diagram, which zooms in on a fragment of the cache:

![ProcessingDate](/images/processingdate.png "ProcessingDate")

You can see that:

* class Node stores TotalExposure (for all dates)
* class Node stores a list of ProcessingDateNode objects
* class ProcessingDateNode stores BarGraph, Xva, and Exposure for the children of the Node
* class Hierarchy stores a list of ProcessingDateHierarchy objects
* class ProcessingDateHierarchy stores BarGraph and Xva for the nodes in the hierarchy

That's it.  It's not as complicated as it seems.

## 4.3 Load

Class Cache invokes class Load to copy the data from disk to memory.  Class Load is functionally a part of class Cache but has been separated out for clarity.  Class Load invokes setter functions in class Cache in order to populate the cache.  The load is performed at startup and is invoked once per processing date.  Tree-level data is attached to the Node objects, and hierarchy-level data is attached to the Hierarchy objects.

## 4.4 Rest Endpoints

The cache implements a series of rest endpoints which serve up the data to the front end, depending on which parameters the user has entered into the browser.

A web page is provided for manual inspection of the rest endpoints.  The page is not required for production use - the client accesses the rest endpoints programatically - but the page is helpful for understanding the rest API for the ORE Dashboard app.

If you are running the server locally after invoking the `heroku local web` command (described above), then you should find the debugging page at the following URL:

<http://localhost:5000/api/cache.html>

If you have deployed the app to the cloud, then the URL should look something like:

<http://blooming-dawn-94116.herokuapp.com/api/cache.html>

At the time of this writing, the page above is online, and it is referred to for the rest of this section of the document.

The specification of the rest API depends upon whether we are in hierarchy view or tree view (explained previously).

### 4.4.1 Hierarchy View

This refers to the case where we display all of the nodes in one of the five levels of the tree.

In this case the path to an item in the hierarchy is always:

    hierarchy/item

For example, to get to credit rating AAA:

    creditrating/AAA

To get to netting set CSA_46:

    nettingset/CSA_46

etc.

### 4.4.2 Tree View

This refers to the case where we display all of the children of one node.

In this case there are two ways to define the path:

    A) hierarchy/item

    B) creditrating/counterparty/nettingset/trade

For example, to get to netting set CSA_46:

    A) nettingset/CSA_46

    B) A/CUST_J/CSA_46

Approach "A" is more concise.

### 4.4.3 Navigating the Tree

There are two rest endpoints to return the children of a given node.

- tree - uses approach A
- tree2 - uses approach B

For example, if you want to get a list of the trades belonging to
netting set CSA_46, you can do either of the following:

<http://blooming-dawn-94116.herokuapp.com/api/tree/nettingset/CSA_46>

<http://blooming-dawn-94116.herokuapp.com/api/tree2/Total/A/CUST_J/CSA_46>

Both of the above two endpoints return the same data:

    [ "Trade_3336", "Trade_3409", "Trade_5450", "Trade_7805", "Trade_8268" ]

### 4.4.4 Bar Graph

A bar graph displays the top 5 children of a given node for a given metric.

Top 5 counterparties for metric CE:

<http://blooming-dawn-94116.herokuapp.com/api/bargraph/20150630/counterparty/ce>

Top 5 AAA counterparties for metric CE:

<http://blooming-dawn-94116.herokuapp.com/api/bargraph-tree/20150630/creditrating/AAA/ce>

Note that, in the tree view for bar graphs, "Approach A" is implemented.

### 4.4.5 Donut Graph

The donut graph displays all of the children of a given node for a given metric.

CVA for all counterparties:

<http://blooming-dawn-94116.herokuapp.com/api/xva/20150630/counterparty/cva>

CVA for all AAA counterparties (there is only one):

<http://blooming-dawn-94116.herokuapp.com/api/xva-tree/20150630/creditrating/AAA/cva>

Again the xva tree view uses "Approach A".

### 4.4.6 Line Graph

These are only relevant in tree view.

If the user chooses any of the 5 hierarchies from the drop down, the two line graphs should just display the root node:

<http://blooming-dawn-94116.herokuapp.com/api/exposure-tree/20150630/total/Total>

<http://blooming-dawn-94116.herokuapp.com/api/totalexposure-tree/total/Total>

Don't get confused by the "total/Total".  The rest endpoints for the line graphs employ tree view, Approach A:

    hierarchy/item

where hierarchy = "total" (level 0) and item = "Total" (the name of the
root node).

Exposure for CUST_A:

<http://blooming-dawn-94116.herokuapp.com/api/exposure-tree/20150630/counterparty/CUST_A>

Total exposure for CUST_A:

<http://blooming-dawn-94116.herokuapp.com/api/totalexposure-tree/counterparty/CUST_A>
