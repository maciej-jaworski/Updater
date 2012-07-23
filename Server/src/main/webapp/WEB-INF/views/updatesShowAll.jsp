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
<h1>Updates</h1>
 <c:url var="updateUrl" value="/packages?id=${programId}" />
<a href="${updateUrl}">Back to Packages List</a>
<c:out value=" | " />
<c:url var="addUrl" value="/updates/add?id=${packId}&pid=${programId}" />
<a href="${addUrl}">Add new Update</a>
<table style="border: 1px solid; width: 800px; text-align:center">
 <thead style="background:#fcf">
  <tr>
   <th>Changelog</th>
   <th>Date Added</th>
   <th>Version</th>
   <th colspan="4"></th>
  </tr>
 </thead>
 <tbody>
 <c:forEach items="${updates}" var="update">
   <c:url var="editUrl" value="/updates/edit?id=${update.id}" />
   <c:url var="deleteUrl" value="/updates/delete?id=${update.id}&packid=${packId}&progid=${programId }" />
  <tr>
   <td><c:out value="${update.changelog}" /></td>
   <td><c:out value="${update.data}" /></td>
   <td><c:out value="${update.ver1}.${update.ver2}.${update.ver3}.${update.ver4}" /></td>
   <td><a href="${deleteUrl}">Delete</a></td>
  </tr>
 </c:forEach>
 </tbody>
</table>
 
<c:if test="${empty updates}">
 There are currently no updates in the system.
</c:if>
 
</body>
</html>