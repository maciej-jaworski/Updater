<?xml version="1.0" encoding="ISO-8859-1" ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Packages</title>
</head>
<body>
<a href="<c:url value="/logout" />">Logout</a><c:out value=" | " />
<a href="<c:url value="/changepw" />">Change Password</a>
<h1>Packages</h1>
<c:url var="backUrl" value="/programs" />
<a href="${backUrl}">Back To Programs List</a>
<c:if test="${user.packageAdmin}"><c:out value=" | " />
<c:url var="addUrl" value="/packages/add?id=${programId}" />
<a href="${addUrl}">Add new Package.</a></c:if>
<table style="border: 1px solid; width: 500px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Package Name</th>
   <th colspan="3"></th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${packs}" var="pack">
   <c:url var="showUrl" value="/updates/list?id=${pack.id}&pid=${programId}" />
   <c:url var="editUrl" value="/packages/edit?id=${pack.id}&pid=${programId}" />
   <c:url var="deleteUrl" value="/packages/delete?id=${pack.id}&pid=${programId}" />
  <tr>
   <td><c:out value="${pack.name}" /></td>
   <td><a href="${showUrl}">Show Updates</a></td>
   <c:if test="${user.packageAdmin}"><td><a href="${editUrl}">Edit</a></td></c:if>
   <c:if test="${user.packageAdmin}"><td><a href="${deleteUrl}">Delete</a></td></c:if>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty packs}">
 There are currently no packages in the system for the specified program.
</c:if>
 
</body>
</html>