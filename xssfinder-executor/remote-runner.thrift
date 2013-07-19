namespace java org.xssfinder.remote

struct PageDefinition {}

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

exception TUntraversableException {
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
  void startRoute(1:string pageIdentifier),

  /**
   * Put XSS attacks into all available inputs
   *
   * @return A map of input identifiers -> attack identifiers
   */
  map<string, string> putXssAttackStringsInInputs(),

  /**
   * @return The set of currently XSS attack identifiers observable on the current page
   */
  set<string> getCurrentXssIds(),

  /**
   * @return The number of forms observable on the current page
   */
  i32 getFormCount(),

  /**
   * Traverse the given method on the current page object in the given mode
   *
   * @param method The method to invoke on the current page object
   * @param mode   The mode in which to traverse the method
   * @return A map of input identifiers -> attack identifiers
   */
  map<string, string> traverseMethod(1:MethodDefinition method, 2:TraversalMode mode) throws (1:TUntraversableException untraversable),

  void invokeAfterRouteHandler()
}
