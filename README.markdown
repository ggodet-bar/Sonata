Sonata Framework Readme
=======================

This lightweight framework connects components built following the Symphony modeling convention.

Features
---------

*	Minimal configuration (basically, less than 4 lines of configuration *per component* are necessary for setting up the connections) 
*	The connection behaviour is specified *outside* of the components for maximizing reuse
*	**NO** coupling between the components (except for those few lines in the configuration file, plus the specification of connection behaviour)
*	Customization for those who don't buy the magic (but there'll be some extra configuration) !

The original purpose of the framework is to cleanly separate UI components from the functional core by minimizing the coupling between those components. However, it could be used for dividing any set of components!


Generating the Sonata plugin
----------------------------

1.	Make sure you hava [Ant](http://ant.apache.org), [JDom](http://www.jdom.org) and [AspectJ](http://eclipse.org/aspectj) installed. *Note that the AJDT plugin is not sufficient for building the plugin!*
2.	Copy your `jdom.jar` into the `lib` folder of your Sonata clone
3.	Go to the root of your Sonata clone
4.	Type the following in your terminal:	`ant jar -Daspectjpath=<path_to_aspectj>`
5.	If everything goes well, you should have a `.jar` file in the newly created `output` directory of your project: this is the Sonata plugin ready to roll!

Optional: You can generate the project's javadoc by running `ant doc`


Documents
----------

You should have a look at the [wiki](http://wiki.github.com/ggodet-bar/Sonata) for some information on *what to do next with that jar file*.

Additionally, some example applications should soon be uploaded on github. Stay tuned!

For those interested, some of the underlying concepts (in particular, Symphony Objects) behind the Sonata framework have been discussed in a few articles written during my Ph.D:

*	[When Interaction Choices Trigger Business Evolution](http://iihm.imag.fr/publs/2008/CAISE08_ShortPaper_GodetBar_DupuyChessa_Rieu.pdf)

You may also check out Sonata's javadoc (which is currently being translated from french to english!).


