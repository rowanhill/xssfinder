namespace java org.xssfinder.remote
namespace php XssFinder

struct MethodDefinition {
  1:string identifier,
  2:string returnTypeIdentifier,
  3:string owningTypeIdentifier,
  4:bool parameterised,
  5:bool submitAnnotated,
  6:bool customTraversed,
}

struct PageDefinition {
  1:string identifier,
  2:set<MethodDefinition> methods,
  3:bool crawlStartPoint,
}

enum TraversalMode {
  NORMAL,
  SUBMIT
}

/**
 * Thrown when the remote executor is asked to traverse an incorrectly configured traversal.
 */
exception TUntraversableException {
  1:string message
}

/**
 * Thrown when the remote executor is asked to use an incorrectly configured lifecycle event handler
 */
exception TLifecycleEventHandlerException {
  1:string message
}

/**
 * Thrown when the remote executor attempted to interact with the web site (e.g. perform a traversal) but encountered an
 * unexpected error.
 */
exception TWebInteractionException {
  1:string message
}

service Executor {
  /**
   * Gets definitions of all page objects in the given namespace
   *
   * @param namespaceIdentifier The namespace in which to search for page definitions
   * @return set<PageDefinition> A set of PageDefinitions for all page objects in the namespace
   */
  set<PageDefinition> getPageDefinitions(1:string namespaceIdentifier),

  /**
   * Navigate the driver to the URL associated with the specified PageDefinition
   *
   * @param pageIdentifier The identifier of the crawlStartPoint PageDefinition to start the root at
   */
  void startRoute(1:string pageIdentifier) throws (1:TWebInteractionException webInteraction),

  /**
   * Put XSS attacks into all available inputs
   *
   * @return A map of input identifiers -> attack identifiers
   */
  map<string, string> putXssAttackStringsInInputs() throws (1:TWebInteractionException webInteraction),

  /**
   * @return The set of currently XSS attack identifiers observable on the current page
   */
  set<string> getCurrentXssIds() throws (1:TWebInteractionException webInteraction),

  /**
   * @return The number of forms observable on the current page
   */
  i32 getFormCount() throws (1:TWebInteractionException webInteraction),

  /**
   * Traverse the given method on the current page object in the given mode
   *
   * @param method The method to invoke on the current page object
   * @param mode   The mode in which to traverse the method
   * @return A map of input identifiers -> attack identifiers
   */
  map<string, string> traverseMethod(1:MethodDefinition method, 2:TraversalMode mode)
      throws (1:TUntraversableException untraversable, 2:TWebInteractionException webInteraction),

  /**
   * Invoke the 'after route' event handler for a route starting at the identified page
   *
   * @param rootPageIdentifier The root page of the route that has just finished
   */
  void invokeAfterRouteHandler(1:string rootPageIdentifier)
      throws (1:TWebInteractionException webInteraction, 2:TLifecycleEventHandlerException lifecycleEventHandler)
}
