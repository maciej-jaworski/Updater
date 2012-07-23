<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Updates</title>
</head>
<body>
<a href="<c:url value="/logout" />">Logout</a><c:out value=" | " />
<a href="<c:url value="/changepw" />">Change Password</a>
<c:url var="packsUrl" value="/packages/" />
<c:if test="${user.packageAdmin}"><c:out value=" | " /><a href="${packsUrl}">Show Packages</a></c:if>
<c:url var="usersUrl" value="/users/" />
<c:if test="${user.admin}"><c:out value=" | " /><a href="${usersUrl}">Show System Users</a></c:if>
<h1>Packages</h1>
<table style="border: 1px solid; width: 400px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Package Name</th>
   <th colspan="2"></th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${packs}" var="pack">
 <c:url var="showUrl" value="/updates/list?id=${pack.id}" />
  <tr>
   <td><c:out value="${pack.name}" /></td>
   <td><a href="${showUrl}">Show Updates</a></td>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty packs}">
 There are currently no packages in the system.
</c:if>
 
</body>
</html>