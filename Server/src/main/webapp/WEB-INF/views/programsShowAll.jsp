<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Programs</title>
</head>
<body>
<a href="<c:url value="/logout" />">Logout</a><c:out value=" | " />
<a href="<c:url value="/changepw" />">Change Password</a>
<c:url var="usersUrl" value="/users/" />
<c:if test="${user.admin}"><c:out value=" | " /><a href="${usersUrl}">Show System Users</a></c:if>
<h1>Programs</h1>
<c:url var="addUrl" value="/programs/add" />
<c:if test="${user.packageAdmin}"><a href="${addUrl}">Add New Program</a></c:if>
<table style="border: 1px solid; width: 400px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Program Name</th>
   <th colspan="3"></th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${progs}" var="prog">
 	<c:url var="showUrl" value="/packages?id=${prog.id}" />
    <c:url var="editUrl" value="/programs/edit?id=${prog.id}" />
	<c:url var="deleteUrl" value="/programs/delete?id=${prog.id}" />
  <tr>
   <td><c:out value="${prog.name}" /></td>
   <td><a href="${showUrl}">Show Packages</a></td>
   <c:if test="${user.packageAdmin}"><td><a href="${editUrl}">Edit</a></td></c:if>
   <c:if test="${user.packageAdmin}"><td><a href="${deleteUrl}">Delete</a></td></c:if>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty progs}">
 There are currently no packages in the system.
</c:if>
 
</body>
</html>