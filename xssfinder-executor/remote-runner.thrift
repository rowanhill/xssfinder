namespace java org.xssfinder.remote

struct PageDefinition {}

struct MethodDefinition {
  1:string identifier,
  2:PageDefinition returnType,
  3:bool parameterised,
  4:bool submitAnnotated,
  5:bool customTraversed,
}

struct PageDefinition {
  1:string identifier,
  2:set<MethodDefinition> methods,
  3:bool crawlStartPoint,
  4:optional string startPointUrl,
}

service Executor {
  /**
   * Gets definitions of all page objects in the given namespace
   *
   * @param namespaceIdentifier The namespace in which to search for page definitions
   * @return set<PageDefinition> A set of PageDefinitions for all page objects in the namespace
   */
  set<PageDefinition> getPageDefinitions(1:string namespaceIdentifier)
}