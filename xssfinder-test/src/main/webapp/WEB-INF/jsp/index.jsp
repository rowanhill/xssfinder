<%@page pageEncoding="UTF-8"%>
<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@page isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<body>
  <p>You are currently logged in. Would you like to <a href="/simple/j_spring_security_logout">log out</a>?
  <ul>
  <c:forEach items="${strings}" var="string">
      <li>${string}</li><% // XSS vuln here %>
  </c:forEach>
  </ul>
  <form action="." method="POST">
     <input type="text" name="textinput" />
     <input id="submit" type="submit" value="Submit" />
  </form>
</body>
</html>