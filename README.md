XSS Finder [![Build Status](https://travis-ci.org/rowanhill/xssfinder.png)](https://travis-ci.org/rowanhill/xssfinder)
==========
XSS Finder builds on your existing Java web test [page objects](https://code.google.com/p/selenium/wiki/PageObjects)
to automatically traverse your web application searching for XSS vulnerabilities.

According to [OWASP](https://www.owasp.org/index.php/Category:OWASP_Top_Ten_Project),
cross site scripting (XSS) is one of the top three security
vulnerabilities affecting web applications.
[Preventing](https://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet)
it is largely a case of correct escaping. Although this is easy
to do, it's also easy to forget. Finding XSS vulnerabilities
through manual inspection is laborious, difficult, and error
prone. XSS Finder seeks to automate this process.

### Theory of Operation ###
When manually testing for XSS vulnerabilities, it's usually just
a case of trawling around the site, looking for inputs, submitting
them with mock malicious input (like `alert('foo')`), and seeing
if the attack is ever executed.

The difficulty in automating this is knowing how to effectively
navigate around the web site under test. Fortunately, if you've
written you web tests using the page object pattern (which is
good practice, regardless of whether or not you wnat to use XSS
Finder), you've already encoded a wealth of information about
how to get around the site.

XSS Finder uses this information (with a bit more metadata, in
the form of annotations) to automate basic XSS detection. In a
nutshell, it does the following:

1. Find all pages, and how they fit together, then pick a few
routes that visit all pages at least once.
2. Run through all the routes, submitting all forms with uniquely
identifiable attacks as it goes.
3. Look for evidence of these attacks being executed as it goes.
4. Run through all the routes a second time (in case later attacks
show up on earlier pages).
5. Write a report on all successful attacks, details where they
originated and where they were executed.

Usage
-----
### Summary ###
1. Annotate page objects with `@Page`.
2. Annotate the entry point page objects with `@CrawlStartPoint`.
3. Annotate the methods which submit forms ith `@SubmitAction`.
4. Add any custom traversers:
    1. Create a class that implements `CustomTraverser`
    2. Annotate the page object method with `@TraverseWith`
5. Add any route lifecycle handlers:
    1. Reference lifecycle handler class in `@CrawlStartPoint`
    2. Annotate a method on that class with `@AfterRoute`
6. Run XSS Finder - currently easiest from a unit test

See the xssfinder-test module for an example.


### Basic Annotations ###
XSS Finder tries to keep things as simple and unobtrusive as
possible, but it does need a hint on which of your classes are
your page objects. You can give it this hint by annotating your
page objects with `@Page`.

Once that's done, XSS Finder can work out a graph that represents
how users can navigate through your site based on methods on your
page objects that return other page objects. In the example below,
XSS Finder knows that it can navigate from the view profile page to
the edit profile page.


```java
@Page
public class ProfilePage {
    public EditProfilePage edit() {
        // ...
    }
}

@Page
public class EditProfilePage {
    // ...
}
```

With most website, you can't just jump in anywhere, though: maybe you
have to log in first, or there's an obvious home page. To tell XSS
Finder where to start, you need to add `@CrawlStartPoint` annotations.
You need to have exactly one such annotated class in each connected
set of pages; any more or less, and XSS Finder doesn't know how to
begin.

XSS Finder will generate a set of routes through the web site under
test that excercises every page, but not necessarily every method
that moves between pages. It's important that all the submit
actions are traversed, though, to ensure the XSS vulnerabilities
are flushed out. You can tell XSS Finder that a method on a page
object is one of these important submissions by annotating it with
`@SubmitAction`. XSS Finder will ensure all of these methods are
called.

### Custom Traversers ###
XSS Finder will do a great job if all the methods to navigate
between pages take no arguments, but without help, it won't tackle
any that need arguments (such as a login method). To make use of
these methods, you need to provide a custom traverses.

A custom traverser is a class that implements `CustomTraverser`.
This defines the method `Object traverse(Object page)`, i.e. takes
a page and returns another.

To tell XSS Finder to use a custom traverser, annotate the method with
`@TraverseWith(YourCustomTraverser.class)`.

### Route Lifecycle Event Handlers ###
If you need to perform any clean-up after a run through a route
you can specify a route lifecycle event handler via the
`lifecycleHandler` attribute of the route's `@CrawlStartPoint`.

Annotate the method on your lifecycle handler you want to be invoked
once the route has finished with `@AfterRoute`.

TODOs
-----
1. Gracefully handle errors encountered traversing a route. (Perhaps
also adding new routes to reach any pages not visited as a result
of the error).
2. Handle submit actions that return void (e.g. for client-side
only submissions that don't change page).
3. Implement as a Maven plugin.
4. Make the reports (lots) prettier.
5. Add DriverWrappers beyond just HtmlUnitDriver.
6. Add more lifecycle events (before route, before/after traversal).
7. Refactor Graph.java - it's a bit too big and complex.s