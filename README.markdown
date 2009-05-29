Sonata Framework Readme
=======================

This lightweight framework connects components built following the Symphony modeling convention. Its features are:

*	Minimal configuration (basically, less than 4 lines of configuration *per component* are necessary for setting up the connections) 
*	The connection behaviour is specified *outside* of the components for maximizing reuse
*	**NO** coupling between the components (except for those few lines in the configuration file, plus the specification of connection behaviour)
*	Customization for those who don't buy the magic (but there'll be some extra configuration) !

The original purpose of the framework is to cleanly separate UI components from the functional core by minimizing the coupling between those components. However, it could be used for dividing any set of components!

* * *

Generating the Sonata plugin
----------------------------

1.	Make sure you hava [Ant](ant.apache.org) and [AspectJ](http://eclipse.org/aspectj) installed. *Note that the AJDT plugin is not sufficient for building the plugin!*
2.	Go to the root of your Sonata clone
3.	Type the following in your terminal:	`ant jar -Daspectjpath=<path_to_aspectj>`
4.	If everything goes well, you should have a `.jar` file in the newly created `output` directory of your project: this is the Sonata plugin ready to roll!

* * *

Setup Instructions
------------------

There's a few things to fiddle with before you can use Sonata:

1.	You need to have [Ant](ant.apache.org), [JDom](http://www.jdom.org) and [AspectJ](http://eclipse.org/aspectj) installed.
2.	Create a new project 
2.	Add to your classpath both `jdom.jar` and the `aspectjrt.jar` (the latter can generally be found in your aspectj install directory)

* * *

Tutorials
----------


* * *

Documents
----------

For those interested, the underlying concepts (in particular, Symphony Objects) behind the Sonata framework have been discussed in a few articles written during my Ph.D:

*	[When Interaction Choices Trigger Business Evolution](http://iihm.imag.fr/publs/2008/CAISE08_ShortPaper_GodetBar_DupuyChessa_Rieu.pdf)

You may also check out Sonata's [javadoc]().




