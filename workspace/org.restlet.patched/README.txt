This project contains Restlet classes needed to be patched for extending their visibility (final/protected >> public) and others sub-classes needed to stay in Restlet packages.

> Increase visibility of classes / methods
security.Authorizer (protected authorize > public)

> Extensions of WADL classes for better description of resources.
wadl.Extended ...

> ContentType parsing modification in order to parse complicated mediaTypes
ContentTypeReader

> Add a new MediaType to represent Model Object when a representation normally returns DTO
APPLICATION_JAVA_OBJECT_SITOOLS_MODEL

> Check that the description of a resource is not null not to crash the WADL generation 

TODO
Other classes of Restlet have been duplicated and extended in fr.cnes.sitools.core.
Their location would be better in this plugin :
fr.cnes.sitools.proxy.DirectoryProxy and AbstractDirectoryResource ...
