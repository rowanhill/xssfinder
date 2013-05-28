<%@page pageEncoding="UTF-8"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
  <p>You are currently logged in. Would you like to <a href="/simple/j_spring_security_logout">log out</a>?
  <ul>
  <c:forEach items="${unsafeStrings}" var="string">
      <li>${string}</li><% // XSS vuln here %>
  </c:forEach>
  </ul>
  <form action="/simple/unsafe" method="POST" id="unsafeForm">
     <input type="text" name="textinput" />
     <input id="unsafeSubmit" type="submit" value="Submit" />
  </form>
  <ul>
    <c:forEach items="${safeStrings}" var="string">
        <li><c:out value="${string}" /></li><% // No XSS vuln here %>
    </c:forEach>
  </ul>
  <form action="/simple/safe" method="POST" id="safeForm">
     <input type="text" name="textinput" />
     <input id="safeSubmit" type="submit" value="Submit" />
  </form>
</body>
</html>