<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Users</title>
</head>
<body>
<a href="<c:url value="/logout" />">Logout</a><c:out value=" | " />
<a href="<c:url value="/changepw" />">Change Password</a>
<h1>Users</h1>

<c:url var="addUrl" value="/users/add" />
<a href="${addUrl}">Add new User.</a>
<table style="border: 1px solid; width: 800px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Full Name</th>
   <th>Username</th>
   <th>Admin Privileges</th>
   <th>Package Admin Privileges</th>
   <th colspan="4"></th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${users}" var="user">
   <c:url var="editUrl" value="/users/edit?id=${user.id}" />
   <c:url var="deleteUrl" value="/users/delete?id=${user.id}" />
   <c:url var="resetUrl" value="/users/reset?id=${user.id}" />
  <tr>
   <td><c:out value="${user.name}" /></td>
   <td><c:out value="${user.username}" /></td>
   <td><c:out value="${user.admin}" /></td>
   <td><c:out value="${user.packageAdmin}" /></td>
   <td><a href="${editUrl}">Edit</a></td>
   <td><a href="${deleteUrl}">Delete</a></td>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty users}">
 There are currently no users in the system.
</c:if>
 
</body>
</html>