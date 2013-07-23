XSS Finder [![Build Status](https://travis-ci.org/rowanhill/xssfinder.png?branch=master)](https://travis-ci.org/rowanhill/xssfinder)
==========
XSS Finder builds on your existing Java web test [page objects](https://code.google.com/p/selenium/wiki/PageObjects)
to automatically traverse your web application searching for XSS vulnerabilities.

According to [OWASP](https://www.owasp.org/index.php/Category:OWASP_Top_Ten_Project),
cross site scripting (XSS) is one of the top three security
vulnerabilities affecting web applications.
[Preventing](https://www.owasp.org/index.php/XSS_%28Cross_Site_Scripting%29_Prevention_Cheat_Sheet)
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
good practice, regardless of whether or not you want to use XSS
Finder), you've already encoded a wealth of information about
how to get around the site.

XSS Finder uses this information (with a bit more metadata, in
the form of annotations) to automate basic XSS detection. In a
nutshell, it does the following:

1. Find all pages, and how they fit together, then pick a few
routes that visit all pages at least once.
1. Run through all the routes, submitting all forms with uniquely
identifiable attacks as it goes.
1. Look for evidence of these attacks being executed as it goes.
1. Run through all the routes a second time (in case later attacks
show up on earlier pages).
1. Write a report on all successful attacks, details where they
originated and where they were executed.

Usage
-----
### Summary ###
1. Annotate page objects with `@Page`.
1. Annotate the entry point page objects with `@CrawlStartPoint`.
1. Annotate the methods which submit forms with `@SubmitAction`.
1. Add any custom traversers:
    1. Create a class that implements `CustomTraverser`
    1. Annotate the page object method with `@TraverseWith`
1. Add any custom submitters:
    1. Create a class that implements `CustomSubmitter`
    1. Supply the class to the page object method's `@SubmitAction` annotation.
1. Add any route lifecycle handlers:
    1. Reference lifecycle handler class in `@CrawlStartPoint`
    1. Annotate a method on that class with `@AfterRoute`
1. Run XSS Finder - currently easiest from a unit test

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
test that exercises every page, but not necessarily every method
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

```java
@Page
public class RegisterPage {
    @TraverseWith(RegisterTraverser.class)
    public HomePage register(String username, String password) {
        // ...
    }
}

public class RegisterTraverser implements CustomTraverser {
    @Override
    public HomePage traverse(Object page) {
        if (!(page instanceof RegisterPage)) {
            throw new UntraversableException(page.toString() + " was not instance of RegisterPage");
        }
        RegisterPage registerPage = (RegisterPage)page;
        return registerPage.register(
            // ...
        );
    }
}
```

### Custom Submitters ###
If you have a submit method (annotated with `@SubmitAction`) which also
takes parameters (so is annotated with `@TraverseWith`), you'll want to
user a custom submitter, as well.

Custom submitters implement `CustomSubmitter`. They're like custom traversers,
but are used when XSS Finder is attacking the page, so the parameters to
provide when you call the annotated method on the page object should contain
XSS attacks. You generate the attacks using the provided `LabelledXssGenerator`.

Don't worry if providing XSS attacks as parameters won't result in the web
session ending up on the right page (e.g. if you give XSS attacks to a login
method, you may end up on an error page, rather than the page specified by the
return type of the login method). When a custom submitter is used, it's always
the last page traversal of a chain, so the resulting page doesn't matter.

To tell XSS Finder to use a custom submitter, annotate the method with
`@SubmitAction(YourCustomSubmitter.class)`.

```java
@Page
public class LoginPage {
    @TraverseWith(LoginTraverser.class)
    @SubmitAction(LoginTraverser.class)
    public HomePage login(String username, String password) {
        // ...
    }
}

public class LoginTraverser implements CustomTraverser, CustomSubmitter {
    @Override
    public HomePage traverse(Object page) {
        // ...
    }

    @Override
    public HomePage Object submit(Object page, LabelledXssGenerator xssGenerator) {
        if (!(page instanceof LoginPage)) {
            throw new UntraversableException(page.toString() + " was not instance of LoginPage");
        }
        LoginPage loginPage = (LoginPage)page;
        return loginPage.logInAs(
                xssGenerator.getXssAttackTextForLabel("username"),
                xssGenerator.getXssAttackTextForLabel("password")
        );
    }
}
```

### Route Lifecycle Event Handlers ###
If you need to perform any clean-up after a run through a route
you can specify a route lifecycle event handler via the
`lifecycleHandler` attribute of the route's `@CrawlStartPoint`.

Annotate the method on your lifecycle handler you want to be invoked
once the route has finished with `@AfterRoute`.

TODOs
-----
1. Investigate splitting control & coordination (generating & running routes, creating XSS attacks, logging results &
   making reports, etc) from execution (finding pages, invoking traversals) and connecting via Thrift, to allow multiple
   language implementations of the execution section. See thrift branch.
1. Record entirety of route that had error
1. Record errors executing lifecycle event handlers
1. Pass DriverWrapper to @AfterRoute method
1. Somehow group / de-duplicate errors that happen on both runs (attack / observe)
1. Allow no-args, no-@TraverseWith submit methods to be non-terminal (to reduce total # routes)
1. Handle submit actions that return void (e.g. for client-side
only submissions that don't change page).
1. Implement as a Maven plugin.
1. Write a Builder interface for setting up & running XSS Finder (e.g. from JUnit)
1. Make the reports (lots) prettier.
1. Add DriverWrappers beyond just HtmlUnitDriver.
1. Add more lifecycle events (before route, before/after traversal).
1. Consider annotation inheritance - at a minimum, `@Page` probably
wants to be marked as 
[`@Inherited`](http://docs.oracle.com/javase/6/docs/api/java/lang/annotation/Inherited.html)
1. Have RouteRunner#run create (from a factory) and return an XssJournal,
rather than take one as a param
